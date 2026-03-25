package org.cloudserviceengineering.app;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import java.io.StringReader;
import org.cloud.paas.medicalhistoryservice.MedicalHistory_implRemote;
import org.cloud.paas.usermanagementservice.MultipleTenantManageService_implRemote;
import util.ejbUtility;

/**
 * DeepSeek AI 助手 Servlet
 * 功能：接收用户消息，调用 DeepSeek API 返回智能回复
 * 支持获取患者病历数据作为上下文
 */
@WebServlet("/DeepSeekAI_Tool")
public class DeepSeekAI_Tool extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // DeepSeek API 配置
    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/chat/completions";
    private static final String DEEPSEEK_API_KEY = "sk-c3bbbe9ed8bc40148459b1bcbc0b8e51"; 
    private static final String MODEL = "deepseek-chat";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();


        // 1. 校验登录态
        HttpSession session = request.getSession();
        Long patientId = (Long) session.getAttribute("patientId");
        String username = (String) session.getAttribute("patientUsername");

        if (patientId == null) {
            System.out.println("警告：登录态失效");
            JsonObject errorResponse = Json.createObjectBuilder()
                .add("success", false)
                .add("reply", "请先登录后再使用 AI 助手")
                .build();
            out.print(errorResponse.toString());
            return;
        }

        // 2. 读取请求体中的用户消息
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        String userMessage = "";
        try {
            JsonReader jsonReader = Json.createReader(new StringReader(sb.toString()));
            JsonObject requestJson = jsonReader.readObject();
            userMessage = requestJson.getString("message", "");
            jsonReader.close();
        } catch (Exception e) {
            System.out.println("解析请求 JSON 失败：" + e.getMessage());
        }

        if (userMessage.isEmpty()) {
            JsonObject errorResponse = Json.createObjectBuilder()
                .add("success", false)
                .add("reply", "消息不能为空")
                .build();
            out.print(errorResponse.toString());
            return;
        }

        System.out.println("用户消息：" + userMessage);

        // 3. 获取患者病历数据作为上下文
        String patientContext = getPatientContext(patientId, username);

        // 4. 构建系统提示词
        String systemPrompt = buildSystemPrompt(patientContext);

        // 5. 调用 DeepSeek API
        String aiReply = callDeepSeekAPI(systemPrompt, userMessage);

        // 6. 返回响应
        JsonObject successResponse = Json.createObjectBuilder()
            .add("success", true)
            .add("reply", aiReply)
            .build();
        out.print(successResponse.toString());
    }

    /**
     * 获取患者病历上下文
     */
    private String getPatientContext(Long patientId, String username) {
        StringBuilder context = new StringBuilder();
        context.append("患者姓名：").append(username).append("\n");

        // 获取患者统计数据（就诊次数、待缴费用）
        try {
            MultipleTenantManageService_implRemote userService = ejbUtility.getTenantService();
            String statsJson = userService.getPatientStats(patientId);
            if (statsJson != null) {
                JsonReader jr = Json.createReader(new StringReader(statsJson));
                JsonObject stats = jr.readObject();
                jr.close();
                int visitCount = stats.getInt("visitCount", 0);
                double pendingFee = stats.getJsonNumber("pendingFee").doubleValue();
                context.append("累计就诊次数：").append(visitCount).append("次\n");
                if (pendingFee > 0) {
                    context.append("待缴费用：").append(String.format("%.2f", pendingFee)).append("元（提醒患者及时缴费）\n");
                } else {
                    context.append("待缴费用：无\n");
                }
            }
        } catch (Exception e) {
            System.out.println("获取患者统计数据失败：" + e.getMessage());
        }

        // 获取就诊记录详情
        try {
            MedicalHistory_implRemote medicalHistoryEJB = ejbUtility.getMedicalHistoryService();
            List<Map<String, Object>> historyList = medicalHistoryEJB.getMedicalHistoryByPatientId(patientId);

            if (historyList != null && !historyList.isEmpty()) {
                context.append("\n近期就诊记录（最近3条）：\n");
                int count = 0;
                for (Map<String, Object> process : historyList) {
                    if (count >= 3) break;

                    String status = (String) process.get("processStatus");
                    String createdAt = (String) process.get("createdAt");
                    String completedAt = (String) process.get("completedAt");
                    Long processId = (Long) process.get("processId");

                    context.append("\n就诊流程 #").append(processId).append("：\n");
                    context.append("  - 状态：").append(status != null ? status : "未知").append("\n");
                    if (createdAt != null && !createdAt.isEmpty()) {
                        context.append("  - 创建时间：").append(createdAt).append("\n");
                    }
                    if (completedAt != null && !completedAt.isEmpty()) {
                        context.append("  - 完成时间：").append(completedAt).append("\n");
                    }

                    // 解析流程节点，获取诊断和医嘱
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> nodes = (List<Map<String, Object>>) process.get("processNodes");
                    if (nodes != null && !nodes.isEmpty()) {
                        for (Map<String, Object> node : nodes) {
                            String nodeName = (String) node.get("nodeName");
                            String diagnosisText = (String) node.get("diagnosisText");
                            String reminder = (String) node.get("reminder");

                            // 只显示有诊断或医嘱的节点
                            if ((diagnosisText != null && !diagnosisText.isEmpty()) ||
                                (reminder != null && !reminder.isEmpty())) {
                                context.append("  【").append(nodeName != null ? nodeName : "未知环节").append("】\n");
                                if (diagnosisText != null && !diagnosisText.isEmpty()) {
                                    context.append("    诊断：").append(diagnosisText).append("\n");
                                }
                                if (reminder != null && !reminder.isEmpty()) {
                                    context.append("    医嘱/处方：").append(reminder).append("\n");
                                }
                            }
                        }
                    }
                    count++;
                }
            } else {
                context.append("\n暂无就诊记录（可以引导患者进行首次预约挂号）\n");
            }
        } catch (NamingException e) {
            System.out.println("获取病历数据失败：" + e.getMessage());
            context.append("病历数据获取失败\n");
        }

        return context.toString();
    }

    /**
     * 构建系统提示词
     */
    private String buildSystemPrompt(String patientContext) {
        return "你是一个专业的医疗健康 AI 助手，名字叫\"小医\"，服务于\"云医疗服务平台\"。\n\n" +
               "【你的职责】\n" +
               "1. 回答用户的健康咨询问题\n" +
               "2. 指导用户如何使用本医疗系统的各项功能\n" +
               "3. 根据患者的就诊历史和病历提供个性化建议\n" +
               "4. 提醒用户及时就医，不要自行诊断和用药\n\n" +
               "【云医疗服务平台功能介绍】\n" +
               "本系统提供以下功能，你需要指导用户如何使用：\n\n" +
               "1. 预约挂号：\n" +
               "   - 点击上方菜单的\"预约挂号\"\n" +
               "   - 选择医院\n" +
               "   - 选择科室（内科、外科、儿科、妇产科、眼科、口腔科、皮肤科、骨科、神经内科、心血管科等）\n" +
               "   - 选择医生和就诊时间\n" +
               "   - 点击\"确认预约\"完成挂号\n\n" +
               "2. 就诊流程查看：\n" +
               "   - 点击左侧菜单的\"就诊流程\"\n" +
               "   - 可以查看当前正在进行的就诊流程和各环节状态\n" +
               "   - 流程包括：挂号 → 候诊 → 就诊 → 检查/检验 → 取药 → 完成\n\n" +
               "3. 看病历史/就诊记录：\n" +
               "   - 点击左侧菜单的\"看病历史\"\n" +
               "   - 可以查看所有历史就诊记录\n" +
               "   - 点击\"查看详情\"可以看到诊断结果、处方、检查报告等详细信息\n\n" +
               "4. 费用缴纳：\n" +
               "   - 点击左侧菜单的\"费用缴纳\"\n" +
               "   - 查看待缴费项目（挂号费、检查费、药费等）\n" +
               "   - 支持微信支付和支付宝支付\n\n" +
               "5. 个人信息：\n" +
               "   - 点击左侧菜单的\"个人信息\"\n" +
               "   - 可以查看和修改个人资料（姓名、手机号、身份证等）\n" +
               "   - 可以修改登录密码\n" +
               "   - 显示就诊次数和待缴费用统计\n\n" +
               "【注意事项】\n" +
               "- 回答要专业、准确、通俗易懂\n" +
               "- 涉及具体诊断和用药时，务必提醒用户咨询专业医生\n" +
               "- 指导系统操作时要具体、清晰\n" +
               "- 保持友好、耐心的态度\n" +
               "- 回答要简洁，控制在300字以内\n\n" +
               "【当前患者信息】\n" + patientContext;
    }

    /**
     * 调用 DeepSeek API
     */
    private String callDeepSeekAPI(String systemPrompt, String userMessage) {
        try {
            URL url = new URL(DEEPSEEK_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + DEEPSEEK_API_KEY);
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(60000);

            // 构建请求体
            JsonArrayBuilder messagesBuilder = Json.createArrayBuilder()
                .add(Json.createObjectBuilder()
                    .add("role", "system")
                    .add("content", systemPrompt))
                .add(Json.createObjectBuilder()
                    .add("role", "user")
                    .add("content", userMessage));

            JsonObject requestBody = Json.createObjectBuilder()
                .add("model", MODEL)
                .add("messages", messagesBuilder)
                .add("temperature", 0.7)
                .add("max_tokens", 500)
                .build();

            // 发送请求
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            // 读取响应
            int responseCode = conn.getResponseCode();
            System.out.println("DeepSeek API 响应码：" + responseCode);

            BufferedReader br;
            if (responseCode >= 200 && responseCode < 300) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            }

            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine);
            }
            br.close();

            System.out.println("DeepSeek API 响应：" + response.toString());

            // 解析响应
            if (responseCode >= 200 && responseCode < 300) {
                JsonReader jsonReader = Json.createReader(new StringReader(response.toString()));
                JsonObject responseJson = jsonReader.readObject();
                JsonArray choices = responseJson.getJsonArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    JsonObject firstChoice = choices.getJsonObject(0);
                    JsonObject message = firstChoice.getJsonObject("message");
                    return message.getString("content", "抱歉，我暂时无法回答这个问题。");
                }
            }

            return "抱歉，AI 服务暂时不可用，请稍后再试。";

        } catch (Exception e) {
            System.out.println("调用 DeepSeek API 失败：" + e.getMessage());
            e.printStackTrace();
            return "抱歉，网络连接出现问题，请检查网络后重试。";
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        JsonObject info = Json.createObjectBuilder()
            .add("service", "DeepSeek AI Assistant")
            .add("status", "running")
            .add("method", "Please use POST method with JSON body: {\"message\": \"your question\"}")
            .build();
        out.print(info.toString());
    }
}

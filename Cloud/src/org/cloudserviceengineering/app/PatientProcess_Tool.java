package org.cloudserviceengineering.app;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.naming.NamingException;

import org.cloud.paas.patientprocessservice.PatientProcessService_implRemote;
import util.ejbUtility;

/**
 * 患者就医进程Servlet
 * 处理患者端页面(patient_process.jsp)的AJAX请求
 * 使用JNDI通过EJBClientUtility获取PatientProcessService_implRemote服务
 */
@WebServlet("/PatientProcess_Tool")
public class PatientProcess_Tool extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    // 步骤名称映射
    private static final String[] STEP_NAMES = {"挂号", "问诊", "检查", "治疗", "取药", "完成"};
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doPost(request, response);
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        String action = request.getParameter("action");
        
        if (action == null || action.isEmpty()) {
            // 校验登录态
            if (request.getSession().getAttribute("patientId") == null) {
                response.sendRedirect(request.getContextPath() + "/index.jsp");
                return;
            }
            request.getRequestDispatcher("/patient_process.jsp").forward(request, response);
            return;
        }
        
        // ========== 1. 设置CORS和字符编码 ==========
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        System.out.println("==================== [PatientProcess_Tool] ====================");
        System.out.println("[PatientProcess_Tool] action参数: " + action);
        String processIdStr = request.getParameter("processId");
        String stepStr = request.getParameter("step");
        String patientName = request.getParameter("patientName");
        
        System.out.println("[PatientProcess_Tool] 参数 - action: " + action);
        System.out.println("[PatientProcess_Tool] 参数 - processId: " + processIdStr);
        System.out.println("[PatientProcess_Tool] 参数 - step: " + stepStr);
        System.out.println("[PatientProcess_Tool] 参数 - patientName: " + patientName);
        
        PrintWriter out = response.getWriter();
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        
        try {
            // ========== 2. 获取EJB服务 ==========
            System.out.println("[PatientProcess_Tool] 尝试通过JNDI获取EJB服务...");
            PatientProcessService_implRemote patientProcessService = ejbUtility.getPatientProcessService();
            
            if (patientProcessService == null) {
                jsonBuilder.add("success", false)
                          .add("message", "无法连接到患者进程服务");
                out.print(jsonBuilder.build().toString());
                return;
            }
            
            System.out.println("[PatientProcess_Tool] EJB服务获取成功");
            
            // ========== 4. 根据action处理请求 ==========
            switch (action) {
                case "getStepData":
                    handleGetStepData(patientProcessService, processIdStr, stepStr, jsonBuilder);
                    break;
                    
                case "updateStep":
                    String nodeStatus = request.getParameter("nodeStatus");
                    String diagnosisText = request.getParameter("diagnosisText");
                    handleUpdateStep(patientProcessService, processIdStr, stepStr, nodeStatus, diagnosisText, jsonBuilder);
                    break;
                    
                case "getMedicineInfo":
                    String medicineIdStr = request.getParameter("medicineId");
                    handleGetMedicineInfo(patientProcessService, medicineIdStr, jsonBuilder);
                    break;
                    
                case "searchMedicine":
                    String medicineName = request.getParameter("medicineName");
                    handleSearchMedicine(patientProcessService, medicineName, jsonBuilder);
                    break;
                    
                case "getAllProcessNodes":
                    handleGetAllProcessNodes(patientProcessService, processIdStr, jsonBuilder);
                    break;
                    
                case "getMedicalProcessesForPatient1":
                    handleGetMedicalProcessesForPatient1(patientProcessService, jsonBuilder);
                    break;
                    
                case "getProcessNodeByName":
                    String nodeName = request.getParameter("nodeName");
                    handleGetProcessNodeByName(patientProcessService, processIdStr, nodeName, jsonBuilder);
                    break;
                    
                case "testConnection":
                    handleTestConnection(patientProcessService, jsonBuilder);
                    break;
                    
                case "getLatestMedicalProcessInfo":
                    handleGetLatestMedicalProcessInfo(patientProcessService, jsonBuilder, request);
                    break;
                    
                default:
                    jsonBuilder.add("success", false)
                              .add("message", "未知的操作类型: " + action);
            }
            
        } catch (NamingException e) {
            System.out.println("[PatientProcess_Tool] JNDI查找失败: " + e.getMessage());
            e.printStackTrace();
            
            jsonBuilder.add("success", false)
                      .add("message", "EJB服务连接失败: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[PatientProcess_Tool] 处理请求时发生错误: " + e.getMessage());
            e.printStackTrace();
            
            jsonBuilder.add("success", false)
                      .add("message", "系统错误: " + e.getMessage());
        }
        
        out.print(jsonBuilder.build().toString());
        System.out.println("[PatientProcess_Tool] 请求处理完成");
        System.out.println("==============================================================\n");
    }
 // ========== 获取最新医疗进程信息 ==========
    private void handleGetLatestMedicalProcessInfo(PatientProcessService_implRemote service,
            JsonObjectBuilder jsonBuilder, HttpServletRequest request) {

        System.out.println("[PatientProcess_Tool] 处理获取最新医疗进程信息");

        try {
            // 从 session 获取当前登录用户的 patientId
            Long patientId = (Long) request.getSession().getAttribute("patientId");
            System.out.println("[PatientProcess_Tool] 当前登录患者ID: " + patientId);

            if (patientId == null) {
                jsonBuilder.add("success", false)
                          .add("message", "请先登录");
                return;
            }

            // 调用按患者ID查询的新方法
            Map<String, Object> processInfo = service.getLatestMedicalProcessInfoByPatientId(patientId);

            if (processInfo != null && !processInfo.isEmpty()) {
                JsonObjectBuilder dataBuilder = Json.createObjectBuilder();

                for (Map.Entry<String, Object> entry : processInfo.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    dataBuilder.add(key, value != null ? value.toString() : "");
                }

                jsonBuilder.add("success", true)
                          .add("data", dataBuilder.build());

                System.out.println("[PatientProcess_Tool] 患者ID=" + patientId + " 的最新医疗进程信息获取成功");

            } else {
                jsonBuilder.add("success", false)
                          .add("message", "未找到最新医疗进程信息");
            }

        } catch (RemoteException e) {
            jsonBuilder.add("success", false)
                      .add("message", "远程调用失败: " + e.getMessage());
        } catch (Exception e) {
            jsonBuilder.add("success", false)
                      .add("message", "获取最新医疗进程信息失败: " + e.getMessage());
        }
    }
    // ========== 5. 获取步骤数据 ==========
 // ========== 5. 获取步骤数据 ==========
    private void handleGetStepData(PatientProcessService_implRemote service, String processIdStr, String stepStr, 
            JsonObjectBuilder jsonBuilder) {
        
        System.out.println("[PatientProcess_Tool] 处理获取步骤数据: processId=" + processIdStr + ", step=" + stepStr);
        
        try {
            if (processIdStr == null || processIdStr.isEmpty() || stepStr == null || stepStr.isEmpty()) {
                jsonBuilder.add("success", false)
                          .add("message", "缺少必要参数: processId 或 step");
                return;
            }
            
            int processId = Integer.parseInt(processIdStr);
            int step = Integer.parseInt(stepStr);
            
            // 验证步骤范围
            if (step < 1 || step > STEP_NAMES.length) {
                jsonBuilder.add("success", false)
                          .add("message", "步骤号无效，有效范围: 1-" + STEP_NAMES.length);
                return;
            }
            
            // 获取步骤数据
            Map<String, Object> stepData = service.getProcessNodeByStep(processId, step);
            
            if (stepData != null && !stepData.isEmpty()) {
                // 根据步骤准备响应数据
                JsonObjectBuilder dataBuilder = Json.createObjectBuilder();
                
                dataBuilder.add("nodeStatus", getStringValue(stepData.get("nodeStatus"), "待完成"));
                dataBuilder.add("diagnosisText", getStringValue(stepData.get("diagnosisText"), "暂无诊断信息"));
                dataBuilder.add("reminder", getStringValue(stepData.get("reminder"), "请完成此步骤"));
                dataBuilder.add("createdAt", getStringValue(stepData.get("createdAt"), ""));
                dataBuilder.add("nodeName", getStringValue(stepData.get("nodeName"), STEP_NAMES[step-1]));
                dataBuilder.add("location", getStringValue(stepData.get("location"), ""));
                dataBuilder.add("doctorName", getStringValue(stepData.get("doctorName"), ""));
                dataBuilder.add("doctorTitle", getStringValue(stepData.get("doctorTitle"), ""));
                dataBuilder.add("medicineName", getStringValue(stepData.get("medicineName"), ""));
                dataBuilder.add("useMethod", getStringValue(stepData.get("useMethod"), ""));
                dataBuilder.add("pictures", getStringValue(stepData.get("pictures"), ""));
                
                if (step == 5) { // 取药步骤       
                    Object medicineInfoObj = stepData.get("medicineInfo");
                    
                    if (medicineInfoObj != null) {
                        try {
                            if (medicineInfoObj instanceof Map) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> medicineInfo = (Map<String, Object>) medicineInfoObj;
    
                                JsonObjectBuilder medicineBuilder = Json.createObjectBuilder();
                               
                                // 从数据库中获取的药品信息包含所有字段
                                medicineBuilder.add("name", getStringValue(medicineInfo.get("name"), "未知药品"));
                                medicineBuilder.add("dosage", getStringValue(medicineInfo.get("dosage"), "未指定"));
                                medicineBuilder.add("useMethod", getStringValue(medicineInfo.get("useMethod"), "未指定"));
                                medicineBuilder.add("price", getStringValue(medicineInfo.get("price"), "0.00"));
                                medicineBuilder.add("sideEffect", getStringValue(medicineInfo.get("sideEffect"), "无"));

                                if (medicineInfo.containsKey("medicineId")) {
                                    medicineBuilder.add("medicineId", getStringValue(medicineInfo.get("medicineId"), ""));
                                }
       
                                dataBuilder.add("medicineInfo", medicineBuilder.build());
                                
                                System.out.println("[PatientProcess_Tool] 从medicineInfo对象获取药品信息成功，包含所有五个字段");
                            } else {
                                System.out.println("[PatientProcess_Tool] medicineInfoObj不是Map类型: " + medicineInfoObj.getClass());
                            }
                        } catch (Exception e) {
                            System.out.println("[PatientProcess_Tool] 处理medicineInfo对象失败: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("[PatientProcess_Tool] stepData中没有medicineInfo字段");
                        // 创建包含所有五个字段的默认药品信息
                        JsonObjectBuilder medicineBuilder = Json.createObjectBuilder();
                        medicineBuilder.add("name", "待开具药品");
                        medicineBuilder.add("useMethod", "请遵医嘱");
                        medicineBuilder.add("dosage", "请遵医嘱");
                        medicineBuilder.add("sideEffect", "无");
                        medicineBuilder.add("price", "0.00");
                        
                        dataBuilder.add("medicineInfo", medicineBuilder.build());
                    }
                }
                
                jsonBuilder.add("success", true)
                          .add("data", dataBuilder.build());
                
                System.out.println("[PatientProcess_Tool] 步骤数据获取成功，步骤: " + step);
                
            } else {
                // 如果数据库中没有数据，创建默认数据
                JsonObjectBuilder dataBuilder = Json.createObjectBuilder();
                dataBuilder.add("nodeStatus", "待完成");
                dataBuilder.add("diagnosisText", "暂无诊断信息");
                dataBuilder.add("reminder", "请完成此步骤");
                dataBuilder.add("nodeName", STEP_NAMES[step-1]);
                
                jsonBuilder.add("success", true)
                          .add("data", dataBuilder.build());
                
                System.out.println("[PatientProcess_Tool] 使用默认步骤数据，步骤: " + step);
            }
            
        } catch (NumberFormatException e) {
            jsonBuilder.add("success", false)
                      .add("message", "参数格式错误: " + e.getMessage());
        } catch (RemoteException e) {
            jsonBuilder.add("success", false)
                      .add("message", "远程调用失败: " + e.getMessage());
        } catch (Exception e) {
            jsonBuilder.add("success", false)
                      .add("message", "获取步骤数据失败: " + e.getMessage());
        }
    }
    
    // ========== 6. 更新步骤数据 ==========
    private void handleUpdateStep(PatientProcessService_implRemote service, String processIdStr, String stepStr,
            String nodeStatus, String diagnosisText, JsonObjectBuilder jsonBuilder) {
        
        System.out.println("[PatientProcess_Tool] 处理更新步骤数据: processId=" + processIdStr + ", step=" + stepStr);
        
        try {
            if (processIdStr == null || processIdStr.isEmpty() || stepStr == null || stepStr.isEmpty()) {
                jsonBuilder.add("success", false)
                          .add("message", "缺少必要参数: processId 或 step");
                return;
            }
            
            int processId = Integer.parseInt(processIdStr);
            int step = Integer.parseInt(stepStr);
            
            // 验证步骤范围
            if (step < 1 || step > STEP_NAMES.length) {
                jsonBuilder.add("success", false)
                          .add("message", "步骤号无效，有效范围: 1-" + STEP_NAMES.length);
                return;
            }
            
            // 如果nodeStatus为空，使用默认值
            if (nodeStatus == null || nodeStatus.isEmpty()) {
                nodeStatus = "已完成";
            }
            
            // 如果diagnosisText为空，使用默认值
            if (diagnosisText == null || diagnosisText.isEmpty()) {
                diagnosisText = "步骤已更新";
            }
            
            // 调用EJB服务更新节点状态
            boolean result = service.updateNodeStatus(processId, step, nodeStatus, diagnosisText);
            
            if (result) {
                jsonBuilder.add("success", true)
                          .add("message", "步骤更新成功");
                System.out.println("[PatientProcess_Tool] 步骤更新成功，processId: " + processId + ", step: " + step);
            } else {
                jsonBuilder.add("success", false)
                          .add("message", "步骤更新失败");
            }
            
        } catch (NumberFormatException e) {
            jsonBuilder.add("success", false)
                      .add("message", "参数格式错误: " + e.getMessage());
        } catch (RemoteException e) {
            jsonBuilder.add("success", false)
                      .add("message", "远程调用失败: " + e.getMessage());
        } catch (Exception e) {
            jsonBuilder.add("success", false)
                      .add("message", "更新步骤数据失败: " + e.getMessage());
        }
    }
    
    // ========== 7. 获取药品信息 ==========
    private void handleGetMedicineInfo(PatientProcessService_implRemote service, String medicineIdStr, 
            JsonObjectBuilder jsonBuilder) {
        
        System.out.println("[PatientProcess_Tool] 处理获取药品信息: medicineId=" + medicineIdStr);
        
        try {
            if (medicineIdStr == null || medicineIdStr.isEmpty()) {
                jsonBuilder.add("success", false)
                          .add("message", "缺少必要参数: medicineId");
                return;
            }
            
            int medicineId = Integer.parseInt(medicineIdStr);
            Map<String, Object> medicineInfo = service.getMedicineInfo(medicineId);
            
            if (medicineInfo != null && !medicineInfo.isEmpty()) {
                JsonObjectBuilder dataBuilder = Json.createObjectBuilder();
                
                // 添加所有字段
                for (Map.Entry<String, Object> entry : medicineInfo.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    dataBuilder.add(key, value != null ? value.toString() : "");
                }
                
                jsonBuilder.add("success", true)
                          .add("data", dataBuilder.build());
                
                System.out.println("[PatientProcess_Tool] 药品信息获取成功，medicineId: " + medicineId);
                
            } else {
                jsonBuilder.add("success", false)
                          .add("message", "未找到药品信息");
            }
            
        } catch (NumberFormatException e) {
            jsonBuilder.add("success", false)
                      .add("message", "参数格式错误: " + e.getMessage());
        } catch (RemoteException e) {
            jsonBuilder.add("success", false)
                      .add("message", "远程调用失败: " + e.getMessage());
        } catch (Exception e) {
            jsonBuilder.add("success", false)
                      .add("message", "获取药品信息失败: " + e.getMessage());
        }
    }
    
    // ========== 8. 搜索药品 ==========
    private void handleSearchMedicine(PatientProcessService_implRemote service, String medicineName, 
            JsonObjectBuilder jsonBuilder) {
        
        System.out.println("[PatientProcess_Tool] 处理搜索药品: " + medicineName);
        
        try {
            if (medicineName == null || medicineName.isEmpty()) {
                jsonBuilder.add("success", false)
                          .add("message", "缺少必要参数: medicineName");
                return;
            }
            
            List<Map<String, Object>> medicines = service.searchMedicineByName(medicineName);
            
            if (medicines != null && !medicines.isEmpty()) {
                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                
                for (Map<String, Object> medicine : medicines) {
                    JsonObjectBuilder medicineBuilder = Json.createObjectBuilder();
                    
                    for (Map.Entry<String, Object> entry : medicine.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        medicineBuilder.add(key, value != null ? value.toString() : "");
                    }
                    
                    arrayBuilder.add(medicineBuilder.build());
                }
                
                jsonBuilder.add("success", true)
                          .add("data", arrayBuilder)
                          .add("total", medicines.size());
                
                System.out.println("[PatientProcess_Tool] 搜索到药品数量: " + medicines.size());
                
            } else {
                jsonBuilder.add("success", true)
                          .add("data", Json.createArrayBuilder())
                          .add("total", 0)
                          .add("message", "未找到相关药品");
            }
            
        } catch (RemoteException e) {
            jsonBuilder.add("success", false)
                      .add("message", "远程调用失败: " + e.getMessage());
        } catch (Exception e) {
            jsonBuilder.add("success", false)
                      .add("message", "搜索药品失败: " + e.getMessage());
        }
    }
    
    // ========== 9. 获取所有流程节点 ==========
    private void handleGetAllProcessNodes(PatientProcessService_implRemote service, String processIdStr, 
            JsonObjectBuilder jsonBuilder) {
        
        System.out.println("[PatientProcess_Tool] 处理获取所有流程节点: processId=" + processIdStr);
        
        try {
            if (processIdStr == null || processIdStr.isEmpty()) {
                jsonBuilder.add("success", false)
                          .add("message", "缺少必要参数: processId");
                return;
            }
            
            int processId = Integer.parseInt(processIdStr);
            List<Map<String, Object>> nodes = service.getAllProcessNodes(processId);
            
            if (nodes != null && !nodes.isEmpty()) {
                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                
                for (Map<String, Object> node : nodes) {
                    JsonObjectBuilder nodeBuilder = Json.createObjectBuilder();
                    
                    for (Map.Entry<String, Object> entry : node.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        nodeBuilder.add(key, value != null ? value.toString() : "");
                    }
                    
                    arrayBuilder.add(nodeBuilder.build());
                }
                
                jsonBuilder.add("success", true)
                          .add("data", arrayBuilder)
                          .add("total", nodes.size());
                
                System.out.println("[PatientProcess_Tool] 获取到流程节点数量: " + nodes.size());
                
            } else {
                // 如果数据库中没有数据，创建默认节点
                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                
                for (int i = 0; i < STEP_NAMES.length; i++) {
                    JsonObjectBuilder nodeBuilder = Json.createObjectBuilder();
                    nodeBuilder.add("nodeId", String.valueOf(i + 1));
                    nodeBuilder.add("nodeName", STEP_NAMES[i]);
                    nodeBuilder.add("nodeStatus", "待完成");
                    nodeBuilder.add("diagnosisText", "暂无诊断信息");
                    nodeBuilder.add("reminder", "请完成此步骤");
                    arrayBuilder.add(nodeBuilder.build());
                }
                
                jsonBuilder.add("success", true)
                          .add("data", arrayBuilder)
                          .add("total", STEP_NAMES.length);
                
                System.out.println("[PatientProcess_Tool] 使用默认流程节点数据");
            }
            
        } catch (NumberFormatException e) {
            jsonBuilder.add("success", false)
                      .add("message", "参数格式错误: " + e.getMessage());
        } catch (RemoteException e) {
            jsonBuilder.add("success", false)
                      .add("message", "远程调用失败: " + e.getMessage());
        } catch (Exception e) {
            jsonBuilder.add("success", false)
                      .add("message", "获取流程节点失败: " + e.getMessage());
        }
    }
    
    // ========== 10. 获取患者ID为1的所有就医流程 ==========
    private void handleGetMedicalProcessesForPatient1(PatientProcessService_implRemote service, 
            JsonObjectBuilder jsonBuilder) {
        
        System.out.println("[PatientProcess_Tool] 处理获取患者ID为1的所有就医流程");
        
        try {
            List<Map<String, Object>> processes = service.getMedicalProcessesForPatient1();
            
            if (processes != null && !processes.isEmpty()) {
                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                
                for (Map<String, Object> process : processes) {
                    JsonObjectBuilder processBuilder = Json.createObjectBuilder();
                    
                    for (Map.Entry<String, Object> entry : process.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        processBuilder.add(key, value != null ? value.toString() : "");
                    }
                    
                    arrayBuilder.add(processBuilder.build());
                }
                
                jsonBuilder.add("success", true)
                          .add("data", arrayBuilder)
                          .add("total", processes.size());
                
                System.out.println("[PatientProcess_Tool] 获取到就医流程数量: " + processes.size());
                
            } else {
                jsonBuilder.add("success", true)
                          .add("data", Json.createArrayBuilder())
                          .add("total", 0)
                          .add("message", "未找到就医流程");
            }
            
        } catch (RemoteException e) {
            jsonBuilder.add("success", false)
                      .add("message", "远程调用失败: " + e.getMessage());
        } catch (Exception e) {
            jsonBuilder.add("success", false)
                      .add("message", "获取就医流程失败: " + e.getMessage());
        }
    }
    
    // ========== 11. 根据节点名称获取流程节点信息 ==========
    private void handleGetProcessNodeByName(PatientProcessService_implRemote service, String processIdStr, 
            String nodeName, JsonObjectBuilder jsonBuilder) {
        
        System.out.println("[PatientProcess_Tool] 处理根据节点名称获取流程节点信息: processId=" + processIdStr + ", nodeName=" + nodeName);
        
        try {
            if (processIdStr == null || processIdStr.isEmpty() || nodeName == null || nodeName.isEmpty()) {
                jsonBuilder.add("success", false)
                          .add("message", "缺少必要参数: processId 或 nodeName");
                return;
            }
            
            int processId = Integer.parseInt(processIdStr);
            Map<String, Object> nodeData = service.getProcessNodeByName(processId, nodeName);
            
            if (nodeData != null && !nodeData.isEmpty()) {
                JsonObjectBuilder dataBuilder = Json.createObjectBuilder();
                
                for (Map.Entry<String, Object> entry : nodeData.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    dataBuilder.add(key, value != null ? value.toString() : "");
                }
                
                jsonBuilder.add("success", true)
                          .add("data", dataBuilder.build());
                
                System.out.println("[PatientProcess_Tool] 节点信息获取成功: " + nodeName);
                
            } else {
                jsonBuilder.add("success", false)
                          .add("message", "未找到节点信息: " + nodeName);
            }
            
        } catch (NumberFormatException e) {
            jsonBuilder.add("success", false)
                      .add("message", "参数格式错误: " + e.getMessage());
        } catch (RemoteException e) {
            jsonBuilder.add("success", false)
                      .add("message", "远程调用失败: " + e.getMessage());
        } catch (Exception e) {
            jsonBuilder.add("success", false)
                      .add("message", "获取节点信息失败: " + e.getMessage());
        }
    }
    
    // ========== 12. 测试连接 ==========
    private void handleTestConnection(PatientProcessService_implRemote service, JsonObjectBuilder jsonBuilder) {
        
        System.out.println("[PatientProcess_Tool] 处理测试连接");
        
        try {
            String connectionStatus = service.testConnection();
            jsonBuilder.add("success", true)
                      .add("message", connectionStatus);
            
            System.out.println("[PatientProcess_Tool] 测试连接成功: " + connectionStatus);
            
        } catch (RemoteException e) {
            jsonBuilder.add("success", false)
                      .add("message", "远程调用失败: " + e.getMessage());
        } catch (Exception e) {
            jsonBuilder.add("success", false)
                      .add("message", "测试连接失败: " + e.getMessage());
        }
    }
    
    // ========== 13. 支持OPTIONS请求 ==========
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    // ========== 14. 工具方法 ==========
    
    /**
     * 安全获取字符串值
     */
    private String getStringValue(Object obj, String defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        return obj.toString();
    }
    
    /**
     * 将Map转换为JSON对象
     */
    private JsonObject convertMapToJson(Map<String, Object> map) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                
                if (value == null) {
                    builder.addNull(key);
                } else if (value instanceof String) {
                    builder.add(key, (String) value);
                } else if (value instanceof Integer) {
                    builder.add(key, (Integer) value);
                } else if (value instanceof Long) {
                    builder.add(key, (Long) value);
                } else if (value instanceof Boolean) {
                    builder.add(key, (Boolean) value);
                } else {
                    builder.add(key, value.toString());
                }
            }
        }
        
        return builder.build();
    }
}
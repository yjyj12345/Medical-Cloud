package org.cloudserviceengineering.app;

import java.io.IOException;
import java.io.PrintWriter;
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
import org.cloud.paas.diseasemanagementservice.DiseaseManageService_implRemote;
import util.ejbUtility;

@WebServlet("/DiseaseManagement")
public class DiseaseManageService_Tool extends HttpServlet {
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    	request.setCharacterEncoding("UTF-8");
        // ========== 1. 设置CORS和字符编码 ==========
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        System.out.println("==================== [DiseaseManagementServlet] ====================");
        
        String action = request.getParameter("action");
        String processId = request.getParameter("processId");
        String nodeId = request.getParameter("nodeId");
        String format = request.getParameter("format");
        
        System.out.println("[DiseaseManagementServlet] 参数 - action: " + action);
        System.out.println("[DiseaseManagementServlet] 参数 - processId: " + processId);
        System.out.println("[DiseaseManagementServlet] 参数 - nodeId: " + nodeId);
        System.out.println("[DiseaseManagementServlet] 参数 - format: " + format);
        
        try {
            // 获取EJB服务
            System.out.println("[DiseaseManagementServlet] 尝试连接EJB服务...");
            DiseaseManageService_implRemote diseaseService = ejbUtility.getDiagnoseService();
            System.out.println("[DiseaseManagementServlet] EJB服务连接成功: " + (diseaseService != null));
            
            // ========== 2. 判断请求类型 ==========
            if (format != null && format.equals("json")) {
                // JSON API模式
                response.setContentType("application/json;charset=UTF-8");
                handleJsonApi(request, response, diseaseService, action);
            } else if (action == null || action.equals("viewPage")) {
                // 页面模式：显示医生端页面
                handleViewPage(request, response, diseaseService, processId);
            } else {
                // 表单提交模式：处理业务逻辑，然后重定向回页面
                handleFormSubmit(request, response, diseaseService, action);
            }
            
        } catch (Exception e) {
            System.out.println("[DiseaseManagementServlet] EJB调用错误: " + e.getMessage());
            e.printStackTrace();
            
            // 错误处理：如果是API调用返回JSON，否则返回错误页面
            if (format != null && format.equals("json")) {
                response.setContentType("application/json;charset=UTF-8");
                JsonObject errorJson = Json.createObjectBuilder()
                    .add("success", false)
                    .add("message", "服务调用失败: " + e.getMessage())
                    .build();
                PrintWriter out = response.getWriter();
                out.print(errorJson.toString());
            } else {
                request.setAttribute("errorMsg", "系统错误: " + e.getMessage());
                request.getRequestDispatcher("/doctor_process.jsp").forward(request, response);
            }
        }
        
        System.out.println("[DiseaseManagementServlet] 请求处理完成");
        System.out.println("==============================================================\n");
    }
    
    // ========== 3. 处理JSON API请求 ==========
    private void handleJsonApi(HttpServletRequest request, HttpServletResponse response,
            DiseaseManageService_implRemote diseaseService, String action) throws IOException {
        
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        
        if ("checkNodePayment".equals(action)) {
            handleCheckNodePayment(request, response, diseaseService, jsonBuilder);
        } else if ("getNodePayments".equals(action)) {
            handleGetNodePayments(request, response, diseaseService, jsonBuilder);
        } else if ("addPaymentOrder".equals(action)) {
            handleAddPaymentOrder(request, response, diseaseService, jsonBuilder);
        } else if ("markPaymentPaid".equals(action)) {
            handleMarkPaymentPaid(request, response, diseaseService, jsonBuilder);
        } else if ("deletePaymentOrder".equals(action)) {
            handleDeletePaymentOrder(request, response, diseaseService, jsonBuilder);
        } else if ("getNodeInfo".equals(action)) {
            handleGetNodeInfo(request, response, diseaseService, jsonBuilder);
        } else if ("getLatestNode".equals(action)) {
            handleGetLatestNode(request, response, diseaseService, jsonBuilder);
        } else if ("markNodeCompleted".equals(action)) {
            handleMarkNodeCompleted(request, response, diseaseService, jsonBuilder);
        } else if ("transferToNextNode".equals(action)) {
            handleTransferToNextNode(request, response, diseaseService, jsonBuilder);
        } else if ("getAllLocations".equals(action)) {
            handleGetAllLocations(response, diseaseService, jsonBuilder);
        } else if ("getLocationById".equals(action)) {
            handleGetLocationById(request, response, diseaseService, jsonBuilder);
        } else if ("getAllMedicines".equals(action)) {
            handleGetAllMedicines(response, diseaseService, jsonBuilder);
        } else if ("getAllPatients".equals(action)) {
            handleGetAllPatients(response, diseaseService, jsonBuilder);
        } else if ("getPatientById".equals(action)) {
            handleGetPatientById(request, response, diseaseService, jsonBuilder);
        } else if ("getAllPatientProcesses".equals(action)) {
            handleGetAllPatientProcesses(response, diseaseService, jsonBuilder);
        } else if ("testConnection".equals(action)) {
            handleTestConnection(response, diseaseService, jsonBuilder);
        } else if ("updateConsultation".equals(action)) {
            handleUpdateConsultationJson(request, response, diseaseService, jsonBuilder);
        } else if ("updateExamination".equals(action)) {
            handleUpdateExaminationJson(request, response, diseaseService, jsonBuilder);
        } else if ("updateTreatment".equals(action)) {
            handleUpdateTreatmentJson(request, response, diseaseService, jsonBuilder);
        } else if ("updateMedication".equals(action)) {
            handleUpdateMedicationJson(request, response, diseaseService, jsonBuilder);
        } else if ("goToNextStage".equals(action)) {
            handleTransferToNextNode(request, response, diseaseService, jsonBuilder);
        } else if ("validateTransfer".equals(action)) {
            handleValidateTransfer(request, response, diseaseService, jsonBuilder);
        } else if ("completeProcess".equals(action)) {
            handleCompleteProcessJson(request, response, diseaseService, jsonBuilder);
        } else {
            jsonBuilder.add("success", false)
                      .add("message", "未知的操作类型");
            PrintWriter out = response.getWriter();
            out.print(jsonBuilder.build().toString());
        }
    }
    

    private void handleUpdateConsultationJson(HttpServletRequest request, HttpServletResponse response,
            DiseaseManageService_implRemote diseaseService, JsonObjectBuilder jsonBuilder) throws IOException {
        String processId = request.getParameter("processId");
        String diagnosisText = request.getParameter("diagnosisText");
        String locationId = request.getParameter("locationId");
        String reminder = request.getParameter("reminder");
        
        try {
            Long locId = (locationId != null && !locationId.isEmpty()) ? Long.parseLong(locationId) : null;
            boolean success = diseaseService.updateConsultationNode(processId, diagnosisText, locId, reminder);
            jsonBuilder.add("success", success)
                      .add("message", success ? "问诊信息更新成功" : "问诊信息更新失败");
        } catch (Exception e) {
            jsonBuilder.add("success", false)
                      .add("message", "更新失败: " + e.getMessage());
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonBuilder.build().toString());
    }
    
    private void handleUpdateExaminationJson(HttpServletRequest request, HttpServletResponse response,
            DiseaseManageService_implRemote diseaseService, JsonObjectBuilder jsonBuilder) throws IOException {
        String processId = request.getParameter("processId");
        String locationId = request.getParameter("locationId");
        String diagnosisText = request.getParameter("diagnosisText");
        String pictures = request.getParameter("pictures");
        String reminder = request.getParameter("reminder");
        
        try {
            Long locId = (locationId != null && !locationId.isEmpty()) ? Long.parseLong(locationId) : null;
            boolean success = diseaseService.updateExaminationNode(processId, locId, diagnosisText, pictures, reminder);
            jsonBuilder.add("success", success)
                      .add("message", success ? "检查信息更新成功" : "检查信息更新失败");
        } catch (Exception e) {
            jsonBuilder.add("success", false)
                      .add("message", "更新失败: " + e.getMessage());
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonBuilder.build().toString());
    }
    
    private void handleUpdateTreatmentJson(HttpServletRequest request, HttpServletResponse response,
            DiseaseManageService_implRemote diseaseService, JsonObjectBuilder jsonBuilder) throws IOException {
        String processId = request.getParameter("processId");
        String locationId = request.getParameter("locationId");
        String diagnosisText = request.getParameter("diagnosisText");
        String reminder = request.getParameter("reminder");
        
        try {
            Long locId = (locationId != null && !locationId.isEmpty()) ? Long.parseLong(locationId) : null;
            boolean success = diseaseService.updateTreatmentNode(processId, locId, diagnosisText, reminder);
            jsonBuilder.add("success", success)
                      .add("message", success ? "治疗信息更新成功" : "治疗信息更新失败");
        } catch (Exception e) {
            jsonBuilder.add("success", false)
                      .add("message", "更新失败: " + e.getMessage());
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonBuilder.build().toString());
    }
    
    private void handleUpdateMedicationJson(HttpServletRequest request, HttpServletResponse response,
            DiseaseManageService_implRemote diseaseService, JsonObjectBuilder jsonBuilder) throws IOException {
        String processId = request.getParameter("processId");
        String locationId = request.getParameter("locationId");
        String medicineId = request.getParameter("medicineId");
        String reminder = request.getParameter("reminder");
        
        try {
            Long locId = (locationId != null && !locationId.isEmpty()) ? Long.parseLong(locationId) : null;
            Long medId = (medicineId != null && !medicineId.isEmpty()) ? Long.parseLong(medicineId) : null;
            boolean success = diseaseService.updateMedicationNode(processId, locId, medId, reminder);
            jsonBuilder.add("success", success)
                      .add("message", success ? "取药信息更新成功" : "取药信息更新失败");
        } catch (Exception e) {
            jsonBuilder.add("success", false)
                      .add("message", "更新失败: " + e.getMessage());
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonBuilder.build().toString());
    }
    
    private void handleCompleteProcessJson(HttpServletRequest request, HttpServletResponse response,
            DiseaseManageService_implRemote diseaseService, JsonObjectBuilder jsonBuilder) throws IOException {
        String processId = request.getParameter("processId");
        
        try {
            boolean success = diseaseService.markNodeAsCompleted(processId, "6");
            if (success) {
                diseaseService.updateMedicalProcessStatus(processId, "已完成");
            }
            jsonBuilder.add("success", success)
                      .add("message", success ? "治疗流程已完成" : "完成流程失败");
        } catch (Exception e) {
            jsonBuilder.add("success", false)
                      .add("message", "完成失败: " + e.getMessage());
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonBuilder.build().toString());
    }
    
    // ========== 4. 处理页面显示请求 ==========
 // ========== 4. 处理页面显示请求 ==========
    private void handleViewPage(HttpServletRequest request, HttpServletResponse response,
            DiseaseManageService_implRemote diseaseService, String processId) 
            throws ServletException, IOException {
        
        System.out.println("[DiseaseManagementServlet] 处理页面显示请求");
        
        try {
            // 获取所有患者流程数据
            List<Map<String, Object>> patientProcesses = diseaseService.getAllPatientProcesses();
            request.setAttribute("patientList", patientProcesses);
            System.out.println("加载患者流程数量: " + patientProcesses.size());
            
            // 获取所有地点数据
            List<Map<String, Object>> locations = diseaseService.getAllLocations();
            request.setAttribute("locationList", locations);
            System.out.println("加载地点数量: " + locations.size());
            
            // 获取所有药品数据
            List<Map<String, Object>> medicines = diseaseService.getAllMedicines();
            request.setAttribute("medicineList", medicines);
            System.out.println("加载药品数量: " + medicines.size());
            
            // 确定当前患者
            Map<String, Object> currentPatient = null;
            String currentProcessId = processId;
            
            // 记录节点详细信息，用于填充表单
            Map<String, Object> currentStageData = new HashMap<>();
            
            if (patientProcesses != null && !patientProcesses.isEmpty()) {
                if (currentProcessId == null || currentProcessId.isEmpty()) {
                    // 如果没有指定processId，使用第一个患者
                    currentPatient = patientProcesses.get(0);
                    currentProcessId = currentPatient.get("processId").toString();
                    System.out.println("使用默认患者，processId: " + currentProcessId);
                } else {
                    // 查找指定的患者
                    for (Map<String, Object> patient : patientProcesses) {
                        if (patient.get("processId").toString().equals(currentProcessId)) {
                            currentPatient = patient;
                            break;
                        }
                    }
                    if (currentPatient == null) {
                        currentPatient = patientProcesses.get(0);
                        currentProcessId = currentPatient.get("processId").toString();
                        System.out.println("未找到指定患者，使用默认，processId: " + currentProcessId);
                    } else {
                        System.out.println("找到指定患者，processId: " + currentProcessId);
                    }
                }
                
                // 获取当前患者的最新节点
                if (currentProcessId != null) {
                    // 1. 获取最新节点基本信息
                    Map<String, Object> latestNode = diseaseService.getLatestNode(currentProcessId);
                    request.setAttribute("nodeInfo", latestNode);
                    
                    // 2. 获取当前阶段ID
                    int currentStage = 1;
                    if (latestNode != null && latestNode.get("nodeId") != null) {
                        try {
                            Long nodeId = (Long) latestNode.get("nodeId");
                            currentStage = nodeId.intValue();
                            System.out.println("当前阶段ID: " + currentStage);
                        } catch (Exception e) {
                            System.out.println("解析节点ID失败: " + e.getMessage());
                            currentStage = 1;
                        }
                    }
                    request.setAttribute("currentStage", currentStage);
                    
                    // 3. 根据当前阶段获取对应节点的详细信息
                    if (currentStage >= 1 && currentStage <= 6) {
                        String nodeIdStr = String.valueOf(currentStage);
                        Map<String, Object> nodeDetail = diseaseService.getNodeInfo(currentProcessId, nodeIdStr);
                        
                        if (nodeDetail != null) {
                            System.out.println("节点详细信息: " + nodeDetail.toString());
                            
                            // 提取关键信息用于填充表单
                            // 诊断文本
                            if (nodeDetail.get("diagnosisText") != null) {
                                String diagnosisText = nodeDetail.get("diagnosisText").toString();
                                request.setAttribute("diagnosisText", diagnosisText);
                                currentStageData.put("diagnosisText", diagnosisText);
                                System.out.println("提取诊断文本: " + diagnosisText);
                            }
                            
                            // 位置ID
                            if (nodeDetail.get("locationId") != null) {
                                String locationId = nodeDetail.get("locationId").toString();
                                request.setAttribute("locationId", locationId);
                                currentStageData.put("locationId", locationId);
                                System.out.println("提取位置ID: " + locationId);
                            }
                            
                            // 提醒信息
                            if (nodeDetail.get("reminder") != null) {
                                String reminder = nodeDetail.get("reminder").toString();
                                request.setAttribute("reminder", reminder);
                                currentStageData.put("reminder", reminder);
                                System.out.println("提取提醒信息: " + reminder);
                            }
                            
                            // 药品信息
                            if (nodeDetail.get("medicineId") != null) {
                                String medicineId = nodeDetail.get("medicineId").toString();
                                request.setAttribute("medicineId", medicineId);
                                currentStageData.put("medicineId", medicineId);
                                System.out.println("提取药品ID: " + medicineId);
                            }
                            
                            if (nodeDetail.get("medicineName") != null) {
                                String medicineName = nodeDetail.get("medicineName").toString();
                                request.setAttribute("medicineName", medicineName);
                                currentStageData.put("medicineName", medicineName);
                                System.out.println("提取药品名称: " + medicineName);
                            }
                            
                            // 图片信息（用于检查阶段）
                            if (nodeDetail.get("pictures") != null) {
                                String pictures = nodeDetail.get("pictures").toString();
                                request.setAttribute("pictures", pictures);
                                currentStageData.put("pictures", pictures);
                                System.out.println("提取图片信息: " + pictures);
                            }
                        } else {
                            System.out.println("未找到节点详细信息，nodeId=" + nodeIdStr);
                        }
                    }
                }
            }
            
            // 将当前阶段数据传递到JSP
            request.setAttribute("currentStageData", currentStageData);
            request.setAttribute("currentPatient", currentPatient);
            
            // 获取消息
            String successMsg = request.getParameter("successMsg");
            String errorMsg = request.getParameter("errorMsg");
            if (successMsg != null) {
                request.setAttribute("successMsg", successMsg);
                System.out.println("成功消息: " + successMsg);
            }
            if (errorMsg != null) {
                request.setAttribute("errorMsg", errorMsg);
                System.out.println("错误消息: " + errorMsg);
            }
            
            // 转发到JSP页面
            System.out.println("即将转发到JSP页面...");
            request.getRequestDispatcher("/doctor_process.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.out.println("加载页面数据失败: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMsg", "加载数据失败: " + e.getMessage());
            request.getRequestDispatcher("/doctor_process.jsp").forward(request, response);
        }
    }
    
    // ========== 5. 处理表单提交请求 ==========
    private void handleFormSubmit(HttpServletRequest request, HttpServletResponse response,
            DiseaseManageService_implRemote diseaseService, String action) 
            throws ServletException, IOException {
        
        System.out.println("[DiseaseManagementServlet] 处理表单提交: " + action);
        
        String processId = request.getParameter("processId");
        String message = "";
        boolean success = false;
        
        try {
            switch (action) {
                case "selectPatient":
                    // 选择患者 - 直接重定向到页面模式
                    response.sendRedirect(request.getContextPath() + 
                        "/DiseaseManagement?action=viewPage&processId=" + processId);
                    return;
                    
                case "updateConsultation":
                    String diagnosisText = request.getParameter("diagnosisText");
                    String locationId = request.getParameter("locationId");
                    String consultationReminder = request.getParameter("reminder");
                    Long locId = Long.parseLong(locationId);
                    success = diseaseService.updateConsultationNode(processId, diagnosisText, locId, consultationReminder);
                    message = success ? "问诊信息更新成功" : "问诊信息更新失败";
                    break;
                    
                case "updateExamination":
                    String examLocationId = request.getParameter("locationId");
                    String examDiagnosisText = request.getParameter("diagnosisText");
                    String examReminder = request.getParameter("reminder");
                    Long examLocId = Long.parseLong(examLocationId);
                    success = diseaseService.updateExaminationNode(processId, examLocId, examDiagnosisText, null, examReminder);
                    message = success ? "检查信息更新成功" : "检查信息更新失败";
                    break;
                    
                case "updateTreatment":
                    String treatmentLocationId = request.getParameter("locationId");
                    String treatmentPlan = request.getParameter("treatmentPlan");
                    String treatmentReminder = request.getParameter("reminder");
                 // 调试：打印接收到的中文
                    System.out.println("[DiseaseManagementServlet] 接收的治疗方案原文: " + treatmentPlan);
                    System.out.println("[DiseaseManagementServlet] 接收的备注原文: " + treatmentReminder);
                    Long treatmentLocId = Long.parseLong(treatmentLocationId);
                    success = diseaseService.updateTreatmentNode(processId, treatmentLocId, treatmentPlan, treatmentReminder);
                    message = success ? "治疗信息更新成功" : "治疗信息更新失败";  // ✅ 添加这行
                    break;
                    
                case "updateMedication":
                    String medLocationId = request.getParameter("locationId");
                    String medicineId = request.getParameter("medicineId");
                    String medicationReminder = request.getParameter("reminder");
                    Long medLocId = Long.parseLong(medLocationId);
                    Long medId = (medicineId != null && !medicineId.isEmpty()) ? Long.parseLong(medicineId) : null;
                    success = diseaseService.updateMedicationNode(processId, medLocId, medId, medicationReminder);
                    message = success ? "取药信息更新成功" : "取药信息更新失败";
                    break;
                    
                case "completeProcess":
                    // 标记完成节点为已完成
                    success = diseaseService.markNodeAsCompleted(processId, "6");
                    
                    // 同时更新 MedicalProcesses 表的状态为"已完成"
                    if (success) {
                        boolean updateSuccess = diseaseService.updateMedicalProcessStatus(processId, "已完成");
                        if (!updateSuccess) {
                            System.out.println("警告：流程节点完成，但更新MedicalProcesses状态失败");
                        }
                    }
                    
                    message = success ? "治疗流程已全部完成" : "完成流程失败";
                    break;
                    
                case "goToNextStage":
                    success = diseaseService.transferToNextNode(processId);
                    message = success ? "已成功流转到下一阶段" : "流转失败";
                    break;
                    
                default:
                    message = "未知的操作类型";
            }
            
            // 重定向回页面，并传递消息
            String redirectUrl = request.getContextPath() + "/DiseaseManagement?action=viewPage&processId=" + processId;
            if (success) {
                redirectUrl += "&successMsg=" + java.net.URLEncoder.encode(message, "UTF-8");
            } else {
                redirectUrl += "&errorMsg=" + java.net.URLEncoder.encode(message, "UTF-8");
            }
            
            response.sendRedirect(redirectUrl);
            
        } catch (Exception e) {
            System.out.println("处理表单提交失败: " + e.getMessage());
            String redirectUrl = request.getContextPath() + "/DiseaseManagement?action=viewPage&processId=" + processId +
                               "&errorMsg=" + java.net.URLEncoder.encode("操作失败: " + e.getMessage(), "UTF-8");
            response.sendRedirect(redirectUrl);
        }
    }
    
    // ========== 6. 支付相关处理方法 ==========
    
    private void handleCheckNodePayment(HttpServletRequest request, HttpServletResponse response,
            DiseaseManageService_implRemote diseaseService, JsonObjectBuilder jsonBuilder) throws IOException {
        
        String processId = request.getParameter("processId");
        String nodeId = request.getParameter("nodeId");
        
        System.out.println("[DiseaseManagementServlet] 检查节点支付状态, processId: " + processId + ", nodeId: " + nodeId);
        
        try {
            boolean paymentCompleted = diseaseService.checkNodePaymentCompleted(processId, nodeId);
            jsonBuilder.add("success", true)
                      .add("paymentCompleted", paymentCompleted)
                      .add("message", paymentCompleted ? "支付已完成" : "存在未支付订单");
            
        } catch (Exception e) {
            System.out.println("[DiseaseManagementServlet] 检查支付状态失败: " + e.getMessage());
            jsonBuilder.add("success", false)
                      .add("message", "检查支付状态失败: " + e.getMessage());
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonBuilder.build().toString());
    }
    
    private void handleGetNodePayments(HttpServletRequest request, HttpServletResponse response,
            DiseaseManageService_implRemote diseaseService, JsonObjectBuilder jsonBuilder) throws IOException {
        
        String processId = request.getParameter("processId");
        String nodeId = request.getParameter("nodeId");
        
        System.out.println("[DiseaseManagementServlet] 获取节点支付订单, processId: " + processId + ", nodeId: " + nodeId);
        
        try {
            List<Map<String, Object>> payments = diseaseService.getNodePayments(processId, nodeId);
            
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (Map<String, Object> payment : payments) {
                JsonObjectBuilder paymentBuilder = Json.createObjectBuilder();
                
                for (Map.Entry<String, Object> entry : payment.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    
                    if (value == null) {
                        paymentBuilder.addNull(key);
                    } else if (value instanceof String) {
                        paymentBuilder.add(key, (String) value);
                    } else if (value instanceof Integer) {
                        paymentBuilder.add(key, (Integer) value);
                    } else if (value instanceof Long) {
                        paymentBuilder.add(key, (Long) value);
                    } else if (value instanceof java.sql.Timestamp) {
                        paymentBuilder.add(key, value.toString());
                    } else {
                        paymentBuilder.add(key, value.toString());
                    }
                }
                
                arrayBuilder.add(paymentBuilder.build());
            }
            
            jsonBuilder.add("success", true)
                      .add("data", arrayBuilder)
                      .add("total", payments.size());
            
        } catch (Exception e) {
            System.out.println("[DiseaseManagementServlet] 获取支付订单失败: " + e.getMessage());
            jsonBuilder.add("success", false)
                      .add("message", "获取支付订单失败: " + e.getMessage())
                      .add("data", Json.createArrayBuilder());
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonBuilder.build().toString());
    }
    
    private void handleAddPaymentOrder(HttpServletRequest request, HttpServletResponse response,
            DiseaseManageService_implRemote diseaseService, JsonObjectBuilder jsonBuilder) throws IOException {
        
        String processId = request.getParameter("processId");
        String nodeId = request.getParameter("nodeId");
        String orderContent = request.getParameter("orderContent");
        String amountStr = request.getParameter("amount");
        
        System.out.println("[DiseaseManagementServlet] 添加支付订单, processId: " + processId + 
                         ", nodeId: " + nodeId + ", content: " + orderContent);
        
        try {
            if (processId == null || nodeId == null || orderContent == null || amountStr == null) {
                jsonBuilder.add("success", false)
                          .add("message", "缺少必要参数");
            } else {
                Long amount = Long.parseLong(amountStr);
                boolean result = diseaseService.addPaymentOrder(processId, nodeId, orderContent, amount);
                jsonBuilder.add("success", result)
                          .add("message", result ? "支付订单添加成功" : "支付订单添加失败");
            }
            
        } catch (NumberFormatException e) {
            jsonBuilder.add("success", false)
                      .add("message", "金额格式错误");
        } catch (Exception e) {
            System.out.println("[DiseaseManagementServlet] 添加支付订单失败: " + e.getMessage());
            jsonBuilder.add("success", false)
                      .add("message", "添加支付订单失败: " + e.getMessage());
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonBuilder.build().toString());
    }
    
    private void handleMarkPaymentPaid(HttpServletRequest request, HttpServletResponse response,
            DiseaseManageService_implRemote diseaseService, JsonObjectBuilder jsonBuilder) throws IOException {
        
        String orderIdStr = request.getParameter("orderId");
        
        System.out.println("[DiseaseManagementServlet] 标记支付为已支付, orderId: " + orderIdStr);
        
        try {
            if (orderIdStr == null) {
                jsonBuilder.add("success", false)
                          .add("message", "缺少orderId参数");
            } else {
                Long orderId = Long.parseLong(orderIdStr);
                boolean result = diseaseService.markPaymentAsPaid(orderId);
                jsonBuilder.add("success", result)
                          .add("message", result ? "支付标记成功" : "支付标记失败");
            }
            
        } catch (NumberFormatException e) {
            jsonBuilder.add("success", false)
                      .add("message", "orderId格式错误");
        } catch (Exception e) {
            System.out.println("[DiseaseManagementServlet] 标记支付失败: " + e.getMessage());
            jsonBuilder.add("success", false)
                      .add("message", "标记支付失败: " + e.getMessage());
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonBuilder.build().toString());
    }
    
    private void handleDeletePaymentOrder(HttpServletRequest request, HttpServletResponse response,
            DiseaseManageService_implRemote diseaseService, JsonObjectBuilder jsonBuilder) throws IOException {
        
        String orderIdStr = request.getParameter("orderId");
        
        System.out.println("[DiseaseManagementServlet] 删除支付订单, orderId: " + orderIdStr);
        
        try {
            if (orderIdStr == null) {
                jsonBuilder.add("success", false)
                          .add("message", "缺少orderId参数");
            } else {
                Long orderId = Long.parseLong(orderIdStr);
                boolean result = diseaseService.deletePaymentOrder(orderId);
                jsonBuilder.add("success", result)
                          .add("message", result ? "支付订单删除成功" : "支付订单删除失败");
            }
            
        } catch (NumberFormatException e) {
            jsonBuilder.add("success", false)
                      .add("message", "orderId格式错误");
        } catch (Exception e) {
            System.out.println("[DiseaseManagementServlet] 删除支付订单失败: " + e.getMessage());
            jsonBuilder.add("success", false)
                      .add("message", "删除支付订单失败: " + e.getMessage());
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonBuilder.build().toString());
    }
    
    // ========== 7. 节点相关处理方法 ==========
    
    private void handleGetNodeInfo(HttpServletRequest request, HttpServletResponse response,
            DiseaseManageService_implRemote diseaseService, JsonObjectBuilder jsonBuilder) throws IOException {
        
        String processId = request.getParameter("processId");
        String nodeId = request.getParameter("nodeId");
        
        System.out.println("[DiseaseManagementServlet] 处理获取节点信息");
        
        if (processId == null || nodeId == null) {
            jsonBuilder.add("success", false)
                      .add("message", "缺少必要参数: processId 或 nodeId");
        } else {
            Map<String, Object> nodeInfo = diseaseService.getNodeInfo(processId, nodeId);
            
            if (nodeInfo != null) {
                jsonBuilder.add("success", true)
                          .add("data", convertMapToJson(nodeInfo));
            } else {
                jsonBuilder.add("success", false)
                          .add("message", "未找到节点信息");
            }
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonBuilder.build().toString());
    }
    
    private void handleGetLatestNode(HttpServletRequest request, HttpServletResponse response,
            DiseaseManageService_implRemote diseaseService, JsonObjectBuilder jsonBuilder) throws IOException {
        
        String processId = request.getParameter("processId");
        
        System.out.println("[DiseaseManagementServlet] 处理获取最新节点");
        
        if (processId == null) {
            jsonBuilder.add("success", false)
                      .add("message", "缺少必要参数: processId");
        } else {
            Map<String, Object> latestNode = diseaseService.getLatestNode(processId);
            
            if (latestNode != null) {
                jsonBuilder.add("success", true)
                          .add("data", convertMapToJson(latestNode));
            } else {
                jsonBuilder.add("success", false)
                          .add("message", "未找到最新节点");
            }
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonBuilder.build().toString());
    }
    
    private void handleMarkNodeCompleted(HttpServletRequest request, HttpServletResponse response,
            DiseaseManageService_implRemote diseaseService, JsonObjectBuilder jsonBuilder) throws IOException {
        
        String processId = request.getParameter("processId");
        String nodeId = request.getParameter("nodeId");
        
        System.out.println("[DiseaseManagementServlet] 处理标记节点完成");
        
        if (processId == null || nodeId == null) {
            jsonBuilder.add("success", false)
                      .add("message", "缺少必要参数: processId 或 nodeId");
        } else {
            boolean result = diseaseService.markNodeAsCompleted(processId, nodeId);
            jsonBuilder.add("success", result)
                      .add("message", result ? "节点标记完成成功" : "节点标记完成失败");
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonBuilder.build().toString());
    }
    
    private void handleTransferToNextNode(HttpServletRequest request, HttpServletResponse response,
            DiseaseManageService_implRemote diseaseService, JsonObjectBuilder jsonBuilder) throws IOException {
        
        String processId = request.getParameter("processId");
        
        System.out.println("[DiseaseManagementServlet] 处理流转到下一个节点");
        
        if (processId == null) {
            jsonBuilder.add("success", false)
                      .add("message", "缺少必要参数: processId");
        } else {
            // 先验证
            Map<String, Object> validation = diseaseService.validateTransfer(processId);
            boolean canTransfer = (boolean) validation.getOrDefault("canTransfer", false);
            String message = (String) validation.getOrDefault("message", "验证失败");
            
            if (!canTransfer) {
                jsonBuilder.add("success", false)
                          .add("message", message);
            } else {
                boolean result = diseaseService.transferToNextNode(processId);
                jsonBuilder.add("success", result)
                          .add("message", result ? "流转到下一个节点成功" : "流转失败，请稍后重试");
            }
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonBuilder.build().toString());
    }
    
    private void handleValidateTransfer(HttpServletRequest request, HttpServletResponse response,
            DiseaseManageService_implRemote diseaseService, JsonObjectBuilder jsonBuilder) throws IOException {
        
        String processId = request.getParameter("processId");
        
        System.out.println("[DiseaseManagementServlet] 验证流转条件");
        
        if (processId == null) {
            jsonBuilder.add("success", false)
                      .add("canTransfer", false)
                      .add("message", "缺少必要参数: processId");
        } else {
            try {
                Map<String, Object> result = diseaseService.validateTransfer(processId);
                boolean canTransfer = (boolean) result.getOrDefault("canTransfer", false);
                String message = (String) result.getOrDefault("message", "验证失败");
                
                jsonBuilder.add("success", true)
                          .add("canTransfer", canTransfer)
                          .add("message", message);
            } catch (Exception e) {
                jsonBuilder.add("success", false)
                          .add("canTransfer", false)
                          .add("message", "验证失败: " + e.getMessage());
            }
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonBuilder.build().toString());
    }
    
    // ========== 8. 数据获取处理方法 ==========
    
    private void handleGetAllLocations(HttpServletResponse response,
            DiseaseManageService_implRemote diseaseService, JsonObjectBuilder jsonBuilder) throws IOException {
        
        System.out.println("[DiseaseManagementServlet] 处理获取所有地点");
        
        try {
            List<Map<String, Object>> locations = diseaseService.getAllLocations();
            
            if (locations != null && !locations.isEmpty()) {
                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                for (Map<String, Object> location : locations) {
                    JsonObjectBuilder locBuilder = Json.createObjectBuilder();
                    
                    for (Map.Entry<String, Object> entry : location.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        
                        if (value == null) {
                            locBuilder.addNull(key);
                        } else if (value instanceof String) {
                            locBuilder.add(key, (String) value);
                        } else if (value instanceof Integer) {
                            locBuilder.add(key, (Integer) value);
                        } else if (value instanceof Long) {
                            locBuilder.add(key, (Long) value);
                        } else {
                            locBuilder.add(key, value.toString());
                        }
                    }
                    
                    arrayBuilder.add(locBuilder.build());
                }
                
                jsonBuilder.add("success", true)
                          .add("data", arrayBuilder)
                          .add("total", locations.size());
            } else {
                jsonBuilder.add("success", false)
                          .add("message", "未找到地点数据")
                          .add("data", Json.createArrayBuilder());
            }
            
        } catch (Exception e) {
            System.out.println("[DiseaseManagementServlet] 获取地点失败: " + e.getMessage());
            jsonBuilder.add("success", false)
                      .add("message", "获取地点数据失败: " + e.getMessage())
                      .add("data", Json.createArrayBuilder());
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonBuilder.build().toString());
    }
    
    private void handleGetLocationById(HttpServletRequest request, HttpServletResponse response,
            DiseaseManageService_implRemote diseaseService, JsonObjectBuilder jsonBuilder) throws IOException {
        
        String locationId = request.getParameter("locationId");
        System.out.println("[DiseaseManagementServlet] 处理获取地点信息, ID: " + locationId);
        
        try {
            if (locationId == null || locationId.isEmpty()) {
                jsonBuilder.add("success", false)
                          .add("message", "缺少locationId参数");
            } else {
                Long id = Long.parseLong(locationId);
                Map<String, Object> location = diseaseService.getLocationById(id);
                
                if (location != null) {
                    JsonObjectBuilder locBuilder = Json.createObjectBuilder();
                    for (Map.Entry<String, Object> entry : location.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        
                        if (value == null) {
                            locBuilder.addNull(key);
                        } else if (value instanceof String) {
                            locBuilder.add(key, (String) value);
                        } else if (value instanceof Integer) {
                            locBuilder.add(key, (Integer) value);
                        } else if (value instanceof Long) {
                            locBuilder.add(key, (Long) value);
                        } else {
                            locBuilder.add(key, value.toString());
                        }
                    }
                    
                    jsonBuilder.add("success", true)
                              .add("data", locBuilder.build());
                } else {
                    jsonBuilder.add("success", false)
                              .add("message", "未找到地点信息");
                }
            }
        } catch (NumberFormatException e) {
            jsonBuilder.add("success", false)
                      .add("message", "locationId格式错误");
        } catch (Exception e) {
            System.out.println("[DiseaseManagementServlet] 获取地点信息失败: " + e.getMessage());
            jsonBuilder.add("success", false)
                      .add("message", "获取地点信息失败: " + e.getMessage());
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonBuilder.build().toString());
    }
    
    private void handleGetAllMedicines(HttpServletResponse response,
            DiseaseManageService_implRemote diseaseService, JsonObjectBuilder jsonBuilder) throws IOException {
        
        System.out.println("[DiseaseManagementServlet] 处理获取所有药品");
        
        try {
            List<Map<String, Object>> medicines = diseaseService.getAllMedicines();
            
            if (medicines != null && !medicines.isEmpty()) {
                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                for (Map<String, Object> medicine : medicines) {
                    JsonObjectBuilder medBuilder = Json.createObjectBuilder();
                    
                    for (Map.Entry<String, Object> entry : medicine.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        
                        if (value == null) {
                            medBuilder.addNull(key);
                        } else if (value instanceof String) {
                            medBuilder.add(key, (String) value);
                        } else if (value instanceof Integer) {
                            medBuilder.add(key, (Integer) value);
                        } else if (value instanceof Long) {
                            medBuilder.add(key, (Long) value);
                        } else if (value instanceof java.math.BigDecimal) {
                            medBuilder.add(key, value.toString());
                        } else {
                            medBuilder.add(key, value.toString());
                        }
                    }
                    
                    arrayBuilder.add(medBuilder.build());
                }
                
                System.out.println("[DiseaseManagementServlet] "+arrayBuilder);
                jsonBuilder.add("success", true)
                          .add("data", arrayBuilder)
                          .add("total", medicines.size());
            } else {
                jsonBuilder.add("success", false)
                          .add("message", "未找到药品数据")
                          .add("data", Json.createArrayBuilder());
            }
            
        } catch (Exception e) {
            System.out.println("[DiseaseManagementServlet] 获取药品失败: " + e.getMessage());
            jsonBuilder.add("success", false)
                      .add("message", "获取药品数据失败: " + e.getMessage())
                      .add("data", Json.createArrayBuilder());
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonBuilder.build().toString());
    }
    
    private void handleGetAllPatients(HttpServletResponse response,
            DiseaseManageService_implRemote diseaseService, JsonObjectBuilder jsonBuilder) throws IOException {
        
        System.out.println("[DiseaseManagementServlet] 处理获取所有患者");
        
        try {
            List<Map<String, Object>> patients = diseaseService.getAllPatients();
            
            if (patients != null && !patients.isEmpty()) {
                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                for (Map<String, Object> patient : patients) {
                    JsonObjectBuilder patientBuilder = Json.createObjectBuilder();
                    
                    for (Map.Entry<String, Object> entry : patient.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        
                        if (value == null) {
                            patientBuilder.addNull(key);
                        } else if (value instanceof String) {
                            patientBuilder.add(key, (String) value);
                        } else if (value instanceof Integer) {
                            patientBuilder.add(key, (Integer) value);
                        } else if (value instanceof Long) {
                            patientBuilder.add(key, (Long) value);
                        } else {
                            patientBuilder.add(key, value.toString());
                        }
                    }
                    
                    arrayBuilder.add(patientBuilder.build());
                }
                
                jsonBuilder.add("success", true)
                          .add("data", arrayBuilder)
                          .add("total", patients.size());
            } else {
                jsonBuilder.add("success", false)
                          .add("message", "未找到患者数据")
                          .add("data", Json.createArrayBuilder());
            }
            
        } catch (Exception e) {
            System.out.println("[DiseaseManagementServlet] 获取患者失败: " + e.getMessage());
            jsonBuilder.add("success", false)
                      .add("message", "获取患者数据失败: " + e.getMessage())
                      .add("data", Json.createArrayBuilder());
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonBuilder.build().toString());
    }
    
    private void handleGetPatientById(HttpServletRequest request, HttpServletResponse response,
            DiseaseManageService_implRemote diseaseService, JsonObjectBuilder jsonBuilder) throws IOException {
        
        String patientId = request.getParameter("patientId");
        System.out.println("[DiseaseManagementServlet] 处理获取患者信息, ID: " + patientId);
        
        try {
            if (patientId == null || patientId.isEmpty()) {
                jsonBuilder.add("success", false)
                          .add("message", "缺少patientId参数");
            } else {
                Long id = Long.parseLong(patientId);
                Map<String, Object> patient = diseaseService.getPatientById(id);
                
                if (patient != null) {
                    JsonObjectBuilder patientBuilder = Json.createObjectBuilder();
                    for (Map.Entry<String, Object> entry : patient.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        
                        if (value == null) {
                            patientBuilder.addNull(key);
                        } else if (value instanceof String) {
                            patientBuilder.add(key, (String) value);
                        } else if (value instanceof Integer) {
                            patientBuilder.add(key, (Integer) value);
                        } else if (value instanceof Long) {
                            patientBuilder.add(key, (Long) value);
                        } else {
                            patientBuilder.add(key, value.toString());
                        }
                    }
                    
                    jsonBuilder.add("success", true)
                              .add("data", patientBuilder.build());
                } else {
                    jsonBuilder.add("success", false)
                              .add("message", "未找到患者信息");
                }
            }
        } catch (NumberFormatException e) {
            jsonBuilder.add("success", false)
                      .add("message", "patientId格式错误");
        } catch (Exception e) {
            System.out.println("[DiseaseManagementServlet] 获取患者信息失败: " + e.getMessage());
            jsonBuilder.add("success", false)
                      .add("message", "获取患者信息失败: " + e.getMessage());
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonBuilder.build().toString());
    }
    
    private void handleGetAllPatientProcesses(HttpServletResponse response,
            DiseaseManageService_implRemote diseaseService, JsonObjectBuilder jsonBuilder) throws IOException {
        
        System.out.println("[DiseaseManagementServlet] 处理获取所有患者流程");
        
        try {
            List<Map<String, Object>> patientProcesses = diseaseService.getAllPatientProcesses();
            
            if (patientProcesses != null && !patientProcesses.isEmpty()) {
                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                for (Map<String, Object> process : patientProcesses) {
                    JsonObjectBuilder processBuilder = Json.createObjectBuilder();
                    
                    for (Map.Entry<String, Object> entry : process.entrySet()) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
                        
                        if (value == null) {
                            processBuilder.addNull(key);
                        } else if (value instanceof String) {
                            processBuilder.add(key, (String) value);
                        } else if (value instanceof Integer) {
                            processBuilder.add(key, (Integer) value);
                        } else if (value instanceof Long) {
                            processBuilder.add(key, (Long) value);
                        } else if (value instanceof java.sql.Timestamp) {
                            processBuilder.add(key, value.toString());
                        } else {
                            processBuilder.add(key, value.toString());
                        }
                    }
                    
                    arrayBuilder.add(processBuilder.build());
                }
                
                jsonBuilder.add("success", true)
                          .add("data", arrayBuilder)
                          .add("total", patientProcesses.size());
            } else {
                jsonBuilder.add("success", false)
                          .add("message", "未找到患者流程数据")
                          .add("data", Json.createArrayBuilder());
            }
            
        } catch (Exception e) {
            System.out.println("[DiseaseManagementServlet] 获取患者流程失败: " + e.getMessage());
            jsonBuilder.add("success", false)
                      .add("message", "获取患者流程数据失败: " + e.getMessage())
                      .add("data", Json.createArrayBuilder());
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonBuilder.build().toString());
    }
    
    // ========== 9. 测试方法 ==========
    
    private void handleTestConnection(HttpServletResponse response,
            DiseaseManageService_implRemote diseaseService, JsonObjectBuilder jsonBuilder) throws IOException {
        
        System.out.println("[DiseaseManagementServlet] 处理测试连接");
        
        try {
            Map<String, Object> testResult = diseaseService.getLatestNode("1");
            jsonBuilder.add("success", true)
                      .add("message", "连接测试成功")
                      .add("testResult", testResult != null ? "获取到数据" : "未获取到数据");
        } catch (Exception e) {
            jsonBuilder.add("success", false)
                      .add("message", "连接测试失败: " + e.getMessage());
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonBuilder.build().toString());
    }
    
    // ========== 10. 工具方法 ==========
    
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
                } else if (value instanceof java.sql.Timestamp) {
                    builder.add(key, value.toString());
                } else {
                    builder.add(key, value.toString());
                }
            }
        }
        
        return builder.build();
    }
    
    // ========== 11. 支持OPTIONS请求 ==========
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    	request.setCharacterEncoding("UTF-8");
        doGet(request, response);
    }
}
package org.cloud.paas.diseasemanagementservice;

import java.util.List;
import java.util.Map;

public interface DiseaseManageService_implRemote {
    
    Map<String, Object> getNodeInfo(String processId, String nodeId);
    Map<String, Object> getLatestNode(String processId);
    boolean markNodeAsCompleted(String processId, String nodeId);
    boolean transferToNextNode(String processId);
    
    /**
     * 验证流转条件并返回详细错误信息
     * @return Map包含 "canTransfer" (boolean) 和 "message" (String)
     */
    Map<String, Object> validateTransfer(String processId);

    boolean updateRegisterNode(String processId, String reminder);
    boolean updateConsultationNode(String processId, String diagnosisText, 
                                   Long locationId, String reminder);
    boolean updateExaminationNode(String processId, Long locationId, 
                                  String diagnosisText, String pictures, String reminder);
    boolean updateTreatmentNode(String processId, Long locationId, 
                                String treatmentPlan, String reminder);
    boolean updateMedicationNode(String processId, Long locationId, 
                                 Long medicineId, String reminder);
    boolean updateCompletionNode(String processId, String reminder);

    List<Map<String, Object>> getAllLocations();
    Map<String, Object> getLocationById(Long locationId);
    List<Map<String, Object>> getAllMedicines();
    List<Map<String, Object>> getAllPatientProcesses();

    /**
     * 检查指定流程节点是否已支付完成
     */
    boolean checkNodePaymentCompleted(String processId, String nodeId);
    
    /**
     * 获取指定节点的支付订单列表
     */
    List<Map<String, Object>> getNodePayments(String processId, String nodeId);
    
    /**
     * 为节点添加支付订单
     */
    boolean addPaymentOrder(String processId, String nodeId, String orderContent, Long amount);
    
    /**
     * 标记支付订单为已支付
     */
    boolean markPaymentAsPaid(Long orderId);
    
    /**
     * 删除支付订单
     */
    boolean deletePaymentOrder(Long orderId);
    
    List<Map<String, Object>> getAllPatients();
    
    Map<String, Object> getPatientById(Long patientId);
    
    boolean updateMedicalProcessStatus(String processId, String newStatus);
}


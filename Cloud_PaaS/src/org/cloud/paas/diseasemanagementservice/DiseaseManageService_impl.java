package org.cloud.paas.diseasemanagementservice;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.jpa.Location;
import org.jpa.LocationFacadeLocal;
import org.jpa.Medicine;
import org.jpa.MedicineFacadeLocal;
import org.jpa.ProcessNode;
import org.jpa.ProcessNodeFacadeLocal;
import org.jpa.ProcessNodeId;
import org.jpa.Payment;
import org.jpa.PaymentFacadeLocal;
import org.jpa.Patient;
import org.jpa.PatientFacadeLocal;
import org.jpa.MedicalProcesses;
import org.jpa.MedicalProcessesFacadeLocal;

@Stateless(name = "DiseaseManageService_impl")
@Remote(DiseaseManageService_implRemote.class)
public class DiseaseManageService_impl implements DiseaseManageService_implRemote {

    @EJB
    private ProcessNodeFacadeLocal processNodeFacade;
    
    @EJB
    private LocationFacadeLocal locationFacade;
    
    @EJB
    private MedicineFacadeLocal medicineFacade;
    
    @EJB
    private PaymentFacadeLocal paymentFacade;
    
    @EJB
    private PatientFacadeLocal patientFacade;
    
    @EJB
    private MedicalProcessesFacadeLocal medicalProcessesFacade;
    
    
    @PersistenceContext
    private EntityManager entityManager;
    
    // ========== 节点ID常量 ==========
    private static final int NODE_ID_REGISTER = 1;
    private static final int NODE_ID_CONSULTATION = 2;
    private static final int NODE_ID_EXAMINATION = 3;    // 检查（需要支付）
    private static final int NODE_ID_TREATMENT = 4;      // 治疗（需要支付）
    private static final int NODE_ID_MEDICATION = 5;     // 取药（需要支付）
    private static final int NODE_ID_COMPLETION = 6;
    
    // ========== 支付相关常量 ==========
    private static final String PAYMENT_STATUS_UNPAID = "未支付";
    private static final String PAYMENT_STATUS_PAID = "已支付";
    
    // ========== 核心支付方法 ==========
    
    /**
     * 检查指定流程的特定节点是否已支付完成
     */
    @Override
    public boolean checkNodePaymentCompleted(String processId, String nodeId) {
        try {
            Long pid = Long.parseLong(processId);
            Long nid = Long.parseLong(nodeId);
            
            // 使用JPQL查询该节点的未支付订单数量
            String jpql = "SELECT COUNT(p) FROM Payment p WHERE p.processNode.id.nodeId = :nodeId " +
                         "AND p.processNode.id.processId = :processId AND p.orderStatus = :unpaidStatus";
            
            Query query = entityManager.createQuery(jpql);
            query.setParameter("nodeId", nid);
            query.setParameter("processId", pid);
            query.setParameter("unpaidStatus", PAYMENT_STATUS_UNPAID);
            
            Long count = (Long) query.getSingleResult();
            
            if (count > 0) {
                System.out.println("节点" + nodeId + "存在未支付的订单，数量: " + count);
                return false;
            }
            
            System.out.println("节点" + nodeId + "所有支付已完成");
            return true;
            
        } catch (Exception e) {
            System.out.println("检查支付状态失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 获取指定节点的支付订单列表
     */
    @Override
    public List<Map<String, Object>> getNodePayments(String processId, String nodeId) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        try {
            Long pid = Long.parseLong(processId);
            Long nid = Long.parseLong(nodeId);
            
            // 使用JPQL查询支付记录
            String jpql = "SELECT p FROM Payment p WHERE p.processNode.id.nodeId = :nodeId " +
                         "AND p.processNode.id.processId = :processId ORDER BY p.createAt DESC";
            
            Query query = entityManager.createQuery(jpql);
            query.setParameter("nodeId", nid);
            query.setParameter("processId", pid);
            
            @SuppressWarnings("unchecked")
            List<Payment> payments = query.getResultList();
            
            for (Payment payment : payments) {
                Map<String, Object> paymentMap = new HashMap<>();
                paymentMap.put("orderId", payment.getOrderId());
                paymentMap.put("orderContent", payment.getOrderContent());
                paymentMap.put("amount", payment.getAmount());
                paymentMap.put("orderStatus", payment.getOrderStatus());
                paymentMap.put("createAt", payment.getCreateAt());
                paymentMap.put("paidAt", payment.getPaidAt());
                
                result.add(paymentMap);
            }
            
            System.out.println("获取节点" + nodeId + "的支付订单，数量: " + result.size());
            
        } catch (Exception e) {
            System.out.println("获取支付订单失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 为节点添加支付订单
     */
    @Override
    public boolean addPaymentOrder(String processId, String nodeId, String orderContent, Long amount) {
        try {
            Long pid = Long.parseLong(processId);
            Long nid = Long.parseLong(nodeId);
            
            // 1. 获取流程节点
            ProcessNodeId nodeIdObj = new ProcessNodeId(nid, pid);
            ProcessNode node = processNodeFacade.findById(nodeIdObj);
            
            if (node == null) {
                System.out.println("节点不存在，无法添加支付订单");
                return false;
            }
            
            // 2. 生成唯一的订单ID
            Long orderId = generateOrderId();
            
            // 3. 创建支付订单
            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setProcessNode(node);
            payment.setOrderContent(orderContent);
            payment.setAmount(amount);
            payment.setOrderStatus(PAYMENT_STATUS_UNPAID);
            payment.setCreateAt(new Timestamp(System.currentTimeMillis()));
            
            // 4. 保存支付订单
            paymentFacade.save(payment);
            
            System.out.println("为节点" + nodeId + "添加支付订单成功: " + orderContent + ", 金额: " + amount + ", 订单ID: " + orderId);
            return true;
            
        } catch (Exception e) {
            System.out.println("添加支付订单失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 标记支付订单为已支付
     */
    @Override
    public boolean markPaymentAsPaid(Long orderId) {
        try {
            // 1. 获取支付订单
            Payment payment = paymentFacade.findById(orderId);
            
            if (payment == null) {
                System.out.println("支付订单不存在: " + orderId);
                return false;
            }
            
            // 2. 更新支付状态
            payment.setOrderStatus(PAYMENT_STATUS_PAID);
            payment.setPaidAt(new Timestamp(System.currentTimeMillis()));
            
            // 3. 保存更新
            paymentFacade.update(payment);
            
            System.out.println("支付订单标记为已支付: " + orderId);
            return true;
            
        } catch (Exception e) {
            System.out.println("标记支付失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 删除支付订单
     */
    @Override
    public boolean deletePaymentOrder(Long orderId) {
        try {
            // 1. 获取支付订单
            Payment payment = paymentFacade.findById(orderId);
            
            if (payment == null) {
                System.out.println("支付订单不存在: " + orderId);
                return false;
            }
            
            // 2. 删除支付订单
            paymentFacade.delete(payment);
            
            System.out.println("删除支付订单成功: " + orderId);
            return true;
            
        } catch (Exception e) {
            System.out.println("删除支付订单失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 生成唯一的订单ID
     */
    private Long generateOrderId() {
        try {
            // 使用JPQL查询最大的订单ID
            String jpql = "SELECT MAX(p.orderId) FROM Payment p";
            Query query = entityManager.createQuery(jpql);
            
            Long maxId = (Long) query.getSingleResult();
            if (maxId == null) {
                maxId = 0L;
            }
            
            // 返回最大ID+1
            return maxId + 1;
        } catch (Exception e) {
            System.out.println("生成订单ID失败，使用时间戳: " + e.getMessage());
            return System.currentTimeMillis();
        }
    }
    
    // ========== 流程流转方法 ==========
    
    /**
     * 验证流转条件并返回详细错误信息
     */
    @Override
    public Map<String, Object> validateTransfer(String processId) {
        Map<String, Object> result = new HashMap<>();
        result.put("canTransfer", false);
        
        try {
            Long pid = Long.parseLong(processId);
            
            // 1. 查找当前最新节点
            ProcessNode currentLatestNode = findLatestNode(pid);
            
            if (currentLatestNode == null) {
                result.put("message", "未找到当前流程节点");
                return result;
            }
            
            Long currentNodeId = currentLatestNode.getId().getNodeId();
            String currentNodeName = getNodeNameById(currentNodeId.intValue());
            
            // 2. 检查各节点的必填字段
            List<String> missingFields = new ArrayList<>();
            
            if (currentNodeId == NODE_ID_CONSULTATION) {
                // 问诊阶段：需要填写诊断内容和就诊地点
                if (currentLatestNode.getDiagnosisText() == null || currentLatestNode.getDiagnosisText().trim().isEmpty()) {
                    missingFields.add("诊断结果");
                }
                if (currentLatestNode.getLocationId() == null) {
                    missingFields.add("就诊地点");
                }
            } else if (currentNodeId == NODE_ID_EXAMINATION) {
                // 检查阶段：需要填写检查地点
                if (currentLatestNode.getLocationId() == null) {
                    missingFields.add("检查地点");
                }
            } else if (currentNodeId == NODE_ID_TREATMENT) {
                // 治疗阶段：需要填写治疗地点
                if (currentLatestNode.getLocationId() == null) {
                    missingFields.add("治疗地点");
                }
            } else if (currentNodeId == NODE_ID_MEDICATION) {
                // 取药阶段：需要填写取药地点
                if (currentLatestNode.getLocationId() == null) {
                    missingFields.add("取药地点");
                }
            }
            
            if (!missingFields.isEmpty()) {
                result.put("message", "请先填写以下必填项：" + String.join("、", missingFields));
                return result;
            }
            
            // 3. 检查支付状态
            if (currentNodeId == NODE_ID_EXAMINATION || 
                currentNodeId == NODE_ID_TREATMENT || 
                currentNodeId == NODE_ID_MEDICATION) {
                
                boolean paymentCompleted = checkNodePaymentCompleted(processId, currentNodeId.toString());
                if (!paymentCompleted) {
                    result.put("message", "【" + currentNodeName + "】阶段存在未支付的费用，请等待患者完成缴费后再流转");
                    return result;
                }
            }
            
            // 4. 检查是否已经是最后一个节点
            Long nextNodeId = getNextNodeId(currentNodeId);
            if (nextNodeId == null) {
                result.put("message", "已经是最后一个节点，无法继续流转");
                return result;
            }
            
            // 所有验证通过
            result.put("canTransfer", true);
            result.put("message", "可以流转到下一阶段");
            return result;
            
        } catch (Exception e) {
            result.put("message", "验证失败：" + e.getMessage());
            return result;
        }
    }
    
    /**
     * 流转到下一个节点（需要检查支付状态）
     */
    @Override
    public boolean transferToNextNode(String processId) {
        try {
            Long pid = Long.parseLong(processId);
            Timestamp now = new Timestamp(System.currentTimeMillis());
            
            // 1. 查找当前最新节点
            ProcessNode currentLatestNode = findLatestNode(pid);
            
            if (currentLatestNode == null) {
                System.out.println("未找到流程" + processId + "的最新节点");
                return false;
            }
            
            Long currentNodeId = currentLatestNode.getId().getNodeId();
            String currentNodeName = getNodeNameById(currentNodeId.intValue());
            System.out.println("当前最新节点: " + currentNodeName);
            
            // 2. 检查当前节点是否需要支付完成才能离开
            // 需要付费的节点：检查(3)、治疗(4)、取药(5)
            if (currentNodeId == NODE_ID_EXAMINATION || 
                currentNodeId == NODE_ID_TREATMENT || 
                currentNodeId == NODE_ID_MEDICATION) {
                
                boolean paymentCompleted = checkNodePaymentCompleted(processId, currentNodeId.toString());
                if (!paymentCompleted) {
                    System.out.println("节点" + currentNodeName + "支付未完成，不能流转到下一步");
                    return false;
                }
            }
            
            // 3. 获取下一个节点ID
            Long nextNodeId = getNextNodeId(currentNodeId);
            if (nextNodeId == null) {
                System.out.println("已经是最后一个节点: " + currentNodeName);
                return false;
            }
            
            // 4. 查找下一个节点
            ProcessNodeId nextNodeIdObj = new ProcessNodeId(nextNodeId, pid);
            ProcessNode nextNode = processNodeFacade.findById(nextNodeIdObj);
            
            // 如果节点不存在则自动创建
            if (nextNode == null) {
                System.out.println("下一个节点不存在，自动创建: nodeId=" + nextNodeId);
                nextNode = new ProcessNode();
                nextNode.setId(nextNodeIdObj);
                nextNode.setMedicalProcesses(currentLatestNode.getMedicalProcesses());
                nextNode.setNodeName(getNodeNameById(nextNodeId.intValue()));
                nextNode.setNodeStatus("待完成");
                nextNode.setIsLatest(false);
                nextNode.setCreateAt(now);
                nextNode.setUpdatedAt(now);
                processNodeFacade.save(nextNode);
            }
            
            // 5. 更新当前节点：is_latest = false
            currentLatestNode.setIsLatest(false);
            currentLatestNode.setUpdatedAt(now);
            processNodeFacade.update(currentLatestNode);
            
            // 6. 更新下一个节点：is_latest = true, node_status = "进行中"
            nextNode.setIsLatest(true);
            nextNode.setNodeStatus("进行中");
            nextNode.setUpdatedAt(now);
            processNodeFacade.update(nextNode);
            
            System.out.println("流转成功: " + currentNodeName + " → " + getNodeNameById(nextNodeId.intValue()));
            return true;
            
        } catch (NumberFormatException e) {
            System.out.println("流程ID格式错误: " + processId);
            return false;
        } catch (Exception e) {
            System.out.println("流转到下一个节点失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 获取下一个节点的ID
     */
    private Long getNextNodeId(Long currentNodeId) {
        switch (currentNodeId.intValue()) {
            case NODE_ID_REGISTER:     return (long)NODE_ID_CONSULTATION;
            case NODE_ID_CONSULTATION: return (long)NODE_ID_EXAMINATION;
            case NODE_ID_EXAMINATION:  return (long)NODE_ID_TREATMENT;
            case NODE_ID_TREATMENT:    return (long)NODE_ID_MEDICATION;
            case NODE_ID_MEDICATION:   return (long)NODE_ID_COMPLETION;
            case NODE_ID_COMPLETION:   return null;  // 没有下一个节点
            default:                   return null;
        }
    }
    
    /**
     * 根据节点ID获取节点名称
     */
    private String getNodeNameById(int nodeId) {
        switch (nodeId) {
            case NODE_ID_REGISTER:     return "挂号";
            case NODE_ID_CONSULTATION: return "问诊";
            case NODE_ID_EXAMINATION:  return "检查";
            case NODE_ID_TREATMENT:    return "治疗";
            case NODE_ID_MEDICATION:   return "取药";
            case NODE_ID_COMPLETION:   return "完成";
            default:                   return "未知";
        }
    }
    
    // ========== 辅助方法 ==========
    
    /**
     * 查找指定流程的最新节点
     */
    private ProcessNode findLatestNode(Long processId) {
        try {
            // 使用JPQL查询最新节点
            String jpql = "SELECT p FROM ProcessNode p WHERE p.id.processId = :processId AND p.isLatest = true";
            
            Query query = entityManager.createQuery(jpql);
            query.setParameter("processId", processId);
            
            @SuppressWarnings("unchecked")
            List<ProcessNode> results = query.getResultList();
            
            if (!results.isEmpty()) {
                return results.get(0);
            }
            
            // 如果没有最新节点，查找创建时间最新的节点
            String jpql2 = "SELECT p FROM ProcessNode p WHERE p.id.processId = :processId " +
                          "ORDER BY p.updatedAt DESC";
            
            query = entityManager.createQuery(jpql2);
            query.setParameter("processId", processId);
            query.setMaxResults(1);
            
            results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
            
        } catch (Exception e) {
            System.out.println("查找最新节点失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 根据流程ID获取患者信息
     */
    private Map<String, Object> getPatientInfoByProcessId(Long processId) {
        try {
            // 1. 先获取 MedicalProcesses
            MedicalProcesses medicalProcess = medicalProcessesFacade.findById(processId);
            if (medicalProcess == null) {
                System.out.println("未找到流程: " + processId);
                return null;
            }
            
            // 2. 从 MedicalProcesses 获取 Patient
            Patient patient = medicalProcess.getPatient();
            if (patient == null) {
                System.out.println("流程" + processId + "没有关联的患者");
                return null;
            }
            
            // 3. 构建患者信息
            Map<String, Object> patientInfo = new HashMap<>();
            patientInfo.put("patientId", patient.getPatientId());
            patientInfo.put("username", patient.getUsername());
            patientInfo.put("phone", patient.getPhoneNum());
            patientInfo.put("gender", patient.getGender());
            
            return patientInfo;
            
        } catch (Exception e) {
            System.out.println("获取患者信息失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 将ProcessNode实体转换为Map
     */
    private Map<String, Object> convertNodeToMap(ProcessNode node) {
        if (node == null) {
            return null;
        }
        
        Map<String, Object> map = new HashMap<>();
        
        // 基本字段
        map.put("nodeId", node.getId().getNodeId());
        map.put("processId", node.getId().getProcessId());
        map.put("nodeName", node.getNodeName());
        map.put("nodeStatus", node.getNodeStatus());
        map.put("isLatest", node.getIsLatest());
        
        // 可空字段
        if (node.getDiagnosisText() != null) {
            map.put("diagnosisText", node.getDiagnosisText());
        }
        
        if (node.getLocationId() != null) {
            map.put("locationId", node.getLocationId());
        }
        
        if (node.getPictures() != null) {
            map.put("pictures", node.getPictures());
        }
        
        if (node.getReminder() != null) {
            map.put("reminder", node.getReminder());
        }
        
        // Medicine外键处理
        if (node.getMedicine() != null) {
            try {
                Medicine medicine = node.getMedicine();
                map.put("medicineId", medicine.getMedicineId());
                map.put("medicineName", medicine.getName());
            } catch (Exception e) {
                System.out.println("获取Medicine信息失败: " + e.getMessage());
            }
        }
        
        // 时间字段
        if (node.getCreateAt() != null) {
            map.put("createAt", node.getCreateAt());
        }
        
        if (node.getUpdatedAt() != null) {
            map.put("updatedAt", node.getUpdatedAt());
        }
        
        // 添加支付状态
        boolean paymentCompleted = checkNodePaymentCompleted(
            node.getId().getProcessId().toString(), 
            node.getId().getNodeId().toString()
        );
        map.put("paymentCompleted", paymentCompleted);
        
        return map;
    }
    
    // ========== 原有业务方法 ==========
    
    @Override
    public Map<String, Object> getNodeInfo(String processId, String nodeId) {
        try {
            Long pid = Long.parseLong(processId);
            Long nid = Long.parseLong(nodeId);
            
            ProcessNodeId processNodeId = new ProcessNodeId(nid, pid);
            ProcessNode node = processNodeFacade.findById(processNodeId);
            
            return convertNodeToMap(node);
            
        } catch (NumberFormatException e) {
            System.out.println("参数格式错误: processId=" + processId + ", nodeId=" + nodeId);
            return null;
        } catch (Exception e) {
            System.out.println("获取节点信息失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public Map<String, Object> getLatestNode(String processId) {
        try {
            Long pid = Long.parseLong(processId);
            ProcessNode latestNode = findLatestNode(pid);
            
            if (latestNode == null) {
                System.out.println("未找到流程" + processId + "的最新节点");
                return null;
            }
            
            return convertNodeToMap(latestNode);
            
        } catch (NumberFormatException e) {
            System.out.println("流程ID格式错误: " + processId);
            return null;
        } catch (Exception e) {
            System.out.println("获取最新节点失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public boolean markNodeAsCompleted(String processId, String nodeId) {
        try {
            Long pid = Long.parseLong(processId);
            Long nid = Long.parseLong(nodeId);
            
            ProcessNodeId processNodeId = new ProcessNodeId(nid, pid);
            ProcessNode node = processNodeFacade.findById(processNodeId);
            
            if (node == null) {
                System.out.println("节点不存在: processId=" + processId + ", nodeId=" + nodeId);
                return false;
            }
            
            // 更新节点状态为"已完成"
            node.setNodeStatus("已完成");
            node.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            
            processNodeFacade.update(node);
            
            System.out.println("标记节点为已完成: " + getNodeNameById(nid.intValue()));
            return true;
            
        } catch (NumberFormatException e) {
            System.out.println("参数格式错误: processId=" + processId + ", nodeId=" + nodeId);
            return false;
        } catch (Exception e) {
            System.out.println("标记节点完成失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // ========== 节点内容更新方法 ==========
    
    @Override
    public boolean updateRegisterNode(String processId, String reminder) {
        return updateNodeWithOptionalFields(processId, NODE_ID_REGISTER, null, null, null, null, reminder);
    }
    
    @Override
    public boolean updateConsultationNode(String processId, String diagnosisText, Long locationId, String reminder) {
        if (diagnosisText == null || diagnosisText.trim().isEmpty()) {
            System.out.println("诊断文本不能为空");
            return false;
        }
        if (locationId == null) {
            System.out.println("位置ID不能为空");
            return false;
        }
        
        return updateNodeWithOptionalFields(processId, NODE_ID_CONSULTATION, 
                                           diagnosisText.trim(), locationId, null, null, reminder);
    }
    
    @Override
    public boolean updateExaminationNode(String processId, Long locationId, String diagnosisText, String pictures, String reminder) {
        // locationId
        return updateNodeWithOptionalFields(processId, NODE_ID_EXAMINATION, 
                                           diagnosisText, locationId, pictures, null, reminder);
    }
    
    @Override
    public boolean updateTreatmentNode(String processId, Long locationId, String treatmentPlan, String reminder) {
        String diagText = (treatmentPlan != null && !treatmentPlan.trim().isEmpty()) ? treatmentPlan.trim() : null;
        return updateNodeWithOptionalFields(processId, NODE_ID_TREATMENT, 
                                           diagText, locationId, null, null, reminder);
    }
    
    @Override
    public boolean updateMedicationNode(String processId, Long locationId, Long medicineId, String reminder) {
        return updateNodeWithOptionalFields(processId, NODE_ID_MEDICATION, 
                                           null, locationId, null, medicineId, reminder);
    }
    
    @Override
    public boolean updateCompletionNode(String processId, String reminder) {
        return updateNodeWithOptionalFields(processId, NODE_ID_COMPLETION, null, null, null, null, reminder);
    }
    
    /**
     * 通用节点更新方法
     */
    private boolean updateNodeWithOptionalFields(String processId, int nodeIdInt,
                                                String diagnosisText, Long locationId,
                                                String pictures, Long medicineId,
                                                String reminder) {
        try {
            Long pid = Long.parseLong(processId);
            Long nid = (long) nodeIdInt;
            
            ProcessNodeId nodeId = new ProcessNodeId(nid, pid);
            ProcessNode node = processNodeFacade.findById(nodeId);
            
            // 如果节点不存在，自动创建
            if (node == null) {
                System.out.println("节点不存在，尝试创建: processId=" + processId + ", nodeId=" + nodeIdInt);
                node = createProcessNode(pid, nodeIdInt);
                if (node == null) {
                    System.out.println("创建节点失败: processId=" + processId + ", nodeId=" + nodeIdInt);
                    return false;
                }
            }
            
            // 更新字段（只更新非null的值）
            if (diagnosisText != null) {
                node.setDiagnosisText(diagnosisText.trim());
            }
            
            if (locationId != null) {
                node.setLocationId(locationId);
            }
            
            if (pictures != null) {
                if (pictures.trim().isEmpty()) {
                    node.setPictures(null); // 清空图片
                } else {
                    node.setPictures(pictures.trim());
                }
            }
            
            if (reminder != null && !reminder.trim().isEmpty()) {
                node.setReminder(reminder.trim());
            }
            
            // Medicine处理
            if (medicineId != null) {
                try {
                    Medicine medicine = medicineFacade.findById(medicineId);
                    if (medicine != null) {
                        node.setMedicine(medicine);
                        System.out.println("设置药品ID: " + medicineId + " 成功");
                    }
                } catch (Exception e) {
                    System.out.println("设置药品失败: " + e.getMessage());
                }
            }
            
            // 更新时间戳
            node.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            
            processNodeFacade.update(node);
            
            System.out.println("更新节点内容成功: " + getNodeNameById(nodeIdInt));
            return true;
            
        } catch (NumberFormatException e) {
            System.out.println("流程ID格式错误: " + processId);
            return false;
        } catch (Exception e) {
            System.out.println("更新节点内容失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 创建新的流程节点
     */
    private ProcessNode createProcessNode(Long processId, int nodeId) {
        try {
            MedicalProcesses medicalProcess = medicalProcessesFacade.findById(processId);
            if (medicalProcess == null) {
                System.out.println("创建节点失败: 医疗进程不存在, processId=" + processId);
                return null;
            }
            
            ProcessNodeId id = new ProcessNodeId((long)nodeId, processId);
            ProcessNode newNode = new ProcessNode();
            newNode.setId(id);
            newNode.setMedicalProcesses(medicalProcess);
            newNode.setNodeName(getNodeNameById(nodeId));
            newNode.setNodeStatus("进行中");
            newNode.setReminder(getDefaultReminder(nodeId));
            newNode.setCreateAt(new Timestamp(System.currentTimeMillis()));
            newNode.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            newNode.setIsLatest(true);
            
            processNodeFacade.save(newNode);
            System.out.println("创建节点成功: processId=" + processId + ", nodeId=" + nodeId);
            return newNode;
            
        } catch (Exception e) {
            System.out.println("创建节点异常: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private String getDefaultReminder(int nodeId) {
        switch (nodeId) {
            case 1: return "请完成挂号流程";
            case 2: return "请向医生详细描述症状";
            case 3: return "请按照医生指示完成检查";
            case 4: return "请按时进行治疗";
            case 5: return "请取药，按时服药";
            case 6: return "看病流程已完成";
            default: return "请完成此步骤";
        }
    }
    
    // ========== 数据获取方法（完全从数据库读取）==========
    
    @Override
    public List<Map<String, Object>> getAllLocations() {
        try {
            System.out.println("开始从数据库获取所有地点数据...");
            List<Location> locationList = locationFacade.findAll();
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (Location location : locationList) {
                Map<String, Object> locationMap = new HashMap<>();
                locationMap.put("locationId", location.getLocationId());
                locationMap.put("locationName", location.getLocationName());
                locationMap.put("position", location.getPosition());
                
                if (location.getRouteGuide() != null) {
                    locationMap.put("routeGuide", location.getRouteGuide());
                }
                
                result.add(locationMap);
            }
            
            System.out.println("从数据库获取地点数据成功，数量: " + result.size());
            return result;
            
        } catch (Exception e) {
            System.out.println("获取地点数据失败: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public Map<String, Object> getLocationById(Long locationId) {
        try {
            System.out.println("从数据库获取地点信息，ID: " + locationId);
            Location location = locationFacade.findById(locationId);
            if (location == null) {
                System.out.println("未找到地点: " + locationId);
                return null;
            }
            
            Map<String, Object> locationMap = new HashMap<>();
            locationMap.put("locationId", location.getLocationId());
            locationMap.put("locationName", location.getLocationName());
            locationMap.put("position", location.getPosition());
            
            if (location.getRouteGuide() != null) {
                locationMap.put("routeGuide", location.getRouteGuide());
            }
            
            return locationMap;
            
        } catch (Exception e) {
            System.out.println("获取地点信息失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public List<Map<String, Object>> getAllMedicines() {
        try {
            System.out.println("开始从数据库获取所有药品数据...");
            List<Medicine> medicineList = medicineFacade.findAll();
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (Medicine medicine : medicineList) {
                Map<String, Object> medicineMap = new HashMap<>();
                medicineMap.put("medicineId", medicine.getMedicineId());
                
                // 使用Medicine的所有字段
                if (medicine.getName() != null) {
                    medicineMap.put("name", medicine.getName());
                }
                if (medicine.getUseMethod() != null) {
                    medicineMap.put("useMethod", medicine.getUseMethod());
                }
                if (medicine.getDosage() != null) {
                    medicineMap.put("dosage", medicine.getDosage());
                }
                if (medicine.getSideEffect() != null) {
                    medicineMap.put("sideEffect", medicine.getSideEffect());
                }
                if (medicine.getPrice() != null) {
                    medicineMap.put("price", medicine.getPrice());
                }
                
                result.add(medicineMap);
            }
            
            System.out.println("从数据库获取药品数据成功，数量: " + result.size());
            return result;
            
        } catch (Exception e) {
            System.out.println("获取药品数据失败: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Map<String, Object>> getAllPatientProcesses() {
        try {
            System.out.println("开始从数据库获取所有患者流程数据...");

            String sql = "SELECT " +
                        "mp.ProcessID as processId, " +
                        "mp.PatientID as patientId, " +
                        "p.username as patientName, " +
                        "p.gender as gender, " +
                        "p.phone_num as phone, " +
                        "mp.process_status as status, " +
                        "COALESCE(pn.NodeID, 1) as currentStage " +
                        "FROM medical_processes mp " +
                        "JOIN patient p ON mp.PatientID = p.PatientID " +
                        "LEFT JOIN process_node pn ON mp.ProcessID = pn.ProcessID AND pn.is_latest = 1 " +
                        "WHERE mp.process_status != '已取消' " +
                        "ORDER BY mp.created_at DESC";
            
            Query query = entityManager.createNativeQuery(sql);
            
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();
            
            List<Map<String, Object>> result = new ArrayList<>();
            
            System.out.println("查询到患者流程数量: " + results.size());
            
            for (Object[] row : results) {
                Map<String, Object> patientProcess = new HashMap<>();
                
                // 调试输出
                System.out.println("处理行数据: " + Arrays.toString(row));
                
                // 流程ID
                Long processId = ((Number) row[0]).longValue();
                patientProcess.put("processId", processId.toString()); // 转为String方便JSP使用
                
                // 患者ID
                Long patientId = ((Number) row[1]).longValue();
                patientProcess.put("patientId", patientId.toString());
                
                // 患者姓名
                String patientName = (String) row[2];
                patientProcess.put("patientName", (patientName != null && !patientName.trim().isEmpty()) ? patientName : "患者" + patientId);
                
                // 性别
                String gender = (String) row[3];
                patientProcess.put("gender", gender != null ? gender : "未知");
                
                // 电话
                String phone = (String) row[4];
                patientProcess.put("phone", phone != null ? phone : "未知");
                
                // 状态
                String status = (String) row[5];
                patientProcess.put("status", status != null ? status : "未开始");
                
                // 当前阶段
                Long currentStage = ((Number) row[6]).longValue();
                patientProcess.put("currentStage", currentStage.intValue()); // 转为Integer
                
                // 添加阶段标签（可选）
                String[] stageLabels = {"", "挂号", "问诊", "收费", "检查", "取药", "完成"};
                int stage = currentStage.intValue();
                String stageLabel = (stage >= 1 && stage <= 6) ? stageLabels[stage] : "未知";
                patientProcess.put("stageLabel", stageLabel);
                
                result.add(patientProcess);
                
                System.out.println("添加患者: " + patientName + ", 流程ID: " + processId + ", 阶段: " + stage);
            }
            
            System.out.println("从数据库获取患者流程数据成功，数量: " + result.size());
            return result;
            
        } catch (Exception e) {
            System.out.println("获取患者流程数据失败: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
 @Override
    public List<Map<String, Object>> getAllPatients() {
        try {
            System.out.println("开始从数据库获取所有患者信息...");
            List<Patient> patientList = patientFacade.findAll();
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (Patient patient : patientList) {
                Map<String, Object> patientMap = new HashMap<>();
                patientMap.put("patientId", patient.getPatientId());
                patientMap.put("username", patient.getUsername());
                patientMap.put("phoneNum", patient.getPhoneNum());
                patientMap.put("gender", patient.getGender());
                
                result.add(patientMap);
            }
            
            System.out.println("从数据库获取患者信息成功，数量: " + result.size());
            return result;
            
        } catch (Exception e) {
            System.out.println("获取患者信息失败: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public Map<String, Object> getPatientById(Long patientId) {
        try {
            System.out.println("从数据库获取患者信息，ID: " + patientId);
            Patient patient = patientFacade.findById(patientId);
            if (patient == null) {
                System.out.println("未找到患者: " + patientId);
                return null;
            }
            
            Map<String, Object> patientMap = new HashMap<>();
            patientMap.put("patientId", patient.getPatientId());
            patientMap.put("username", patient.getUsername());
            patientMap.put("phoneNum", patient.getPhoneNum());
            patientMap.put("gender", patient.getGender());
            
            return patientMap;
            
        } catch (Exception e) {
            System.out.println("获取患者信息失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    
    /**
     * 生成唯一的流程ID
     */
    private Long generateProcessId() {
        try {
            String jpql = "SELECT MAX(mp.processId) FROM MedicalProcesses mp";
            Query query = entityManager.createQuery(jpql);
            
            Long maxId = (Long) query.getSingleResult();
            if (maxId == null) {
                maxId = 0L;
            }
            
            return maxId + 1;
        } catch (Exception e) {
            System.out.println("生成流程ID失败，使用时间戳: " + e.getMessage());
            return System.currentTimeMillis();
        }
    }

    @Override
    public boolean updateMedicalProcessStatus(String processId, String newStatus) {
        try {
            Long pid = Long.parseLong(processId);
            
            // 使用 MedicalProcessesFacade 查找流程
            MedicalProcesses medicalProcess = medicalProcessesFacade.findById(pid);
            
            if (medicalProcess == null) {
                System.out.println("流程不存在: " + processId);
                return false;
            }
            
            // 更新流程状态
            medicalProcess.setProcessStatus(newStatus);
            medicalProcess.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            
            // 如果状态是已完成或已取消，设置完成时间
            if ("已完成".equals(newStatus) || "已取消".equals(newStatus)) {
                medicalProcess.setCompletedAt(new Timestamp(System.currentTimeMillis()));
            }
            
            // 保存更新
            medicalProcessesFacade.update(medicalProcess);
            
            System.out.println("更新流程状态成功: " + processId + " -> " + newStatus);
            return true;
            
        } catch (NumberFormatException e) {
            System.out.println("流程ID格式错误: " + processId);
            return false;
        } catch (Exception e) {
            System.out.println("更新流程状态失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
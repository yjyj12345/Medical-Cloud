package org.cloud.paas.medicalhistoryservice;

import org.jpa.MedicalProcesses;
import org.jpa.MedicalProcessesFacadeLocal;
import org.jpa.Patient;
import org.jpa.PatientFacadeLocal;
import org.jpa.ProcessNode;
import org.jpa.ProcessNodeFacadeLocal;
import java.util.HashMap;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Collections;
import java.util.Comparator;
import javax.ejb.Remote;
import java.util.Map;

/**
 * 医疗历史查询实现类
 */
@Stateless
@Remote(MedicalHistory_implRemote.class)
public class MedicalHistory_impl implements MedicalHistory_implRemote {

    @EJB
    private PatientFacadeLocal patientFacade;
    @EJB
    private MedicalProcessesFacadeLocal medicalProcessesFacade;
    @EJB
    private ProcessNodeFacadeLocal processNodeFacade;

    /** 根据用户名查询PatientID */
    @Override
    public Long getPatientIdByUsername(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }

        List<Patient> patients = patientFacade.findByUsername(username);
        if (patients == null || patients.isEmpty()) {
            return null;
        }

        return patients.get(0).getPatientId();
    }

    /** 根据PatientID查询看病历史 */
    @Override
    public List<Map<String, Object>> getMedicalHistoryByPatientId(Long patientId) {
        System.out.println("===== 开始查询看病历史 =====");
        System.out.println("patientId: " + patientId);

        if (patientId == null) {
            System.out.println("错误: patientId为空");
            return new ArrayList<>();
        }

        // 查询患者
        Patient patient = patientFacade.findById(patientId);
        System.out.println("查询患者结果: " + (patient == null ? "null" : patient.getUsername()));

        if (patient == null) {
            System.out.println("错误: 未找到patientId=" + patientId + "的患者");
            return new ArrayList<>();
        }

        // 获取MedicalProcesses
        Set<MedicalProcesses> medicalProcessesSet = patient.getMedicalProcesseses();
        System.out.println("患者" + patient.getUsername() + "的MedicalProcesses数量: " + 
                         (medicalProcessesSet == null ? "null" : medicalProcessesSet.size()));

        if (medicalProcessesSet == null || medicalProcessesSet.isEmpty()) {
            System.out.println("提示: 患者" + patient.getUsername() + "没有就医记录");
            return new ArrayList<>();
        }

        // 将Set转为List并按processId升序排序（#1在前面）
        List<MedicalProcesses> medicalProcessesList = new ArrayList<>(medicalProcessesSet);
        Collections.sort(medicalProcessesList, new Comparator<MedicalProcesses>() {
            @Override
            public int compare(MedicalProcesses p1, MedicalProcesses p2) {
                // 升序排列：processId小的在前面
                return Long.compare(p1.getProcessId(), p2.getProcessId());
            }
        });

        // 遍历就医进程
        List<Map<String, Object>> historyList = new ArrayList<>();
        for (MedicalProcesses process : medicalProcessesList) {
            String status = process.getProcessStatus();
            if ("已取消".equals(status) || "cancelled".equalsIgnoreCase(status) || "canceled".equalsIgnoreCase(status)) {
                System.out.println("跳过已取消的进程: processId=" + process.getProcessId());
                continue;
            }
            
            System.out.println("处理就医进程: processId=" + process.getProcessId() + ", 状态=" + status);
            
            // 状态转换为中文
            String displayStatus = convertStatusToChinese(status);
            
            Map<String, Object> processMap = new HashMap<>();
            processMap.put("processId", process.getProcessId());
            processMap.put("processStatus", displayStatus);
            processMap.put("createdAt", formatTimestamp(process.getCreatedAt()));
            processMap.put("updatedAt", formatTimestamp(process.getUpdatedAt()));
            processMap.put("completedAt", formatTimestamp(process.getCompletedAt()));

            // 获取ProcessNode
            Set<ProcessNode> processNodes = process.getProcessNodes();
            System.out.println("ProcessId=" + process.getProcessId() + "的节点数量: " + 
                             (processNodes == null ? "null" : processNodes.size()));

            List<Map<String, Object>> nodeList = new ArrayList<>();
            if (processNodes != null && !processNodes.isEmpty()) {
                for (ProcessNode node : processNodes) {
                    System.out.println("处理节点: nodeId=" + node.getId().getNodeId() + ", 名称=" + node.getNodeName());
                    
                    Map<String, Object> nodeMap = new HashMap<>();
                    nodeMap.put("nodeId", node.getId().getNodeId());
                    nodeMap.put("nodeName", node.getNodeName());
                    nodeMap.put("nodeStatus", convertNodeStatusToChinese(node.getNodeStatus()));
                    nodeMap.put("diagnosisText", node.getDiagnosisText());
                    nodeMap.put("reminder", node.getReminder());
                    nodeMap.put("isLatest", node.getIsLatest());
                    nodeMap.put("createAt", formatTimestamp(node.getCreateAt()));
                    nodeMap.put("pictures", node.getPictures());
                    nodeMap.put("locationId", node.getLocationId());

                    nodeList.add(nodeMap);
                }
            } else {
                System.out.println("提示: ProcessId=" + process.getProcessId() + "没有流程节点");
            }

            processMap.put("processNodes", nodeList);
            historyList.add(processMap);
        }

        System.out.println("===== 查询完成 =====");
        System.out.println("返回记录数: " + historyList.size());
        return historyList;
    }

    /** 格式化时间戳 */
    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        return timestamp.toString();
    }
    
    /** 将流程状态转换为中文 */
    private String convertStatusToChinese(String status) {
        if (status == null) return "未知";
        switch (status.toLowerCase()) {
            case "completed":
            case "已完成":
                return "已完成";
            case "inprogress":
            case "in_progress":
            case "进行中":
            case "已预约":
                return "进行中";
            case "cancelled":
            case "canceled":
            case "已取消":
                return "已取消";
            default:
                return status;
        }
    }
    
    /** 将节点状态转换为中文 */
    private String convertNodeStatusToChinese(String status) {
        if (status == null) return "待完成";
        switch (status.toLowerCase()) {
            case "completed":
            case "已完成":
                return "已完成";
            case "inprogress":
            case "in_progress":
            case "进行中":
                return "进行中";
            case "pending":
            case "待完成":
                return "待完成";
            default:
                return status;
        }
    }
}

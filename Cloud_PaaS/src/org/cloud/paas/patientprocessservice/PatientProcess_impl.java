package org.cloud.paas.patientprocessservice;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;

import org.cloud.paas.diseasemanagementservice.DiseaseManageService_implRemote;
import org.jpa.LogUtil;
import org.jpa.MedicalProcesses;
import org.jpa.MedicalProcessesFacadeLocal;
import org.jpa.Medicine;
import org.jpa.MedicineFacadeLocal;
import org.jpa.Patient;
import org.jpa.PatientFacadeLocal;
import org.jpa.ProcessNode;
import org.jpa.ProcessNodeFacadeLocal;
import org.jpa.ProcessNodeId;
import org.jpa.Location;
import org.jpa.LocationFacadeLocal;
import org.jpa.Doctor;
/**
 * 患者就医进程远程接口实现类
 */
@Stateless(name = "PatientProcess_impl")
@Remote(PatientProcessService_implRemote.class)
public class PatientProcess_impl extends UnicastRemoteObject implements PatientProcessService_implRemote {
    
    private static final long serialVersionUID = 1L;
    
    // 步骤名称映射（与nodeId对应）
    private static final String[] STEP_NAMES = {"挂号", "问诊", "检查", "治疗", "取药", "完成"};
    
    // 步骤状态常量
    private static final String STATUS_PENDING = "待完成";
    private static final String STATUS_IN_PROGRESS = "进行中";
    private static final String STATUS_COMPLETED = "已完成";
    
    // 使用EJB注入Facade
    @EJB
    private ProcessNodeFacadeLocal processNodeFacade;
    
    @EJB
    private MedicalProcessesFacadeLocal medicalProcessesFacade;
    
    @EJB
    private PatientFacadeLocal patientFacade;
    
    @EJB
    private MedicineFacadeLocal medicineFacade;

    @EJB
    private LocationFacadeLocal locationFacade;
    
    public PatientProcess_impl() throws RemoteException {
        super();
    }
    
    // ============== 原有的接口方法实现 ==============
    
    @Override
    public Map<String, Object> getProcessNodeByStep(int processId, int step) throws RemoteException {
        return getNodeInfo(processId, step);
    }
    
    @Override
    public Map<String, Object> getProcessNodeByName(int processId, String nodeName) throws RemoteException {
        for (int i = 0; i < STEP_NAMES.length; i++) {
            if (STEP_NAMES[i].equals(nodeName)) {
                return getNodeInfo(processId, i + 1);
            }
        }
        throw new RemoteException("节点名称无效: " + nodeName);
    }
    
    @Override
    public List<Map<String, Object>> getAllProcessNodes(int processId) throws RemoteException {
        List<Map<String, Object>> nodeList = new ArrayList<>();
        
        for (int i = 1; i <= STEP_NAMES.length; i++) {
            nodeList.add(getNodeInfo(processId, i));
        }
        
        return nodeList;
    }
    
    @Override
    public List<Map<String, Object>> getMedicalProcessesForPatient1() throws RemoteException {
        return getMedicalProcessesForPatient(1L);
    }
    
    private List<Map<String, Object>> getMedicalProcessesForPatient(Long patientId) throws RemoteException {
        List<Map<String, Object>> processList = new ArrayList<>();
        
        try {
            // 获取所有流程，然后过滤出指定患者的流程
            List<MedicalProcesses> allProcesses = medicalProcessesFacade.findAll();
            
            for (MedicalProcesses process : allProcesses) {
                if (process.getPatient() != null && 
                    process.getPatient().getPatientId().equals(patientId)) {
                    Map<String, Object> processMap = new HashMap<>();
                    processMap.put("processId", process.getProcessId());
                    processMap.put("createdAt", process.getCreatedAt());
                    processMap.put("status", process.getProcessStatus());
                    processList.add(processMap);
                }
            }
            
        } catch (Exception e) {
            throw new RemoteException("获取患者就医流程失败: " + e.getMessage(), e);
        }
        
        return processList;
    }
    
    @Override
    public boolean updateNodeStatus(int processId, int step, String nodeStatus, String diagnosisText) throws RemoteException {
        return updateNodeStatusInternal(processId, step, nodeStatus, diagnosisText);
    }
    
    @Override
    public Map<String, Object> getMedicineInfo(int medicineId) throws RemoteException {
        try {
            Medicine medicine = medicineFacade.findById(Long.valueOf(medicineId));
            return medicine != null ? convertMedicineToMap(medicine) : null;
        } catch (Exception e) {
            throw new RemoteException("获取药品信息失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Map<String, Object>> searchMedicineByName(String medicineName) throws RemoteException {
        List<Map<String, Object>> medicineList = new ArrayList<>();
        
        try {
            // 获取所有药品，然后进行模糊匹配
            List<Medicine> allMedicines = medicineFacade.findAll();
            
            for (Medicine medicine : allMedicines) {
                if (medicine.getName() != null && 
                    medicine.getName().toLowerCase().contains(medicineName.toLowerCase())) {
                    medicineList.add(convertMedicineToMap(medicine));
                }
            }
            
        } catch (Exception e) {
            throw new RemoteException("搜索药品失败: " + e.getMessage(), e);
        }
        
        return medicineList;
    }
    private Map<String, Object> createDefaultMedicineInfo(String name) {
        System.out.println("创建默认药品信息: " + name);
        Map<String, Object> medicineMap = new HashMap<>();
        medicineMap.put("medicineId", 0);
        medicineMap.put("name", name);
        medicineMap.put("useMethod", "请遵医嘱");
        medicineMap.put("dosage", "请遵医嘱");
        medicineMap.put("sideEffect", "详见说明书");
        medicineMap.put("price", "0.00");
        return medicineMap;
    }
    @Override
    public String testConnection() throws RemoteException {
        try {
            List<MedicalProcesses> processes = medicalProcessesFacade.findAll();
            return "数据库连接成功，找到 " + processes.size() + " 条就医流程记录";
        } catch (Exception e) {
            throw new RemoteException("数据库连接测试失败: " + e.getMessage(), e);
        }
    }

    private MedicalProcesses getLatestMedicalProcess() {
        try {
            LogUtil.log("开始获取最新医疗进程", Level.INFO, null);
            
            // 获取所有记录，按created_at降序排序取最新的一条
            List<MedicalProcesses> allProcesses = medicalProcessesFacade.findAll();
            // 按创建时间降序排序
            java.util.Collections.sort(allProcesses, new java.util.Comparator<MedicalProcesses>() {
                @Override
                public int compare(MedicalProcesses a, MedicalProcesses b) {
                    if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                }
            });
            
            if (allProcesses == null || allProcesses.isEmpty()) {
                LogUtil.log("未找到任何医疗进程", Level.WARNING, null);
                return null;
            }
            
            // 直接返回第一条（最新创建的）
            MedicalProcesses latest = allProcesses.get(0);
            LogUtil.log("找到最新医疗进程: ID=" + latest.getProcessId() + 
                       ", 创建时间=" + latest.getCreatedAt(), Level.INFO, null);
            
            return latest;
            
        } catch (Exception e) {
            LogUtil.log("获取最新医疗进程失败: " + e.getMessage(), Level.SEVERE, e);
            return null;
        }
    }
    
    @Override
    public Map<String, Object> getNodeInfo(int processId, int nodeId) throws RemoteException {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 获取节点状态
            String nodeStatus = getNodeStatusFromDB(processId, nodeId);
            
            // 2. 获取节点详细信息
            String diagnosisText = "";
            String reminder = "";
            String locationName = "";
            ProcessNode node = getProcessNode(processId, nodeId);
            
            if (node != null) {
                diagnosisText = node.getDiagnosisText() != null ? node.getDiagnosisText() : "";
                reminder = node.getReminder() != null ? node.getReminder() : getDefaultReminderForNode(nodeId);
                
                // 获取地点信息
                if (node.getLocationId() != null) {
                    try {
                        Location location = locationFacade.findById(node.getLocationId());
                        if (location != null) {
                            locationName = location.getLocationName();
                            if (location.getPosition() != null && !location.getPosition().isEmpty()) {
                                locationName += " (" + location.getPosition() + ")";
                            }
                        }
                    } catch (Exception e) {
                        // 忽略获取地点信息失败
                    }
                }
            } else {
                reminder = getDefaultReminderForNode(nodeId);
            }
            
             // 3. 获取医疗进程信息（医生、创建时间等）
            MedicalProcesses medicalProcess = medicalProcessesFacade.findById((long) processId);
            String doctorName = "";
            String doctorTitle = "";
            Timestamp createdAt = null;
            
            if (medicalProcess != null) {
                createdAt = medicalProcess.getCreatedAt();
                Doctor doctor = medicalProcess.getDoctor();
                if (doctor != null) {
                    doctorName = doctor.getUsername();
                    doctorTitle = doctor.getTitle();
                }
            }
            
            // 4. 如果是取药节点，获取药品信息
            Map<String, Object> medicineInfo = null;
            String medicineName = "";
            String useMethod = "";
            if (nodeId == 5) {
                System.out.println("========== 取药节点调试 START ==========");
                ProcessNode node5 = getProcessNode(processId, nodeId);
                
                if (node5 != null) {
                    System.out.println("取药节点找到: " + node5.getId());
                    System.out.println("MedicineID字段: " + (node5.getMedicine() != null ? node5.getMedicine().getMedicineId() : "null"));
                    
                    Medicine medicine = node5.getMedicine();
                    if (medicine != null) {
                        System.out.println("药品对象不为null");
                        System.out.println("药品名称: " + medicine.getName());
                        System.out.println("药品用法: " + medicine.getUseMethod());
                        System.out.println("药品剂量: " + medicine.getDosage());
                        System.out.println("药品副作用: " + medicine.getSideEffect());
                        System.out.println("药品价格: " + medicine.getPrice());
                        medicineName = medicine.getName();
                        useMethod = medicine.getUseMethod();
           
                        Map<String, Object> testMap = new HashMap<>();
                        testMap.put("name", medicine.getName());
                        testMap.put("useMethod", medicine.getUseMethod());
                        testMap.put("dosage", medicine.getDosage());
                        testMap.put("sideEffect", medicine.getSideEffect());
                        testMap.put("price", medicine.getPrice() != null ? medicine.getPrice().toString() : "0.00");
                        
                        medicineInfo = testMap; 
                    } else {
                        System.out.println("药品对象为null");
                        medicineInfo = new HashMap<>();
                        medicineInfo.put("name", "待开具药品");
                        medicineInfo.put("useMethod", "请遵医嘱");
                        medicineInfo.put("dosage", "请遵医嘱");
                        medicineInfo.put("sideEffect", "详见说明书");
                        medicineInfo.put("price", "0.00");
                    }
                } else {
                    System.out.println("取药节点未找到");
                    // 创建默认药品信息
                    medicineInfo = new HashMap<>();
                    medicineInfo.put("name", "节点不存在");
                    medicineInfo.put("useMethod", "未知");
                    medicineInfo.put("dosage", "未知");
                    medicineInfo.put("sideEffect", "未知");
                    medicineInfo.put("price", "0.00");
                }
                System.out.println("返回的medicineInfo: " + medicineInfo);
                System.out.println("========== 取药节点调试 END ==========");
            }

            // 5. 构建返回结果
            System.out.println("5. 构建返回结果");
            result.put("processId", processId);
            result.put("nodeId", nodeId);
            result.put("nodeName", STEP_NAMES[nodeId - 1]);
            result.put("nodeStatus", nodeStatus);
            result.put("diagnosisText", diagnosisText);
            result.put("reminder", reminder);
            result.put("location", locationName);
            result.put("doctorName", doctorName);
            result.put("doctorTitle", doctorTitle);
             // 6. 根据节点类型设置特定信息
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (nodeId == 1) {
                if (createdAt != null) {
                    result.put("createdAt", sdf.format(createdAt));
                } else {
                    result.put("createdAt", "未记录");
                }
                 if (medicalProcess != null) {
                    Patient patient = patientFacade.findById(medicalProcess.getPatient().getPatientId());
                    if (patient != null) {
                        diagnosisText = "患者 " + patient.getUsername() + " 挂号成功";
                    }
                }
                result.put("diagnosisText", diagnosisText);
            }
            
            // 7. 添加药品信息（取药节点）
            if (medicineInfo != null) {
                result.put("medicineInfo", medicineInfo);
                result.put("medicineName", medicineName);
                result.put("useMethod", useMethod);
            }
            
            // 8. 添加图片信息（检查节点）
            if (node != null && node.getPictures() != null) {
                result.put("pictures", node.getPictures());
            } else {
                result.put("pictures", "");
            }
            
            System.out.println("getNodeInfo 完成，结果: " + result);
            
        } catch (Exception e) {
            System.err.println("getNodeInfo 异常: " + e.getMessage());
            e.printStackTrace();
            throw new RemoteException("获取节点信息失败: " + e.getMessage(), e);
        }
        
        return result;
    }
    @Override
    public Map<String, Object> moveToNextNode(int processId, int currentNodeId) throws RemoteException {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 检查是否是最后一个节点
            if (currentNodeId >= STEP_NAMES.length) {
                result.put("success", false);
                result.put("message", "已经是最后一个节点，无法前进");
                return result;
            }
            
            int nextNodeId = currentNodeId + 1;
            
            // 2. 更新当前节点状态为"已完成"
            boolean currentUpdated = updateNodeStatusInternal(processId, currentNodeId, STATUS_COMPLETED, null);
            if (!currentUpdated) {
                result.put("success", false);
                result.put("message", "更新当前节点状态失败");
                return result;
            }
            
            // 3. 更新下一个节点状态为"进行中"
            boolean nextUpdated = updateNodeStatusInternal(processId, nextNodeId, STATUS_IN_PROGRESS, null);
            if (!nextUpdated) {
                result.put("success", false);
                result.put("message", "更新下一个节点状态失败");
                return result;
            }
            
            // 4. 获取下一个节点的详细信息
            Map<String, Object> nextNodeInfo = getNodeInfo(processId, nextNodeId);
            
            // 5. 返回成功结果
            result.put("success", true);
            result.put("message", "已前进到" + STEP_NAMES[nextNodeId - 1] + "节点");
            result.put("nextNodeId", nextNodeId);
            result.put("nextNodeName", STEP_NAMES[nextNodeId - 1]);
            result.put("nextNodeInfo", nextNodeInfo);
            
        } catch (Exception e) {
            throw new RemoteException("前进到下一个节点失败: " + e.getMessage(), e);
        }
        
        return result;
    }
    
    @Override
    public boolean updateNodeContent(int processId, int nodeId, String diagnosisText, String reminder) throws RemoteException {
        try {
            ProcessNode node = getOrCreateProcessNode(processId, nodeId);
            
            if (node != null) {
                if (diagnosisText != null && !diagnosisText.trim().isEmpty()) {
                    node.setDiagnosisText(diagnosisText.trim());
                }
                
                if (reminder != null && !reminder.trim().isEmpty()) {
                    node.setReminder(reminder.trim());
                } else {
                    node.setReminder(getDefaultReminderForNode(nodeId));
                }
                
                node.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                processNodeFacade.update(node);
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            throw new RemoteException("更新节点内容失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean setMedicineForNode(int processId, int medicineId) throws RemoteException {
        try {
            int nodeId = 5;
            ProcessNode node = getOrCreateProcessNode(processId, nodeId);
            
            if (node != null) {
                Medicine medicine = medicineFacade.findById((long)medicineId);
                if (medicine != null) {
                    node.setMedicine(medicine);
                    node.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    
                    String medicineInfo = String.format("药品: %s, 用法: %s, 剂量: %s, 副作用: %s, 价格: %s",
                        medicine.getName(),
                        medicine.getUseMethod() != null ? medicine.getUseMethod() : "无",
                        medicine.getDosage() != null ? medicine.getDosage() : "无",
                        medicine.getSideEffect() != null ? medicine.getSideEffect() : "无",
                        medicine.getPrice() != null ? medicine.getPrice().toString() : "无");
                    
                    node.setDiagnosisText(medicineInfo);
                    processNodeFacade.update(node);
                    return true;
                }
            }
            
            return false;
            
        } catch (Exception e) {
            throw new RemoteException("设置药品信息失败: " + e.getMessage(), e);
        }
    }
    
 // ============== 获取最新医疗进程信息 ==============
    @Override
    public Map<String, Object> getLatestMedicalProcessInfo() throws RemoteException {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 获取最新的医疗进程（按created_at降序取第一条）
            MedicalProcesses latestProcess = getLatestMedicalProcess();
            
            if (latestProcess == null) {
                result.put("success", false);
                result.put("error", "未找到任何就医进程");
                return result;
            }
            
            // 2. 获取进程ID和患者ID
            Long processId = latestProcess.getProcessId();
            Long patientId = latestProcess.getPatient().getPatientId();
            
            // 3. 查询患者姓名
            Patient patient = patientFacade.findById(patientId);
            String patientName = patient != null ? patient.getUsername() : "未知患者";
            
            // 4. 获取当前最新节点
            int currentNodeId = 1;  // 默认值
            ProcessNodeId nodeId1 = new ProcessNodeId(1L, processId);
            for (long i = 1; i <= 6; i++) {
                ProcessNodeId nid = new ProcessNodeId(i, processId);
                ProcessNode node = processNodeFacade.findById(nid);
                if (node != null && Boolean.TRUE.equals(node.getIsLatest())) {
                    currentNodeId = (int) i;
                    break;
                }
            }
            
            // 5. 获取当前节点的详细信息
            Map<String, Object> currentNodeInfo = getNodeInfo(processId.intValue(), currentNodeId);
            
            // 6. 构建返回结果
            result.putAll(currentNodeInfo); // 包含节点信息
            result.put("success", true);
            result.put("patientId", patientId);
            result.put("patientName", patientName);
            result.put("currentNodeId", currentNodeId); // 固定为1
            result.put("message", patientName + "的看病进程");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "获取最新就医进程信息失败: " + e.getMessage());
            LogUtil.log("getLatestMedicalProcessInfo失败: " + e.getMessage(), Level.SEVERE, e);
        }
        
        return result;
    }

    // ============== 根据患者ID获取最新医疗进程信息 ==============
    @Override
    public Map<String, Object> getLatestMedicalProcessInfoByPatientId(Long patientId) throws RemoteException {
        Map<String, Object> result = new HashMap<>();

        try {
            if (patientId == null) {
                result.put("success", false);
                result.put("error", "患者ID不能为空");
                return result;
            }

            LogUtil.log("开始获取患者ID=" + patientId + "的最新医疗进程", Level.INFO, null);

            // 1. 获取该患者的所有医疗进程
            List<MedicalProcesses> allProcesses = medicalProcessesFacade.findAll();
            MedicalProcesses latestProcess = null;

            // 筛选该患者的进程并找到最新的一条
            for (MedicalProcesses process : allProcesses) {
                if (process.getPatient() != null &&
                    patientId.equals(process.getPatient().getPatientId())) {
                    if (latestProcess == null ||
                        (process.getCreatedAt() != null &&
                         (latestProcess.getCreatedAt() == null ||
                          process.getCreatedAt().after(latestProcess.getCreatedAt())))) {
                        latestProcess = process;
                    }
                }
            }

            if (latestProcess == null) {
                result.put("success", false);
                result.put("error", "该患者暂无就医进程");
                return result;
            }

            // 2. 获取进程ID
            Long processId = latestProcess.getProcessId();

            // 3. 查询患者姓名
            Patient patient = patientFacade.findById(patientId);
            String patientName = patient != null ? patient.getUsername() : "未知患者";

            // 4. 获取当前最新节点
            int currentNodeId = 1;  // 默认值
            for (long i = 1; i <= 6; i++) {
                ProcessNodeId nid = new ProcessNodeId(i, processId);
                ProcessNode node = processNodeFacade.findById(nid);
                if (node != null && Boolean.TRUE.equals(node.getIsLatest())) {
                    currentNodeId = (int) i;
                    break;
                }
            }

            // 5. 获取当前节点的详细信息
            Map<String, Object> currentNodeInfo = getNodeInfo(processId.intValue(), currentNodeId);

            // 6. 构建返回结果
            result.putAll(currentNodeInfo);
            result.put("success", true);
            result.put("processId", processId);
            result.put("patientId", patientId);
            result.put("patientName", patientName);
            result.put("currentNodeId", currentNodeId);
            result.put("message", patientName + "的看病进程");

            LogUtil.log("成功获取患者ID=" + patientId + "的最新医疗进程，processId=" + processId, Level.INFO, null);

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "获取最新就医进程信息失败: " + e.getMessage());
            LogUtil.log("getLatestMedicalProcessInfoByPatientId失败: " + e.getMessage(), Level.SEVERE, e);
        }

        return result;
    }


    private String getNodeStatusFromDB(int processId, int nodeId) {
        try {
            ProcessNode node = getProcessNode(processId, nodeId);
            
            if (node != null && node.getNodeStatus() != null) {
                return node.getNodeStatus();
            }
            
            // 如果节点不存在，根据节点ID返回默认状态
            // 挂号节点默认"进行中"，其他节点默认"待完成"
            if (nodeId == 1) {
                return STATUS_IN_PROGRESS;
            } else {
                return STATUS_PENDING;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return nodeId == 1 ? STATUS_IN_PROGRESS : STATUS_PENDING;
        }
    }
    
    private ProcessNode getOrCreateProcessNode(int processId, int nodeId) {
        try {
            ProcessNodeId id = new ProcessNodeId((long)nodeId, (long)processId);
            ProcessNode node = processNodeFacade.findById(id);
            
            if (node == null) {
                node = createNewProcessNode(processId, nodeId);
            }
            
            return node;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private ProcessNode getProcessNode(int processId, int nodeId) {
        try {
            ProcessNodeId id = new ProcessNodeId((long)nodeId, (long)processId);
            return processNodeFacade.findById(id);
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private ProcessNode createNewProcessNode(int processId, int nodeId) {
        try {
            MedicalProcesses medicalProcess = medicalProcessesFacade.findById((long)processId);
            if (medicalProcess == null) {
                return null;
            }
            
            ProcessNodeId id = new ProcessNodeId((long)nodeId, (long)processId);
            
            ProcessNode newNode = new ProcessNode();
            newNode.setId(id);
            newNode.setMedicalProcesses(medicalProcess);
            newNode.setNodeName(STEP_NAMES[nodeId - 1]);
            newNode.setNodeStatus(nodeId == 1 ? STATUS_IN_PROGRESS : STATUS_PENDING);
            newNode.setReminder(getDefaultReminderForNode(nodeId));
            newNode.setCreateAt(new Timestamp(System.currentTimeMillis()));
            newNode.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            newNode.setIsLatest(nodeId == 1);
            
            processNodeFacade.save(newNode);
            return newNode;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private boolean updateNodeStatusInternal(int processId, int nodeId, String status, String diagnosisText) {
        try {
            ProcessNode node = getOrCreateProcessNode(processId, nodeId);
            
            if (node != null) {
                node.setNodeStatus(status);
                
                if (diagnosisText != null && !diagnosisText.trim().isEmpty()) {
                    node.setDiagnosisText(diagnosisText.trim());
                }
                
                node.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                processNodeFacade.update(node);
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private String getDefaultReminderForNode(int nodeId) {
        switch (nodeId) {
            case 1: return "请完成挂号流程，准备好医保卡和身份证";
            case 2: return "请向医生详细描述症状，按时服药";
            case 3: return "请按照医生指示完成检查，检查前请空腹";
            case 4: return "请按时进行治疗，注意休息和饮食";
            case 5: return "请取药，仔细阅读药品说明书，按时服药";
            case 6: return "看病流程已完成，如有不适请及时复诊";
            default: return "请完成此步骤";
        }
    }
    
    private Map<String, Object> convertMedicineToMap(Medicine medicine) {
        Map<String, Object> medicineMap = new HashMap<>();
        
        if (medicine == null) {
            System.out.println("警告：convertMedicineToMap接收到null药品对象");
            return createDefaultMedicineInfo("药品信息为空");
        }
        
        try {
            System.out.println("=== 开始转换药品信息 ===");
            System.out.println("药品ID: " + medicine.getMedicineId());
            
            // 安全获取每个字段
            Long medicineId = medicine.getMedicineId();
            String name = medicine.getName();
            String useMethod = medicine.getUseMethod();
            String dosage = medicine.getDosage();
            String sideEffect = medicine.getSideEffect();
            Double price = medicine.getPrice(); // 注意：这是Double类型
            
            System.out.println("原始字段值:");
            System.out.println("  name: " + name);
            System.out.println("  useMethod: " + useMethod);
            System.out.println("  dosage: " + dosage);
            System.out.println("  sideEffect: " + sideEffect);
            System.out.println("  price (原始): " + price + " (类型: " + (price != null ? price.getClass().getName() : "null") + ")");
            
            // 设置字段，确保不为null
            medicineMap.put("medicineId", medicineId != null ? medicineId : 0);
            medicineMap.put("name", name != null ? name : "未知药品");
            medicineMap.put("useMethod", useMethod != null ? useMethod : "请遵医嘱");
            medicineMap.put("dosage", dosage != null ? dosage : "请遵医嘱");
            medicineMap.put("sideEffect", sideEffect != null ? sideEffect : "详见说明书");
            
            // 处理价格：将Double转换为字符串，保留两位小数
            if (price != null) {
                String priceStr = String.format("%.2f", price);
                medicineMap.put("price", priceStr);
                System.out.println("  价格(转换后): " + priceStr);
            } else {
                medicineMap.put("price", "0.00");
                System.out.println("  价格: null，使用默认值 0.00");
            }
            
            System.out.println("转换完成: " + medicineMap);
            
        } catch (Exception e) {
            System.err.println("转换药品信息异常: " + e.getMessage());
            e.printStackTrace();
            
            // 返回默认药品信息
            medicineMap.put("error", "转换失败: " + e.getMessage());
            medicineMap.put("name", "数据异常");
            medicineMap.put("useMethod", "未知");
            medicineMap.put("dosage", "未知");
            medicineMap.put("sideEffect", "未知");
            medicineMap.put("price", "0.00");
        }
        
        return medicineMap;
    }
}
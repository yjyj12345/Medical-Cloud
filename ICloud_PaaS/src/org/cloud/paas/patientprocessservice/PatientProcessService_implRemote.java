package org.cloud.paas.patientprocessservice;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;


/**
 * 患者就医进程远程接口
 */

public interface PatientProcessService_implRemote extends java.rmi.Remote {
    
    /**
     * 根据ProcessID和步骤号获取流程节点信息
     * @param processId 流程ID
     * @param step 步骤号（1=挂号，2=问诊，3=检查，4=治疗，5=取药，6=完成）
     * @return 流程节点信息
     * @throws RemoteException 远程调用异常
     */
    Map<String, Object> getProcessNodeByStep(int processId, int step) throws RemoteException;
    
    /**
     * 根据ProcessID和节点名称获取流程节点信息
     * @param processId 流程ID
     * @param nodeName 节点名称（挂号、问诊、检查、治疗、取药、完成）
     * @return 流程节点信息
     * @throws RemoteException 远程调用异常
     */
    Map<String, Object> getProcessNodeByName(int processId, String nodeName) throws RemoteException;
    Map<String, Object> getLatestMedicalProcessInfo() throws RemoteException;

    /**
     * 根据患者ID获取最新的医疗进程信息
     * @param patientId 患者ID
     * @return 最新的医疗进程信息
     * @throws RemoteException 远程调用异常
     */
    Map<String, Object> getLatestMedicalProcessInfoByPatientId(Long patientId) throws RemoteException;
    /**
     * 获取指定ProcessID的所有节点信息
     * @param processId 流程ID
     * @return 节点信息列表
     * @throws RemoteException 远程调用异常
     */
    List<Map<String, Object>> getAllProcessNodes(int processId) throws RemoteException;
    
    /**
     * 获取患者ID为1的所有就医流程
     * @return 就医流程列表
     * @throws RemoteException 远程调用异常
     */
    List<Map<String, Object>> getMedicalProcessesForPatient1() throws RemoteException;
    
    /**
     * 更新节点状态和诊断文本
     * @param processId 流程ID
     * @param step 步骤号
     * @param nodeStatus 节点状态
     * @param diagnosisText 诊断文本
     * @return 是否更新成功
     * @throws RemoteException 远程调用异常
     */
    boolean updateNodeStatus(int processId, int step, String nodeStatus, String diagnosisText) throws RemoteException;
    
    /**
     * 根据药品ID获取药品信息
     * @param medicineId 药品ID
     * @return 药品信息
     * @throws RemoteException 远程调用异常
     */
    Map<String, Object> getMedicineInfo(int medicineId) throws RemoteException;
    
    /**
     * 根据药品名称搜索药品信息
     * @param medicineName 药品名称
     * @return 药品信息列表
     * @throws RemoteException 远程调用异常
     */
    List<Map<String, Object>> searchMedicineByName(String medicineName) throws RemoteException;
    
    /**
     * 测试RMI连接
     * @return 连接状态消息
     * @throws RemoteException 远程调用异常
     */
    String testConnection() throws RemoteException;
    
 
    Map<String, Object> getNodeInfo(int processId, int nodeId) throws RemoteException;
    
    /**
     * @param processId 流程ID
     * @param currentNodeId 当前节点ID
     * @return 下一个节点信息
     * @throws RemoteException 远程调用异常
     */
    Map<String, Object> moveToNextNode(int processId, int currentNodeId) throws RemoteException;
    
    /**
     * 更新节点内容（诊断、提醒等信息）
     * @param processId 流程ID
     * @param nodeId 节点ID
     * @param diagnosisText 诊断文本
     * @param reminder 提醒信息
     * @return 是否更新成功
     * @throws RemoteException 远程调用异常
     */
    boolean updateNodeContent(int processId, int nodeId, String diagnosisText, String reminder) throws RemoteException;
    
    /**
     * 为取药节点设置药品信息
     * @param processId 流程ID
     * @param medicineId 药品ID
     * @return 是否设置成功
     * @throws RemoteException 远程调用异常
     */
    boolean setMedicineForNode(int processId, int medicineId) throws RemoteException;
}
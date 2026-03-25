package org.cloud.paas.medicalhistoryservice;

import javax.ejb.Remote;
import java.util.List;
import java.util.Map;

/**
 * 病历查询远程接口
 */
@Remote
public interface MedicalHistory_implRemote {

    /**
     * 根据用户名查询患者ID
     * @param username 登录用户名
     * @return 患者ID（null表示用户名不存在）
     */
	Long getPatientIdByUsername(String username);

    /**
     * 根据患者ID查询所有看病历史
     * @param patientId 患者ID
     * @return 诊疗进程列表
     */
    List<Map<String, Object>> getMedicalHistoryByPatientId(Long patientId);
}
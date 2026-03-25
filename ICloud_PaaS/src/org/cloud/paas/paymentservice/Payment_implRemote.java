package org.cloud.paas.paymentservice;

import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

/**
 * 付款远程接口
 */
@Remote
public interface Payment_implRemote {
	
    /**
     * 根据用户名查询患者ID
     * @param username 登录用户名
     * @return 患者ID（null表示用户名不存在）
     */
	Long getPatientIdByUsername(String username);
	
    /**
     * 根据患者ID查询病历
     * @param patientId 患者ID
     * @return 诊疗进程列表
     */
	List<Map<String, Object>> getPaymentByPatientId(Long patientId);
    
    boolean PayByOrderIDs(String orderIdLst);

}

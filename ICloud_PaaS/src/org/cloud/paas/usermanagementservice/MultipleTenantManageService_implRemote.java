package org.cloud.paas.usermanagementservice;

import javax.ejb.Remote;

@Remote
public interface MultipleTenantManageService_implRemote {
    
    // 患者相关
    boolean patientRegister(String username, String password, String phoneNum, String gender);
    boolean patientLogin(String username, String password);
    Long getPatientIdByUsername(String username);
    
    // 患者信息管理
    String getPatientInfo(Long patientId);
    boolean updatePatientInfo(Long patientId, String username, String phoneNum, String gender);
    boolean updatePatientPassword(Long patientId, String oldPassword, String newPassword);
    String getPatientStats(Long patientId);
    
    // 医生相关
    boolean doctorRegister(String username, String password, String phoneNum, String gender, String title, String specialty, Long organizationId);
    boolean doctorLogin(String username, String password);
    Long getDoctorIdByUsername(String username);
    String getDoctorInfo(Long doctorId);
}


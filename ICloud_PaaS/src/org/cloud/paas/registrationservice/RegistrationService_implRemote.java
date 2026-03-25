package org.cloud.paas.registrationservice;

import java.util.List;
import java.util.Map;

public interface RegistrationService_implRemote {

    /**
     * 获取所有科室列表
     */
    List<String> getAllSpecialties();
    
    /**
     * 根据科室获取医生列表
     */
    List<Map<String, Object>> getDoctorsBySpecialty(String specialty);
    
    /**
     * 获取所有医院/机构列表
     */
    List<Map<String, Object>> getAllOrganizations();
    
    /**
     * 创建预约挂号（创建MedicalProcesses）
     */
    Map<String, Object> createAppointment(Long patientId, Long doctorId, Long organizationId, String appointmentDate);
    
    /**
     * 获取患者的预约列表
     */
    List<Map<String, Object>> getPatientAppointments(Long patientId);
    
    /**
     * 取消预约
     */
    boolean cancelAppointment(Long processId);
}


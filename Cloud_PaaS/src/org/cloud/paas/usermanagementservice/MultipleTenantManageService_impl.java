package org.cloud.paas.usermanagementservice;
import java.util.List;
import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jpa.Patient;
import org.jpa.PatientFacadeLocal;
import org.jpa.Doctor;
import org.jpa.DoctorFacadeLocal;

@Stateless
public class MultipleTenantManageService_impl implements MultipleTenantManageService_implRemote {

	@EJB
    private PatientFacadeLocal patientFacade;
    
    @EJB
    private DoctorFacadeLocal doctorFacade;
    
    @PersistenceContext(unitName = "Cloud_PaaS")
    private EntityManager em;

    // 生成唯一PatientId
	private Long generatePatientId() {
        List<Patient> allPatients = patientFacade.findAll();
        Long maxId = 0L;
        for (Patient p : allPatients) {
            if (p.getPatientId() > maxId) {
                maxId = p.getPatientId();
            }
        }
        return maxId + 1;
    }

    /**
     * 患者注册实现
     * @param username 用户名
     * @param password 密码
     * @param phoneNum 手机号（可选）
     * @param gender 性别（必填）
     * @return 注册成功返回true，失败返回false
     */
	@Override
    public boolean patientRegister(String username, String password, String phoneNum, String gender) {
        // 1. 参数校验
        if (username == null || password == null || phoneNum == null || gender == null) {
            return false;
        }

        // 2. 检查用户名是否已存在
        List<Patient> existPatients = patientFacade.findByUsername(username);
        if (!existPatients.isEmpty()) {
            return false;
        }

        // 3. 创建Patient实体
        Patient newPatient = new Patient();
        newPatient.setPatientId(generatePatientId());
        newPatient.setUsername(username);
        newPatient.setPassword(password);
        newPatient.setPhoneNum(phoneNum);
        newPatient.setGender(gender);

        // 4. 保存实体
        try {
            patientFacade.save(newPatient);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 患者登录实现
     */
    @Override
    public boolean patientLogin(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        List<Patient> patients = patientFacade.findByUsername(username);
        if (patients.isEmpty()) {
            return false;
        }
        Patient patient = patients.get(0);
        return password.equals(patient.getPassword());
    }
    
    /**
     * 根据用户名获取患者ID
     */
    @Override
    public Long getPatientIdByUsername(String username) {
        if (username == null) return null;
        List<Patient> patients = patientFacade.findByUsername(username);
        if (patients.isEmpty()) return null;
        return patients.get(0).getPatientId();
    }
    
    // ==================== 医生相关方法 ====================
    
    private Long generateDoctorId() {
        List<Doctor> allDoctors = doctorFacade.findAll();
        Long maxId = 0L;
        for (Doctor d : allDoctors) {
            if (d.getDoctorId() > maxId) {
                maxId = d.getDoctorId();
            }
        }
        return maxId + 1;
    }
    
    /**
     * 医生注册实现
     */
    @Override
    public boolean doctorRegister(String username, String password, String phoneNum, String gender,
                                  String title, String specialty, Long organizationId) {
        if (username == null || password == null || phoneNum == null || gender == null) {
            return false;
        }
        
        // 检查用户名是否已存在
        List<Doctor> existDoctors = doctorFacade.findByUsername(username);
        if (!existDoctors.isEmpty()) {
            return false;
        }
        
        // 创建Doctor实体
        Doctor newDoctor = new Doctor();
        newDoctor.setDoctorId(generateDoctorId());
        newDoctor.setUsername(username);
        newDoctor.setPassword(password);
        newDoctor.setPhoneNum(phoneNum);
        newDoctor.setGender(gender);
        newDoctor.setTitle(title != null ? title : "医师");
        newDoctor.setSpecialty(specialty != null ? specialty : "全科");
        newDoctor.setOrganizationId(organizationId != null ? organizationId : 1L);
        
        try {
            doctorFacade.save(newDoctor);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 医生登录实现
     */
    @Override
    public boolean doctorLogin(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        List<Doctor> doctors = doctorFacade.findByUsername(username);
        if (doctors.isEmpty()) {
            return false;
        }
        Doctor doctor = doctors.get(0);
        return password.equals(doctor.getPassword());
    }
    
    /**
     * 根据用户名获取医生ID
     */
    @Override
    public Long getDoctorIdByUsername(String username) {
        if (username == null) return null;
        List<Doctor> doctors = doctorFacade.findByUsername(username);
        if (doctors.isEmpty()) return null;
        return doctors.get(0).getDoctorId();
    }
    
    /**
     * 获取医生信息（返回JSON格式字符串）
     */
    @Override
    public String getDoctorInfo(Long doctorId) {
        if (doctorId == null) return null;
        Doctor doctor = doctorFacade.findById(doctorId);
        if (doctor == null) return null;
        
        // 构建JSON字符串
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"doctorId\":").append(doctor.getDoctorId()).append(",");
        json.append("\"username\":\"").append(escapeJson(doctor.getUsername())).append("\",");
        json.append("\"phoneNum\":\"").append(escapeJson(doctor.getPhoneNum() != null ? doctor.getPhoneNum() : "")).append("\",");
        json.append("\"gender\":\"").append(escapeJson(doctor.getGender() != null ? doctor.getGender() : "")).append("\",");
        json.append("\"title\":\"").append(escapeJson(doctor.getTitle() != null ? doctor.getTitle() : "")).append("\",");
        json.append("\"specialty\":\"").append(escapeJson(doctor.getSpecialty() != null ? doctor.getSpecialty() : "")).append("\",");
        json.append("\"organizationId\":").append(doctor.getOrganizationId() != null ? doctor.getOrganizationId() : "null");
        json.append("}");
        return json.toString();
    }
    
    // ==================== 患者信息管理 ====================
    
    /**
     * 获取患者信息（返回JSON格式字符串）
     */
    @Override
    public String getPatientInfo(Long patientId) {
        if (patientId == null) return null;
        Patient patient = patientFacade.findById(patientId);
        if (patient == null) return null;
        
        // 构建JSON字符串
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"patientId\":").append(patient.getPatientId()).append(",");
        json.append("\"username\":\"").append(escapeJson(patient.getUsername())).append("\",");
        json.append("\"phoneNum\":\"").append(escapeJson(patient.getPhoneNum() != null ? patient.getPhoneNum() : "")).append("\",");
        json.append("\"gender\":\"").append(escapeJson(patient.getGender() != null ? patient.getGender() : "")).append("\"");
        json.append("}");
        return json.toString();
    }
    
    /**
     * 更新患者基本信息
     */
    @Override
    public boolean updatePatientInfo(Long patientId, String username, String phoneNum, String gender) {
        if (patientId == null || username == null || username.trim().isEmpty()) {
            return false;
        }
        
        Patient patient = patientFacade.findById(patientId);
        if (patient == null) {
            return false;
        }
        
        // 检查用户名是否被其他人使用
        List<Patient> existPatients = patientFacade.findByUsername(username);
        for (Patient p : existPatients) {
            if (!p.getPatientId().equals(patientId)) {
                return false; // 用户名已被其他人使用
            }
        }
        
        patient.setUsername(username);
        patient.setPhoneNum(phoneNum);
        patient.setGender(gender);
        
        try {
            patientFacade.update(patient);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 修改患者密码
     */
    @Override
    public boolean updatePatientPassword(Long patientId, String oldPassword, String newPassword) {
        if (patientId == null || oldPassword == null || newPassword == null) {
            return false;
        }
        if (newPassword.trim().isEmpty() || newPassword.length() < 6) {
            return false; // 密码至少6位
        }
        
        Patient patient = patientFacade.findById(patientId);
        if (patient == null) {
            return false;
        }
        
        // 验证旧密码
        if (!oldPassword.equals(patient.getPassword())) {
            return false;
        }
        
        patient.setPassword(newPassword);
        
        try {
            patientFacade.update(patient);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 转义JSON特殊字符
     */
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    /**
     * 获取患者统计数据（就诊次数、待缴费用）
     */
    @Override
    public String getPatientStats(Long patientId) {
        if (patientId == null) return null;
        
        int visitCount = 0;
        double pendingFee = 0.0;
        
        try {
            // 查询就诊次数（不包含已取消的）
            String visitSql = "SELECT COUNT(*) FROM medical_processes WHERE PatientID = ?1 AND process_status != '已取消'";
            Query visitQuery = em.createNativeQuery(visitSql);
            visitQuery.setParameter(1, patientId);
            Object visitResult = visitQuery.getSingleResult();
            if (visitResult != null) {
                visitCount = ((Number) visitResult).intValue();
            }
            
            // 查询待缴费用（状态为 未支付 的订单总金额）
            // payment 表通过 process_node 关联到 medical_processes 获取 PatientID
            String feeSql = "SELECT COALESCE(SUM(p.amount), 0) FROM payment p " +
                           "JOIN process_node pn ON p.NodeID = pn.NodeID AND p.ProcessID = pn.ProcessID " +
                           "JOIN medical_processes mp ON pn.ProcessID = mp.ProcessID " +
                           "WHERE mp.PatientID = ?1 AND p.order_status = '未支付'";
            Query feeQuery = em.createNativeQuery(feeSql);
            feeQuery.setParameter(1, patientId);
            Object feeResult = feeQuery.getSingleResult();
            if (feeResult != null) {
                if (feeResult instanceof BigDecimal) {
                    pendingFee = ((BigDecimal) feeResult).doubleValue();
                } else {
                    pendingFee = ((Number) feeResult).doubleValue();
                }
            }
        } catch (Exception e) {
            System.out.println("查询患者统计数据失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 构建JSON
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"visitCount\":").append(visitCount).append(",");
        json.append("\"pendingFee\":").append(String.format("%.2f", pendingFee));
        json.append("}");
        return json.toString();
    }
}

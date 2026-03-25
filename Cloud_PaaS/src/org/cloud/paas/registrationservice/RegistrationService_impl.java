package org.cloud.paas.registrationservice;

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
import org.jpa.Doctor;
import org.jpa.DoctorFacadeLocal;
import org.jpa.Organization;
import org.jpa.OrganizationFacadeLocal;

@Stateless(name = "RegistrationService_impl")
@Remote(RegistrationService_implRemote.class)
public class RegistrationService_impl implements RegistrationService_implRemote {

    @PersistenceContext
    private EntityManager entityManager;

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
    
    @EJB
    private DoctorFacadeLocal doctorFacade;
    
    @EJB
    private OrganizationFacadeLocal organizationFacade;
    
    private static final int NODE_ID_REGISTER = 1;

    @Override
    public List<String> getAllSpecialties() {
        try {
            System.out.println("获取所有科室列表...");
            String jpql = "SELECT DISTINCT d.specialty FROM Doctor d WHERE d.specialty IS NOT NULL ORDER BY d.specialty";
            Query query = entityManager.createQuery(jpql);
            
            @SuppressWarnings("unchecked")
            List<String> specialties = query.getResultList();
            
            System.out.println("获取科室列表成功，数量: " + specialties.size());
            return specialties;
            
        } catch (Exception e) {
            System.out.println("获取科室列表失败: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Map<String, Object>> getDoctorsBySpecialty(String specialty) {
        try {
            System.out.println("根据科室获取医生列表: " + specialty);
            List<Doctor> doctors;
            
            if (specialty == null || specialty.isEmpty() || "all".equals(specialty)) {
                doctors = doctorFacade.findAll();
            } else {
                doctors = doctorFacade.findBySpecialty(specialty);
            }
            
            List<Map<String, Object>> result = new ArrayList<>();
            for (Doctor doctor : doctors) {
                Map<String, Object> doctorMap = new HashMap<>();
                doctorMap.put("doctorId", doctor.getDoctorId());
                doctorMap.put("username", doctor.getUsername());
                doctorMap.put("gender", doctor.getGender());
                doctorMap.put("title", doctor.getTitle());
                doctorMap.put("specialty", doctor.getSpecialty());
                doctorMap.put("organizationId", doctor.getOrganizationId());
                result.add(doctorMap);
            }
            
            System.out.println("获取医生列表成功，数量: " + result.size());
            return result;
            
        } catch (Exception e) {
            System.out.println("获取医生列表失败: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Map<String, Object>> getAllOrganizations() {
        try {
            System.out.println("获取所有机构列表...");
            List<Organization> organizations = organizationFacade.findAll();
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (Organization org : organizations) {
                Map<String, Object> orgMap = new HashMap<>();
                orgMap.put("organizationId", org.getOrganizationId());
                orgMap.put("organizationName", org.getOrganizationName());
                orgMap.put("organizationType", org.getOrganizationType());
                orgMap.put("status", org.getStatus());
                result.add(orgMap);
            }
            
            System.out.println("获取机构列表成功，数量: " + result.size());
            return result;
            
        } catch (Exception e) {
            System.out.println("获取机构列表失败: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public Map<String, Object> createAppointment(Long patientId, Long doctorId, Long organizationId, String appointmentDate) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            System.out.println("创建预约挂号: patientId=" + patientId + ", doctorId=" + doctorId);
            
            // 1. 获取患者
            Patient patient = patientFacade.findById(patientId);
            if (patient == null) {
                result.put("success", false);
                result.put("message", "患者不存在");
                return result;
            }
            
            // 2. 获取医生
            Doctor doctor = doctorFacade.findById(doctorId);
            if (doctor == null) {
                result.put("success", false);
                result.put("message", "医生不存在");
                return result;
            }
            
            // 3. 获取机构
            Organization organization = organizationFacade.findById(organizationId);
            if (organization == null) {
                result.put("success", false);
                result.put("message", "医疗机构不存在");
                return result;
            }
            
            // 4. 生成流程ID
            Long processId = generateProcessId();
            Timestamp now = new Timestamp(System.currentTimeMillis());
            
            // 5. 创建MedicalProcesses
            MedicalProcesses process = new MedicalProcesses();
            process.setProcessId(processId);
            process.setPatient(patient);
            process.setDoctor(doctor);
            process.setOrganization(organization);
            process.setProcessStatus("已预约");
            process.setCreatedAt(now);
            process.setUpdatedAt(now);
            
            medicalProcessesFacade.save(process);
            
            // 6. 创建挂号节点
            ProcessNodeId nodeId = new ProcessNodeId((long) NODE_ID_REGISTER, processId);
            ProcessNode registerNode = new ProcessNode();
            registerNode.setId(nodeId);
            registerNode.setMedicalProcesses(process);
            registerNode.setNodeName("挂号");
            registerNode.setNodeStatus("进行中");
            registerNode.setIsLatest(true);
            registerNode.setReminder("请于 " + appointmentDate + " 到医院就诊");
            registerNode.setCreateAt(now);
            registerNode.setUpdatedAt(now);
            
            processNodeFacade.save(registerNode);
            
            // 7. 创建其他节点（待完成状态）
            String[] nodeNames = {"问诊", "检查", "治疗", "取药", "完成"};
            for (int i = 2; i <= 6; i++) {
                ProcessNodeId nId = new ProcessNodeId((long) i, processId);
                ProcessNode node = new ProcessNode();
                node.setId(nId);
                node.setMedicalProcesses(process);
                node.setNodeName(nodeNames[i - 2]);
                node.setNodeStatus("待完成");
                node.setIsLatest(false);
                node.setCreateAt(now);
                node.setUpdatedAt(now);
                processNodeFacade.save(node);
            }
            
            System.out.println("预约挂号成功，流程ID: " + processId);
            
            result.put("success", true);
            result.put("processId", processId);
            result.put("message", "预约成功");
            result.put("doctorName", doctor.getUsername());
            result.put("specialty", doctor.getSpecialty());
            result.put("appointmentDate", appointmentDate);
            
            return result;
            
        } catch (Exception e) {
            System.out.println("创建预约失败: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "预约失败: " + e.getMessage());
            return result;
        }
    }
    
    @Override
    public List<Map<String, Object>> getPatientAppointments(Long patientId) {
        try {
            System.out.println("获取患者预约列表: " + patientId);
            
            String sql = "SELECT mp.ProcessID, mp.process_status, mp.created_at, " +
                        "d.username as doctorName, d.specialty, d.title, " +
                        "o.OrganizationName " +
                        "FROM medical_processes mp " +
                        "JOIN doctor d ON mp.DoctorID = d.DoctorID " +
                        "JOIN organization o ON mp.OrganizationID = o.OrganizationID " +
                        "WHERE mp.PatientID = ? AND mp.process_status != '已取消' " +
                        "ORDER BY mp.created_at DESC";
            
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, patientId);
            
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();
            
            List<Map<String, Object>> appointments = new ArrayList<>();
            for (Object[] row : results) {
                Map<String, Object> appointment = new HashMap<>();
                appointment.put("processId", ((Number) row[0]).longValue());
                appointment.put("status", row[1]);
                appointment.put("createdAt", row[2]);
                appointment.put("doctorName", row[3]);
                appointment.put("specialty", row[4]);
                appointment.put("title", row[5]);
                appointment.put("organizationName", row[6]);
                appointments.add(appointment);
            }
            
            System.out.println("获取预约列表成功，数量: " + appointments.size());
            return appointments;
            
        } catch (Exception e) {
            System.out.println("获取预约列表失败: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean cancelAppointment(Long processId) {
        try {
            System.out.println("取消预约: " + processId);
            
            MedicalProcesses process = medicalProcessesFacade.findById(processId);
            if (process == null) {
                System.out.println("预约不存在: " + processId);
                return false;
            }
            
            // 只有已预约状态才能取消
            if (!"已预约".equals(process.getProcessStatus())) {
                System.out.println("当前状态不可取消: " + process.getProcessStatus());
                return false;
            }
            
            process.setProcessStatus("已取消");
            process.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            process.setCompletedAt(new Timestamp(System.currentTimeMillis()));
            
            medicalProcessesFacade.update(process);
            
            System.out.println("取消预约成功: " + processId);
            return true;
            
        } catch (Exception e) {
            System.out.println("取消预约失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
  
    private Long generateProcessId() {
        try {
            String jpql = "SELECT MAX(m.processId) FROM MedicalProcesses m";
            Query query = entityManager.createQuery(jpql);
            Object result = query.getSingleResult();
            if (result != null) {
                return ((Number) result).longValue() + 1;
            }
            return 1L;
        } catch (Exception e) {
            System.out.println("生成流程ID失败: " + e.getMessage());
            return System.currentTimeMillis();
        }
    }
}
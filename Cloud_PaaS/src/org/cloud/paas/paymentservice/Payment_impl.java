package org.cloud.paas.paymentservice;

import org.jpa.MedicalProcesses;
import org.jpa.MedicalProcessesFacadeLocal;
import org.jpa.Patient;
import org.jpa.PatientFacadeLocal;
import org.jpa.Payment;
import org.jpa.PaymentFacadeLocal;
import org.jpa.ProcessNode;
import java.util.HashMap;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.ejb.Remote;    
import java.util.Map;


/**
 * 病历查询接口实现类
 */
@Stateless
// 关联远程接口
public class Payment_impl implements Payment_implRemote {

    // 注入现有Facade
    @EJB
    private PatientFacadeLocal patientFacade;
    @EJB
    private MedicalProcessesFacadeLocal medicalProcessesFacade;
    @EJB
    private PaymentFacadeLocal paymentFacade;

    /**
     * 根据用户名查询PatientID
     */
    @Override
    public Long getPatientIdByUsername(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }

        List<Patient> patients = patientFacade.findByUsername(username);
        if (patients == null || patients.isEmpty()) {
            return null; // 用户名不存在
        }

        return patients.get(0).getPatientId();
    }

    /**
     * 根据PatientID查询完整看病历史
     */
    @Override
    public List<Map<String, Object>> getPaymentByPatientId(Long patientId) {

        System.out.println("===== 开始查询付款信息 =====");
        System.out.println("传入的patientId：" + patientId);

        if (patientId == null) {
            System.out.println("【错误】patientId为空，返回空列表");
            return new ArrayList<>();
        }

        // 通过 Patient -> MedicalProcesses -> ProcessNode -> Payment 关联链查询
        Patient patient = patientFacade.findById(patientId);
        if (patient == null) {
            System.out.println("【提示】患者" + patientId + "不存在");
            return new ArrayList<>();
        }
        Set<MedicalProcesses> medicalProcessesSet = patient.getMedicalProcesseses();
        
        List<Map<String, Object>> paymentList = new ArrayList<>();
        
        if (medicalProcessesSet != null) {
            for (MedicalProcesses mp : medicalProcessesSet) {
                Set<ProcessNode> processNodes = mp.getProcessNodes();
                if (processNodes != null) {
                    for (ProcessNode pn : processNodes) {
                        Set<Payment> payments = pn.getPayments();
                        if (payments != null) {
                            for (Payment payment : payments) {
                                System.out.println("解析付款数据：orderId=" + payment.getOrderId() + "，状态=" + payment.getOrderStatus());
                                
                                Map<String, Object> processMap = new HashMap<>();
                                processMap.put("orderId", payment.getOrderId());
                                processMap.put("nodeId", payment.getProcessNode().getId().getNodeId());
                                processMap.put("processId", payment.getProcessNode().getId().getProcessId());
                                processMap.put("orderContent", payment.getOrderContent());
                                processMap.put("amount", payment.getAmount());
                                processMap.put("orderStatus", payment.getOrderStatus());
                                processMap.put("createAt", formatTimestamp(payment.getCreateAt()));
                                processMap.put("paidAt", formatTimestamp(payment.getPaidAt()));

                                paymentList.add(processMap);
                            }
                        }
                    }
                }
            }
        }

        System.out.println("===== 查询完成 =====");
        System.out.println("最终返回的付款记录列表数量：" + paymentList.size());
        return paymentList;
    }
    
    public boolean PayByOrderIDs(String orderIdLst) {
        
        if (orderIdLst == null || orderIdLst.trim().length() == 0) {
            return true;
        }
        
        String[] parts = orderIdLst.split(",");
        for (String part : parts) {
            String id = part.trim();
            if (id.length() > 0) {
                Long orderid = Long.parseLong(id);
                // 使用现有的 findById 和 update 方法
                Payment payment = paymentFacade.findById(orderid);
                if (payment != null) {
                    payment.setOrderStatus("已支付");
                    payment.setPaidAt(new Timestamp(System.currentTimeMillis()));
                    paymentFacade.update(payment);
                }
            }
        }
    	return true;
    }

    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        return timestamp.toString().replace(" ", " "); // 保留默认格式，或自定义为"yyyy-MM-dd HH:mm:ss"
    }
}
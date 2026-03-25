package util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

import org.cloud.paas.usermanagementservice.MultipleTenantManageService_implRemote;
import org.cloud.paas.medicalhistoryservice.MedicalHistory_implRemote;
import org.cloud.paas.paymentservice.Payment_implRemote;
import org.cloud.paas.diseasemanagementservice.DiseaseManageService_implRemote;
import org.cloud.paas.organizationmanagementservice.MultiOrganizationManageService_implRemote;
import org.cloud.paas.patientprocessservice.PatientProcessService_implRemote;
import org.cloud.paas.registrationservice.RegistrationService_implRemote;

/**
 * EJB工具类 - 统一管理EJB服务获取
 */
public class ejbUtility {

    private static final String APP_NAME = "Cloud_PaaSEAR";
    private static final String MODULE_NAME = "Cloud_PaaS";
    private static final String DISTINCT_NAME = "";

    /** 创建EJB上下文 */
    private static Context createContext() throws NamingException {
        Properties prop = new Properties();
        prop.setProperty(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        return new InitialContext(prop);
    }

    /** 构建JNDI名称 */
    private static String buildJndiName(String beanName, String interfaceClassName) {
        return "ejb:" + APP_NAME + "/" + MODULE_NAME + "/" + DISTINCT_NAME + "/" + beanName + "!" + interfaceClassName;
    }

    /** 获取用户管理EJB（注册/登录） */
    public static MultipleTenantManageService_implRemote getTenantService() throws NamingException {
        String jndi = buildJndiName("MultipleTenantManageService_impl", 
                MultipleTenantManageService_implRemote.class.getName());
        System.out.println("[EJB] 用户管理服务 JNDI: " + jndi);
        Context ctx = createContext();
        return (MultipleTenantManageService_implRemote) ctx.lookup(jndi);
    }

    /** 获取医疗历史EJB */
    public static MedicalHistory_implRemote getMedicalHistoryService() throws NamingException {
        String jndi = buildJndiName("MedicalHistory_impl", 
                MedicalHistory_implRemote.class.getName());
        System.out.println("[EJB] 医疗历史服务 JNDI: " + jndi);
        Context ctx = createContext();
        return (MedicalHistory_implRemote) ctx.lookup(jndi);
    }

    /** 获取缴费记录EJB */
    public static Payment_implRemote getPaymentService() throws NamingException {
        String jndi = buildJndiName("Payment_impl", 
                Payment_implRemote.class.getName());
        System.out.println("[EJB] 缴费服务 JNDI: " + jndi);
        Context ctx = createContext();
        return (Payment_implRemote) ctx.lookup(jndi);
    }

    /** 获取疾病管理EJB */
    public static DiseaseManageService_implRemote getDiagnoseService() throws NamingException {
        String jndi = buildJndiName("DiseaseManageService_impl", 
                DiseaseManageService_implRemote.class.getName());
        System.out.println("[EJB] 疾病管理服务 JNDI: " + jndi);
        Context ctx = createContext();
        return (DiseaseManageService_implRemote) ctx.lookup(jndi);
    }

    /** 获取组织管理EJB */
    public static MultiOrganizationManageService_implRemote getOrgService() throws NamingException {
        String jndi = buildJndiName("MultiOrganizationManageService_impl", 
                MultiOrganizationManageService_implRemote.class.getName());
        System.out.println("[EJB] 组织管理服务 JNDI: " + jndi);
        Context ctx = createContext();
        return (MultiOrganizationManageService_implRemote) ctx.lookup(jndi);
    }

    /** 获取患者进程EJB */
    public static PatientProcessService_implRemote getPatientProcessService() throws NamingException {
        String jndi = buildJndiName("PatientProcess_impl", 
                PatientProcessService_implRemote.class.getName());
        System.out.println("[EJB] 患者进程服务 JNDI: " + jndi);
        Context ctx = createContext();
        return (PatientProcessService_implRemote) ctx.lookup(jndi);
    }

    /** 获取预约挂号EJB */
    public static RegistrationService_implRemote getRegistrationService() throws NamingException {
        String jndi = buildJndiName("RegistrationService_impl", 
                RegistrationService_implRemote.class.getName());
        System.out.println("[EJB] 预约挂号服务 JNDI: " + jndi);
        Context ctx = createContext();
        return (RegistrationService_implRemote) ctx.lookup(jndi);
    }
}

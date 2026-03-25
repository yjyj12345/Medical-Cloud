package org.cloudserviceengineering.cloudfactory;

public class CloudServiceFactory {
	private ICloud cloud;

    public ICloud produce_domaincloud(String domainType) {
        // 1. ���п� + ͳһת��д/Сд������ƴд/��Сд���⵼��ƥ��ʧ��
        if (domainType == null || domainType.trim().isEmpty()) {
            // ���ף�Ĭ�Ϸ���ҽ���ƣ�����null
            return new HealthcareCloud();
        }
        // ͳһת��д������ǰ�˴��εĴ�Сд������"healthcare"/"HealthCare"����ƥ�䣩
        String type = domainType.trim().toUpperCase();

        // 2. ������if�ĳ�else if��������֧ƥ��/����
        if(type.equals("HEALTHCARE")) {
            cloud = new HealthcareCloud();
        } else if(type.equals("ENTERPRISE")) {
            cloud = new EnterpriseCloud();
        } else if(type.equals("EDUCATION")) {
            cloud = new EducationCloud();
        } else if(type.equals("EPIDEMIC")) {
            cloud = new EpidemicCloud();
        } else if(type.equals("GOVERNMENT")) {
            cloud = new GovernmentCloud();
        } else {
            // ���ף�δ֪����Ҳ����ҽ���ƣ�����null
            cloud = new HealthcareCloud();
        }
        return cloud;
    }
}

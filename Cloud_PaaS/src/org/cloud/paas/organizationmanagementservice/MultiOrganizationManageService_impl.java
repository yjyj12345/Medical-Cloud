package org.cloud.paas.organizationmanagementservice;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.ejb.EJB;
//import javax.activation.DataSource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.jpa.Organization;
import org.jpa.OrganizationFacadeLocal;

@Stateless
public class MultiOrganizationManageService_impl implements MultiOrganizationManageService_implRemote {

	@EJB
	OrganizationFacadeLocal facade;

	@Override
	public boolean HospitalType(int hospitalID, String hospitalName, String hospitalType) {
		// TODO Auto-generated method stub
		try {
			System.out.println("��ʼ�������ݿ�");
			Organization organization=new Organization();
			organization.setOrganizationId((long) hospitalID);
			organization.setOrganizationName(hospitalName);
			organization.setOrganizationType(hospitalType);
			facade.save(organization);
			System.out.println("�������ݿ�ɹ�");
			System.out.println("���");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("�д���");
			e.printStackTrace(); // ��ӡ��ϸ�쳣�����������ݿ�����ʧ�ܡ�JPAע������
		}
		return true;
	}

}
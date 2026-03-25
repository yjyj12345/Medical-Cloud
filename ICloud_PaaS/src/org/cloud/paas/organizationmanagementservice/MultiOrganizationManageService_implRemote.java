package org.cloud.paas.organizationmanagementservice;

import javax.ejb.Remote;

@Remote
public interface MultiOrganizationManageService_implRemote {
	public boolean HospitalType(int hospitalID,String hospitalName,String hospitalType);
}

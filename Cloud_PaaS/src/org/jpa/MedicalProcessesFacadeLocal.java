package org.jpa;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import javax.ejb.Local;

/**
 * Local interface for MedicalProcessesFacade.
 * 
 * @author MyEclipse Persistence Tools
 */
@Local

public interface MedicalProcessesFacadeLocal {
	/**
	 * Perform an initial save of a previously unsaved MedicalProcesses entity.
	 * All subsequent persist actions of this entity should use the #update()
	 * method.
	 * 
	 * @param entity
	 *            MedicalProcesses entity to persist
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void save(MedicalProcesses entity);

	/**
	 * Delete a persistent MedicalProcesses entity.
	 * 
	 * @param entity
	 *            MedicalProcesses entity to delete
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void delete(MedicalProcesses entity);

	/**
	 * Persist a previously saved MedicalProcesses entity and return it or a
	 * copy of it to the sender. A copy of the MedicalProcesses entity parameter
	 * is returned when the JPA persistence mechanism has not previously been
	 * tracking the updated entity.
	 * 
	 * @param entity
	 *            MedicalProcesses entity to update
	 * @return MedicalProcesses the persisted MedicalProcesses entity instance,
	 *         may not be the same
	 * @throws RuntimeException
	 *             if the operation fails
	 */
	public MedicalProcesses update(MedicalProcesses entity);

	public MedicalProcesses findById(Long id);

	/**
	 * Find all MedicalProcesses entities with a specific property value.
	 * 
	 * @param propertyName
	 *            the name of the MedicalProcesses property to query
	 * @param value
	 *            the property value to match
	 * @return List<MedicalProcesses> found by query
	 */
	public List<MedicalProcesses> findByProperty(String propertyName, Object value);

	public List<MedicalProcesses> findByProcessStatus(Object processStatus);

	/**
	 * Find all MedicalProcesses entities.
	 * 
	 * @return List<MedicalProcesses> all MedicalProcesses entities
	 */
	public List<MedicalProcesses> findAll();
}
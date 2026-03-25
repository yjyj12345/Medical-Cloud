package org.jpa;

import java.util.List;
import java.util.Set;
import javax.ejb.Local;

/**
 * Local interface for DoctorFacade.
 * 
 * @author MyEclipse Persistence Tools
 */
@Local

public interface DoctorFacadeLocal {
	/**
	 * Perform an initial save of a previously unsaved Doctor entity. All
	 * subsequent persist actions of this entity should use the #update()
	 * method.
	 * 
	 * @param entity
	 *            Doctor entity to persist
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void save(Doctor entity);

	/**
	 * Delete a persistent Doctor entity.
	 * 
	 * @param entity
	 *            Doctor entity to delete
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void delete(Doctor entity);

	/**
	 * Persist a previously saved Doctor entity and return it or a copy of it to
	 * the sender. A copy of the Doctor entity parameter is returned when the
	 * JPA persistence mechanism has not previously been tracking the updated
	 * entity.
	 * 
	 * @param entity
	 *            Doctor entity to update
	 * @return Doctor the persisted Doctor entity instance, may not be the same
	 * @throws RuntimeException
	 *             if the operation fails
	 */
	public Doctor update(Doctor entity);

	public Doctor findById(Long id);

	/**
	 * Find all Doctor entities with a specific property value.
	 * 
	 * @param propertyName
	 *            the name of the Doctor property to query
	 * @param value
	 *            the property value to match
	 * @return List<Doctor> found by query
	 */
	public List<Doctor> findByProperty(String propertyName, Object value);

	public List<Doctor> findByUsername(Object username);

	public List<Doctor> findByPassword(Object password);

	public List<Doctor> findByPhoneNum(Object phoneNum);

	public List<Doctor> findByGender(Object gender);

	public List<Doctor> findByTitle(Object title);

	public List<Doctor> findBySpecialty(Object specialty);

	public List<Doctor> findByOrganizationId(Object organizationId);

	/**
	 * Find all Doctor entities.
	 * 
	 * @return List<Doctor> all Doctor entities
	 */
	public List<Doctor> findAll();
}
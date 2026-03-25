package org.jpa;

import java.util.List;
import java.util.Set;
import javax.ejb.Local;

/**
 * Local interface for MedicineFacade.
 * 
 * @author MyEclipse Persistence Tools
 */
@Local

public interface MedicineFacadeLocal {
	/**
	 * Perform an initial save of a previously unsaved Medicine entity. All
	 * subsequent persist actions of this entity should use the #update()
	 * method.
	 * 
	 * @param entity
	 *            Medicine entity to persist
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void save(Medicine entity);

	/**
	 * Delete a persistent Medicine entity.
	 * 
	 * @param entity
	 *            Medicine entity to delete
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void delete(Medicine entity);

	/**
	 * Persist a previously saved Medicine entity and return it or a copy of it
	 * to the sender. A copy of the Medicine entity parameter is returned when
	 * the JPA persistence mechanism has not previously been tracking the
	 * updated entity.
	 * 
	 * @param entity
	 *            Medicine entity to update
	 * @return Medicine the persisted Medicine entity instance, may not be the
	 *         same
	 * @throws RuntimeException
	 *             if the operation fails
	 */
	public Medicine update(Medicine entity);

	public Medicine findById(Long id);

	/**
	 * Find all Medicine entities with a specific property value.
	 * 
	 * @param propertyName
	 *            the name of the Medicine property to query
	 * @param value
	 *            the property value to match
	 * @return List<Medicine> found by query
	 */
	public List<Medicine> findByProperty(String propertyName, Object value);

	public List<Medicine> findByUseMethod(Object useMethod);

	public List<Medicine> findByDosage(Object dosage);

	public List<Medicine> findBySideEffect(Object sideEffect);

	public List<Medicine> findByName(Object name);

	public List<Medicine> findByPrice(Object price);

	/**
	 * Find all Medicine entities.
	 * 
	 * @return List<Medicine> all Medicine entities
	 */
	public List<Medicine> findAll();
}
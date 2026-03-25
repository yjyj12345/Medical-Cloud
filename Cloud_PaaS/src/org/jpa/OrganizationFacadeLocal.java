package org.jpa;

import java.util.List;
import java.util.Set;
import javax.ejb.Local;

/**
 * Local interface for OrganizationFacade.
 * 
 * @author MyEclipse Persistence Tools
 */
@Local

public interface OrganizationFacadeLocal {
	/**
	 * Perform an initial save of a previously unsaved Organization entity. All
	 * subsequent persist actions of this entity should use the #update()
	 * method.
	 * 
	 * @param entity
	 *            Organization entity to persist
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void save(Organization entity);

	/**
	 * Delete a persistent Organization entity.
	 * 
	 * @param entity
	 *            Organization entity to delete
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void delete(Organization entity);

	/**
	 * Persist a previously saved Organization entity and return it or a copy of
	 * it to the sender. A copy of the Organization entity parameter is returned
	 * when the JPA persistence mechanism has not previously been tracking the
	 * updated entity.
	 * 
	 * @param entity
	 *            Organization entity to update
	 * @return Organization the persisted Organization entity instance, may not
	 *         be the same
	 * @throws RuntimeException
	 *             if the operation fails
	 */
	public Organization update(Organization entity);

	public Organization findById(Long id);

	/**
	 * Find all Organization entities with a specific property value.
	 * 
	 * @param propertyName
	 *            the name of the Organization property to query
	 * @param value
	 *            the property value to match
	 * @return List<Organization> found by query
	 */
	public List<Organization> findByProperty(String propertyName, Object value);

	public List<Organization> findByOrganizationName(Object organizationName);

	public List<Organization> findByOrganizationType(Object organizationType);

	public List<Organization> findByStatus(Object status);

	/**
	 * Find all Organization entities.
	 * 
	 * @return List<Organization> all Organization entities
	 */
	public List<Organization> findAll();
}
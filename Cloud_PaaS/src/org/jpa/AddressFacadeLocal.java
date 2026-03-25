package org.jpa;

import java.util.List;
import java.util.Set;
import javax.ejb.Local;

/**
 * Local interface for AddressFacade.
 * 
 * @author MyEclipse Persistence Tools
 */
@Local

public interface AddressFacadeLocal {
	/**
	 * Perform an initial save of a previously unsaved Address entity. All
	 * subsequent persist actions of this entity should use the #update()
	 * method.
	 * 
	 * @param entity
	 *            Address entity to persist
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void save(Address entity);

	/**
	 * Delete a persistent Address entity.
	 * 
	 * @param entity
	 *            Address entity to delete
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void delete(Address entity);

	/**
	 * Persist a previously saved Address entity and return it or a copy of it
	 * to the sender. A copy of the Address entity parameter is returned when
	 * the JPA persistence mechanism has not previously been tracking the
	 * updated entity.
	 * 
	 * @param entity
	 *            Address entity to update
	 * @return Address the persisted Address entity instance, may not be the
	 *         same
	 * @throws RuntimeException
	 *             if the operation fails
	 */
	public Address update(Address entity);

	public Address findById(Long id);

	/**
	 * Find all Address entities with a specific property value.
	 * 
	 * @param propertyName
	 *            the name of the Address property to query
	 * @param value
	 *            the property value to match
	 * @return List<Address> found by query
	 */
	public List<Address> findByProperty(String propertyName, Object value);

	public List<Address> findByAddress(Object address);

	public List<Address> findByCountry(Object country);

	/**
	 * Find all Address entities.
	 * 
	 * @return List<Address> all Address entities
	 */
	public List<Address> findAll();
}
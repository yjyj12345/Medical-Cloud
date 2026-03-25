package org.jpa;

import java.util.List;
import javax.ejb.Local;

/**
 * Local interface for LocationFacade.
 * 
 * @author MyEclipse Persistence Tools
 */
@Local

public interface LocationFacadeLocal {
	/**
	 * Perform an initial save of a previously unsaved Location entity. All
	 * subsequent persist actions of this entity should use the #update()
	 * method.
	 * 
	 * @param entity
	 *            Location entity to persist
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void save(Location entity);

	/**
	 * Delete a persistent Location entity.
	 * 
	 * @param entity
	 *            Location entity to delete
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void delete(Location entity);

	/**
	 * Persist a previously saved Location entity and return it or a copy of it
	 * to the sender. A copy of the Location entity parameter is returned when
	 * the JPA persistence mechanism has not previously been tracking the
	 * updated entity.
	 * 
	 * @param entity
	 *            Location entity to update
	 * @return Location the persisted Location entity instance, may not be the
	 *         same
	 * @throws RuntimeException
	 *             if the operation fails
	 */
	public Location update(Location entity);

	public Location findById(Long id);

	/**
	 * Find all Location entities with a specific property value.
	 * 
	 * @param propertyName
	 *            the name of the Location property to query
	 * @param value
	 *            the property value to match
	 * @return List<Location> found by query
	 */
	public List<Location> findByProperty(String propertyName, Object value);

	public List<Location> findByLocationName(Object locationName);

	public List<Location> findByPosition(Object position);

	public List<Location> findByRouteGuide(Object routeGuide);

	/**
	 * Find all Location entities.
	 * 
	 * @return List<Location> all Location entities
	 */
	public List<Location> findAll();
}
package org.jpa;

import java.util.List;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Facade for entity Location.
 * 
 * @see Org.jpa.Location
 * @author MyEclipse Persistence Tools
 */
@Stateless
public class LocationFacade implements LocationFacadeLocal {
	// property constants
	public static final String LOCATION_NAME = "locationName";
	public static final String POSITION = "position";
	public static final String ROUTE_GUIDE = "routeGuide";

	@PersistenceContext
	private EntityManager entityManager;

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
	public void save(Location entity) {
		LogUtil.log("saving Location instance", Level.INFO, null);
		try {
			entityManager.persist(entity);
			LogUtil.log("save successful", Level.INFO, null);
		} catch (RuntimeException re) {
			LogUtil.log("save failed", Level.SEVERE, re);
			throw re;
		}
	}

	/**
	 * Delete a persistent Location entity.
	 * 
	 * @param entity
	 *            Location entity to delete
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void delete(Location entity) {
		LogUtil.log("deleting Location instance", Level.INFO, null);
		try {
			entity = entityManager.getReference(Location.class, entity.getLocationId());
			entityManager.remove(entity);
			LogUtil.log("delete successful", Level.INFO, null);
		} catch (RuntimeException re) {
			LogUtil.log("delete failed", Level.SEVERE, re);
			throw re;
		}
	}

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
	public Location update(Location entity) {
		LogUtil.log("updating Location instance", Level.INFO, null);
		try {
			Location result = entityManager.merge(entity);
			LogUtil.log("update successful", Level.INFO, null);
			return result;
		} catch (RuntimeException re) {
			LogUtil.log("update failed", Level.SEVERE, re);
			throw re;
		}
	}

	public Location findById(Long id) {
		LogUtil.log("finding Location instance with id: " + id, Level.INFO, null);
		try {
			Location instance = entityManager.find(Location.class, id);
			return instance;
		} catch (RuntimeException re) {
			LogUtil.log("find failed", Level.SEVERE, re);
			throw re;
		}
	}

	/**
	 * Find all Location entities with a specific property value.
	 * 
	 * @param propertyName
	 *            the name of the Location property to query
	 * @param value
	 *            the property value to match
	 * @return List<Location> found by query
	 */
	@SuppressWarnings("unchecked")
	public List<Location> findByProperty(String propertyName, final Object value) {
		LogUtil.log("finding Location instance with property: " + propertyName + ", value: " + value, Level.INFO, null);
		try {
			final String queryString = "select model from Location model where model." + propertyName
					+ "= :propertyValue";
			Query query = entityManager.createQuery(queryString);
			query.setParameter("propertyValue", value);
			return query.getResultList();
		} catch (RuntimeException re) {
			LogUtil.log("find by property name failed", Level.SEVERE, re);
			throw re;
		}
	}

	public List<Location> findByLocationName(Object locationName) {
		return findByProperty(LOCATION_NAME, locationName);
	}

	public List<Location> findByPosition(Object position) {
		return findByProperty(POSITION, position);
	}

	public List<Location> findByRouteGuide(Object routeGuide) {
		return findByProperty(ROUTE_GUIDE, routeGuide);
	}

	/**
	 * Find all Location entities.
	 * 
	 * @return List<Location> all Location entities
	 */
	@SuppressWarnings("unchecked")
	public List<Location> findAll() {
		LogUtil.log("finding all Location instances", Level.INFO, null);
		try {
			final String queryString = "select model from Location model";
			Query query = entityManager.createQuery(queryString);
			return query.getResultList();
		} catch (RuntimeException re) {
			LogUtil.log("find all failed", Level.SEVERE, re);
			throw re;
		}
	}

}
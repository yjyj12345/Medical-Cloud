package org.jpa;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Facade for entity Organization.
 * 
 * @see Org.jpa.Organization
 * @author MyEclipse Persistence Tools
 */
@Stateless
public class OrganizationFacade implements OrganizationFacadeLocal {
	// property constants
	public static final String ORGANIZATION_NAME = "organizationName";
	public static final String ORGANIZATION_TYPE = "organizationType";
	public static final String STATUS = "status";

	@PersistenceContext
	private EntityManager entityManager;

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
	public void save(Organization entity) {
		LogUtil.log("saving Organization instance", Level.INFO, null);
		try {
			entityManager.persist(entity);
			LogUtil.log("save successful", Level.INFO, null);
		} catch (RuntimeException re) {
			LogUtil.log("save failed", Level.SEVERE, re);
			throw re;
		}
	}

	/**
	 * Delete a persistent Organization entity.
	 * 
	 * @param entity
	 *            Organization entity to delete
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void delete(Organization entity) {
		LogUtil.log("deleting Organization instance", Level.INFO, null);
		try {
			entity = entityManager.getReference(Organization.class, entity.getOrganizationId());
			entityManager.remove(entity);
			LogUtil.log("delete successful", Level.INFO, null);
		} catch (RuntimeException re) {
			LogUtil.log("delete failed", Level.SEVERE, re);
			throw re;
		}
	}

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
	public Organization update(Organization entity) {
		LogUtil.log("updating Organization instance", Level.INFO, null);
		try {
			Organization result = entityManager.merge(entity);
			LogUtil.log("update successful", Level.INFO, null);
			return result;
		} catch (RuntimeException re) {
			LogUtil.log("update failed", Level.SEVERE, re);
			throw re;
		}
	}

	public Organization findById(Long id) {
		LogUtil.log("finding Organization instance with id: " + id, Level.INFO, null);
		try {
			Organization instance = entityManager.find(Organization.class, id);
			return instance;
		} catch (RuntimeException re) {
			LogUtil.log("find failed", Level.SEVERE, re);
			throw re;
		}
	}

	/**
	 * Find all Organization entities with a specific property value.
	 * 
	 * @param propertyName
	 *            the name of the Organization property to query
	 * @param value
	 *            the property value to match
	 * @return List<Organization> found by query
	 */
	@SuppressWarnings("unchecked")
	public List<Organization> findByProperty(String propertyName, final Object value) {
		LogUtil.log("finding Organization instance with property: " + propertyName + ", value: " + value, Level.INFO,
				null);
		try {
			final String queryString = "select model from Organization model where model." + propertyName
					+ "= :propertyValue";
			Query query = entityManager.createQuery(queryString);
			query.setParameter("propertyValue", value);
			return query.getResultList();
		} catch (RuntimeException re) {
			LogUtil.log("find by property name failed", Level.SEVERE, re);
			throw re;
		}
	}

	public List<Organization> findByOrganizationName(Object organizationName) {
		return findByProperty(ORGANIZATION_NAME, organizationName);
	}

	public List<Organization> findByOrganizationType(Object organizationType) {
		return findByProperty(ORGANIZATION_TYPE, organizationType);
	}

	public List<Organization> findByStatus(Object status) {
		return findByProperty(STATUS, status);
	}

	/**
	 * Find all Organization entities.
	 * 
	 * @return List<Organization> all Organization entities
	 */
	@SuppressWarnings("unchecked")
	public List<Organization> findAll() {
		LogUtil.log("finding all Organization instances", Level.INFO, null);
		try {
			final String queryString = "select model from Organization model";
			Query query = entityManager.createQuery(queryString);
			return query.getResultList();
		} catch (RuntimeException re) {
			LogUtil.log("find all failed", Level.SEVERE, re);
			throw re;
		}
	}

}
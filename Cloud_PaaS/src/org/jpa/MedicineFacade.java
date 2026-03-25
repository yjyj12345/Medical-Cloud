package org.jpa;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Facade for entity Medicine.
 * 
 * @see Org.jpa.Medicine
 * @author MyEclipse Persistence Tools
 */
@Stateless
public class MedicineFacade implements MedicineFacadeLocal {
	// property constants
	public static final String USE_METHOD = "useMethod";
	public static final String DOSAGE = "dosage";
	public static final String SIDE_EFFECT = "sideEffect";
	public static final String NAME = "name";
	public static final String PRICE = "price";

	@PersistenceContext
	private EntityManager entityManager;

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
	public void save(Medicine entity) {
		LogUtil.log("saving Medicine instance", Level.INFO, null);
		try {
			entityManager.persist(entity);
			LogUtil.log("save successful", Level.INFO, null);
		} catch (RuntimeException re) {
			LogUtil.log("save failed", Level.SEVERE, re);
			throw re;
		}
	}

	/**
	 * Delete a persistent Medicine entity.
	 * 
	 * @param entity
	 *            Medicine entity to delete
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void delete(Medicine entity) {
		LogUtil.log("deleting Medicine instance", Level.INFO, null);
		try {
			entity = entityManager.getReference(Medicine.class, entity.getMedicineId());
			entityManager.remove(entity);
			LogUtil.log("delete successful", Level.INFO, null);
		} catch (RuntimeException re) {
			LogUtil.log("delete failed", Level.SEVERE, re);
			throw re;
		}
	}

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
	public Medicine update(Medicine entity) {
		LogUtil.log("updating Medicine instance", Level.INFO, null);
		try {
			Medicine result = entityManager.merge(entity);
			LogUtil.log("update successful", Level.INFO, null);
			return result;
		} catch (RuntimeException re) {
			LogUtil.log("update failed", Level.SEVERE, re);
			throw re;
		}
	}

	public Medicine findById(Long id) {
		LogUtil.log("finding Medicine instance with id: " + id, Level.INFO, null);
		try {
			Medicine instance = entityManager.find(Medicine.class, id);
			return instance;
		} catch (RuntimeException re) {
			LogUtil.log("find failed", Level.SEVERE, re);
			throw re;
		}
	}

	/**
	 * Find all Medicine entities with a specific property value.
	 * 
	 * @param propertyName
	 *            the name of the Medicine property to query
	 * @param value
	 *            the property value to match
	 * @return List<Medicine> found by query
	 */
	@SuppressWarnings("unchecked")
	public List<Medicine> findByProperty(String propertyName, final Object value) {
		LogUtil.log("finding Medicine instance with property: " + propertyName + ", value: " + value, Level.INFO, null);
		try {
			final String queryString = "select model from Medicine model where model." + propertyName
					+ "= :propertyValue";
			Query query = entityManager.createQuery(queryString);
			query.setParameter("propertyValue", value);
			return query.getResultList();
		} catch (RuntimeException re) {
			LogUtil.log("find by property name failed", Level.SEVERE, re);
			throw re;
		}
	}

	public List<Medicine> findByUseMethod(Object useMethod) {
		return findByProperty(USE_METHOD, useMethod);
	}

	public List<Medicine> findByDosage(Object dosage) {
		return findByProperty(DOSAGE, dosage);
	}

	public List<Medicine> findBySideEffect(Object sideEffect) {
		return findByProperty(SIDE_EFFECT, sideEffect);
	}

	public List<Medicine> findByName(Object name) {
		return findByProperty(NAME, name);
	}

	public List<Medicine> findByPrice(Object price) {
		return findByProperty(PRICE, price);
	}

	/**
	 * Find all Medicine entities.
	 * 
	 * @return List<Medicine> all Medicine entities
	 */
	@SuppressWarnings("unchecked")
	public List<Medicine> findAll() {
		LogUtil.log("finding all Medicine instances", Level.INFO, null);
		try {
			final String queryString = "select model from Medicine model";
			Query query = entityManager.createQuery(queryString);
			return query.getResultList();
		} catch (RuntimeException re) {
			LogUtil.log("find all failed", Level.SEVERE, re);
			throw re;
		}
	}

}
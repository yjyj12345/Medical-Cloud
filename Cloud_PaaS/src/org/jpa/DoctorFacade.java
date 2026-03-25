package org.jpa;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Facade for entity Doctor.
 * 
 * @see Org.jpa.Doctor
 * @author MyEclipse Persistence Tools
 */
@Stateless
public class DoctorFacade implements DoctorFacadeLocal {
	// property constants
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String PHONE_NUM = "phoneNum";
	public static final String GENDER = "gender";
	public static final String TITLE = "title";
	public static final String SPECIALTY = "specialty";
	public static final String ORGANIZATION_ID = "organizationId";

	@PersistenceContext
	private EntityManager entityManager;

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
	public void save(Doctor entity) {
		LogUtil.log("saving Doctor instance", Level.INFO, null);
		try {
			entityManager.persist(entity);
			LogUtil.log("save successful", Level.INFO, null);
		} catch (RuntimeException re) {
			LogUtil.log("save failed", Level.SEVERE, re);
			throw re;
		}
	}

	/**
	 * Delete a persistent Doctor entity.
	 * 
	 * @param entity
	 *            Doctor entity to delete
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void delete(Doctor entity) {
		LogUtil.log("deleting Doctor instance", Level.INFO, null);
		try {
			entity = entityManager.getReference(Doctor.class, entity.getDoctorId());
			entityManager.remove(entity);
			LogUtil.log("delete successful", Level.INFO, null);
		} catch (RuntimeException re) {
			LogUtil.log("delete failed", Level.SEVERE, re);
			throw re;
		}
	}

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
	public Doctor update(Doctor entity) {
		LogUtil.log("updating Doctor instance", Level.INFO, null);
		try {
			Doctor result = entityManager.merge(entity);
			LogUtil.log("update successful", Level.INFO, null);
			return result;
		} catch (RuntimeException re) {
			LogUtil.log("update failed", Level.SEVERE, re);
			throw re;
		}
	}

	public Doctor findById(Long id) {
		LogUtil.log("finding Doctor instance with id: " + id, Level.INFO, null);
		try {
			Doctor instance = entityManager.find(Doctor.class, id);
			return instance;
		} catch (RuntimeException re) {
			LogUtil.log("find failed", Level.SEVERE, re);
			throw re;
		}
	}

	/**
	 * Find all Doctor entities with a specific property value.
	 * 
	 * @param propertyName
	 *            the name of the Doctor property to query
	 * @param value
	 *            the property value to match
	 * @return List<Doctor> found by query
	 */
	@SuppressWarnings("unchecked")
	public List<Doctor> findByProperty(String propertyName, final Object value) {
		LogUtil.log("finding Doctor instance with property: " + propertyName + ", value: " + value, Level.INFO, null);
		try {
			final String queryString = "select model from Doctor model where model." + propertyName
					+ "= :propertyValue";
			Query query = entityManager.createQuery(queryString);
			query.setParameter("propertyValue", value);
			return query.getResultList();
		} catch (RuntimeException re) {
			LogUtil.log("find by property name failed", Level.SEVERE, re);
			throw re;
		}
	}

	public List<Doctor> findByUsername(Object username) {
		return findByProperty(USERNAME, username);
	}

	public List<Doctor> findByPassword(Object password) {
		return findByProperty(PASSWORD, password);
	}

	public List<Doctor> findByPhoneNum(Object phoneNum) {
		return findByProperty(PHONE_NUM, phoneNum);
	}

	public List<Doctor> findByGender(Object gender) {
		return findByProperty(GENDER, gender);
	}

	public List<Doctor> findByTitle(Object title) {
		return findByProperty(TITLE, title);
	}

	public List<Doctor> findBySpecialty(Object specialty) {
		return findByProperty(SPECIALTY, specialty);
	}

	public List<Doctor> findByOrganizationId(Object organizationId) {
		return findByProperty(ORGANIZATION_ID, organizationId);
	}

	/**
	 * Find all Doctor entities.
	 * 
	 * @return List<Doctor> all Doctor entities
	 */
	@SuppressWarnings("unchecked")
	public List<Doctor> findAll() {
		LogUtil.log("finding all Doctor instances", Level.INFO, null);
		try {
			final String queryString = "select model from Doctor model";
			Query query = entityManager.createQuery(queryString);
			return query.getResultList();
		} catch (RuntimeException re) {
			LogUtil.log("find all failed", Level.SEVERE, re);
			throw re;
		}
	}

}
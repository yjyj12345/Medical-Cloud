package org.jpa;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Facade for entity Patient.
 * 
 * @see Org.jpa.Patient
 * @author MyEclipse Persistence Tools
 */
@Stateless
public class PatientFacade implements PatientFacadeLocal {
	// property constants
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String PHONE_NUM = "phoneNum";
	public static final String GENDER = "gender";

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Perform an initial save of a previously unsaved Patient entity. All
	 * subsequent persist actions of this entity should use the #update()
	 * method.
	 * 
	 * @param entity
	 *            Patient entity to persist
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void save(Patient entity) {
		LogUtil.log("saving Patient instance", Level.INFO, null);
		try {
			entityManager.persist(entity);
			LogUtil.log("save successful", Level.INFO, null);
		} catch (RuntimeException re) {
			LogUtil.log("save failed", Level.SEVERE, re);
			throw re;
		}
	}

	/**
	 * Delete a persistent Patient entity.
	 * 
	 * @param entity
	 *            Patient entity to delete
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void delete(Patient entity) {
		LogUtil.log("deleting Patient instance", Level.INFO, null);
		try {
			entity = entityManager.getReference(Patient.class, entity.getPatientId());
			entityManager.remove(entity);
			LogUtil.log("delete successful", Level.INFO, null);
		} catch (RuntimeException re) {
			LogUtil.log("delete failed", Level.SEVERE, re);
			throw re;
		}
	}

	/**
	 * Persist a previously saved Patient entity and return it or a copy of it
	 * to the sender. A copy of the Patient entity parameter is returned when
	 * the JPA persistence mechanism has not previously been tracking the
	 * updated entity.
	 * 
	 * @param entity
	 *            Patient entity to update
	 * @return Patient the persisted Patient entity instance, may not be the
	 *         same
	 * @throws RuntimeException
	 *             if the operation fails
	 */
	public Patient update(Patient entity) {
		LogUtil.log("updating Patient instance", Level.INFO, null);
		try {
			Patient result = entityManager.merge(entity);
			LogUtil.log("update successful", Level.INFO, null);
			return result;
		} catch (RuntimeException re) {
			LogUtil.log("update failed", Level.SEVERE, re);
			throw re;
		}
	}

	public Patient findById(Long id) {
		LogUtil.log("finding Patient instance with id: " + id, Level.INFO, null);
		try {
			Patient instance = entityManager.find(Patient.class, id);
			return instance;
		} catch (RuntimeException re) {
			LogUtil.log("find failed", Level.SEVERE, re);
			throw re;
		}
	}

	/**
	 * Find all Patient entities with a specific property value.
	 * 
	 * @param propertyName
	 *            the name of the Patient property to query
	 * @param value
	 *            the property value to match
	 * @return List<Patient> found by query
	 */
	@SuppressWarnings("unchecked")
	public List<Patient> findByProperty(String propertyName, final Object value) {
		LogUtil.log("finding Patient instance with property: " + propertyName + ", value: " + value, Level.INFO, null);
		try {
			final String queryString = "select model from Patient model where model." + propertyName
					+ "= :propertyValue";
			Query query = entityManager.createQuery(queryString);
			query.setParameter("propertyValue", value);
			return query.getResultList();
		} catch (RuntimeException re) {
			LogUtil.log("find by property name failed", Level.SEVERE, re);
			throw re;
		}
	}

	public List<Patient> findByUsername(Object username) {
		return findByProperty(USERNAME, username);
	}

	public List<Patient> findByPassword(Object password) {
		return findByProperty(PASSWORD, password);
	}

	public List<Patient> findByPhoneNum(Object phoneNum) {
		return findByProperty(PHONE_NUM, phoneNum);
	}

	public List<Patient> findByGender(Object gender) {
		return findByProperty(GENDER, gender);
	}

	/**
	 * Find all Patient entities.
	 * 
	 * @return List<Patient> all Patient entities
	 */
	@SuppressWarnings("unchecked")
	public List<Patient> findAll() {
		LogUtil.log("finding all Patient instances", Level.INFO, null);
		try {
			final String queryString = "select model from Patient model";
			Query query = entityManager.createQuery(queryString);
			return query.getResultList();
		} catch (RuntimeException re) {
			LogUtil.log("find all failed", Level.SEVERE, re);
			throw re;
		}
	}

}
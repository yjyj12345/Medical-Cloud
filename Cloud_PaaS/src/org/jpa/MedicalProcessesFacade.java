package org.jpa;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Facade for entity MedicalProcesses.
 * 
 * @see Org.jpa.MedicalProcesses
 * @author MyEclipse Persistence Tools
 */
@Stateless
public class MedicalProcessesFacade implements MedicalProcessesFacadeLocal {
	// property constants
	public static final String PROCESS_STATUS = "processStatus";

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Perform an initial save of a previously unsaved MedicalProcesses entity.
	 * All subsequent persist actions of this entity should use the #update()
	 * method.
	 * 
	 * @param entity
	 *            MedicalProcesses entity to persist
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void save(MedicalProcesses entity) {
		LogUtil.log("saving MedicalProcesses instance", Level.INFO, null);
		try {
			entityManager.persist(entity);
			LogUtil.log("save successful", Level.INFO, null);
		} catch (RuntimeException re) {
			LogUtil.log("save failed", Level.SEVERE, re);
			throw re;
		}
	}

	/**
	 * Delete a persistent MedicalProcesses entity.
	 * 
	 * @param entity
	 *            MedicalProcesses entity to delete
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void delete(MedicalProcesses entity) {
		LogUtil.log("deleting MedicalProcesses instance", Level.INFO, null);
		try {
			entity = entityManager.getReference(MedicalProcesses.class, entity.getProcessId());
			entityManager.remove(entity);
			LogUtil.log("delete successful", Level.INFO, null);
		} catch (RuntimeException re) {
			LogUtil.log("delete failed", Level.SEVERE, re);
			throw re;
		}
	}

	/**
	 * Persist a previously saved MedicalProcesses entity and return it or a
	 * copy of it to the sender. A copy of the MedicalProcesses entity parameter
	 * is returned when the JPA persistence mechanism has not previously been
	 * tracking the updated entity.
	 * 
	 * @param entity
	 *            MedicalProcesses entity to update
	 * @return MedicalProcesses the persisted MedicalProcesses entity instance,
	 *         may not be the same
	 * @throws RuntimeException
	 *             if the operation fails
	 */
	public MedicalProcesses update(MedicalProcesses entity) {
		LogUtil.log("updating MedicalProcesses instance", Level.INFO, null);
		try {
			MedicalProcesses result = entityManager.merge(entity);
			LogUtil.log("update successful", Level.INFO, null);
			return result;
		} catch (RuntimeException re) {
			LogUtil.log("update failed", Level.SEVERE, re);
			throw re;
		}
	}

	public MedicalProcesses findById(Long id) {
		LogUtil.log("finding MedicalProcesses instance with id: " + id, Level.INFO, null);
		try {
			MedicalProcesses instance = entityManager.find(MedicalProcesses.class, id);
			return instance;
		} catch (RuntimeException re) {
			LogUtil.log("find failed", Level.SEVERE, re);
			throw re;
		}
	}

	/**
	 * Find all MedicalProcesses entities with a specific property value.
	 * 
	 * @param propertyName
	 *            the name of the MedicalProcesses property to query
	 * @param value
	 *            the property value to match
	 * @return List<MedicalProcesses> found by query
	 */
	@SuppressWarnings("unchecked")
	public List<MedicalProcesses> findByProperty(String propertyName, final Object value) {
		LogUtil.log("finding MedicalProcesses instance with property: " + propertyName + ", value: " + value,
				Level.INFO, null);
		try {
			final String queryString = "select model from MedicalProcesses model where model." + propertyName
					+ "= :propertyValue";
			Query query = entityManager.createQuery(queryString);
			query.setParameter("propertyValue", value);
			return query.getResultList();
		} catch (RuntimeException re) {
			LogUtil.log("find by property name failed", Level.SEVERE, re);
			throw re;
		}
	}

	public List<MedicalProcesses> findByProcessStatus(Object processStatus) {
		return findByProperty(PROCESS_STATUS, processStatus);
	}

	/**
	 * Find all MedicalProcesses entities.
	 * 
	 * @return List<MedicalProcesses> all MedicalProcesses entities
	 */
	@SuppressWarnings("unchecked")
	public List<MedicalProcesses> findAll() {
		LogUtil.log("finding all MedicalProcesses instances", Level.INFO, null);
		try {
			final String queryString = "select model from MedicalProcesses model";
			Query query = entityManager.createQuery(queryString);
			return query.getResultList();
		} catch (RuntimeException re) {
			LogUtil.log("find all failed", Level.SEVERE, re);
			throw re;
		}
	}

}
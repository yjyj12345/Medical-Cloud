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
 * Facade for entity ProcessNode.
 * 
 * @see Org.jpa.ProcessNode
 * @author MyEclipse Persistence Tools
 */
@Stateless
public class ProcessNodeFacade implements ProcessNodeFacadeLocal {
	// property constants
	public static final String NODE_NAME = "nodeName";
	public static final String NODE_STATUS = "nodeStatus";
	public static final String DIAGNOSIS_TEXT = "diagnosisText";
	public static final String PICTURES = "pictures";
	public static final String REMINDER = "reminder";
	public static final String IS_LATEST = "isLatest";
	public static final String LOCATION_ID = "locationId";

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Perform an initial save of a previously unsaved ProcessNode entity. All
	 * subsequent persist actions of this entity should use the #update()
	 * method.
	 * 
	 * @param entity
	 *            ProcessNode entity to persist
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void save(ProcessNode entity) {
		LogUtil.log("saving ProcessNode instance", Level.INFO, null);
		try {
			entityManager.persist(entity);
			LogUtil.log("save successful", Level.INFO, null);
		} catch (RuntimeException re) {
			LogUtil.log("save failed", Level.SEVERE, re);
			throw re;
		}
	}

	/**
	 * Delete a persistent ProcessNode entity.
	 * 
	 * @param entity
	 *            ProcessNode entity to delete
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void delete(ProcessNode entity) {
		LogUtil.log("deleting ProcessNode instance", Level.INFO, null);
		try {
			entity = entityManager.getReference(ProcessNode.class, entity.getId());
			entityManager.remove(entity);
			LogUtil.log("delete successful", Level.INFO, null);
		} catch (RuntimeException re) {
			LogUtil.log("delete failed", Level.SEVERE, re);
			throw re;
		}
	}

	/**
	 * Persist a previously saved ProcessNode entity and return it or a copy of
	 * it to the sender. A copy of the ProcessNode entity parameter is returned
	 * when the JPA persistence mechanism has not previously been tracking the
	 * updated entity.
	 * 
	 * @param entity
	 *            ProcessNode entity to update
	 * @return ProcessNode the persisted ProcessNode entity instance, may not be
	 *         the same
	 * @throws RuntimeException
	 *             if the operation fails
	 */
	public ProcessNode update(ProcessNode entity) {
		LogUtil.log("updating ProcessNode instance", Level.INFO, null);
		try {
			ProcessNode result = entityManager.merge(entity);
			LogUtil.log("update successful", Level.INFO, null);
			return result;
		} catch (RuntimeException re) {
			LogUtil.log("update failed", Level.SEVERE, re);
			throw re;
		}
	}

	public ProcessNode findById(ProcessNodeId id) {
		LogUtil.log("finding ProcessNode instance with id: " + id, Level.INFO, null);
		try {
			ProcessNode instance = entityManager.find(ProcessNode.class, id);
			return instance;
		} catch (RuntimeException re) {
			LogUtil.log("find failed", Level.SEVERE, re);
			throw re;
		}
	}

	/**
	 * Find all ProcessNode entities with a specific property value.
	 * 
	 * @param propertyName
	 *            the name of the ProcessNode property to query
	 * @param value
	 *            the property value to match
	 * @return List<ProcessNode> found by query
	 */
	@SuppressWarnings("unchecked")
	public List<ProcessNode> findByProperty(String propertyName, final Object value) {
		LogUtil.log("finding ProcessNode instance with property: " + propertyName + ", value: " + value, Level.INFO,
				null);
		try {
			final String queryString = "select model from ProcessNode model where model." + propertyName
					+ "= :propertyValue";
			Query query = entityManager.createQuery(queryString);
			query.setParameter("propertyValue", value);
			return query.getResultList();
		} catch (RuntimeException re) {
			LogUtil.log("find by property name failed", Level.SEVERE, re);
			throw re;
		}
	}

	public List<ProcessNode> findByNodeName(Object nodeName) {
		return findByProperty(NODE_NAME, nodeName);
	}

	public List<ProcessNode> findByNodeStatus(Object nodeStatus) {
		return findByProperty(NODE_STATUS, nodeStatus);
	}

	public List<ProcessNode> findByDiagnosisText(Object diagnosisText) {
		return findByProperty(DIAGNOSIS_TEXT, diagnosisText);
	}

	public List<ProcessNode> findByPictures(Object pictures) {
		return findByProperty(PICTURES, pictures);
	}

	public List<ProcessNode> findByReminder(Object reminder) {
		return findByProperty(REMINDER, reminder);
	}

	public List<ProcessNode> findByIsLatest(Object isLatest) {
		return findByProperty(IS_LATEST, isLatest);
	}

	public List<ProcessNode> findByLocationId(Object locationId) {
		return findByProperty(LOCATION_ID, locationId);
	}

	/**
	 * Find all ProcessNode entities.
	 * 
	 * @return List<ProcessNode> all ProcessNode entities
	 */
	@SuppressWarnings("unchecked")
	public List<ProcessNode> findAll() {
		LogUtil.log("finding all ProcessNode instances", Level.INFO, null);
		try {
			final String queryString = "select model from ProcessNode model";
			Query query = entityManager.createQuery(queryString);
			return query.getResultList();
		} catch (RuntimeException re) {
			LogUtil.log("find all failed", Level.SEVERE, re);
			throw re;
		}
	}

}
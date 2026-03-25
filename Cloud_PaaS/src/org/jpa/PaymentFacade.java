package org.jpa;

import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Facade for entity Payment.
 * 
 * @see Org.jpa.Payment
 * @author MyEclipse Persistence Tools
 */
@Stateless
public class PaymentFacade implements PaymentFacadeLocal {
	// property constants
	public static final String ORDER_CONTENT = "orderContent";
	public static final String AMOUNT = "amount";
	public static final String ORDER_STATUS = "orderStatus";

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Perform an initial save of a previously unsaved Payment entity. All
	 * subsequent persist actions of this entity should use the #update()
	 * method.
	 * 
	 * @param entity
	 *            Payment entity to persist
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void save(Payment entity) {
		LogUtil.log("saving Payment instance", Level.INFO, null);
		try {
			entityManager.persist(entity);
			LogUtil.log("save successful", Level.INFO, null);
		} catch (RuntimeException re) {
			LogUtil.log("save failed", Level.SEVERE, re);
			throw re;
		}
	}

	/**
	 * Delete a persistent Payment entity.
	 * 
	 * @param entity
	 *            Payment entity to delete
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void delete(Payment entity) {
		LogUtil.log("deleting Payment instance", Level.INFO, null);
		try {
			entity = entityManager.getReference(Payment.class, entity.getOrderId());
			entityManager.remove(entity);
			LogUtil.log("delete successful", Level.INFO, null);
		} catch (RuntimeException re) {
			LogUtil.log("delete failed", Level.SEVERE, re);
			throw re;
		}
	}

	/**
	 * Persist a previously saved Payment entity and return it or a copy of it
	 * to the sender. A copy of the Payment entity parameter is returned when
	 * the JPA persistence mechanism has not previously been tracking the
	 * updated entity.
	 * 
	 * @param entity
	 *            Payment entity to update
	 * @return Payment the persisted Payment entity instance, may not be the
	 *         same
	 * @throws RuntimeException
	 *             if the operation fails
	 */
	public Payment update(Payment entity) {
		LogUtil.log("updating Payment instance", Level.INFO, null);
		try {
			Payment result = entityManager.merge(entity);
			LogUtil.log("update successful", Level.INFO, null);
			return result;
		} catch (RuntimeException re) {
			LogUtil.log("update failed", Level.SEVERE, re);
			throw re;
		}
	}

	public Payment findById(Long id) {
		LogUtil.log("finding Payment instance with id: " + id, Level.INFO, null);
		try {
			Payment instance = entityManager.find(Payment.class, id);
			return instance;
		} catch (RuntimeException re) {
			LogUtil.log("find failed", Level.SEVERE, re);
			throw re;
		}
	}

	/**
	 * Find all Payment entities with a specific property value.
	 * 
	 * @param propertyName
	 *            the name of the Payment property to query
	 * @param value
	 *            the property value to match
	 * @return List<Payment> found by query
	 */
	@SuppressWarnings("unchecked")
	public List<Payment> findByProperty(String propertyName, final Object value) {
		LogUtil.log("finding Payment instance with property: " + propertyName + ", value: " + value, Level.INFO, null);
		try {
			final String queryString = "select model from Payment model where model." + propertyName
					+ "= :propertyValue";
			Query query = entityManager.createQuery(queryString);
			query.setParameter("propertyValue", value);
			return query.getResultList();
		} catch (RuntimeException re) {
			LogUtil.log("find by property name failed", Level.SEVERE, re);
			throw re;
		}
	}

	public List<Payment> findByOrderContent(Object orderContent) {
		return findByProperty(ORDER_CONTENT, orderContent);
	}

	public List<Payment> findByAmount(Object amount) {
		return findByProperty(AMOUNT, amount);
	}

	public List<Payment> findByOrderStatus(Object orderStatus) {
		return findByProperty(ORDER_STATUS, orderStatus);
	}

	/**
	 * Find all Payment entities.
	 * 
	 * @return List<Payment> all Payment entities
	 */
	@SuppressWarnings("unchecked")
	public List<Payment> findAll() {
		LogUtil.log("finding all Payment instances", Level.INFO, null);
		try {
			final String queryString = "select model from Payment model";
			Query query = entityManager.createQuery(queryString);
			return query.getResultList();
		} catch (RuntimeException re) {
			LogUtil.log("find all failed", Level.SEVERE, re);
			throw re;
		}
	}

}
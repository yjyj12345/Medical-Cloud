package org.jpa;

import java.sql.Timestamp;
import java.util.List;
import javax.ejb.Local;

/**
 * Local interface for PaymentFacade.
 * 
 * @author MyEclipse Persistence Tools
 */
@Local

public interface PaymentFacadeLocal {
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
	public void save(Payment entity);

	/**
	 * Delete a persistent Payment entity.
	 * 
	 * @param entity
	 *            Payment entity to delete
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void delete(Payment entity);

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
	public Payment update(Payment entity);

	public Payment findById(Long id);

	/**
	 * Find all Payment entities with a specific property value.
	 * 
	 * @param propertyName
	 *            the name of the Payment property to query
	 * @param value
	 *            the property value to match
	 * @return List<Payment> found by query
	 */
	public List<Payment> findByProperty(String propertyName, Object value);

	public List<Payment> findByOrderContent(Object orderContent);

	public List<Payment> findByAmount(Object amount);

	public List<Payment> findByOrderStatus(Object orderStatus);

	/**
	 * Find all Payment entities.
	 * 
	 * @return List<Payment> all Payment entities
	 */
	public List<Payment> findAll();
}
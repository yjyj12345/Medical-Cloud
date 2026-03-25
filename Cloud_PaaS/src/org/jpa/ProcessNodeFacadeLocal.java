package org.jpa;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import javax.ejb.Local;

/**
 * Local interface for ProcessNodeFacade.
 * 
 * @author MyEclipse Persistence Tools
 */
@Local

public interface ProcessNodeFacadeLocal {
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
	public void save(ProcessNode entity);

	/**
	 * Delete a persistent ProcessNode entity.
	 * 
	 * @param entity
	 *            ProcessNode entity to delete
	 * @throws RuntimeException
	 *             when the operation fails
	 */
	public void delete(ProcessNode entity);

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
	public ProcessNode update(ProcessNode entity);

	public ProcessNode findById(ProcessNodeId id);

	/**
	 * Find all ProcessNode entities with a specific property value.
	 * 
	 * @param propertyName
	 *            the name of the ProcessNode property to query
	 * @param value
	 *            the property value to match
	 * @return List<ProcessNode> found by query
	 */
	public List<ProcessNode> findByProperty(String propertyName, Object value);

	public List<ProcessNode> findByNodeName(Object nodeName);

	public List<ProcessNode> findByNodeStatus(Object nodeStatus);

	public List<ProcessNode> findByDiagnosisText(Object diagnosisText);

	public List<ProcessNode> findByPictures(Object pictures);

	public List<ProcessNode> findByReminder(Object reminder);

	public List<ProcessNode> findByIsLatest(Object isLatest);

	public List<ProcessNode> findByLocationId(Object locationId);

	/**
	 * Find all ProcessNode entities.
	 * 
	 * @return List<ProcessNode> all ProcessNode entities
	 */
	public List<ProcessNode> findAll();
}
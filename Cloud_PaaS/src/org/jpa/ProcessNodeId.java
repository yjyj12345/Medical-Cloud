package org.jpa;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * ProcessNodeId entity. @author MyEclipse Persistence Tools
 */
@Embeddable

public class ProcessNodeId implements java.io.Serializable {

	// Fields

	private Long nodeId;
	private Long processId;

	// Constructors

	/** default constructor */
	public ProcessNodeId() {
	}

	/** full constructor */
	public ProcessNodeId(Long nodeId, Long processId) {
		this.nodeId = nodeId;
		this.processId = processId;
	}

	// Property accessors

	@Column(name = "NodeID", nullable = false)

	public Long getNodeId() {
		return this.nodeId;
	}

	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}

	@Column(name = "ProcessID", nullable = false)

	public Long getProcessId() {
		return this.processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof ProcessNodeId))
			return false;
		ProcessNodeId castOther = (ProcessNodeId) other;

		return ((this.getNodeId() == castOther.getNodeId()) || (this.getNodeId() != null
				&& castOther.getNodeId() != null && this.getNodeId().equals(castOther.getNodeId())))
				&& ((this.getProcessId() == castOther.getProcessId()) || (this.getProcessId() != null
						&& castOther.getProcessId() != null && this.getProcessId().equals(castOther.getProcessId())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getNodeId() == null ? 0 : this.getNodeId().hashCode());
		result = 37 * result + (getProcessId() == null ? 0 : this.getProcessId().hashCode());
		return result;
	}

}
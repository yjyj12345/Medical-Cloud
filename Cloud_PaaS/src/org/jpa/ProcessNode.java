package org.jpa;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * ProcessNode entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "process_node", catalog = "cloud_data_resource_connection")

public class ProcessNode implements java.io.Serializable {

	// Fields

	private ProcessNodeId id;
	private MedicalProcesses medicalProcesses;
	private Medicine medicine;
	private String nodeName;
	private String nodeStatus;
	private String diagnosisText;
	private String pictures;
	private String reminder;
	private Timestamp createAt;
	private Timestamp updatedAt;
	private Boolean isLatest;
	private Long locationId;
	private Set<Payment> payments = new HashSet<Payment>(0);

	// Constructors

	/** default constructor */
	public ProcessNode() {
	}

	/** minimal constructor */
	public ProcessNode(ProcessNodeId id, MedicalProcesses medicalProcesses, String nodeName, String nodeStatus,
			Boolean isLatest) {
		this.id = id;
		this.medicalProcesses = medicalProcesses;
		this.nodeName = nodeName;
		this.nodeStatus = nodeStatus;
		this.isLatest = isLatest;
	}

	/** full constructor */
	public ProcessNode(ProcessNodeId id, MedicalProcesses medicalProcesses, Medicine medicine, String nodeName,
			String nodeStatus, String diagnosisText, String pictures, String reminder, Timestamp createAt,
			Timestamp updatedAt, Boolean isLatest, Long locationId, Set<Payment> payments) {
		this.id = id;
		this.medicalProcesses = medicalProcesses;
		this.medicine = medicine;
		this.nodeName = nodeName;
		this.nodeStatus = nodeStatus;
		this.diagnosisText = diagnosisText;
		this.pictures = pictures;
		this.reminder = reminder;
		this.createAt = createAt;
		this.updatedAt = updatedAt;
		this.isLatest = isLatest;
		this.locationId = locationId;
		this.payments = payments;
	}

	// Property accessors
	@EmbeddedId

	@AttributeOverrides({ @AttributeOverride(name = "nodeId", column = @Column(name = "NodeID", nullable = false)),
			@AttributeOverride(name = "processId", column = @Column(name = "ProcessID", nullable = false)) })

	public ProcessNodeId getId() {
		return this.id;
	}

	public void setId(ProcessNodeId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ProcessID", nullable = false, insertable = false, updatable = false)

	public MedicalProcesses getMedicalProcesses() {
		return this.medicalProcesses;
	}

	public void setMedicalProcesses(MedicalProcesses medicalProcesses) {
		this.medicalProcesses = medicalProcesses;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MedicineID")

	public Medicine getMedicine() {
		return this.medicine;
	}

	public void setMedicine(Medicine medicine) {
		this.medicine = medicine;
	}

	@Column(name = "nodeName", nullable = false)

	public String getNodeName() {
		return this.nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	@Column(name = "node_status", nullable = false)

	public String getNodeStatus() {
		return this.nodeStatus;
	}

	public void setNodeStatus(String nodeStatus) {
		this.nodeStatus = nodeStatus;
	}

	@Column(name = "diagnosis_text", length = 65535)

	public String getDiagnosisText() {
		return this.diagnosisText;
	}

	public void setDiagnosisText(String diagnosisText) {
		this.diagnosisText = diagnosisText;
	}

	@Column(name = "pictures", columnDefinition = "LONGTEXT")

	public String getPictures() {
		return this.pictures;
	}

	public void setPictures(String pictures) {
		this.pictures = pictures;
	}

	@Column(name = "reminder")

	public String getReminder() {
		return this.reminder;
	}

	public void setReminder(String reminder) {
		this.reminder = reminder;
	}

	@Column(name = "create_at", length = 19)

	public Timestamp getCreateAt() {
		return this.createAt;
	}

	public void setCreateAt(Timestamp createAt) {
		this.createAt = createAt;
	}

	@Column(name = "updated_at", length = 19)

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Column(name = "is_latest", nullable = false)

	public Boolean getIsLatest() {
		return this.isLatest;
	}

	public void setIsLatest(Boolean isLatest) {
		this.isLatest = isLatest;
	}

	@Column(name = "LocationID")

	public Long getLocationId() {
		return this.locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "processNode")

	public Set<Payment> getPayments() {
		return this.payments;
	}

	public void setPayments(Set<Payment> payments) {
		this.payments = payments;
	}

}
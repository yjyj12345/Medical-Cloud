package org.jpa;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * MedicalProcesses entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "medical_processes", catalog = "cloud_data_resource_connection")

public class MedicalProcesses implements java.io.Serializable {

	// Fields

	private Long processId;
	private Patient patient;
	private Organization organization;
	private Doctor doctor;
	private String processStatus;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	private Timestamp completedAt;
	private Set<ProcessNode> processNodes = new HashSet<ProcessNode>(0);

	// Constructors

	/** default constructor */
	public MedicalProcesses() {
	}

	/** minimal constructor */
	public MedicalProcesses(Long processId, Patient patient, Organization organization, Doctor doctor,
			String processStatus) {
		this.processId = processId;
		this.patient = patient;
		this.organization = organization;
		this.doctor = doctor;
		this.processStatus = processStatus;
	}

	/** full constructor */
	public MedicalProcesses(Long processId, Patient patient, Organization organization, Doctor doctor,
			String processStatus, Timestamp createdAt, Timestamp updatedAt, Timestamp completedAt,
			Set<ProcessNode> processNodes) {
		this.processId = processId;
		this.patient = patient;
		this.organization = organization;
		this.doctor = doctor;
		this.processStatus = processStatus;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.completedAt = completedAt;
		this.processNodes = processNodes;
	}

	// Property accessors
	@Id

	@Column(name = "ProcessID", unique = true, nullable = false)

	public Long getProcessId() {
		return this.processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PatientID", nullable = false)

	public Patient getPatient() {
		return this.patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OrganizationID", nullable = false)

	public Organization getOrganization() {
		return this.organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DoctorID", nullable = false)

	public Doctor getDoctor() {
		return this.doctor;
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}

	@Column(name = "process_status", nullable = false, length = 20)

	public String getProcessStatus() {
		return this.processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	@Column(name = "created_at", length = 19)

	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	@Column(name = "updated_at", length = 19)

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Column(name = "completed_at", length = 19)

	public Timestamp getCompletedAt() {
		return this.completedAt;
	}

	public void setCompletedAt(Timestamp completedAt) {
		this.completedAt = completedAt;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "medicalProcesses")

	public Set<ProcessNode> getProcessNodes() {
		return this.processNodes;
	}

	public void setProcessNodes(Set<ProcessNode> processNodes) {
		this.processNodes = processNodes;
	}

}
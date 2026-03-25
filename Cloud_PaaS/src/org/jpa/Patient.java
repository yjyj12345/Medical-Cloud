package org.jpa;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Patient entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "patient", catalog = "cloud_data_resource_connection")

public class Patient implements java.io.Serializable {

	// Fields

	private Long patientId;
	private String username;
	private String password;
	private String phoneNum;
	private String gender;
	private Set<MedicalProcesses> medicalProcesseses = new HashSet<MedicalProcesses>(0);

	// Constructors

	/** default constructor */
	public Patient() {
	}

	/** minimal constructor */
	public Patient(Long patientId, String username, String password) {
		this.patientId = patientId;
		this.username = username;
		this.password = password;
	}

	/** full constructor */
	public Patient(Long patientId, String username, String password, String phoneNum, String gender,
			Set<MedicalProcesses> medicalProcesseses) {
		this.patientId = patientId;
		this.username = username;
		this.password = password;
		this.phoneNum = phoneNum;
		this.gender = gender;
		this.medicalProcesseses = medicalProcesseses;
	}

	// Property accessors
	@Id

	@Column(name = "PatientID", unique = true, nullable = false)

	public Long getPatientId() {
		return this.patientId;
	}

	public void setPatientId(Long patientId) {
		this.patientId = patientId;
	}

	@Column(name = "username", nullable = false)

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "password", nullable = false)

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "phone_num", length = 20)

	public String getPhoneNum() {
		return this.phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	@Column(name = "gender", length = 10)

	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "patient")

	public Set<MedicalProcesses> getMedicalProcesseses() {
		return this.medicalProcesseses;
	}

	public void setMedicalProcesseses(Set<MedicalProcesses> medicalProcesseses) {
		this.medicalProcesseses = medicalProcesseses;
	}

}
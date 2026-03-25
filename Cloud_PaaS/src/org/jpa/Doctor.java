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
 * Doctor entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "doctor", catalog = "cloud_data_resource_connection")

public class Doctor implements java.io.Serializable {

	// Fields

	private Long doctorId;
	private String username;
	private String password;
	private String phoneNum;
	private String gender;
	private String title;
	private String specialty;
	private Long organizationId;
	private Set<MedicalProcesses> medicalProcesseses = new HashSet<MedicalProcesses>(0);

	// Constructors

	/** default constructor */
	public Doctor() {
	}

	/** minimal constructor */
	public Doctor(Long doctorId, String username, String password, String phoneNum, String gender, String title,
			String specialty, Long organizationId) {
		this.doctorId = doctorId;
		this.username = username;
		this.password = password;
		this.phoneNum = phoneNum;
		this.gender = gender;
		this.title = title;
		this.specialty = specialty;
		this.organizationId = organizationId;
	}

	/** full constructor */
	public Doctor(Long doctorId, String username, String password, String phoneNum, String gender, String title,
			String specialty, Long organizationId, Set<MedicalProcesses> medicalProcesseses) {
		this.doctorId = doctorId;
		this.username = username;
		this.password = password;
		this.phoneNum = phoneNum;
		this.gender = gender;
		this.title = title;
		this.specialty = specialty;
		this.organizationId = organizationId;
		this.medicalProcesseses = medicalProcesseses;
	}

	// Property accessors
	@Id

	@Column(name = "DoctorID", unique = true, nullable = false)

	public Long getDoctorId() {
		return this.doctorId;
	}

	public void setDoctorId(Long doctorId) {
		this.doctorId = doctorId;
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

	@Column(name = "phone_num", nullable = false, length = 20)

	public String getPhoneNum() {
		return this.phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	@Column(name = "gender", nullable = false, length = 10)

	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	@Column(name = "title", nullable = false)

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "specialty", nullable = false)

	public String getSpecialty() {
		return this.specialty;
	}

	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}

	@Column(name = "OrganizationID", nullable = false)

	public Long getOrganizationId() {
		return this.organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "doctor")

	public Set<MedicalProcesses> getMedicalProcesseses() {
		return this.medicalProcesseses;
	}

	public void setMedicalProcesseses(Set<MedicalProcesses> medicalProcesseses) {
		this.medicalProcesseses = medicalProcesseses;
	}

}
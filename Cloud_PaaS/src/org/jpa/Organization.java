package org.jpa;

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
 * Organization entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "organization", catalog = "cloud_data_resource_connection")

public class Organization implements java.io.Serializable {

	// Fields

	private Long organizationId;
	private Address address;
	private String organizationName;
	private String organizationType;
	private Long status;
	private Set<MedicalProcesses> medicalProcesseses = new HashSet<MedicalProcesses>(0);
	private Set<Location> locations = new HashSet<Location>(0);

	// Constructors

	/** default constructor */
	public Organization() {
	}

	/** minimal constructor */
	public Organization(Long organizationId, Address address, String organizationName, String organizationType,
			Long status) {
		this.organizationId = organizationId;
		this.address = address;
		this.organizationName = organizationName;
		this.organizationType = organizationType;
		this.status = status;
	}

	/** full constructor */
	public Organization(Long organizationId, Address address, String organizationName, String organizationType,
			Long status, Set<MedicalProcesses> medicalProcesseses, Set<Location> locations) {
		this.organizationId = organizationId;
		this.address = address;
		this.organizationName = organizationName;
		this.organizationType = organizationType;
		this.status = status;
		this.medicalProcesseses = medicalProcesseses;
		this.locations = locations;
	}

	// Property accessors
	@Id

	@Column(name = "OrganizationID", unique = true, nullable = false)

	public Long getOrganizationId() {
		return this.organizationId;
	}

	public void setOrganizationId(Long organizationId) {
		this.organizationId = organizationId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AddressID", nullable = false)

	public Address getAddress() {
		return this.address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	@Column(name = "OrganizationName", nullable = false)

	public String getOrganizationName() {
		return this.organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	@Column(name = "OrganizationType", nullable = false)

	public String getOrganizationType() {
		return this.organizationType;
	}

	public void setOrganizationType(String organizationType) {
		this.organizationType = organizationType;
	}

	@Column(name = "status", nullable = false)

	public Long getStatus() {
		return this.status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "organization")

	public Set<MedicalProcesses> getMedicalProcesseses() {
		return this.medicalProcesseses;
	}

	public void setMedicalProcesseses(Set<MedicalProcesses> medicalProcesseses) {
		this.medicalProcesseses = medicalProcesseses;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "organization")

	public Set<Location> getLocations() {
		return this.locations;
	}

	public void setLocations(Set<Location> locations) {
		this.locations = locations;
	}

}
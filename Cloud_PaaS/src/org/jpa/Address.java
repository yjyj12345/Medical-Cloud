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
 * Address entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "address", catalog = "cloud_data_resource_connection")

public class Address implements java.io.Serializable {

	// Fields

	private Long addressId;
	private String address;
	private String country;
	private Set<Organization> organizations = new HashSet<Organization>(0);

	// Constructors

	/** default constructor */
	public Address() {
	}

	/** minimal constructor */
	public Address(Long addressId) {
		this.addressId = addressId;
	}

	/** full constructor */
	public Address(Long addressId, String address, String country, Set<Organization> organizations) {
		this.addressId = addressId;
		this.address = address;
		this.country = country;
		this.organizations = organizations;
	}

	// Property accessors
	@Id

	@Column(name = "AddressID", unique = true, nullable = false)

	public Long getAddressId() {
		return this.addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}

	@Column(name = "Address")

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "Country")

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "address")

	public Set<Organization> getOrganizations() {
		return this.organizations;
	}

	public void setOrganizations(Set<Organization> organizations) {
		this.organizations = organizations;
	}

}
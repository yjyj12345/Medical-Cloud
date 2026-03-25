package org.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Location entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "location", catalog = "cloud_data_resource_connection")

public class Location implements java.io.Serializable {

	// Fields

	private Long locationId;
	private Organization organization;
	private String locationName;
	private String position;
	private String routeGuide;

	// Constructors

	/** default constructor */
	public Location() {
	}

	/** minimal constructor */
	public Location(Long locationId, Organization organization, String locationName, String position) {
		this.locationId = locationId;
		this.organization = organization;
		this.locationName = locationName;
		this.position = position;
	}

	/** full constructor */
	public Location(Long locationId, Organization organization, String locationName, String position,
			String routeGuide) {
		this.locationId = locationId;
		this.organization = organization;
		this.locationName = locationName;
		this.position = position;
		this.routeGuide = routeGuide;
	}

	// Property accessors
	@Id

	@Column(name = "LocationID", unique = true, nullable = false)

	public Long getLocationId() {
		return this.locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OrganizationID", nullable = false)

	public Organization getOrganization() {
		return this.organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	@Column(name = "locationName", nullable = false)

	public String getLocationName() {
		return this.locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	@Column(name = "position", nullable = false)

	public String getPosition() {
		return this.position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	@Column(name = "routeGuide")

	public String getRouteGuide() {
		return this.routeGuide;
	}

	public void setRouteGuide(String routeGuide) {
		this.routeGuide = routeGuide;
	}

}
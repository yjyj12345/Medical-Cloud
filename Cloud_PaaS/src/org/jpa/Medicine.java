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
 * Medicine entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "medicine", catalog = "cloud_data_resource_connection")

public class Medicine implements java.io.Serializable {

	// Fields

	private Long medicineId;
	private String useMethod;
	private String dosage;
	private String sideEffect;
	private String name;
	private Double price;
	private Set<ProcessNode> processNodes = new HashSet<ProcessNode>(0);

	// Constructors

	/** default constructor */
	public Medicine() {
	}

	/** minimal constructor */
	public Medicine(Long medicineId, String useMethod, String dosage, String sideEffect, String name, Double price) {
		this.medicineId = medicineId;
		this.useMethod = useMethod;
		this.dosage = dosage;
		this.sideEffect = sideEffect;
		this.name = name;
		this.price = price;
	}

	/** full constructor */
	public Medicine(Long medicineId, String useMethod, String dosage, String sideEffect, String name, Double price,
			Set<ProcessNode> processNodes) {
		this.medicineId = medicineId;
		this.useMethod = useMethod;
		this.dosage = dosage;
		this.sideEffect = sideEffect;
		this.name = name;
		this.price = price;
		this.processNodes = processNodes;
	}

	// Property accessors
	@Id

	@Column(name = "MedicineID", unique = true, nullable = false)

	public Long getMedicineId() {
		return this.medicineId;
	}

	public void setMedicineId(Long medicineId) {
		this.medicineId = medicineId;
	}

	@Column(name = "use_method", nullable = false)

	public String getUseMethod() {
		return this.useMethod;
	}

	public void setUseMethod(String useMethod) {
		this.useMethod = useMethod;
	}

	@Column(name = "dosage", nullable = false)

	public String getDosage() {
		return this.dosage;
	}

	public void setDosage(String dosage) {
		this.dosage = dosage;
	}

	@Column(name = "side_effect", nullable = false)

	public String getSideEffect() {
		return this.sideEffect;
	}

	public void setSideEffect(String sideEffect) {
		this.sideEffect = sideEffect;
	}

	@Column(name = "name", nullable = false)

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "price", nullable = false, precision = 10)

	public Double getPrice() {
		return this.price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "medicine")

	public Set<ProcessNode> getProcessNodes() {
		return this.processNodes;
	}

	public void setProcessNodes(Set<ProcessNode> processNodes) {
		this.processNodes = processNodes;
	}

}
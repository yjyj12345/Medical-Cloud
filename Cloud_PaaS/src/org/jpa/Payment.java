package org.jpa;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Payment entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "payment", catalog = "cloud_data_resource_connection")

public class Payment implements java.io.Serializable {

	// Fields

	private Long orderId;
	private ProcessNode processNode;
	private String orderContent;
	private Long amount;
	private String orderStatus;
	private Timestamp createAt;
	private Timestamp paidAt;

	// Constructors

	/** default constructor */
	public Payment() {
	}

	/** minimal constructor */
	public Payment(Long orderId, ProcessNode processNode, String orderContent, Long amount, String orderStatus) {
		this.orderId = orderId;
		this.processNode = processNode;
		this.orderContent = orderContent;
		this.amount = amount;
		this.orderStatus = orderStatus;
	}

	/** full constructor */
	public Payment(Long orderId, ProcessNode processNode, String orderContent, Long amount, String orderStatus,
			Timestamp createAt, Timestamp paidAt) {
		this.orderId = orderId;
		this.processNode = processNode;
		this.orderContent = orderContent;
		this.amount = amount;
		this.orderStatus = orderStatus;
		this.createAt = createAt;
		this.paidAt = paidAt;
	}

	// Property accessors
	@Id

	@Column(name = "OrderID", unique = true, nullable = false)

	public Long getOrderId() {
		return this.orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "NodeID", referencedColumnName = "NodeID", nullable = false),
			@JoinColumn(name = "ProcessID", referencedColumnName = "ProcessID", nullable = false) })

	public ProcessNode getProcessNode() {
		return this.processNode;
	}

	public void setProcessNode(ProcessNode processNode) {
		this.processNode = processNode;
	}

	@Column(name = "order_content", nullable = false)

	public String getOrderContent() {
		return this.orderContent;
	}

	public void setOrderContent(String orderContent) {
		this.orderContent = orderContent;
	}

	@Column(name = "amount", nullable = false)

	public Long getAmount() {
		return this.amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	@Column(name = "order_status", nullable = false, length = 20)

	public String getOrderStatus() {
		return this.orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	@Column(name = "create_at", length = 19)

	public Timestamp getCreateAt() {
		return this.createAt;
	}

	public void setCreateAt(Timestamp createAt) {
		this.createAt = createAt;
	}

	@Column(name = "paid_at", length = 19)

	public Timestamp getPaidAt() {
		return this.paidAt;
	}

	public void setPaidAt(Timestamp paidAt) {
		this.paidAt = paidAt;
	}

}
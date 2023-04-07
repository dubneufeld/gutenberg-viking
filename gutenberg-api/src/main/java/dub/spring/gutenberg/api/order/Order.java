package dub.spring.gutenberg.api.order;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import dub.spring.gutenberg.api.user.Address;
import dub.spring.gutenberg.api.user.PaymentMethod;

// POJO
public class Order implements Serializable {
	
	private static final long serialVersionUID = -1538745091466256842L;
	/**
	 * 
	 */
	
		
	private OrderState state;
	private List<Item> lineItems;
	private Address shippingAddress;
	private PaymentMethod paymentMethod;
	private int subtotal;
	private LocalDateTime date;
	
	private long userId;
		

	public OrderState getState() {
		return state;
	}

	public void setState(OrderState state) {
		this.state = state;
	}

	public List<Item> getLineItems() {
		return lineItems;
	}

	public void setLineItems(List<Item> lineItems) {
		this.lineItems = lineItems;
	}

	public Address getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(Address shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public int getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(int subtotal) {
		this.subtotal = subtotal;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}

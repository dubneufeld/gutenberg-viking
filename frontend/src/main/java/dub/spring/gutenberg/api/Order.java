package dub.spring.gutenberg.api;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/** 
 * In the frontend all database details should be hidden 
 * */
// POJO, not document
public class Order implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2623188979726019146L;

	private LocalDateTime date;
	private long userId;
	
	
	private OrderState state;
	private List<Item> lineItems;
	private Address shippingAddress;
	private PaymentMethod paymentMethod;
	private int subtotal;
	
	
	public Order() {
		this.lineItems = new ArrayList<>();
		this.shippingAddress = new Address();
		this.paymentMethod = new PaymentMethod();
		this.userId = 0;
	}

	
	public int getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(int subtotal) {
		this.subtotal = subtotal;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public void setState(OrderState state) {
		this.state = state;
	}

	public OrderState getState() {
		return state;
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


	public LocalDateTime getDate() {
		return date;
	}


	public void setDate(LocalDateTime date) {
		this.date = date;
	}


	

}

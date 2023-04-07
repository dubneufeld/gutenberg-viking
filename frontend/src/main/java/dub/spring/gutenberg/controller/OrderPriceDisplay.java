package dub.spring.gutenberg.controller;

import java.util.ArrayList;
import java.util.List;

import dub.spring.gutenberg.api.Order;

// wrapper class
public class OrderPriceDisplay {
	
	private long userId;
	
	private Order order;
	
	private double subtotal = 0.0;
	private List<DisplayItemPrice> items;
	
	public OrderPriceDisplay() {
		items = new ArrayList<>();
	}
	
	public double getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}
	public List<DisplayItemPrice> getItems() {
		return items;
	}
	public void setItems(List<DisplayItemPrice> items) {
		this.items = items;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
	
	
	
		
}

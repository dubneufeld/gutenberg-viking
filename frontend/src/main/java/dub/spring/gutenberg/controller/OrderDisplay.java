package dub.spring.gutenberg.controller;

import java.util.List;

import dub.spring.gutenberg.api.Item;

// wrapper class
public class OrderDisplay {

	private double subtotal;
	private List<Item> items;
	
	public OrderDisplay(double subtotal, List<Item> items) {
		this.items = items;
		this.subtotal = subtotal;
	}
	
	public OrderDisplay() {
		
	}
	
	public double getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}
	public List<Item> getItems() {
		return items;
	}
	public void setItems(List<Item> items) {
		this.items = items;
	}
	
	
	
	
}

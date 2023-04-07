package dub.spring.gutenberg.service;

import java.time.LocalDateTime;
import java.util.List;

import dub.spring.gutenberg.api.Item;

/** 
 *Encapsulating class used for cart edition 
 * */
public class EditCart {
	
	private long userId;
	private LocalDateTime date;
	
	private List<Item> items;
	
	public EditCart() {}
	
	public EditCart(long userId, LocalDateTime date, List<Item> items) {
		this.userId = userId;
		this.date = date;
		this.items = items;
	}
	
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public List<Item> getItems() {
		return items;
	}
	public void setItems(List<Item> items) {
		this.items = items;
	}
	

}

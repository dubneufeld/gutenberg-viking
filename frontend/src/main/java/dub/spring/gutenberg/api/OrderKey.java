package dub.spring.gutenberg.api;

import java.io.Serializable;
import java.time.LocalDateTime;

// wrapper class
public class OrderKey implements Serializable {

	private static final long serialVersionUID = 1L;
	long userId;
	LocalDateTime date;
	
	public OrderKey() {
		
	}
	
	public OrderKey(long userId, LocalDateTime date) {
		this.date = date;
		this.userId = userId;
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
	
	@Override
	public String toString() {
		return "" + this.userId + "," + this.date;
	}
}

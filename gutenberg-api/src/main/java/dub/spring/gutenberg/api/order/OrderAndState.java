package dub.spring.gutenberg.api.order;

import java.time.LocalDateTime;

//wrapper class
public class OrderAndState {

	long userId;
	LocalDateTime date;
	OrderState state;
	
	public OrderAndState() { }
	
	public OrderAndState(long userId, LocalDateTime date, OrderState state) {
		this.userId = userId;
		this.date = date;
		this.state = state;
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

	public OrderState getState() {
		return state;
	}
	public void setState(OrderState state) {
		this.state = state;
	}
	
}

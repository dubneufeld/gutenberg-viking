package dub.spring.gutenberg.service;

import java.time.LocalDateTime;

// wrapper class
public class OrderAndBook {

	long userId;
	LocalDateTime date;
	
	long bookId;
	
	public OrderAndBook() { }
	
	public OrderAndBook(long userId, LocalDateTime date, long bookId) {
		this.userId = userId;
		this.date = date;
		this.bookId = bookId;
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

	public long getBookId() {
		return bookId;
	}
	public void setBookId(long bookId) {
		this.bookId = bookId;
	}
	
	
	
}

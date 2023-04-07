package dub.spring.gutenberg.api.order;

import java.time.LocalDateTime;

public class OrderAndBook {

	long userId;
	LocalDateTime date;
	
	int bookId;
	
	public OrderAndBook() { }
	
	public OrderAndBook(int userId, int bookId) {
		this.userId = userId;
		this.bookId = bookId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	
	
	
}

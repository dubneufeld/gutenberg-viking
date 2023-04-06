package dub.spring.gutenberg.services;

// Yet another encapsulation object
public class BookUser {

	private int userId;
	private int bookId;
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getBookId() {
		return bookId;
	}
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}
	
	@Override
	public String toString() {
		return userId + " " + bookId;
	}
	

}

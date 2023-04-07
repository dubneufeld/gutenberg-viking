package dub.spring.gutenberg.api;

public class Item {
	
	private long bookId;
	private int quantity;
	
	public Item() {}
	
	public Item(Item that) {
		this.bookId = that.bookId;
		this.quantity = that.quantity;
	}
	
	public Item(long bookId, int quantity) {
		this.bookId = bookId;
		this.quantity = quantity; 
	}
	
	public long getBookId() {
		return bookId;
	}
	public void setBookId(long bookId) {
		this.bookId = bookId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
}

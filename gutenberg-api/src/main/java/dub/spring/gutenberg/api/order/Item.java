package dub.spring.gutenberg.api.order;

// encapsulation object
public class Item {
	
	private int bookId;
	private int quantity;
	
	public Item() {}
	
	public Item(int bookId, int quantity) {
		this.bookId = bookId;
		this.quantity = quantity;
	}
	
	public Item(Item that) {
		this.bookId = that.bookId;
		this.quantity = that.quantity;
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	
	
}

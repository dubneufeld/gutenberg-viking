package dub.spring.gutenberg.services;

// wrapper
public class BookRating {

	private int bookId;
	private Double bookRating;
	

	public Double getBookRating() {
		return bookRating;
	}
	public void setBookRating(Double bookRating) {
		this.bookRating = bookRating;
	}
	public int getBookId() {
		return bookId;
	}
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}
	
	
}

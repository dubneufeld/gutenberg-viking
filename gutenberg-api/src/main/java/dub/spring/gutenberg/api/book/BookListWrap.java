package dub.spring.gutenberg.api.book;

import java.util.List;

// wrapper object
public class BookListWrap {
	
	List<Integer> bookIds;
	
	public BookListWrap() {}
	
	public BookListWrap(List<Integer> bookIds) {
		this.bookIds = bookIds;
	}
	

	public List<Integer> getBookIds() {
		return bookIds;
	}

	public void setBookIds(List<Integer> bookIds) {
		this.bookIds = bookIds;
	}
	
	

}

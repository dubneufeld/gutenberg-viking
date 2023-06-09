package dub.spring.gutenberg.controller;

import java.util.List;

import dub.spring.gutenberg.api.Book;



public class DisplayOthers {
		
	private Book book;
	private List<Book> otherBooks;
		
	public DisplayOthers(Book book, List<Book> otherBooks) {
		this.book = book;
		this.otherBooks = otherBooks;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public List<Book> getOtherBooks() {
		return otherBooks;
	}

	public void setOtherBooks(List<Book> otherBooks) {
		this.otherBooks = otherBooks;
	}
}

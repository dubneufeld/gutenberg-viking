package dub.spring.gutenberg.service;

import java.util.ArrayList;
import java.util.List;

import dub.spring.gutenberg.api.Book;

public class BookWebList {
	
	List<Book> books;
	
	public BookWebList() { }
	
	public BookWebList(List<Book> books) {
		this.books = books;
	}

	public List<Book> getBooks() {
		return books;
	}

	public void setBooks(List<Book> books) {
		this.books = books;
	}
	

}

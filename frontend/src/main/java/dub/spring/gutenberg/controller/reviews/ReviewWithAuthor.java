package dub.spring.gutenberg.controller.reviews;

import dub.spring.gutenberg.api.Review;

// wrapper class
public class ReviewWithAuthor {

	Review review;
	String author;
	boolean votedByUser = false;
	
	public ReviewWithAuthor(Review review, String author) {
		this.review = review;
		this.author = author;
	}
	
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Review getReview() {
		return review;
	}
	public void setReview(Review review) {
		this.review = review;
	}

	public boolean isVotedByUser() {
		return votedByUser;
	}

	public void setVotedByUser(boolean votedByUser) {
		this.votedByUser = votedByUser;
	}
	
}
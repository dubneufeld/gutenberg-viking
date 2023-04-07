package dub.spring.gutenberg.service;

import dub.spring.gutenberg.api.Review;

public class ReviewWrap {
	
	Review review = null;
	boolean ok = false;
	
	public ReviewWrap() {}
	
	public ReviewWrap(Review review, boolean ok) {
		this.review = review;
		this.ok = ok;
	}
	
	public Review getReview() {
		return review;
	}
	public void setReview(Review review) {
		this.review = review;
	}
	public boolean isOk() {
		return ok;
	}
	public void setOk(boolean ok) {
		this.ok = ok;
	}
	
	

}

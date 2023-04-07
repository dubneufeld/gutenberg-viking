package dub.spring.gutenberg.api.order;

import java.util.List;

// encapsulating helper class
public class UserAndReviewedBooks {
	
	private long userId;
	private List<Integer> reviewedBookIds;
	private int outLimit;
	
	public UserAndReviewedBooks() {
		
	}

	public UserAndReviewedBooks(
							long userId, 
							List<Integer> reviewedBookIds,
							int outLimit) {
		this.userId = userId;
		this.reviewedBookIds = reviewedBookIds;	
		this.outLimit = outLimit;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public List<Integer> getReviewedBookIds() {
		return reviewedBookIds;
	}

	public void setReviewedBookIds(List<Integer> reviewedBookIds) {
		this.reviewedBookIds = reviewedBookIds;
	}

	public int getOutLimit() {
		return outLimit;
	}

	public void setOutLimit(int outLimit) {
		this.outLimit = outLimit;
	}
	
	
	
}

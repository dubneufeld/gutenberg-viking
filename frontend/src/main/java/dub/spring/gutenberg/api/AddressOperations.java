package dub.spring.gutenberg.api;

import dub.spring.gutenberg.api.Address;

public class AddressOperations {

	private long userId;
	private Address address;
	private ProfileOperations op;
	

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public ProfileOperations getOp() {
		return op;
	}

	public void setOp(ProfileOperations op) {
		this.op = op;
	}


	
}

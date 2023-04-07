package dub.spring.gutenberg.api.user;

public class AddressOperations {

	private int userId;
	private Address address;
	private ProfileOperations op;
	
	
	

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
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

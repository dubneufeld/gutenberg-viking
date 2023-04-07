package dub.spring.gutenberg.api.user;

public class PaymentOperations {
	
	private int userId;
	private PaymentMethod paymentMethod;
	private ProfileOperations op;
	
	
	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public ProfileOperations getOp() {
		return op;
	}
	public void setOp(ProfileOperations op) {
		this.op = op;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	

}

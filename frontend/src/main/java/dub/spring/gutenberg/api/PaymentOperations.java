package dub.spring.gutenberg.api;

public class PaymentOperations {
	
	private long userId;
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
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	

}

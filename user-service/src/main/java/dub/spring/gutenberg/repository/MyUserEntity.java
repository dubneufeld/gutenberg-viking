package dub.spring.gutenberg.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import dub.spring.gutenberg.api.user.Address;
import dub.spring.gutenberg.api.user.PaymentMethod;
import dub.spring.gutenberg.api.user.UserAuthority;

@Document(collection = "users")
public class MyUserEntity {

	@Id
	private String id;
	
	@Indexed(unique = true)
	private int userId;
	
	@Indexed(unique = true)
	private String username;
	
	private String hashedPassword;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;
	
	private List<Address> addresses;
	
	private List<PaymentMethod> paymentMethods;
	
	private Set<UserAuthority> authorities;
	
	private int mainPayMeth;
	private int mainShippingAddress;
	
	public MyUserEntity() {
		this.addresses = new ArrayList<>();
		this.paymentMethods = new ArrayList<>();
		this.mainPayMeth = 0;
		this.mainShippingAddress = 0;
		this.authorities = new HashSet<>();
	}
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getHashedPassword() {
		return hashedPassword;
	}

	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}

	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getMainPayMeth() {
		return mainPayMeth;
	}

	public void setMainPayMeth(int mainPayMeth) {
		this.mainPayMeth = mainPayMeth;
	}

	public void setPaymentMethods(List<PaymentMethod> paymentMethods) {
		this.paymentMethods = paymentMethods;
	}

	public List<PaymentMethod> getPaymentMethods() {
		return paymentMethods;
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	public int getMainShippingAddress() {
		return mainShippingAddress;
	}

	public void setMainShippingAddress(int mainShippingAddress) {
		this.mainShippingAddress = mainShippingAddress;
	}

	public Set<UserAuthority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Set<UserAuthority> authorities) {
		this.authorities = authorities;
	}
}


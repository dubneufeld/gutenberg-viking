package dub.spring.gutenberg.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;

import dub.spring.gutenberg.UserAuthority;

// POJO
public class MyUser implements UserDetails, Serializable {
	
	private static final long serialVersionUID = 1L;

	private long userId;
	
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
	
	public MyUser() {
		this.addresses = new ArrayList<>();
		this.paymentMethods = new ArrayList<>();
		this.mainPayMeth = 0;
		this.mainShippingAddress = 0;
		this.authorities = new HashSet<>();
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

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	@Override
	public String getPassword() {
	
		return this.getHashedPassword();
	}
	
	
}


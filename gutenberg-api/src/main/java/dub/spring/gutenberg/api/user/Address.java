package dub.spring.gutenberg.api.user;

import org.springframework.lang.NonNull;

//import com.dub.spring.controller.users.Selectable;

/** Not a document */
public class Address {
	
	@NonNull
	private String street = "";
	
	@NonNull
	private String city = "";
	private String zip = "";
	@NonNull
	private String state = "";

	@NonNull
	private String country = "";
	
	public Address() {}
	
	public Address(String street, String city, String zip, 
			String state, String country) {
		this.street = street;
		this.city = city;
		this.zip = zip;
		this.state = state != null ? state : "";
		this.country = country;
	}
	
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	@Override
	public boolean equals(Object that) {
		if (!(that instanceof Address)) {
			return false;
		} else {
			Address thatAddress = (Address)that;
			return this.street.equals(thatAddress.street)
			&& this.city.equals(thatAddress.city)
			&& this.zip.equals(thatAddress.zip)
			&& this.state.equals(thatAddress.state)
			&& this.country.equals(thatAddress.country);
		}
	}
}


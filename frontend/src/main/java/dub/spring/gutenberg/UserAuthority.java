package dub.spring.gutenberg;

import org.springframework.security.core.GrantedAuthority;

public class UserAuthority implements GrantedAuthority {

	private static final long serialVersionUID = 1L;
	private String authority;

    public UserAuthority() { }

    public UserAuthority(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}

package dub.spring.gutenberg;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "gutenberg")
public class GutenbergProperties {

	private String baseBooksUrl;
	private String baseReviewsUrl;
	private String baseOrdersUrl;
	private	String baseUsersUrl;
	private	String compositeUrl;
	
	
	public String getCompositeUrl() {
		return compositeUrl;
	}
	public void setCompositeUrl(String compositeUrl) {
		this.compositeUrl = compositeUrl;
	}
	public String getBaseBooksUrl() {
		return baseBooksUrl;
	}
	public void setBaseBooksUrl(String baseBooksUrl) {
		this.baseBooksUrl = baseBooksUrl;
	}
	public String getBaseReviewsUrl() {
		return baseReviewsUrl;
	}
	public void setBaseReviewsUrl(String baseReviewsUrl) {
		this.baseReviewsUrl = baseReviewsUrl;
	}
	public String getBaseOrdersUrl() {
		return baseOrdersUrl;
	}
	public void setBaseOrdersUrl(String baseOrdersUrl) {
		this.baseOrdersUrl = baseOrdersUrl;
	}
	public String getBaseUsersUrl() {
		return baseUsersUrl;
	}
	public void setBaseUsersUrl(String baseUsersUrl) {
		this.baseUsersUrl = baseUsersUrl;
	}
	
	

}

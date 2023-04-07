package dub.spring.gutenberg.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Category {
	
	private int categoryId;
	
	private String slug;
	
	private String name;
	
	private String description;
		
	private List<Map<String, Object>> ancestors;
	
	private int parentId;
	private List<Integer> children;
	
	public Category() {
		this.children = new ArrayList<>();
	}
	
	

	

	public int getParentId() {
		return parentId;
	}





	public void setParentId(int parentId) {
		this.parentId = parentId;
	}





	public List<Integer> getChildren() {
		return children;
	}





	public void setChildren(List<Integer> children) {
		this.children = children;
	}





	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Map<String, Object>> getAncestors() {
		return ancestors;
	}

	public void setAncestors(List<Map<String, Object>> ancestors) {
		this.ancestors = ancestors;
	}
	
	
}
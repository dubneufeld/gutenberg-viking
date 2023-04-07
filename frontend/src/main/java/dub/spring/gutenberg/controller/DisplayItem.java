package dub.spring.gutenberg.controller;

import dub.spring.gutenberg.api.Item;

public class DisplayItem extends Item {

	protected String title;
	
	public DisplayItem(Item source)  {
		super(source);
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}

package dub.spring.gutenberg.controller.users;

import dub.spring.gutenberg.service.Selectable;

public class SelectableAndIndex {

	Selectable selectable;// marker interface not class
	int index;
	boolean primary;// new field 
	
	public SelectableAndIndex() {}
	
	public SelectableAndIndex(Selectable selectable, int index, int main) {
		this.selectable = selectable;
		this.index = index;
		this.primary = (index == main);
	}

	public Selectable getSelectable() {
		return selectable;
	}

	public void setSelectable(Selectable selectable) {
		this.selectable = selectable;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}
	
}
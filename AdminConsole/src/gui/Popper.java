package gui;

public class Popper {
	String[] elements;
	private int size;
	public Popper(int size) {
		elements = new String[size];
		this.size = size;
	}
	
	public void pop(String element) {
		String[] newArray = new String[this.size];
		newArray[this.size -1] = element;
		for(int i = size-2; i>0; i--) {
			newArray[i] = elements[i+1];
		}
		this.elements = newArray;
	}

}

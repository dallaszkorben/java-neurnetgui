package hu.akoel.neurnetgui.accessories;

public class MutableInteger {

	private int value;

	public MutableInteger(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}

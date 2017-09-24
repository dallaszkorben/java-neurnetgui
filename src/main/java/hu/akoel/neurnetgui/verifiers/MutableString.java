package hu.akoel.neurnetgui.verifiers;

public class MutableString {

	private String value;

	public MutableString(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}

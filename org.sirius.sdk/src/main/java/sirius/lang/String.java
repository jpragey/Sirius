package sirius.lang;

public class String {
	public java.lang.String value;

	public String(java.lang.String value) {
		super();
		this.value = value;
	}

	public String() {
		super();
		this.value = "";
	}

	public java.lang.String getJvmValue() {
		return value;
	}
	
//	public String add(String other) {
//		return new String(this.value + other.value);
//	}
//	public String sub(String other) {
//		return new String(this.value - other.value);
//	}
//	public String mult(String other) {
//		return new String(this.value * other.value);
//	}
//	public String div(String other) {
//		return new String(this.value / other.value);
//	}
	
	
}

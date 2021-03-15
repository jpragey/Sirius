package sirius.lang;

public class Integer {
	public int value;

	public Integer(int value) {
		super();
		this.value = value;
	}

	public Integer() {
		super();
		this.value = 0;
	}

	public int getValue() {
		return value;
	}
	
	public Integer add(Integer other) {
		return new Integer(this.value + other.value);
	}
	public Integer sub(Integer other) {
		return new Integer(this.value - other.value);
	}
	public Integer mult(Integer other) {
		return new Integer(this.value * other.value);
	}
	public Integer div(Integer other) {
		return new Integer(this.value / other.value);
	}
	
	
}

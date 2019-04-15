package concurrency.cookbook.chapter06.recipe03;

public class Counter {
	private String value;
	private int counter;
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}
	public void increment() {
		counter ++;
	}

}

package concurrency.cookbook.chapter01.recipe07;

/**
 * Processing uncontrolled exceptions in a thread
 * @author cuilj
 *
 */
public class Main {
	public static void main(String[] args) {
		Task task = new Task();
		Thread thread = new Thread(task);
		thread.setUncaughtExceptionHandler(new ExceptionHandler());
		thread.start();
	}
}

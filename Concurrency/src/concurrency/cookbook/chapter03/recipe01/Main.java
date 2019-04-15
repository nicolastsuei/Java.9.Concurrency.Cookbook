package concurrency.cookbook.chapter03.recipe01;

/**
 * Controlling concurrent access to one or more copies of a resource.
 * @author cuilj
 *
 */
public class Main {

	public static void main(String[] args) {
		PrintQueue printQueue = new PrintQueue();
		Thread[] threads = new Thread[12];
		for (int i = 0; i < threads.length ; i ++){
			threads[i] = new Thread(new Job(printQueue), "Thread" + i);
		}
		for (int i = 0; i < threads.length ; i ++){
			threads[i].start();
		}
	}

}

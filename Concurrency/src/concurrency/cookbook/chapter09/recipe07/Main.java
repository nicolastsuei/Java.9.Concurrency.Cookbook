package concurrency.cookbook.chapter09.recipe07;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Analyzing concurrent code with FindBugs
 * @author cuilijian
 *
 */
public class Main {

	public static void main(String[] args) {
		ReentrantLock lock=new ReentrantLock();
		for (int i=0; i<10; i++) {
			Task task=new Task(lock);
			Thread thread=new Thread(task);
			thread.run();
		}
	}

}

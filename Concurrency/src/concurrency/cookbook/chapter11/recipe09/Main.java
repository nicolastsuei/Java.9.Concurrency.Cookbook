package concurrency.cookbook.chapter11.recipe09;

import java.util.concurrent.locks.ReentrantLock;
/**
 * Avoiding the use of blocking operations inside a lock
 * @author cuilijian
 *
 */
public class Main {

	public static void main(String[] args) {
		ReentrantLock lock=new ReentrantLock();
		for (int i=0; i<10; i++) {
			Task task=new Task(lock);
			Thread thread=new Thread(task);
			thread.start();
		}
	}
}

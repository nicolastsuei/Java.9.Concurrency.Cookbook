package concurrency.cookbook.chapter01.recipe06;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Creating and running a daemon thread
 * @author cuilj
 *
 */
public class Main {
	public static void main(String[] args) {
		Deque<Event> deque = new ConcurrentLinkedDeque<Event>();
		WriterTask writer = new WriterTask(deque);
		for(int i=0; i < Runtime.getRuntime().availableProcessors(); i ++){
			Thread thread = new Thread(writer);
			thread.start();
		}
		CleanerTask cleaner = new CleanerTask(deque);
		cleaner.start();
	}

}

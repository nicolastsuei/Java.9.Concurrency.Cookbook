package concurrency.cookbook.chapter10.recipe05;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Monitoring a Semaphore class
 * @author cuilijian
 *
 */
public class Main {

	public static void main(String[] args) throws Exception{
		Semaphore semaphore=new Semaphore(3);
		Thread threads[]=new Thread[10];
		for (int i=0; i<threads.length; i++) {
			Task task=new Task(semaphore);
			threads[i]=new Thread(task);
			threads[i].start();
			TimeUnit.MILLISECONDS.sleep(200);
			showLog(semaphore);
		}
		for (int i=0; i<5; i++) {
			showLog(semaphore);
			TimeUnit.SECONDS.sleep(1);
		}
	}

	private static void showLog(Semaphore semaphore) {
		System.out.printf("********************\n");
		System.out.printf("Main: Semaphore Log\n");
		System.out.printf("Main: Semaphore: Avalaible Permits: %d\n", semaphore.availablePermits());
		System.out.printf("Main: Semaphore: Queued Threads: %s\n", semaphore.hasQueuedThreads());
		System.out.printf("Main: Semaphore: Queue Length: %d\n", semaphore.getQueueLength());
		System.out.printf("Main: Semaphore: Fairness: %s\n", semaphore.isFair());
		System.out.printf("********************\n"); 
	}
}

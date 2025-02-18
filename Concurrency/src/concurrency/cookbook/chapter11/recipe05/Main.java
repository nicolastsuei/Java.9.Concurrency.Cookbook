package concurrency.cookbook.chapter11.recipe05;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Delegating the management of threads to executors
 * @author cuilijian
 *
 */
public class Main {

	public static void main(String[] args) {
		Thread threads[]=new Thread[1000];
		Date start,end;
		start=new Date();
		for (int i=0; i<threads.length; i++) {
			Task task=new Task();
			threads[i]=new Thread(task);
			threads[i].start();
		}
		for (int i=0; i<threads.length; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		end=new Date();
		System.out.printf("Main: Threads: %d\n", (end.getTime()-start.getTime()));

		ThreadPoolExecutor executor=(ThreadPoolExecutor)Executors.newCachedThreadPool();
		start=new Date();
		for (int i=0; i<threads.length; i++) {
			Task task=new Task();
			executor.execute(task);
		}
		executor.shutdown();
		try {
			executor.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		end=new Date();
		System.out.printf("Main: Executor: %d\n", (end.getTime()-start.getTime()));
	}

}

package concurrency.cookbook.chapter04.recipe05;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Running a task in an executor after a delay
 * @author cuilj
 *
 */
public class Main {
	public static void main(String[] args) {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		System.out.printf("Main : Starting at : %s\n", new Date());
		for (int i = 0 ; i < 5 ; i ++){
			Task task = new Task("Task " + i);
			executor.schedule(task, i+1, TimeUnit.SECONDS);
		}

		executor.shutdown();
		try {
			executor.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.printf("Main : Ends at : %s\n", new Date());
	}

}

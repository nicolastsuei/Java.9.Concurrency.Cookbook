package concurrency.cookbook.chapter08.recipe05;

import java.util.concurrent.TimeUnit;

public class Task implements Runnable {

	@Override
	public void run() {
	System.out.printf("Task: Begin.\n");
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.printf("Task: End.\n");
	}
}

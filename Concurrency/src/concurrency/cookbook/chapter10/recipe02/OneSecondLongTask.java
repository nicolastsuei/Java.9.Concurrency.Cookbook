package concurrency.cookbook.chapter10.recipe02;

import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

public class OneSecondLongTask extends RecursiveAction{

	@Override
	protected void compute() {
		System.out.printf("Task: Starting.\n");
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.printf("Task: Finish.\n");
	}

}

package concurrency.cookbook.chapter10.recipe02;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * Processing uncontrolled exceptions in a ForkJoinPool class
 * @author cuilijian
 *
 */
public class Main {

	public static void main(String[] args) {
		OneSecondLongTask task=new OneSecondLongTask();
		Handler handler = new Handler();
		AlwaysThrowsExceptionWorkerThreadFactory factory=new AlwaysThrowsExceptionWorkerThreadFactory();
		ForkJoinPool pool=new ForkJoinPool(2,factory,handler,false);
		pool.execute(task);
		
		pool.shutdown();
		
		try {
			pool.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.printf("Task: Finish.\n");
	}

}

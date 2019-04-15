package concurrency.cookbook.chapter08.recipe04;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Using our ThreadFactory in an Executor object
 * @author cuilijian
 *
 */
public class Main {

	public static void main(String[] args) throws Exception{
		MyThreadFactory threadFactory=new MyThreadFactory("MyThreadFactory");
		ExecutorService executor=Executors.newCachedThreadPool(threadFactory);
		MyTask task=new MyTask();
		executor.submit(task);
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.DAYS);
		System.out.printf("Main: End of the program.\n");
	}

}

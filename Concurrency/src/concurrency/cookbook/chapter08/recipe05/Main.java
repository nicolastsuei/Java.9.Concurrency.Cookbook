package concurrency.cookbook.chapter08.recipe05;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Customizing tasks running in a scheduled thread pool
 * @author cuilijian
 *
 */
public class Main {

	public static void main(String[] args)  throws Exception{
		MyScheduledThreadPoolExecutor executor=new MyScheduledThreadPoolExecutor(4);
		Task task=new Task();
		System.out.printf("Main: %s\n",new Date());
		executor.schedule(task, 1, TimeUnit.SECONDS);
		TimeUnit.SECONDS.sleep(3);
		task=new Task();
		System.out.printf("Main: %s\n",new Date());
		executor.scheduleAtFixedRate(task, 1, 3, TimeUnit.SECONDS);
		TimeUnit.SECONDS.sleep(10);
		
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.DAYS);
		
		System.out.printf("Main: End of the program.\n");
	}

}

package concurrency.cookbook.chapter10.recipe01;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Processing results for Runnable objects in the Executor framework
 * @author cuilijian
 *
 */
public class Main {

	public static void main(String[] args) {
		ExecutorService executor = Executors.newCachedThreadPool();
		
		FileSearch system=new FileSearch("C:\\Windows", "log");
		FileSearch apps=new FileSearch("C:\\Program Files","log");
		FileSearch documents=new FileSearch("C:\\Documents And Settings","log");
		
		Task systemTask=new Task(system,null);
		Task appsTask=new Task(apps,null);
		Task documentsTask=new Task(documents,null);
		
		executor.submit(systemTask);
		executor.submit(appsTask);
		executor.submit(documentsTask);
		
		executor.shutdown();
		
		try {
			executor.awaitTermination(1, TimeUnit.DAYS);
			} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		try {
			System.out.printf("Main: System Task: Number of Results: %d\n", systemTask.get().size());
			System.out.printf("Main: App Task: Number of Results: %d\n", appsTask.get().size());
			System.out.printf("Main: Documents Task: Number of Results: %d\n",documentsTask.get().size());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

}

package concurrency.cookbook.chapter11.recipe08;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * Using the fork/join framework instead of executors
 * @author cuilijian
 *
 */
public class Main {
	public static void main(String[] args) {
		int array[]=new int[100000];
		Task task=new Task(array);
		ExecutorService executor=Executors.newCachedThreadPool();
		Date start,end;
		start=new Date();
		executor.execute(task);
		executor.shutdown();
		try {
			executor.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		end=new Date();
		System.out.printf("Main: Executor: %d\n", (end.getTime()-start.getTime()));
		
		TaskFJ taskFJ=new TaskFJ(array,1,100000);
		ForkJoinPool pool=new ForkJoinPool();
		start=new Date();
		pool.execute(taskFJ);
		pool.shutdown();
		try {
			pool.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		end=new Date();
		System.out.printf("Core: Fork/Join: %d\n", (end.getTime()-start.getTime()));
		
	}
}

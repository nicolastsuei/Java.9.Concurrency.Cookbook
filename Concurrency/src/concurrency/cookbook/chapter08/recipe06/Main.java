package concurrency.cookbook.chapter08.recipe06;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * Implementing the ThreadFactory interface to generate custom threads for the fork/join framework
 * @author cuilijian
 *
 */
public class Main {

	public static void main(String[] args)  throws Exception{
		MyWorkerThreadFactory factory=new MyWorkerThreadFactory();

		ForkJoinPool pool=new ForkJoinPool(4, factory, null, false);

		int array[]=new int[100000];
		for (int i=0; i<array.length; i++){
			array[i]=1;
		}
		
		MyRecursiveTask task=new MyRecursiveTask(array,0,array.length);
		pool.execute(task);
		task.join();
		pool.shutdown();
		pool.awaitTermination(1, TimeUnit.DAYS);
		System.out.printf("Main: Result: %d\n",task.get());
		System.out.printf("Main: End of the program\n");
	}

}

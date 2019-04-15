package concurrency.cookbook.chapter04.recipe08;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Controlling a task finishing in an executor
 * @author cuilj
 *
 */
public class Main {

	public static void main(String[] args) {
		ExecutorService executor = Executors.newCachedThreadPool();
		ResultTask resultTasks[] = new ResultTask[5];
		
		for(int i = 0 ; i < 5 ; i ++){
			ExecutableTask executableTask = new ExecutableTask("Task " +i);
			resultTasks[i] = new ResultTask(executableTask);
			executor.submit(resultTasks[i]);
		}
		
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (int i = 0 ; i < resultTasks.length ; i ++){
			try {
				if(!resultTasks[i].isCancelled()) {
					System.out.printf("%s\n", resultTasks[i].get());
				}
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		executor.shutdown();
	}

}

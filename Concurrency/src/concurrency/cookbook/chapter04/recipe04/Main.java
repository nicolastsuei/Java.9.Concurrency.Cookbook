package concurrency.cookbook.chapter04.recipe04;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Running multiple tasks and processing all the results
 * @author cuilj
 *
 */
public class Main {

	public static void main(String[] args) {
		ExecutorService executor = (ExecutorService)Executors.newCachedThreadPool();
		List<Task> taskList = new ArrayList<>();
		for(int i = 0 ; i < 10 ; i ++){
			Task task = new Task("Task-" + i);
			taskList.add(task);
		}

		List<Future<Result>> resultList = null;
		try {
			resultList = executor.invokeAll(taskList);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		executor.shutdown();
		
		System.out.printf("Main : Printing the results");
		for (int i = 0 ; i < resultList.size() ; i ++){
			Future<Result> future =resultList.get(i);
			Result result;
			try {
				result = future.get();
				System.out.printf(result.getName() + " : " + result.getValue() + "\n");
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

}

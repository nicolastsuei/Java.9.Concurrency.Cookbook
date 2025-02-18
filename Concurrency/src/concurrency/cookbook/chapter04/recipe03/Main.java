package concurrency.cookbook.chapter04.recipe03;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Running multiple tasks and processing the first result
 * @author cuilj
 *
 */
public class Main {
	public static void main(String[] args) {
		
		String username = "test";
		String password = "test";
		
		UserValidator ldapValidator = new UserValidator("LDAP");
		UserValidator dbValidator = new UserValidator("DataBase");
		
		ValidatorTask ldapTask = new ValidatorTask(ldapValidator, username, password);
		ValidatorTask dbTask = new ValidatorTask(dbValidator, username, password);
		
		List<ValidatorTask> taskList = new ArrayList<>();
		taskList.add(ldapTask);
		taskList.add(dbTask);
		
		ExecutorService executor = (ExecutorService)Executors.newCachedThreadPool();
		String result;
		
		try {
			result = executor.invokeAny(taskList);
			System.out.printf("Main : Result : %s\n", result);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		executor.shutdown();
		System.out.printf("Main : End of the Execution\n");
	}

}

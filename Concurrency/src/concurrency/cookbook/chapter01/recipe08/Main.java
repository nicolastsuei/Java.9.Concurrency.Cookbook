package concurrency.cookbook.chapter01.recipe08;

import java.util.concurrent.TimeUnit;

/**
 * Using thread local variables
 * @author cuilj
 *
 */
public class Main {
	public static void main(String[] args) {
//		UnsafeTask task = new UnsafeTask();
		SafeTask task = new SafeTask();
		for(int i = 0; i<10 ; i++){
			Thread thread = new Thread(task);
			thread.start();
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}

package concurrency.cookbook.chapter01.recipe05;

import java.util.Date;

/**
 * Waiting for the finalization of a thread
 * @author cuilj
 *
 */
public class Main {
	public static void main(String[] args) {
		DataSourcesLoader dsLoader = new DataSourcesLoader();
		Thread thread1 = new Thread(dsLoader, "DataSourcesLoader");

		NetworkConnectionsLoader ncLoader = new NetworkConnectionsLoader();
		Thread thread2 = new Thread(ncLoader, "NetworkConnectionsLoader");
		
		thread1.start();
		thread2.start();
		
		try {
			thread1.join();
			thread2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.printf("Main: Configuration has been loaded: %s\n", new Date());
	}

}

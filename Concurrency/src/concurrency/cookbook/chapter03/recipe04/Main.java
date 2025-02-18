package concurrency.cookbook.chapter03.recipe04;

import java.util.concurrent.Phaser;

/**
 * Running concurrent-phased tasks
 * @author cuilj
 *
 */
public class Main {

	public static void main(String[] args) {
		Phaser phaser = new Phaser(3);
		FileSearch system = new FileSearch("C:\\Windows", "log", phaser);
		FileSearch apps = new FileSearch("C:\\Program Files", "log", phaser);
		FileSearch documents = new FileSearch("C:\\Documents And Settings", "log", phaser);
		
		Thread systemThread = new Thread(system, "System");
		systemThread.start();
		
		Thread appsThread = new Thread(apps, "Apps");
		appsThread.start();
		
		Thread documentsThread = new Thread(documents, "Documents");
		documentsThread.start();
		
		try {
			systemThread.join();
			appsThread.join();
			documentsThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.printf("Terminated : " + phaser.isTerminated() + "\n");
	}

}

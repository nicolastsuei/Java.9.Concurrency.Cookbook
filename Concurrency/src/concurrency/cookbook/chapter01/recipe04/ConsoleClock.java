package concurrency.cookbook.chapter01.recipe04;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ConsoleClock implements Runnable {

	@Override
	public void run() {
		for(int i = 0 ; i < 20 ; i ++){
			System.out.printf("%s\n", new Date());
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				System.out.printf("The ConsoleClock has been interrupted");
			}
		}
	}
}

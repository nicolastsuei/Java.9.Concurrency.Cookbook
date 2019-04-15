package concurrency.cookbook.chapter02.recipe01;

import java.util.concurrent.TimeUnit;

public class Sensor implements Runnable{
	private ParkingStats stats;
	public Sensor(ParkingStats stats) {
		this.stats = stats;
	}
	
	@Override
	public void run() {
		for(int i = 0; i < 10; i++){
			stats.carComeIn();
			stats.carComeIn();
			try {
				TimeUnit.MILLISECONDS.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			stats.motorComeIn();
			try {
				TimeUnit.MILLISECONDS.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			stats.motorGoOut();
			stats.carGoOut();
			stats.carGoOut();
		}
	}

}

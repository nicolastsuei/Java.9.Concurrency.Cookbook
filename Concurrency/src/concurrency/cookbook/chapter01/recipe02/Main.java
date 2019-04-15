package concurrency.cookbook.chapter01.recipe02;

/**
 * Interrupting a thread
 * @author cuilj
 *
 */
public class Main {
	public static void main(String[] args) {
		Thread task = new PrimeGenerator();
		task.start();
		
		try{
			Thread.sleep(5000);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		task.interrupt();
		
		System.out.printf("Main: Status of the Thread: %s\n",  task.getState());
		System.out.printf("Main: isInterrupted: %s\n",  task.isInterrupted());
		System.out.printf("Main: isAlive: %s\n",  task.isAlive());
	}

}

package concurrency.cookbook.chapter10.recipe06;

/**
 * Generating concurrent random numbers
 * @author cuilijian
 *
 */
public class Main {

	public static void main(String[] args) {
		Thread threads[]=new Thread[3];
		for (int i=0; i<3; i++) {
			TaskLocalRandom task=new TaskLocalRandom();
			threads[i]=new Thread(task);
			threads[i].start();
		}
	}

}

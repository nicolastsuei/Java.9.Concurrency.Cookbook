package concurrency.cookbook.chapter11.recipe07;

/**
 * Taking precautions using lazy initialization
 * @author cuilijian
 *
 */
public class Main {

	public static void main(String[] args) {
		for (int i=0; i<20; i++){
			Task task=new Task();
			Thread thread=new Thread(task);
			thread.start();
		}
	}
}

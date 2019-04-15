package concurrency.cookbook.chapter03.recipe06;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

/**
 * Exchanging data between concurrent tasks
 * @author cuilj
 *
 */
public class Main {
	public static void main(String[] args) {
		List<String> buffer1 = new ArrayList<>();
		List<String> buffer2 = new ArrayList<>();
		
		Exchanger<List<String>> exchanger = new Exchanger<>();
		
		Producer producer = new Producer(buffer1, exchanger);
		Consumer consumer = new Consumer(buffer2, exchanger);
		
		Thread threadProducer = new Thread(producer);
		Thread threadConsumer = new Thread(consumer);
		
		threadProducer.start();
		threadConsumer.start();
	}
}

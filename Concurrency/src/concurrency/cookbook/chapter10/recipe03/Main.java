package concurrency.cookbook.chapter10.recipe03;

import java.util.concurrent.LinkedTransferQueue;

/**
 * Using a blocking thread-safe queue for communicating with producers and consumers
 * @author cuilijian
 *
 */
public class Main {
	public static void main(String[] args) {
		final int THREADS=100;
		LinkedTransferQueue<String> buffer=new LinkedTransferQueue<>();
		Thread producerThreads[]=new Thread[THREADS];
		Thread consumerThreads[]=new Thread[THREADS];
		
		for (int i=0; i<THREADS; i++){
			Consumer consumer=new Consumer("Consumer "+i,buffer);
			consumerThreads[i]=new Thread(consumer);
			consumerThreads[i].start();
		}
		
		for (int i=0; i<THREADS; i++) {
			Producer producer=new Producer("Producer: "+ i , buffer);
			producerThreads[i]=new Thread(producer);
			producerThreads[i].start();
		}	
		
		for (int i=0; i<THREADS; i++){
			try {
				producerThreads[i].join();
				consumerThreads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		

		System.out.printf("Main: Size of the buffer: %d\n", buffer.size());
		System.out.printf("Main: End of the example\n");
	}
}

package concurrency.cookbook.chapter04.recipe09;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Separating the launching of tasks and the processing of their results in an executor
 * @author cuilj
 *
 */
public class Main {

	public static void main(String[] args) {
		ExecutorService executor = Executors.newCachedThreadPool();
		CompletionService<String> service = new ExecutorCompletionService<>(executor);
		ReportRequest faceRequest = new ReportRequest("Face", service);
		ReportRequest onlineRequest = new ReportRequest("Online", service);
		
		Thread faceThread = new Thread(faceRequest);
		Thread onlineThread = new Thread(onlineRequest);

		ReportProcessor processor = new ReportProcessor(service);
		Thread senderThread = new Thread(processor);
		
		System.out.printf("Main : Starting the Threads\n");
		faceThread.start();
		onlineThread.start();
		senderThread.start();
		
		try {
			System.out.printf("Main : Waiting for the report generators.\n");
			faceThread.join();
			onlineThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.printf("Main : Shutting down the executor.\n");
		executor.shutdown();
		try {
			executor.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		processor.stopProcessing();
		System.out.println("Main : Ends");
	}

}

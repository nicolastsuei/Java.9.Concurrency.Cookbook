package concurrency.cookbook.chapter04.recipe09;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ReportProcessor implements Runnable{

	private final CompletionService<String> service;
	private volatile boolean end;
	
	public ReportProcessor(CompletionService<String> service) {
		this.service = service;
		end = false;
	}

	@Override
	public void run() {
		while (!end){
			try {
				Future<String> result = service.poll(20, TimeUnit.SECONDS);
				if(result != null) {
					String report = result.get();
					System.out.printf("ReportReceiver : Report Received : %s\n", report);
				}
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		System.out.printf("ReportSender : End\n");
	}
	
	public void stopProcessing(){
		this.end = true;
	}
}

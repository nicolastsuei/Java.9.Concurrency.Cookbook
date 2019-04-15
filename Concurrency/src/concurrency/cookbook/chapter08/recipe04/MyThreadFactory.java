package concurrency.cookbook.chapter08.recipe04;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class MyThreadFactory implements ThreadFactory{
	
	private AtomicInteger counter;
	private String prefix;
	
	public MyThreadFactory (String prefix) {
		this.prefix=prefix;
		counter=new AtomicInteger(1);
	}
	
	@Override
	public Thread newThread(Runnable r) {
		MyThread myThread=new MyThread(r,prefix+"-"+counter.getAndIncrement());
		return myThread;
	}
}

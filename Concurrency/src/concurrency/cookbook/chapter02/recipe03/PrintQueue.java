package concurrency.cookbook.chapter02.recipe03;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PrintQueue {
	
	private Lock queueLock;
	public PrintQueue(boolean fairMode){
		queueLock = new ReentrantLock(fairMode);
	}
	
	public void printJob(Object document){
		queueLock.lock();
		
		try {
			Long duration = (long)(Math.random() * 10000);
			System.out.println(Thread.currentThread().getName()+ ": PrintQueue: Printing the first Job during "+(duration/1000)+" seconds");
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			queueLock.unlock();
		}
		
		queueLock.lock();
		
		try {
			Long duration = (long)(Math.random() * 10000);
			System.out.printf("%s: PrintQueue: Printing the second Job during %d seconds\n", Thread.currentThread().getName(), (duration/1000));
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			queueLock.unlock();
		}
	}
}

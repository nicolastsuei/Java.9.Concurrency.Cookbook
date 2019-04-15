package concurrency.cookbook.chapter09.recipe09;

import java.util.concurrent.locks.Lock;

public class Task2 implements Runnable{

	private Lock lock1, lock2;

	public Task2(Lock lock1, Lock lock2) {
		this.lock1=lock1;
		this.lock2=lock2;
	}

	@Override
	public void run() {
		lock2.lock();
		System.out.printf("Task 2: Lock 2 locked\n");
		lock1.lock();
		System.out.printf("Task 2: Lock 1 locked\n");
		
		lock1.unlock();
		lock2.unlock();
	}
}

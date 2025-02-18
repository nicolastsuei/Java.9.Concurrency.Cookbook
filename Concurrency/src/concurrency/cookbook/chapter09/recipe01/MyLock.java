package concurrency.cookbook.chapter09.recipe01;

import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;

public class MyLock extends ReentrantLock {

	public String getOwnerName() {
		if (this.getOwner()==null) {
			return "None";
		}
		return this.getOwner().getName();
	}
	
	public Collection<Thread> getThreads() {
		return this.getQueuedThreads();
	}
}

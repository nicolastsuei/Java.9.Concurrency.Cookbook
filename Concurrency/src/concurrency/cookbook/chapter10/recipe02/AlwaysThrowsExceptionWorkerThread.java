package concurrency.cookbook.chapter10.recipe02;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

public class AlwaysThrowsExceptionWorkerThread extends ForkJoinWorkerThread {
	protected AlwaysThrowsExceptionWorkerThread(ForkJoinPool pool) {
		super(pool);
	}
	
	protected void onStart() {
		super.onStart();
		throw new RuntimeException("Exception from worker thread");
	}
}

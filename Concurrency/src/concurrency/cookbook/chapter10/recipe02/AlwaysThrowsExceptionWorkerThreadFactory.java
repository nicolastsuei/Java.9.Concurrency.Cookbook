package concurrency.cookbook.chapter10.recipe02;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.ForkJoinWorkerThread;

public class AlwaysThrowsExceptionWorkerThreadFactory implements ForkJoinWorkerThreadFactory {

	@Override
	public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
		return new AlwaysThrowsExceptionWorkerThread(pool);
	}

}

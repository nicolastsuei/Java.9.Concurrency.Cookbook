package concurrency.cookbook.chapter10.recipe02;

import java.lang.Thread.UncaughtExceptionHandler;

public class Handler implements UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		System.out.printf("Handler: Thread %s has thrown anException.\n",t.getName());
		System.out.printf("%s\n",e);
		System.exit(-1);
	}

}

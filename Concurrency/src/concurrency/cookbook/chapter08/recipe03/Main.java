package concurrency.cookbook.chapter08.recipe03;

/**
 * Implementing the ThreadFactory interface to generate custom threads
 * @author cuilijian
 *
 */
public class Main {
	public static void main(String[] args) throws Exception {
		MyThreadFactory myFactory=new MyThreadFactory("MyThreadFactory");
		MyTask task=new MyTask();
		Thread thread=myFactory.newThread(task);
		thread.start();
		thread.join();
		System.out.printf("Main: Thread information.\n");
		System.out.printf("%s\n",thread);
		System.out.printf("Main: End of the example.\n");
	}
}

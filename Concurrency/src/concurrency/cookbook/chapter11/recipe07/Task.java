package concurrency.cookbook.chapter11.recipe07;

public class Task implements Runnable{

	@Override
	public void run() {
		System.out.printf("%s: Getting the connection...\n", Thread.currentThread().getName());
		DBConnectionOK connection=DBConnectionOK.getConnection();
		System.out.printf("%s: End\n", Thread.currentThread().getName());
	}

}

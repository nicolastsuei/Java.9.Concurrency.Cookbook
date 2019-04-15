package concurrency.cookbook.chapter11.recipe07;

public class DBConnectionOK {
	private DBConnectionOK() {
		System.out.printf("%s: Connection created.\n",Thread.currentThread().getName());
	}
	
	private static class LazyDBConnectionOK {
		private static final DBConnectionOK INSTANCE = new DBConnectionOK();
	}
	
	public static DBConnectionOK getConnection() {
		return LazyDBConnectionOK.INSTANCE;
	}	
}

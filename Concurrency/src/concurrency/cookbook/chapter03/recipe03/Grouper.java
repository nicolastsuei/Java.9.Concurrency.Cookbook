package concurrency.cookbook.chapter03.recipe03;

public class Grouper implements Runnable{
	
	private final Results results;
	public Grouper(Results results){
		this.results = results;
	}
	
	@Override
	public void run() {
		int finalResult = 0;
		System.out.printf("Grouper : Processing results...\n");
		int data[] =results.getData();
		for(int number:data){
			finalResult += number;
		}
		System.out.printf("Grouper : Total result : %d.\n", finalResult);
	}
	
}

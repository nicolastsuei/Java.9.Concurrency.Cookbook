package concurrency.cookbook.chapter09.recipe04;

import java.util.concurrent.RecursiveAction;

public class Task extends RecursiveAction{
	private final int array[];
	private final int start;
	private final int end;
	
	public Task (int array[], int start, int end) {
		this.array=array;
		this.start=start;
		this.end=end;
	}

	@Override
	protected void compute() {
		if (end-start>100) {
			int mid=(start+end)/2;
			Task task1=new Task(array,start,mid);
			Task task2=new Task(array,mid,end);
			task1.fork();
			task2.fork();
			task1.join();
			task2.join();
		} else {
			for (int i=start; i<end; i++) {
				array[i]++;
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
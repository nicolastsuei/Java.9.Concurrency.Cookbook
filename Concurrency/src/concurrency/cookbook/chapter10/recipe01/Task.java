package concurrency.cookbook.chapter10.recipe01;

import java.util.List;
import java.util.concurrent.FutureTask;

public class Task extends FutureTask<List<String>> {
	private FileSearch fileSearch;
	
	public Task(Runnable runnable, List<String> result) {
		super(runnable, result);
		this.fileSearch=(FileSearch)runnable;
	}
	
	@Override
	protected void set(List<String> v) {
		v=fileSearch.getResults();
		super.set(v);
	}
}

package concurrency.cookbook.chapter05.recipe05;

import java.util.concurrent.ConcurrentLinkedDeque;

public class TaskManager {
	private final ConcurrentLinkedDeque<SearchNumberTask> tasks;
	
	public TaskManager(){
		tasks=new ConcurrentLinkedDeque<>();
	}
	
	public void addTask(SearchNumberTask task){
		tasks.add(task);
	}
	
	public void cancelTasks(SearchNumberTask cancelTask){
		for (SearchNumberTask task :tasks) {
			if (task!=cancelTask) {
				task.cancel(true);
				task.logCancelMessage();
			}
		}
	}
}

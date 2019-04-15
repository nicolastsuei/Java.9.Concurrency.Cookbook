package concurrency.cookbook.chapter05.recipe03;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountedCompleter;

public class FolderProcessor extends CountedCompleter<List<String>>{

	private String path;
	private String extension;
	private List<FolderProcessor> tasks;
	private List<String> resultList;
	
	public FolderProcessor(CountedCompleter<?> completer, String path, String extension) {
		super(completer);
		this.path = path;
		this.extension = extension;
	}
	
	public FolderProcessor(String path, String extension) {
		this.path = path;
		this.extension = extension;
	}

	@Override
	public void compute() {
		resultList = new ArrayList<>();
		tasks = new ArrayList<>();
		File file = new File(path);
		File content[] = file.listFiles();
		
		if (content != null) {
			for (int i = 0; i < content.length; i++) {
				if (content[i].isDirectory()) {
					FolderProcessor task=new FolderProcessor(this, content[i].getAbsolutePath(), extension);
					task.fork();
					addToPendingCount(1);
					tasks.add(task);
				}else{
					if (checkFile(content[i].getName())){
						resultList.add(content[i].getAbsolutePath());
					}
				}
			}
			
			if (tasks.size()>50) {
				System.out.printf("%s: %d tasks ran.\n",
				file.getAbsolutePath(),tasks.size());
			}
		}
	
		tryComplete();
	}
	
	@Override
	public void onCompletion(CountedCompleter<?> completer) {
		for (FolderProcessor childTask : tasks) {
			resultList.addAll(childTask.getResultList());
		}
	}
	
	private boolean checkFile(String name) {
		return name.endsWith(extension);
	}

	private  List<String> getResultList() {
		return resultList;
	}
}

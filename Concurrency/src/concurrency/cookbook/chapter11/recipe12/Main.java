package concurrency.cookbook.chapter11.recipe12;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
/**
 * Using executors instead of thread groupss
 * @author cuilijian
 *
 */
public class Main {
	public static void main (String[] args) {
		List<Person> persons=PersonGenerator.generatePersonList(100000);
		
		Date start, end;
		start = new Date();
		Map<String, List<Person>> personsByName = persons.parallelStream().collect(Collectors.groupingByConcurrent(p -> p.getFirstName()));
		end = new Date();
		System.out.printf("Collect: %d - %d\n", personsByName.size(), end.getTime()-start.getTime());
		
		start = new Date();
		ConcurrentHashMap<String, ConcurrentLinkedDeque<Person>> forkJoinMap=new ConcurrentHashMap<>();
		PersonMapTask personMapTask=new PersonMapTask(persons,forkJoinMap);
		ForkJoinPool.commonPool().invoke(personMapTask);
		end = new Date();
		System.out.printf("Collect ForkJoinPool: %d - %d\n", forkJoinMap.size(), end.getTime()-start.getTime());
	}
}

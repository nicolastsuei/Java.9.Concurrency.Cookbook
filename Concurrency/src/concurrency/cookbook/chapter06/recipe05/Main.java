package concurrency.cookbook.chapter06.recipe05;

import java.util.Arrays;
import java.util.List;

/**
 * Filtering the elements of a stream
 * @author cuilijian
 *
 */
public class Main {
	public static void main(String[] args) {
		List<Person> persons=PersonGenerator.generatePersonList(10);
		persons.parallelStream().forEach(p-> {
			System.out.printf("%s, %s\n", p.getLastName(),
			p.getFirstName());
		});
	
		persons.parallelStream().distinct().forEach(p-> {
			System.out.printf("%s, %s\n", p.getLastName(),
			p.getFirstName());
		});
		
		Integer[] numbers={1,3,2,1,2,2,1,3,3,1,1,3,2,1};
		Arrays.asList(numbers).parallelStream().mapToInt(n -> n).distinct().forEach( n -> {
			System.out.printf("Number: %d\n", n);
		});
		
		persons.parallelStream().filter(p -> p.getSalary() < 30000).forEach( p -> {
			System.out.printf("%s, %s\n", p.getLastName(),
			p.getFirstName());
		});
		
		Arrays.asList(numbers).parallelStream().mapToInt(n -> n).filter( n -> n<2).forEach( n-> {
			System.out.printf("%d\n", n);
		});
		
		persons.parallelStream().mapToDouble(p -> p.getSalary()).sorted().limit(5).forEach(s-> {
			System.out.printf("Limit: %f\n",s);
		});
		
		persons.parallelStream().mapToDouble(p -> p.getSalary()).sorted().skip(5).forEach(s-> {
			System.out.printf("Skip: %f\n",s);
		});
	}
}

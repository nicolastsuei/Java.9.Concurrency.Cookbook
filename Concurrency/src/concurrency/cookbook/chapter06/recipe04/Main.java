package concurrency.cookbook.chapter06.recipe04;

import java.util.List;

/**
 * Applying an action to every element of a stream
 * @author cuilijian
 *
 */
public class Main {

	public static void main(String[] args) {
		List<Person> persons = PersonGenerator.generatePersonList(10);
		persons.parallelStream().forEach(p -> {
			System.out.printf("%s, %s\n", p.getLastName(), p.getFirstName());
		});

		List<Double> doubles = DoubleGenerator.generateDoubleList(10, 100);
		System.out.printf("Parallel forEachOrdered() with numbers\n");
		doubles.parallelStream().sorted().forEachOrdered(n -> {
			System.out.printf("%f\n", n);
		});
		
		System.out.printf("Parallel forEach() afer sorted() with numbers\n");
		doubles.parallelStream().sorted().forEach(n -> {
			System.out.printf("%f\n", n);
		});
		
		persons.parallelStream().sorted().forEachOrdered(p -> {
			System.out.printf("%s, %s\n", p.getLastName(), p.getFirstName());
		});
		
		doubles
			.parallelStream()
			.peek(d -> System.out.printf("Step 1: Number: %f\n",d))
			.peek(d -> System.out.printf("Step 2: Number: %f\n",d))
			.forEach(d -> System.out.printf("Final Step: Number: %f\n",d));
	}

}

package concurrency.cookbook.chapter09.recipe10;

import edu.umd.cs.mtc.TestFramework;

/**
 * Testing concurrency code with MultithreadedTC
 * @author cuilijian
 *
 */
public class Main {

	public static void main(String[] args) throws Throwable{
		ProducerConsumerTest test=new ProducerConsumerTest();

		System.out.printf("Main: Starting the test\n");
		TestFramework.runOnce(test);
		System.out.printf("Main: The test has finished\n");
	}

}

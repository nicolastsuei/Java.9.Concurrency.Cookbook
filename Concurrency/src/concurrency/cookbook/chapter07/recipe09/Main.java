package concurrency.cookbook.chapter07.recipe09;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Using the volatile keyword
 * @author cuilijian
 *
 */
public class Main {
	public static void main(String[] args) {
		VolatileFlag volatileFlag=new VolatileFlag();
		Flag flag=new Flag();
		VolatileTask vt=new VolatileTask(volatileFlag);
		Task t=new Task(flag);
		
		Thread thread=new Thread(vt);
		thread.start();
		thread=new Thread(t);
		thread.start();
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.printf("Main: Going to stop volatile task: %s\n",new Date());
		volatileFlag.flag=false;
		System.out.printf("Main: Volatile task stoped: %s\n", new Date());
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.printf("Main: Going to stop task: %s\n", new Date());
		flag.flag=false;
		System.out.printf("Main: Volatile stop flag changed: %s\n", new Date());
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

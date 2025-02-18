package concurrency.cookbook.chapter03.recipe02;
/**
 * Synchronizing tasks in a common point.
 * @author cuilj
 *
 */
public class Main {

	public static void main(String[] args) {
		Videoconference conference = new Videoconference(10);
		Thread threadConference = new Thread(conference);
		threadConference.start();
		
		for(int i = 0 ; i < 10 ; i ++) {
			Participant p = new Participant(conference, "Participant" + i);
			Thread t = new Thread(p);
			t.start();
		}
	}

}

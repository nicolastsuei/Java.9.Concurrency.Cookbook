package concurrency.cookbook.chapter08.recipe12;

import java.util.concurrent.Flow.Subscription;

public class MySubscription implements Subscription{
	private boolean canceled=false;
	private long requested=0;
	@Override
	public void request(long value) {
		requested+=value;
	}
	@Override
	public void cancel() {
		canceled=true;
	}
	
	public boolean isCanceled() {
		return canceled;
	}
	public long getRequested() {
		return requested;
	}
	public void decreaseRequested() {
		requested--;
	}
}

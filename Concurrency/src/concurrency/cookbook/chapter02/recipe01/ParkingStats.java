package concurrency.cookbook.chapter02.recipe01;

public class ParkingStats {

	private long numberCars;
	private long numberMotorcycles;
	private ParkingCash cash;
	
	private final Object controlCars;
	private final Object controlMotorcycles;
	public ParkingStats(ParkingCash cash){
		numberCars = 0;
		numberMotorcycles = 0;
		this.cash = cash;
		controlCars = new Object();
		controlMotorcycles = new Object();
	}
	
	public void carComeIn(){
//		synchronized (controlCars) {
			numberCars ++ ;
//		}
	}
	
	public void carGoOut(){
//		synchronized (controlCars) {
			numberCars --;
//		}
		cash.vehiclePay();
	}
	
	public void motorComeIn() {
//		synchronized (controlMotorcycles) {
			numberMotorcycles ++ ;
//		}
	}
	
	public void motorGoOut(){
//		synchronized (controlMotorcycles) {
			numberMotorcycles -- ;
//		}
		cash.vehiclePay();
	}

	public long getNumberCars() {
		return numberCars;
	}

	public long getNumberMotorcycles() {
		return numberMotorcycles;
	}
}

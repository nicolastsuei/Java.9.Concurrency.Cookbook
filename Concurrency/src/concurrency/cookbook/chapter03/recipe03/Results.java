package concurrency.cookbook.chapter03.recipe03;

public class Results {
	private final int data[];
	public Results(int size){
		data = new int[size];
	}
	
	public void setData(int position , int value){
		data[position] = value ;
	}
	
	public int[] getData(){
		return data;
	}
}

package gui;

public class Passenger {

	int passId;
	private boolean annoyed;
	private boolean isOnBus;
	
	public Passenger(int id){
		passId = id;
	}
	
	public int Id() {
		return passId;
	}

	void setAnnoyed(boolean annoyed) {
		this.annoyed = annoyed;
	}

	boolean isAnnoyed() {
		return annoyed;
	}

	public void gotOnBus() {
		this.isOnBus = true;
	}

	public boolean isOnBus() {
		return isOnBus;
	}
	
}

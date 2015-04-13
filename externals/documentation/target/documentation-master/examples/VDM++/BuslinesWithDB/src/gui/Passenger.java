package gui;

public class Passenger {

	int passId;
	String goal; 
	private boolean annoyed;
	private boolean isOnBus;
	
	public Passenger(int id, String headingFor){
		passId = id;
		goal = headingFor;
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
	
	public String headingFor() {
		return goal;
	}
}

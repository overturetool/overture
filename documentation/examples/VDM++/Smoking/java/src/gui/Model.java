package gui;

import java.io.Serializable;
import java.util.Observable;

class Model extends Observable implements Serializable {

    
	private static final long serialVersionUID = 1L;
	private int smoking; 
	
    public Model() {
    	smoking = 0;
    }
    
	public void paperAdded() {
		setChanged();
        notifyObservers("paperAdded");
	}

	public void tobaccoAdded() {
		setChanged();
        notifyObservers("tobaccoAdded");
	}

	public void matchAdded() {
		setChanged();
        notifyObservers("matchAdded");
	}
	
	public void tableCleared()
	{
		setChanged();
        notifyObservers("tableCleared");
	}

	public void nowSmoking(long smoker) {
		
		smoking = (int) smoker;
		setChanged();
        notifyObservers("smoking");
	}

	int getSmoker() {
		return smoking;
	}

	public void finishedSmoking() {
		setChanged();
        notifyObservers("finishedSmoking");
	}
}

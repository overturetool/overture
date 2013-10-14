package gui;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

class Model extends Observable implements Serializable {
 
	private static final long serialVersionUID = 1L;
    
    private List<Point> livingCells =new ArrayList<Point>();
    
    public Model() {
    }
    
    public void newLivingCell(Point newLiveCell){
    
    	livingCells.add(newLiveCell);
        // Notify observers
        setChanged();
        notifyObservers("newLivingCell");
    }

	public void newSimulationRound() {
		setChanged();
        notifyObservers("newRound");	
        livingCells.clear();
	}

	public List<Point> getLivingCells() {
		return new ArrayList<Point>(livingCells);
	}
	
}
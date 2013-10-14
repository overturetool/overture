package gui;
import java.awt.Point;
import java.io.Serializable;

import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.IntegerValue;
import org.overture.interpreter.values.Value;


public class Graphics implements Serializable  {

	private static final long serialVersionUID = 1L;
	
	Controller ctrl;
    Model model;
    
    int sleepTime;
    
    public Value initialise(Value cellSideCount, Value timeTosleep) throws ValueException  {
    	
    	sleepTime = (int) timeTosleep.nat1Value(null);
    	int sideCount = (int) cellSideCount.nat1Value(null);
    	
        ctrl = new Controller(sideCount);
        model = ctrl.getModel();
        
        return new IntegerValue(0);
    }
    
    public void sleep(long time) {
    	 try {
 			Thread.sleep(time);
 		} catch (InterruptedException e) {
 			e.printStackTrace();
 		}
    }

    public Value sleep(Value time) throws ValueException{
        sleep(time.intValue(null));
    	
        return new IntegerValue(0);
    }

    public Value newRound() throws ValueException {
    	sleep(sleepTime);
        model.newSimulationRound();
        return new IntegerValue(0);
    }

	public Value newLivingCell(Value x, Value y) throws ValueException {
		model.newLivingCell(new Point((int)x.intValue(null), (int) y.intValue(null)));
		return new IntegerValue(0);
	}
}
package gui;

import java.io.Serializable;

import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.VoidValue;


public class Graphics implements Serializable  {

	private static final long serialVersionUID = 1L;
	transient Controller ctrl;
    Model model;
   
    public Value init() {
    	ctrl = new Controller();
    	model = ctrl.getModel();
        return new VoidValue();
    }

    public Value sleep(){
        try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
        return new VoidValue();
    }
    
    public Value move(){
    	model.move(); 
    	Controller.buslinesControl.GfxDone();
    	return new VoidValue();
    }
    
    public Value busInRouteTo(Value busline, Value roadid, Value waypoint, Value time) {
    	
    	try { 
    		String road = roadid.stringValue(null);
    		
    		if(road.startsWith("<"))
    		{
    			road = road.substring(1, road.length()-1);
    		}
    		
    		String wp = waypoint.stringValue(null);
    		if(wp.startsWith("<"))
    		{
        		wp =  wp.substring(1, wp.length()-1);
    		}

			model.busInRouteTo((int) busline.intValue(null), road , wp, (int)time.intValue(null));
		} catch (ValueException e) {
			e.printStackTrace();
		}
		
		return new VoidValue();
    }
    
    public Value passengerAtCentral(Value id, Value goal) throws ValueException{

    	final Value temp = id;
    	String wp = goal.stringValue(null);
		if(wp.startsWith("<"))
		{
    		wp =  wp.substring(1, wp.length()-1);
		}
    	
    	model.passengerAtCentral((int) temp.intValue(null), wp);
    	
    	 return new VoidValue();
    }
    
    public Value passengerAnnoyed(Value id) throws ValueException{

    	final Value temp = id;
    	model.passengerAnnoyed((int) temp.intValue(null));
    	
    	 return new VoidValue();
    }
    
    public Value passengerGotOnBus(Value id) throws ValueException{
	
		final Value temp = id;
    	model.passengerGotOnBus((int) temp.intValue(null));
    	
    	 return new VoidValue();
    }
    
    public Value inflowChanged(Value val) throws ValueException{
    	
		final Value temp = val;
    	model.inflowChanged((int) temp.intValue(null));
    	
    	return new VoidValue();
    }

	public Value busStopping(Value busline) throws ValueException {
		
		model.busArrived((int)busline.intValue(null));
		
		return new VoidValue();
	}
	
	public Value busAdded(Value busline) throws ValueException {

		model.busAdded((int) busline.intValue(null));
		return new VoidValue();
	}
	
	public Value busPassengerCountChanged(Value busline, Value count)
			throws ValueException {

		model.busPassengerCountChanged((int) busline.intValue(null),
				(int) count.intValue(null));
		return new VoidValue();
	}
}
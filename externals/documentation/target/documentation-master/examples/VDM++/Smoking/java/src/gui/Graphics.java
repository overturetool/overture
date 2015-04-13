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
			Thread.sleep(2500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
        return new VoidValue();
    }
    public Value tobaccoAdded()  {
    
        model.tobaccoAdded();
        return new VoidValue();
    }
    
    public Value paperAdded()  {

        model.paperAdded();
        return new VoidValue();
    }
    

    public Value matchAdded()  {

        model.matchAdded();
        return new VoidValue();
    }
    
    
    
    public Value tableCleared()  {
        
    	Thread runner = new Thread(new Runnable(){
			 public void run(){
			         sleep();
			         model.tableCleared();
			 }
		});
    	runner.start();
       
        return new VoidValue();
    }

    public Value nowSmoking(Value smokeid) throws ValueException {

    	final Value temp = smokeid;
    	model.nowSmoking(temp.intValue(null));
    	ctrl.DisableButtons();
    	Thread runner = new Thread(new Runnable(){
			 public void run(){
			         sleep();
			         model.finishedSmoking();
			         ctrl.EnableButtons();
			 }
		});
    	runner.start();
    	
    	 return new VoidValue();
    }
}
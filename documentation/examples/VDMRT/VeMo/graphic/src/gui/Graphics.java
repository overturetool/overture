package gui;

import java.io.Serializable;

import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.VoidValue;


public class Graphics implements Serializable  {

	private static final long serialVersionUID = 1L;
	Controller ctrl;
    Model model;
    public Value init() {
        ctrl = new Controller();
        model = ctrl.getModel();
        return new VoidValue();
    }

    public Value sleep(){
        try {
			Thread.sleep(750);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
        return new VoidValue();
    }

    public Value addVehicle(Value vecID) throws ValueException {
        model.addVehicle(vecID.intValue(null));
        return new VoidValue();
    }

    public Value connectVehicles(Value vecID, Value vecID2) throws ValueException {

        model.connectVehicles(vecID.intValue(null), vecID2.intValue(null));
        return new VoidValue();
    }

    public Value disconnectVehicles(Value vecID, Value vecID2) throws ValueException {
        model.disconnectVehicles(vecID.intValue(null), vecID2.intValue(null));
        return new VoidValue();
    }

    public Value updatePosition(Value vecID, Value x, Value y) throws ValueException {
        model.updatePosition(vecID.intValue(null), (int) x.intValue(null), (int) y.intValue(null));
        model.refresh();
        return new VoidValue();
    }
    
    public Value updateDirection(Value vecID, Value dir) throws ValueException {
    	model.updateDirection(vecID.intValue(null), (int) dir.intValue(null));
    	return new VoidValue();
    }
    
    public Value receivedMessage(Value vecID) throws ValueException {
    	model.receivedMessage(vecID.intValue(null));
    	return new VoidValue();
    }
}
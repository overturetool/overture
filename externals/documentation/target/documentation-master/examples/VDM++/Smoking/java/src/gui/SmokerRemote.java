package gui;

import org.overture.interpreter.debug.RemoteControl;
import org.overture.interpreter.debug.RemoteInterpreter;

public class SmokerRemote implements RemoteControl {

	RemoteInterpreter interpreter;
	@Override
	public void run(RemoteInterpreter intrprtr) throws Exception {
	
		interpreter = intrprtr;
		SmokingControl ctrl = new SmokingControl(interpreter);

		ctrl.init();
	}
}

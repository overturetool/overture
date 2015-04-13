package gui;

import org.overture.interpreter.debug.RemoteControl;
import org.overture.interpreter.debug.RemoteInterpreter;


public class BuslinesRemote implements RemoteControl {

	RemoteInterpreter interpreter;
	@Override
	public void run(RemoteInterpreter intrprtr) throws Exception {
	
		interpreter = intrprtr;
		BuslinesControl ctrl = new BuslinesControl(interpreter);

		ctrl.init();
	}
}

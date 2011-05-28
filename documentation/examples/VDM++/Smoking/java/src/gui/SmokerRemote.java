package gui;

import org.overturetool.vdmj.debug.RemoteControl;
import org.overturetool.vdmj.debug.RemoteInterpreter;

public class SmokerRemote implements RemoteControl {

	RemoteInterpreter interpreter;
	@Override
	public void run(RemoteInterpreter intrprtr) throws Exception {
	
		interpreter = intrprtr;
		SmokingControl ctrl = new SmokingControl(interpreter);

		ctrl.init();
	}
}

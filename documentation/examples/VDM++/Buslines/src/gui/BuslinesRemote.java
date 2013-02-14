package gui;

import org.overturetool.vdmj.debug.RemoteControl;
import org.overturetool.vdmj.debug.RemoteInterpreter;

public class BuslinesRemote implements RemoteControl {

	RemoteInterpreter interpreter;
	@Override
	public void run(RemoteInterpreter intrprtr) throws Exception {
	
		interpreter = intrprtr;
		BuslinesControl ctrl = new BuslinesControl(interpreter);

		ctrl.init();
	}
}

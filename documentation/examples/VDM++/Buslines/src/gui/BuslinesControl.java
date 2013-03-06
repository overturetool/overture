package gui;

import org.overture.interpreter.debug.RemoteInterpreter;
import org.overture.interpreter.values.Value;



public class BuslinesControl implements IBuslinesControl {

	RemoteInterpreter interpreter;
	public BuslinesControl(RemoteInterpreter intrprtr) {
		interpreter = intrprtr;
		Controller.buslinesControl = this;
	}
	
	public void init() {
		try {
			execute("create w := new World()");
			execute("w.Run()");
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	@Override
	public void IncreaseInflow() {
		try {
			//execute("w.env.IncreaseInflow()");
			execute("World`env.go()");
			//execute("w.Yield()");			//Optimization for thread fairness  
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void DecreaseInflow() {
		try {
			execute("w.env.DecreaseInflow()");
			//execute("w.Yield()");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private Value execute(String arguments) throws Exception
	{
		String cmd = arguments;
		if (cmd.toLowerCase().startsWith("create"))
		{
			cmd = cmd.substring(cmd.indexOf(" "));
			cmd = cmd.trim();
			String name = cmd.substring(0, cmd.indexOf(" "));
			String exp = cmd.substring(cmd.indexOf(":=") + 2);
			System.out.println("CREATE:  var: " + name + " exp: " + exp);
			interpreter.create(name, exp);
			return null;
		} else if (cmd.toLowerCase().startsWith("debug")||cmd.toLowerCase().startsWith("print"))
		{
			cmd = /*"p" +*/ cmd.substring(cmd.indexOf(" "));

			cmd = cmd.trim();
		}

		try{
			System.out.println("Calling VDMJ with: "+cmd);
			Value result = interpreter.valueExecute(cmd);
			return result; 
		}catch(Exception e)
		{
			throw e;
		}
	}
}

package gui;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.overturetool.vdmj.debug.RemoteInterpreter;
import org.overturetool.vdmj.values.Value;

public class BuslinesControl implements IBuslinesControl {

	BlockingQueue<String> commandQueue = new LinkedBlockingQueue<String>();
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
			commandQueue.add("w.env.IncreaseInflow()");
			//execute("w.env.IncreaseInflow()");
			//execute("World`env.go()");
			//execute("w.Yield()");			//Optimization for thread fairness  
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void DecreaseInflow() {
		try {
			//execute("w.env.DecreaseInflow()");
			commandQueue.add("w.env.DecreaseInflow()");
			//execute("w.Yield()");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void GfxDone() {
		try {
			commandQueue.add("w.env.step()");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void NewCityPlan() {
		try {
			execute("w.env.city.newCity()");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void AddWaypoint(String wp) {
		try {
			execute("w.addWaypoint(" + wp + ")");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void AddBusstop(String wp) {
		try {
			if(!wp.equalsIgnoreCase("<central>"))
				execute("w.env.city.addBusstop(" + wp + ")");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void AddRoad(String wp1, String wp2, String road, int length, boolean highspeed) {
		try {
			execute("w.addRoad(" + wp1 + ", " + wp2 + ", " + road + ", " + length + ", " + highspeed + ")");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void AddBus(String stm) {
		try {
			execute("w.addBus("+ stm + ")");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void StartSimulation() {
		try {
//			Thread runner = new Thread(new Runnable(){
//				 public void run(){
//					 
//					 try{
//							Thread.sleep(750);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
					 
						try {
							execute("w.StartSimulation()");
						} catch (Exception e) {
							e.printStackTrace();
						}
				// }

			//});
			Thread commandRunner = new Thread(new Runnable(){
				 public void run(){
					String cmd = null;
					try {

						while(true){
							cmd = commandQueue.take();
							execute(cmd);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				 }
				 
			});
			
			commandRunner.start();
			//runner.start();
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

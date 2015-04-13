package gui;

import org.overture.interpreter.debug.RemoteInterpreter;
import org.overture.interpreter.values.Value;

public class SmokingControl implements ISmokingControl
{

	private RemoteInterpreter interpreter;

	public SmokingControl(RemoteInterpreter intrprtr)
	{
		interpreter = intrprtr;
		Controller.smoke = this;
	}

	public void init()
	{
		try
		{
			execute("create w := new World(100)");
			execute("w.Run()");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void AddPaper()
	{
		try
		{
			execute("w.agent.AddPaper()");
			execute("w.Yield()"); // Optimization for thread fairness
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void AddMatch()
	{
		try
		{
			execute("w.agent.AddMatch()");
			execute("w.Yield()");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void AddTobacco()
	{
		try
		{
			execute("w.agent.AddTobacco()");
			execute("w.Yield()");
		} catch (Exception e)
		{
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
		} else if (cmd.toLowerCase().startsWith("debug")
				|| cmd.toLowerCase().startsWith("print"))
		{
			cmd = /* "p" + */cmd.substring(cmd.indexOf(" "));

			cmd = cmd.trim();
		}

		try
		{
			System.out.println("Calling VDMJ with: " + cmd);
			Value result = interpreter.valueExecute(cmd);
			return result;
		} catch (Exception e)
		{
			throw e;
		}
	}

	/**
	 * Notifies Overture that the remote interface is being disposed and that interpretation is finished. After this
	 * Overture is allowed to terminate the current process.
	 */
	public void finish()
	{
		this.interpreter.finish();
	}
}

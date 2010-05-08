package org.overturetool.vdmj.debug;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.VoidValue;

public class RemoteInterpreter
{
	private final Interpreter interpreter;
	private final DBGPReader dbgp;

	public RemoteInterpreter(Interpreter interpreter, DBGPReader dbgp)
	{
		this.interpreter = interpreter;
		this.dbgp = dbgp;
	}

	public String execute(String line) throws Exception
	{
		return valueExecute(line).toString();
	}

	public Value valueExecute(String line) throws Exception
	{
		boolean print = false;
		if(line.startsWith("p ")||line.startsWith("print ")||line.startsWith("debug "))
		{
			line = line.substring(line.indexOf(' '));
			print = true;
		}
		if (interpreter instanceof ClassInterpreter
				&& line.startsWith("create"))
		{
			Pattern p = Pattern.compile("^create (\\w+)\\s*?:=\\s*(.+)$");
			Matcher m = p.matcher(line);

			if (m.matches())
			{
				String var = m.group(1);
				String exp = m.group(2);

				((ClassInterpreter) interpreter).create(var, exp);

			}
			return new VoidValue();

		} else
		{

			Value res = interpreter.execute(line, dbgp);
			if(print && !(res instanceof VoidValue))
			{
				Console.out.println(res.toString());
			}
			return res;
		}
	}

	public void init()
	{
		interpreter.init(null);
	}
}

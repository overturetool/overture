import org.overture.interpreter.runtime.ClassInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ExitException;
import org.overture.interpreter.runtime.StateContext;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.OperationValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.interpreter.values.VoidValue;


public class TestCase
{
	public static Value reflectionRunTest(Value obj, Value name)
			throws Exception
	{
		String methodName = name.toString().replaceAll("\"", "").trim();

		ObjectValue instance = (ObjectValue) obj;
		for (NameValuePair p : instance.members.asList())
		{
			if (p.name.getName().equals(methodName))
			{
				if (p.value instanceof OperationValue)
				{
					OperationValue opVal = (OperationValue) p.value;
					Context mainContext = new StateContext(p.name.getLocation(), "reflection scope");

					mainContext.putAll(ClassInterpreter.getInstance().initialContext);
					// mainContext.putAll(ClassInterpreter.getInstance().);
					mainContext.setThreadState(ClassInterpreter.getInstance().initialContext.threadState.dbgp, ClassInterpreter.getInstance().initialContext.threadState.CPU);
					try{
					opVal.eval(p.name.getLocation(), new ValueList(), mainContext);
					}catch(Exception e)
					{
						if(e instanceof ExitException)
						{
							throw e;
						}
						return ClassInterpreter.getInstance().evaluate("Error`throw(\""+e.getMessage().replaceAll("\"", "\\\\\"").replaceAll("\'", "\\\'").replaceAll("\\\\", "\\\\\\\\")+"\")", mainContext);
					}
				}
			}
		}
		return new VoidValue();

	}
}

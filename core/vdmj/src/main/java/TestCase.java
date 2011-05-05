import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.StateContext;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.OperationValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;
import org.overturetool.vdmj.values.VoidValue;

public class TestCase
{
	public static Value reflectionRunTest(Value obj, Value name)
			throws ValueException
	{
		String methodName = name.toString().replaceAll("\"", "").trim();

		ObjectValue instance = (ObjectValue) obj;
		for (NameValuePair p : instance.members.asList())
		{
			if (p.name.name.equals(methodName))
			{
				if (p.value instanceof OperationValue)
				{
					OperationValue opVal = (OperationValue) p.value;
					Context mainContext = new StateContext(p.name.location, "reflection scope");

					mainContext.putAll(ClassInterpreter.getInstance().initialContext);
					// mainContext.putAll(ClassInterpreter.getInstance().);
					mainContext.setThreadState(ClassInterpreter.getInstance().initialContext.threadState.dbgp, ClassInterpreter.getInstance().initialContext.threadState.CPU);
					opVal.eval(p.name.location, new ValueList(), mainContext);
				}
			}
		}
		return new VoidValue();

	}
}

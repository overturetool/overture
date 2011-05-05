import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.StateContext;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.OperationValue;
import org.overturetool.vdmj.values.SeqValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class TestSuite
{
	public static Value getTestMethodNamed(Value test)
	{
		List<String> tests = new Vector<String>();
		ObjectValue instance = (ObjectValue) test;

		for (NameValuePair p : instance.members.asList())
		{
			if (p.value instanceof OperationValue)
			{
				if (p.name.name.toLowerCase().startsWith("test"))
				{
					tests.add(p.name.name);
				}
			}
		}

		ValueList vals = new ValueList();
		for (String value : tests)
		{
			vals.add(new SeqValue(value));
		}
				
		return new SeqValue(vals);
	}
	
	public static Value createTests(Value test)
	{
		List<String> tests = new Vector<String>();
		ValueList vals = new ValueList();
		ObjectValue instance = (ObjectValue) test;

		for (NameValuePair p : instance.members.asList())
		{
			if (p.value instanceof OperationValue)
			{
				OperationValue opVal = (OperationValue) p.value;
				if(!opVal.isConstructor && p.name.name.toLowerCase().startsWith("test"))
				{
					tests.add(p.name.name);
					
					Context mainContext = new StateContext(
							p.name.location, "reflection scope");

						mainContext.putAll(ClassInterpreter.getInstance().initialContext);
//						mainContext.putAll(ClassInterpreter.getInstance().);
						mainContext.setThreadState(ClassInterpreter.getInstance().initialContext.threadState.dbgp, ClassInterpreter.getInstance().initialContext.threadState.CPU);
					
						try
						{
							//TODO missing check for correct constructor of class
							vals.add(ClassInterpreter.getInstance().evaluate("new "+p.name.module+"(\""+p.name.name+"\")", mainContext ));
						} catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}
		}
		return new SeqValue(vals);
	}
}

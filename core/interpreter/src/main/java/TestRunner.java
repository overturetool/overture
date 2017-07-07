import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.interpreter.runtime.*;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.SetValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueSet;

public class TestRunner
{
	public static Value collectTests(Value obj)
	{
		List<String> tests = new Vector<String>();
		ObjectValue instance = (ObjectValue) obj;

		if (ClassInterpreter.getInstance() instanceof ClassInterpreter)
		{
			for (SClassDefinition def : ((ClassInterpreter) ClassInterpreter.getInstance()).getClasses())
			{
				if (def.getIsAbstract() || !isTestClass(def))
				{
					continue;
				}
				tests.add(def.getName().getName());
			}
		}

		Context mainContext = new StateContext(Interpreter.getInstance().getAssistantFactory(), instance.type.getLocation(), "reflection scope");

		mainContext.putAll(ClassInterpreter.getInstance().initialContext);
		mainContext.setThreadState(ClassInterpreter.getInstance().initialContext.threadState.dbgp, ClassInterpreter.getInstance().initialContext.threadState.CPU);

		ValueSet vals = new ValueSet();
		for (String value : tests)
		{
			try
			{
				vals.add(ClassInterpreter.getInstance().evaluate("new " + value
						+ "()", mainContext));
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try
		{
			return new SetValue(vals);
		}
		catch (ValueException e)
		{
			return null;	// Not reached
		}
	}

	private static boolean isTestClass(SClassDefinition def)
	{
		if (def.getIsAbstract() || def.getName().getName().equals("Test")
				|| def.getName().getName().equals("TestCase")
				|| def.getName().getName().equals("TestSuite"))
		{
			return false;
		}

		if (checkForSuper(def, "TestSuite"))
		{
			// the implementation must be upgrade before this work.
			// The upgrade should handle the static method for creatint the suire
			return false;
		}

		return checkForSuper(def, "Test");
	}

	private static boolean checkForSuper(SClassDefinition def, String superName)
	{
		for (SClassDefinition superDef : def.getSuperDefs())
		{
			if (superDef.getName().getName().equals(superName)
					|| checkForSuper(superDef, superName))
			{
				return true;
			}
		}
		return false;
	}
}

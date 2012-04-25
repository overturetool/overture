import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.StateContext;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.SetValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueSet;


public class TestRunner
{
	public static Value collectTests(Value obj)
	{
		List<String> tests = new Vector<String>();
		ObjectValue instance = (ObjectValue) obj;
		
		if (ClassInterpreter.getInstance() instanceof ClassInterpreter)
		{
			for (ClassDefinition def: ((ClassInterpreter)ClassInterpreter.getInstance()).getClasses())
			{
				if(def.isAbstract || !isTestClass(def))
				{
					continue;
				}
				tests.add(def.getName());
			}
		}

		Context mainContext = new StateContext(instance.type.location, "reflection scope");

		mainContext.putAll(ClassInterpreter.getInstance().initialContext);
		mainContext.setThreadState(ClassInterpreter.getInstance().initialContext.threadState.dbgp, ClassInterpreter.getInstance().initialContext.threadState.CPU);

		
		ValueSet vals = new ValueSet();
		for (String value : tests)
		{
			try
			{
				vals.add(ClassInterpreter.getInstance().evaluate("new "+value+"()", mainContext));
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return new SetValue(vals);
	}

	private static boolean isTestClass(ClassDefinition def)
	{
		if(def.isAbstract || def.getName().equals("Test")|| def.getName().equals("TestCase")|| def.getName().equals("TestSuite"))
		{
			return false;
		}
		
		if(checkForSuper(def,"TestSuite"))
		{
			//the implementation must be upgrade before this work. 
			//The upgrade should handle the static method for creatint the suire
			return false;
		}
		
		return checkForSuper(def,"Test");
	}

	private static boolean checkForSuper(ClassDefinition def, String superName)
	{
		for (ClassDefinition superDef : def.superdefs)
		{
			if(superDef.getName().equals(superName)|| checkForSuper(superDef, superName))
			{
				return true;
			}
		}
		return false;
	}
}

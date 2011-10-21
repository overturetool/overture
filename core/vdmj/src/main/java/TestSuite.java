import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ObjectContext;
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

	public static Value createTests(Value test) throws Exception
	{
		List<String> tests = new Vector<String>();
		ValueList vals = new ValueList();
		ObjectValue instance = (ObjectValue) test;

		for (NameValuePair p : instance.members.asList())
		{
			if (p.value instanceof OperationValue)
			{
				OperationValue opVal = (OperationValue) p.value;
				if (!opVal.isConstructor
						&& p.name.name.toLowerCase().startsWith("test"))
				{
					tests.add(p.name.name);

					Context mainContext = new StateContext(p.name.location, "reflection scope");

					mainContext.putAll(ClassInterpreter.getInstance().initialContext);
					mainContext.setThreadState(ClassInterpreter.getInstance().initialContext.threadState.dbgp, ClassInterpreter.getInstance().initialContext.threadState.CPU);

					try
					{
						ExplicitOperationDefinition ctor = getTestConstructor(instance);
						if (ctor == null
								|| (!ctor.name.module.equals(instance.type.name.location.module) && ctor.paramDefinitions.isEmpty())
								|| !(ctor.accessSpecifier.access == Token.PUBLIC))
						{
							throw new Exception("Class "
									+ p.name.module
									+ " has no public constructor TestCase(String name) or TestCase()");
						}

						String vdmTestExpression = "";
						if (ctor.type.parameters.size() == 1)
						{
							vdmTestExpression = "new " + p.name.module + "(\""
									+ p.name.name + "\")";
							vals.add(ClassInterpreter.getInstance().evaluate(vdmTestExpression, mainContext));
						} else
						{
							boolean foundSetName = false;
							// check that we have setName and that it is accesiable
							for (Definition def : instance.type.classdef.getDefinitions())
							{
								if (def.name.name.equals("setName"))
								{
									foundSetName = true;
								}
							}
							if (!foundSetName)
							{
								throw new Exception("Cannot instantiate test case: "
										+ instance.type.name.name);
							}

							vdmTestExpression = "new " + p.name.module + "()";
							Value testClassInstance = ClassInterpreter.getInstance().evaluate(vdmTestExpression, mainContext);
							ObjectValue tmp = (ObjectValue) testClassInstance;
							ObjectContext octxt = new ObjectContext(mainContext.location, "TestClassContext", mainContext, tmp);
							vdmTestExpression = "setName(\"" + p.name.name
									+ "\")";

							ClassInterpreter.getInstance().evaluate(vdmTestExpression, octxt);
							vals.add(testClassInstance);
						}

					} catch (Exception e)
					{
						throw e;
					}
				}
			}
		}
		return new SeqValue(vals);
	}

	private static ExplicitOperationDefinition getTestConstructor(
			ObjectValue instance)
	{
		ExplicitOperationDefinition ctor = getConstructor(instance, "seq of (char)");
		if (ctor != null)
		{
			return ctor;
		}
		return getConstructor(instance, "()");
	}

	private static ExplicitOperationDefinition getConstructor(
			ObjectValue instance, String typeName)
	{
		boolean searchForDefaultCtor = typeName.trim().equals("()");
		ExplicitOperationDefinition defaultSuperCtor = null;
		for (NameValuePair pair : instance.members.asList())
		{
			if (pair.value instanceof OperationValue)
			{
				OperationValue op = (OperationValue) pair.value;
				if (op.isConstructor)
				{
					if (searchForDefaultCtor
							&& op.expldef.type.parameters.isEmpty())
					{
						if (op.expldef.getName().equals(instance.type.name.name))
						{
							return op.expldef;
						} else
						{
							defaultSuperCtor = op.expldef;
						}
					} else if (op.expldef.type.parameters.size() == 1
							&& op.expldef.type.parameters.get(0).isType(typeName) != null
							&& op.expldef.getName().equals(instance.type.name.name))
					{
						return op.expldef;
					}
				}
			}
		}

		if (searchForDefaultCtor)
		{
			return defaultSuperCtor;
		}
		return null;
	}
}

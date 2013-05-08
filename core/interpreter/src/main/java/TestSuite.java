import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.types.AOperationType;
import org.overture.interpreter.assistant.definition.SClassDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.type.PTypeAssistantInterpreter;
import org.overture.interpreter.runtime.ClassInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.StateContext;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.OperationValue;
import org.overture.interpreter.values.SeqValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.definition.PAccessSpecifierAssistantTC;

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
				if (p.name.getName().toLowerCase().startsWith("test"))
				{
					tests.add(p.name.getName());
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
						&& p.name.getName().toLowerCase().startsWith("test"))
				{
					tests.add(p.name.getName());

					Context mainContext = new StateContext(p.name.getLocation(), "reflection scope");

					mainContext.putAll(ClassInterpreter.getInstance().initialContext);
					mainContext.setThreadState(ClassInterpreter.getInstance().initialContext.threadState.dbgp, ClassInterpreter.getInstance().initialContext.threadState.CPU);

					try
					{
						AExplicitOperationDefinition ctor = getTestConstructor(instance);
						if (ctor == null
								|| (!ctor.getName().getModule().equals(instance.type.getName().getLocation().module) && ctor.getParamDefinitions().isEmpty())
								|| !(PAccessSpecifierAssistantTC.isPublic(ctor.getAccess())))
						{
							throw new Exception("Class "
									+ p.name.getModule()
									+ " has no public constructor TestCase(String name) or TestCase()");
						}

						String vdmTestExpression = "";
						if (ctor.getType().getParameters().size() == 1)
						{
							vdmTestExpression = "new " + p.name.getModule() + "(\""
									+ p.name.getName() + "\")";
							vals.add(ClassInterpreter.getInstance().evaluate(vdmTestExpression, mainContext));
						} else
						{
							boolean foundSetName = false;
							// check that we have setName and that it is accesiable
							for (PDefinition def : SClassDefinitionAssistantInterpreter.getDefinitions(instance.type.getClassdef()))
							{
								if (def.getName().getName().equals("setName"))
								{
									foundSetName = true;
								}
							}
							if (!foundSetName)
							{
								throw new Exception("Cannot instantiate test case: "
										+ instance.type.getName().getName());
							}

							vdmTestExpression = "new " + p.name.getModule() + "()";
							Value testClassInstance = ClassInterpreter.getInstance().evaluate(vdmTestExpression, mainContext);
							ObjectValue tmp = (ObjectValue) testClassInstance;
							ObjectContext octxt = new ObjectContext(mainContext.location, "TestClassContext", mainContext, tmp);
							vdmTestExpression = "setName(\"" + p.name.getName()
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

	private static AExplicitOperationDefinition getTestConstructor(
			ObjectValue instance)
	{
		AExplicitOperationDefinition ctor = getConstructor(instance, "seq of (char)");
		if (ctor != null)
		{
			return ctor;
		}
		return getConstructor(instance, "()");
	}

	private static AExplicitOperationDefinition getConstructor(
			ObjectValue instance, String typeName)
	{
		boolean searchForDefaultCtor = typeName.trim().equals("()");
		AExplicitOperationDefinition defaultSuperCtor = null;
		for (NameValuePair pair : instance.members.asList())
		{
			if (pair.value instanceof OperationValue)
			{
				OperationValue op = (OperationValue) pair.value;
				if (op.isConstructor)
				{
					if (searchForDefaultCtor
							&& op.expldef.getType().getParameters().isEmpty())
					{
						if (op.expldef.getName().equals(instance.type.getName().getName()))
						{
							return op.expldef;
						} else
						{
							defaultSuperCtor = op.expldef;
						}
					} else if (op.expldef.getType().getParameters().size() == 1
							&& PTypeAssistantInterpreter.isType(op.expldef.getType().getParameters().get(0),typeName) != null
							&& op.expldef.getName().equals(instance.type.getName().getName()))
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

package org.overture.interpreter.util;

import java.io.File;
import java.util.List;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.interpreter.runtime.ClassInterpreter;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.runtime.ModuleInterpreter;
import org.overture.interpreter.values.Value;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class InterpreterUtil
{
	public static Interpreter getInterpreter(ModuleListInterpreter modules)
			throws Exception
	{
		return new ModuleInterpreter(modules);
	}

	public static Interpreter getInterpreter(ClassListInterpreter classes)
			throws Exception
	{
		return new ClassInterpreter(classes);
	}

	public static Value interpret(List<INode> ast, String entry, Dialect dialect)
			throws Exception
	{
		if (dialect == Dialect.VDM_SL)
		{
			ModuleListInterpreter list = new ModuleListInterpreter();
			for (INode n : ast)
			{
				list.add((AModuleModules) n);
			}
			Interpreter interpreter = getInterpreter(list);
			interpreter.init(null);
			interpreter.setDefaultName(list.get(0).getName().getName());
			Value val = interpreter.execute(entry, null);
			return val;
		} else
		{
			ClassListInterpreter list = new ClassListInterpreter();
			for (INode n : ast)
			{
				list.add((SClassDefinition) n);
			}
			Interpreter interpreter = getInterpreter(list);
			interpreter.init(null);
			interpreter.setDefaultName(list.get(0).getName().getName());
			Value val = interpreter.execute(entry, null);
			return val;
		}
	}

	public static Value interpret(Dialect vdmPp, String content, File file) throws Exception
	{

		Interpreter interpreter = getInterpreter(new ModuleListInterpreter());
		interpreter.init(null);
		Value val = interpreter.execute(content, null);
		return val;
	}

	public static Value interpret(Dialect dialect, String entry, File file, boolean b)
			throws Exception
	{
		switch (dialect)
		{

			case VDM_SL:
			{
				TypeCheckResult<List<AModuleModules>> result = TypeCheckerUtil.typeCheckSl(file);

				if (result.errors.isEmpty())
				{
					ModuleListInterpreter list = new ModuleListInterpreter();
					list.addAll(result.result);
					Interpreter interpreter = getInterpreter(list);
					interpreter.init(null);
					interpreter.setDefaultName(list.get(0).getName().getName());
					Value val = interpreter.execute(entry, null);
					return val;
				}
			}
			case VDM_PP:
			{
				TypeCheckResult<List<SClassDefinition>> result = TypeCheckerUtil.typeCheckPp(file);
				if (result.errors.isEmpty())
				{
					ClassListInterpreter list = new ClassListInterpreter();
					list.addAll(result.result);
					Interpreter interpreter = getInterpreter(list);
					interpreter.init(null);
					interpreter.setDefaultName(list.get(0).getName().getName());
					Value val = interpreter.execute(entry, null);
					return val;
				}
			}
			case VDM_RT:
			{
				TypeCheckResult<List<SClassDefinition>> result = TypeCheckerUtil.typeCheckRt(file);

				if (result.errors.isEmpty())
				{
					ClassListInterpreter list = new ClassListInterpreter();
					list.addAll(result.result);
					Interpreter interpreter = getInterpreter(list);
					interpreter.init(null);
					interpreter.setDefaultName(list.get(0).getName().getName());
					Value val = interpreter.execute(entry, null);
					return val;
				}
			}
			case CML:
				return null;
		}
		return null;
	}
}

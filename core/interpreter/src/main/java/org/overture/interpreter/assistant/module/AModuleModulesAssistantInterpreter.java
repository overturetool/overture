package org.overture.interpreter.assistant.module;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.statements.PStm;
import org.overture.ast.util.modules.CombinedDefaultModule;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.StateContext;
import org.overture.interpreter.util.ModuleListInterpreter;
import org.overture.pog.pub.IProofObligationList;
import org.overture.pog.pub.ProofObligationGenerator;
import org.overture.typechecker.assistant.module.AModuleModulesAssistantTC;

public class AModuleModulesAssistantInterpreter extends
		AModuleModulesAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AModuleModulesAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public PStm findStatement(ModuleListInterpreter modules, File file,
			int lineno)
	{
		for (AModuleModules m : modules)
		{
			PStm stmt;
			if (m instanceof CombinedDefaultModule)
			{
				stmt = findStatement((CombinedDefaultModule) m, file, lineno);
			} else
			{
				stmt = findStatement(m, file, lineno);
			}

			if (stmt != null)
			{
				return stmt;
			}
		}
		return null;
	}

	public PStm findStatement(CombinedDefaultModule m, File file, int lineno)
	{
		for (AModuleModules module : m.getModules())
		{
			PStm stmt = findStatement(module, file, lineno);

			if (stmt != null)
			{
				return stmt;
			}
		}
		return null;
	}

	public PStm findStatement(AModuleModules m, File file, int lineno)
	{
		if (m.getName().getLocation().getFile().equals(file))
		{
			return af.createPDefinitionAssistant().findStatement(m.getDefs(), lineno);
		}
		return null;
	}

	public PExp findExpression(ModuleListInterpreter modules, File file,
			int lineno)
	{
		for (AModuleModules m : modules)
		{
			PExp exp;
			if (m instanceof CombinedDefaultModule)
			{
				exp = findExpression((CombinedDefaultModule) m, file, lineno);
			} else
			{
				exp = findExpression(m, file, lineno);
			}

			if (exp != null)
			{
				return exp;
			}
		}

		return null;
	}

	public PExp findExpression(CombinedDefaultModule m, File file, int lineno)
	{
		for (AModuleModules module : m.getModules())
		{
			PExp exp = findExpression(module, file, lineno);

			if (exp != null)
			{
				return exp;
			}
		}
		return null;
	}

	public PExp findExpression(AModuleModules m, File file, int lineno)
	{
		if (m.getName().getLocation().getFile().equals(file))
		{
			return af.createPDefinitionListAssistant().findExpression(m.getDefs(), lineno);
		}
		return null;
	}

	public Context getStateContext(AModuleModules defaultModule)
	{
		AStateDefinition sdef = af.createPDefinitionListAssistant().findStateDefinition(defaultModule.getDefs());

		if (sdef != null)
		{
			return af.createAStateDefinitionAssistant().getStateContext(sdef);
		}

		return null;
	}

	/**
	 * Initialize the system for execution from this module. The initial {@link Context} is created, and populated with
	 * name/value pairs from the local definitions and the imported definitions. If state is defined by the module, this
	 * is also initialized, creating the state Context.
	 * 
	 * @param m
	 * @param initialContext
	 * @return True if initialized OK.
	 */
	public Set<ContextException> initialize(AModuleModules m,
			StateContext initialContext)
	{

		Set<ContextException> trouble = new HashSet<ContextException>();

		for (PDefinition d : m.getImportdefs())
		{
			if (d instanceof ARenamedDefinition)
			{
				try
				{
					initialContext.putList(af.createPDefinitionAssistant().getNamedValues(d, initialContext));
				} catch (ContextException e)
				{
					trouble.add(e); // Carry on...
				}
			}
		}

		for (PDefinition d : m.getDefs())
		{
			try
			{
				initialContext.putList(af.createPDefinitionAssistant().getNamedValues(d, initialContext));
			} catch (ContextException e)
			{
				trouble.add(e); // Carry on...
			}
		}

		try
		{
			AStateDefinition sdef = af.createPDefinitionListAssistant().findStateDefinition(m.getDefs());

			if (sdef != null)
			{
				af.createAStateDefinitionAssistant().initState(sdef, initialContext);
			}
		} catch (ContextException e)
		{
			trouble.add(e); // Carry on...
		}

		return trouble;

	}

	public IProofObligationList getProofObligations(AModuleModules m)
			throws AnalysisException
	{
		return ProofObligationGenerator.generateProofObligations(m);
	}

}

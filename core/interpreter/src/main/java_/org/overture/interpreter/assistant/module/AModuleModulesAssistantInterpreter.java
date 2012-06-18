package org.overture.interpreter.assistant.module;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.modules.AModuleModules;
import org.overture.interpreter.assistant.definition.PDefinitionListAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.StateContext;
import org.overture.pog.obligation.POContextStack;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.typechecker.assistant.module.AModuleModulesAssistantTC;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.RenamedDefinition;
import org.overturetool.vdmj.definitions.StateDefinition;

public class AModuleModulesAssistantInterpreter extends
		AModuleModulesAssistantTC
{

	public static Context getStateContext(AModuleModules defaultModule)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Initialize the system for execution from this module. The initial
	 * {@link Context} is created, and populated with name/value pairs from the
	 * local definitions and the imported definitions. If state is defined
	 * by the module, this is also initialized, creating the state Context.
	 *
	 * @return True if initialized OK.
	 */
	public static Set<ContextException> initialize(AModuleModules m,
			StateContext initialContext)
	{
		// TODO Auto-generated method stub
		
		
//		Set<ContextException> trouble = new HashSet<ContextException>();
//
//		for (Definition d: importdefs)
//		{
//			if (d instanceof RenamedDefinition)
//			{
//				try
//				{
//					initialContext.putList(d.getNamedValues(initialContext));
//				}
//				catch (ContextException e)
//				{
//					trouble.add(e);		// Carry on...
//				}
//			}
//		}
//
//		for (Definition d: defs)
//		{
//			try
//			{
//				initialContext.putList(d.getNamedValues(initialContext));
//			}
//			catch (ContextException e)
//			{
//				trouble.add(e);		// Carry on...
//			}
//		}
//
//		try
//		{
//			StateDefinition sdef = defs.findStateDefinition();
//
//			if (sdef != null)
//			{
//				sdef.initState(initialContext);
//			}
//		}
//		catch (ContextException e)
//		{
//			trouble.add(e);		// Carry on...
//		}
//
//		return trouble;
		
		return null;
	}

	public static ProofObligationList getProofObligations(
			AModuleModules m)
	{
		return PDefinitionListAssistantInterpreter.getProofObligations(m.getDefs(),new POContextStack());
	}

}

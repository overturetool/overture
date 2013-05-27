package org.overture.interpreter.assistant.definition;



import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AUntypedDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.ValueList;
import org.overture.pog.POContextStack;
import org.overture.pog.ProofObligationList;
import org.overture.pog.PogVisitor;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;

public class PDefinitionAssistantInterpreter extends PDefinitionAssistantTC
{

	public static NameValuePairList getNamedValues(PDefinition d,
			Context initialContext)
	{
		switch (d.kindPDefinition())
		{
			case AAssignmentDefinition.kindPDefinition:
				return AAssignmentDefinitionAssistantInterpreter.getNamedValues((AAssignmentDefinition) d, initialContext);
			case AEqualsDefinition.kindPDefinition:
				return AEqualsDefinitionAssistantInterpreter.getNamedValues((AEqualsDefinition)d,initialContext);
			case AExplicitFunctionDefinition.kindPDefinition:
				return AExplicitFunctionDefinitionAssistantInterpreter.getNamedValues((AExplicitFunctionDefinition)d,initialContext);
			case AExplicitOperationDefinition.kindPDefinition:
				return AExplicitOperationDefinitionAssistantInterpreter.getNamedValues((AExplicitOperationDefinition)d,initialContext);
			case AImplicitFunctionDefinition.kindPDefinition:
				return AImplicitFunctionDefinitionAssistantInterpreter.getNamedValues((AImplicitFunctionDefinition)d,initialContext);
			case AImplicitOperationDefinition.kindPDefinition:
				return AImplicitOperationDefinitionAssistantInterpreter.getNamedValues((AImplicitOperationDefinition)d,initialContext);
			case AImportedDefinition.kindPDefinition:
				return AImportedDefinitionAssistantInterpreter.getNamedValues((AImportedDefinition)d,initialContext);
			case AInheritedDefinition.kindPDefinition:
				return AInheritedDefinitionAssistantInterpreter.getNamedValues((AInheritedDefinition)d, initialContext);
			case AInstanceVariableDefinition.kindPDefinition:
					return AInstanceVariableDefinitionAssistantInterpreter.getNamedValues((AInstanceVariableDefinition)d,initialContext);
			case ALocalDefinition.kindPDefinition:
				return ALocalDefinitionAssistantInterpreter.getNamedValues((ALocalDefinition)d,initialContext);
			case ARenamedDefinition.kindPDefinition:
				return ARenamedDefinitionAssistantInterpreter.getNamedValues((ARenamedDefinition)d,initialContext);
			case AThreadDefinition.kindPDefinition:
				return AThreadDefinitionAssistantInterpreter.getNamedValues((AThreadDefinition)d,initialContext);
			case ATypeDefinition.kindPDefinition:
				return ATypeDefinitionAssistantInterpreter.getNamedValues((ATypeDefinition)d,initialContext);
			case AUntypedDefinition.kindPDefinition:
				return AUntypedDefinitionAssistantInterpreter.getNamedValues((AUntypedDefinition)d,initialContext);
			case AValueDefinition.kindPDefinition:
				return AValueDefinitionAssistantInterpreter.getNamedValues((AValueDefinition)d,initialContext);
			default:
				return new NameValuePairList();		// Overridden
		}
	}

	public static ProofObligationList getProofObligations(
			PDefinition d, POContextStack ctxt)
	{
		try
		{
			return d.apply(new PogVisitor(), new POContextStack());
		} catch (AnalysisException e)
		{
			e.printStackTrace();
		}
		return new ProofObligationList();
	}

	/**
	 * Return a list of external values that are read by the definition.
	 * @param ctxt The context in which to evaluate the expressions.
	 * @return A list of values read.
	 */
	public static ValueList getValues(PDefinition d,
			ObjectContext ctxt)
	{
		switch (d.kindPDefinition())
		{
			case AAssignmentDefinition.kindPDefinition:
				return AAssignmentDefinitionAssistantInterpreter.getValues((AAssignmentDefinition)d,ctxt);
			case AEqualsDefinition.kindPDefinition:
				return AEqualsDefinitionAssistantInterpreter.getValues((AEqualsDefinition)d, ctxt);
			case AInstanceVariableDefinition.kindPDefinition:
				return AInstanceVariableDefinitionAssistantInterpreter.getValues((AInstanceVariableDefinition)d,ctxt);
			case AValueDefinition.kindPDefinition:
				return AValueDefinitionAssistantInterpreter.getValues((AValueDefinition)d,ctxt);
			default:
				return new ValueList();
		}
		
	}

	public static PExp findExpression(PDefinition d, int lineno)
	{
		switch (d.kindPDefinition())
		{
			case AAssignmentDefinition.kindPDefinition:
				return AAssignmentDefinitionAssistantInterpreter.findExpression((AAssignmentDefinition)d,lineno);
			case SClassDefinition.kindPDefinition:
				return SClassDefinitionAssistantInterpreter.findExpression((SClassDefinition)d,lineno);
			case AEqualsDefinition.kindPDefinition:
				return AEqualsDefinitionAssistantInterpreter.findExpression((AEqualsDefinition)d,lineno);
			case AExplicitFunctionDefinition.kindPDefinition:
				return AExplicitFunctionDefinitionAssistantInterpreter.findExpression((AExplicitFunctionDefinition)d,lineno);
			case AExplicitOperationDefinition.kindPDefinition:
				return AExplicitOperationDefinitionAssistantInterpreter.findExpression((AExplicitOperationDefinition)d,lineno);
			case AImplicitFunctionDefinition.kindPDefinition:
				return AImplicitFunctionDefinitionAssistantInterpreter.findExpression((AImplicitFunctionDefinition)d,lineno);
			case AImplicitOperationDefinition.kindPDefinition:
				return AImplicitOperationDefinitionAssistantInterpreter.findExpression((AImplicitOperationDefinition)d,lineno);
			case AInstanceVariableDefinition.kindPDefinition:
				return AInstanceVariableDefinitionAssistantInterpreter.findExpression((AInstanceVariableDefinition)d,lineno);
			case APerSyncDefinition.kindPDefinition:
				return APerSyncDefinitionAssistantInterpreter.findExpression((APerSyncDefinition)d,lineno);
			case AStateDefinition.kindPDefinition:
				return AStateDefinitionAssistantInterpreter.findExpression((AStateDefinition)d,lineno);
			case AThreadDefinition.kindPDefinition:
				return AThreadDefinitionAssistantInterpreter.findExpression((AThreadDefinition)d,lineno);
			case ATypeDefinition.kindPDefinition:
				return ATypeDefinitionAssistantInterpreter.findExpression((ATypeDefinition)d,lineno);
			case AValueDefinition.kindPDefinition:
				return AValueDefinitionAssistantInterpreter.findExpression((AValueDefinition)d,lineno);
			default:
				return null;
		}
	}

	public static boolean isTypeDefinition(PDefinition def)
	{
		switch (def.kindPDefinition())
		{		
			case SClassDefinition.kindPDefinition:
				return SClassDefinitionAssistantInterpreter.isTypeDefinition((SClassDefinition)def);
			case AImportedDefinition.kindPDefinition:
				return AImportedDefinitionAssistantInterpreter.isTypeDefinition((AImportedDefinition)def);
			case AInheritedDefinition.kindPDefinition:
				return AInheritedDefinitionAssistantInterpreter.isTypeDefinition((AInheritedDefinition)def);
			case ARenamedDefinition.kindPDefinition:
				return ARenamedDefinitionAssistantInterpreter.isTypeDefinition((ARenamedDefinition)def);			
			case ATypeDefinition.kindPDefinition:
				return ATypeDefinitionAssistantInterpreter.isTypeDefinition((ATypeDefinition)def);
			default:
				return false;
		}
	}

	public static boolean isRuntime(PDefinition idef)
	{
		switch(idef.kindPDefinition())
		{
			case AImportedDefinition.kindPDefinition:
				return isRuntime(((AImportedDefinition)idef).getDef());
			case AInheritedDefinition.kindPDefinition:
				return isRuntime(((AInheritedDefinition)idef).getSuperdef());
			case ARenamedDefinition.kindPDefinition:
				return isRuntime(((ARenamedDefinition)idef).getDef());
			case ATypeDefinition.kindPDefinition:
				return false;
			default:
				return true;
			
		}
	}

	public static boolean isValueDefinition(PDefinition d)
	{
		switch(d.kindPDefinition())
		{
			case AImportedDefinition.kindPDefinition:
				return isValueDefinition(((AImportedDefinition)d).getDef());
			case AInheritedDefinition.kindPDefinition:
				return isValueDefinition(((AInheritedDefinition)d).getSuperdef());
			case ARenamedDefinition.kindPDefinition:
				return isValueDefinition(((ARenamedDefinition)d).getDef());
			case AValueDefinition.kindPDefinition:
				return true;
			default:
				return false;
			
		}
	}

	public static boolean isInstanceVariable(PDefinition d)
	{
		switch(d.kindPDefinition())
		{
			case AImportedDefinition.kindPDefinition:
				return isInstanceVariable(((AImportedDefinition)d).getDef());
			case AInheritedDefinition.kindPDefinition:
				return isInstanceVariable(((AInheritedDefinition)d).getSuperdef());
			case ARenamedDefinition.kindPDefinition:
				return isInstanceVariable(((ARenamedDefinition)d).getDef());
			case AInstanceVariableDefinition.kindPDefinition:
				return true;
			default:
				return false;
			
		}
	}

	public static PStm findStatement(LinkedList<PDefinition> definitions,
			int lineno)
	{
		for (PDefinition d: definitions)
		{
			PStm found = findStatement(d,lineno);

			if (found != null)
			{
				return found;
			}
		}

   		return null;
	}

	private static PStm findStatement(PDefinition d, int lineno)
	{
		switch (d.kindPDefinition())
		{			
			case SClassDefinition.kindPDefinition:
				return SClassDefinitionAssistantInterpreter.findStatement((SClassDefinition)d, lineno);
			case AExplicitOperationDefinition.kindPDefinition:
				return AExplicitOperationDefinitionAssistantInterpreter.findStatement((AExplicitOperationDefinition)d,lineno);
			case AImplicitOperationDefinition.kindPDefinition:
				return AImplicitOperationDefinitionAssistantInterpreter.findStatement((AImplicitOperationDefinition)d,lineno);			
			case AThreadDefinition.kindPDefinition:
				return AThreadDefinitionAssistantInterpreter.findStatement((AThreadDefinition)d,lineno);
			default:
				return null;
		}
	}

}

package org.overture.interpreter.assistant.definition;


import java.util.Collection;

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
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AUntypedDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.Value;
import org.overture.pog.obligation.POContextStack;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.visitor.PogVisitor;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;

public class PDefinitionAssistantInterpreter extends PDefinitionAssistantTC
{

	public static NameValuePairList getNamedValues(PDefinition d,
			Context initialContext)
	{
		switch (d.kindPDefinition())
		{
			case ASSIGNMENT:
				try
				{
					return AAssignmentDefinitionAssistantInterpreter.getNamedValues((AAssignmentDefinition) d, initialContext);
				} catch (Throwable e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return new NameValuePairList();
				}
			case EQUALS:
				try
				{
					return AEqualsDefinitionAssistantInterpreter.getNamedValues((AEqualsDefinition)d,initialContext);
				} catch (Throwable e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					return new NameValuePairList();
				}
			case EXPLICITFUNCTION:
				return AExplicitFunctionDefinitionAssistantInterpreter.getNamedValues((AExplicitFunctionDefinition)d,initialContext);
			case EXPLICITOPERATION:
				return AExplicitOperationDefinitionAssistantInterpreter.getNamedValues((AExplicitOperationDefinition)d,initialContext);
			case IMPLICITFUNCTION:
				return AImplicitFunctionDefinitionAssistantInterpreter.getNamedValues((AImplicitFunctionDefinition)d,initialContext);
			case IMPLICITOPERATION:
				return AImplicitOperationDefinitionAssistantInterpreter.getNamedValues((AImplicitOperationDefinition)d,initialContext);
			case IMPORTED:
				return AImportedDefinitionAssistantInterpreter.getNamedValues((AImportedDefinition)d,initialContext);
			case INHERITED:
				return AInheritedDefinitionAssistantInterpreter.getNamedValues((AInheritedDefinition)d, initialContext);
			case INSTANCEVARIABLE:
				try
				{
					return AInstanceVariableDefinitionAssistantInterpreter.getNamedValues((AInstanceVariableDefinition)d,initialContext);
				} catch (Throwable e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					return new NameValuePairList();
				}
			case LOCAL:
				return ALocalDefinitionAssistantInterpreter.getNamedValues((ALocalDefinition)d,initialContext);
			case RENAMED:
				return ARenamedDefinitionAssistantInterpreter.getNamedValues((ARenamedDefinition)d,initialContext);
			case THREAD:
				return AThreadDefinitionAssistantInterpreter.getNamedValues((AThreadDefinition)d,initialContext);
			case TYPE:
				return ATypeDefinitionAssistantInterpreter.getNamedValues((ATypeDefinition)d,initialContext);
			case UNTYPED:
				return AUntypedDefinitionAssistantInterpreter.getNamedValues((AUntypedDefinition)d,initialContext);
			case VALUE:
				try
				{
					return AValueDefinitionAssistantInterpreter.getNamedValues((AValueDefinition)d,initialContext);
				} catch (Throwable e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					return new NameValuePairList();
				}
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
		} catch (Throwable e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ProofObligationList();
	}

	public static Collection<? extends Value> getValues(PDefinition d,
			ObjectContext ctxt)
	{
		// TODO Auto-generated method stub
		return null;
	}

}

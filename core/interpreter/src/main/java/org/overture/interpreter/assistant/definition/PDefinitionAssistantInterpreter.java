package org.overture.interpreter.assistant.definition;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.ValueList;
import org.overture.pog.assistant.PogAssistantFactory;
import org.overture.pog.obligation.POContextStack;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.visitor.PogVisitor;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;

public class PDefinitionAssistantInterpreter extends PDefinitionAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public PDefinitionAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public static NameValuePairList getNamedValues(PDefinition def,
			Context initialContext)
	{
		try
		{
			return def.apply(af.getNamedValueLister(), initialContext);
		} catch (AnalysisException e)
		{
			return null;
		}
//		if (def instanceof AAssignmentDefinition)
//		{
//			return AAssignmentDefinitionAssistantInterpreter.getNamedValues((AAssignmentDefinition) def, initialContext);
//		} else if (def instanceof AEqualsDefinition)
//		{
//			return AEqualsDefinitionAssistantInterpreter.getNamedValues((AEqualsDefinition) def, initialContext);
//		} else if (def instanceof AExplicitFunctionDefinition)
//		{
//			return AExplicitFunctionDefinitionAssistantInterpreter.getNamedValues((AExplicitFunctionDefinition) def, initialContext);
//		} else if (def instanceof AExplicitOperationDefinition)
//		{
//			return AExplicitOperationDefinitionAssistantInterpreter.getNamedValues((AExplicitOperationDefinition) def, initialContext);
//		} else if (def instanceof AImplicitFunctionDefinition)
//		{
//			return AImplicitFunctionDefinitionAssistantInterpreter.getNamedValues((AImplicitFunctionDefinition) def, initialContext);
//		} else if (def instanceof AImplicitOperationDefinition)
//		{
//			return AImplicitOperationDefinitionAssistantInterpreter.getNamedValues((AImplicitOperationDefinition) def, initialContext);
//		} else if (def instanceof AImportedDefinition)
//		{
//			return AImportedDefinitionAssistantInterpreter.getNamedValues((AImportedDefinition) def, initialContext);
//		} else if (def instanceof AInheritedDefinition)
//		{
//			return AInheritedDefinitionAssistantInterpreter.getNamedValues((AInheritedDefinition) def, initialContext);
//		} else if (def instanceof AInstanceVariableDefinition)
//		{
//			return AInstanceVariableDefinitionAssistantInterpreter.getNamedValues((AInstanceVariableDefinition) def, initialContext);
//		} else if (def instanceof ALocalDefinition)
//		{
//			return ALocalDefinitionAssistantInterpreter.getNamedValues((ALocalDefinition) def, initialContext);
//		} else if (def instanceof ARenamedDefinition)
//		{
//			return ARenamedDefinitionAssistantInterpreter.getNamedValues((ARenamedDefinition) def, initialContext);
//		} else if (def instanceof AThreadDefinition)
//		{
//			return AThreadDefinitionAssistantInterpreter.getNamedValues((AThreadDefinition) def, initialContext);
//		} else if (def instanceof ATypeDefinition)
//		{
//			return ATypeDefinitionAssistantInterpreter.getNamedValues((ATypeDefinition) def, initialContext);
//		} else if (def instanceof AUntypedDefinition)
//		{
//			return AUntypedDefinitionAssistantInterpreter.getNamedValues((AUntypedDefinition) def, initialContext);
//		} else if (def instanceof AValueDefinition)
//		{
//			return AValueDefinitionAssistantInterpreter.getNamedValues((AValueDefinition) def, initialContext);
//		} else
//		{
//			return new NameValuePairList(); // Overridden
//		}
	}

	public static ProofObligationList getProofObligations(PDefinition def,
			POContextStack ctxt)
	{
		try
		{
			return def.apply(new PogVisitor(), new POContextStack(new PogAssistantFactory()));
		} catch (AnalysisException e)
		{
			e.printStackTrace();
		}
		return new ProofObligationList();
	}

	/**
	 * Return a list of external values that are read by the definition.
	 * 
	 * @param ctxt
	 *            The context in which to evaluate the expressions.
	 * @return A list of values read.
	 */
	public static ValueList getValues(PDefinition def, ObjectContext ctxt)
	{
		try
		{
			return def.apply(af.getValuesDefinitionLocator(),ctxt);
		} catch (AnalysisException e)
		{
			return null;
		}
//		if (def instanceof AAssignmentDefinition)
//		{
//			return AAssignmentDefinitionAssistantInterpreter.getValues((AAssignmentDefinition) def, ctxt);
//		} else if (def instanceof AEqualsDefinition)
//		{
//			return AEqualsDefinitionAssistantInterpreter.getValues((AEqualsDefinition) def, ctxt);
//		} else if (def instanceof AInstanceVariableDefinition)
//		{
//			return AInstanceVariableDefinitionAssistantInterpreter.getValues((AInstanceVariableDefinition) def, ctxt);
//		} else if (def instanceof AValueDefinition)
//		{
//			return AValueDefinitionAssistantInterpreter.getValues((AValueDefinition) def, ctxt);
//		} else
//		{
//			return new ValueList();
//		}

	}

	public static PExp findExpression(PDefinition def, int lineno)
	{
		try
		{
			return def.apply(af.getExpressionFinder(),lineno);
		} catch (AnalysisException e)
		{
			return null;
		}
//		if (def instanceof AAssignmentDefinition)
//		{
//			return AAssignmentDefinitionAssistantInterpreter.findExpression((AAssignmentDefinition) def, lineno);
//		} else if (def instanceof SClassDefinition)
//		{
//			return SClassDefinitionAssistantInterpreter.findExpression((SClassDefinition) def, lineno);
//		} else if (def instanceof AClassInvariantDefinition)
//		{
//			return AClassInvariantDefinitionAssistantInterpreter.findExpression((AClassInvariantDefinition)def, lineno);
//		} else if (def instanceof AEqualsDefinition)
//		{
//			return AEqualsDefinitionAssistantInterpreter.findExpression((AEqualsDefinition) def, lineno);
//		} else if (def instanceof AExplicitFunctionDefinition)
//		{
//			return AExplicitFunctionDefinitionAssistantInterpreter.findExpression((AExplicitFunctionDefinition) def, lineno);
//		} else if (def instanceof AExplicitOperationDefinition)
//		{
//			return AExplicitOperationDefinitionAssistantInterpreter.findExpression((AExplicitOperationDefinition) def, lineno);
//		} else if (def instanceof AImplicitFunctionDefinition)
//		{
//			return AImplicitFunctionDefinitionAssistantInterpreter.findExpression((AImplicitFunctionDefinition) def, lineno);
//		} else if (def instanceof AImplicitOperationDefinition)
//		{
//			return AImplicitOperationDefinitionAssistantInterpreter.findExpression((AImplicitOperationDefinition) def, lineno);
//		} else if (def instanceof AInstanceVariableDefinition)
//		{
//			return AInstanceVariableDefinitionAssistantInterpreter.findExpression((AInstanceVariableDefinition) def, lineno);
//		} else if (def instanceof APerSyncDefinition)
//		{
//			return APerSyncDefinitionAssistantInterpreter.findExpression((APerSyncDefinition) def, lineno);
//		} else if (def instanceof AStateDefinition)
//		{
//			return AStateDefinitionAssistantInterpreter.findExpression((AStateDefinition) def, lineno);
//		} else if (def instanceof AThreadDefinition)
//		{
//			return AThreadDefinitionAssistantInterpreter.findExpression((AThreadDefinition) def, lineno);
//		} else if (def instanceof ATypeDefinition)
//		{
//			return ATypeDefinitionAssistantInterpreter.findExpression((ATypeDefinition) def, lineno);
//		} else if (def instanceof AValueDefinition)
//		{
//			return AValueDefinitionAssistantInterpreter.findExpression((AValueDefinition) def, lineno);
//		} else
//		{
//			return null;
//		}
	}

	public static boolean isTypeDefinition(PDefinition def)
	{
		try
		{
			return def.apply(af.getTypeDefinitionChecker());
		} catch (AnalysisException e)
		{
			return false;
		}
//		if (def instanceof SClassDefinition)
//		{
//			return SClassDefinitionAssistantInterpreter.isTypeDefinition((SClassDefinition) def);
//		} else if (def instanceof AImportedDefinition)
//		{
//			return AImportedDefinitionAssistantInterpreter.isTypeDefinition((AImportedDefinition) def);
//		} else if (def instanceof AInheritedDefinition)
//		{
//			return AInheritedDefinitionAssistantInterpreter.isTypeDefinition((AInheritedDefinition) def);
//		} else if (def instanceof ARenamedDefinition)
//		{
//			return ARenamedDefinitionAssistantInterpreter.isTypeDefinition((ARenamedDefinition) def);
//		} else if (def instanceof ATypeDefinition)
//		{
//			return ATypeDefinitionAssistantInterpreter.isTypeDefinition((ATypeDefinition) def);
//		} else
//		{
//			return false;
//		}
	}

	public static boolean isRuntime(PDefinition def)
	{
		try
		{
			return def.apply(af.getDefinitionRunTimeChecker());
		} catch (AnalysisException e)
		{
			return true;
		}
//		if (def instanceof AImportedDefinition)
//		{
//			return isRuntime(((AImportedDefinition) def).getDef());
//		} else if (def instanceof AInheritedDefinition)
//		{
//			return isRuntime(((AInheritedDefinition) def).getSuperdef());
//		} else if (def instanceof ARenamedDefinition)
//		{
//			return isRuntime(((ARenamedDefinition) def).getDef());
//		} else if (def instanceof ATypeDefinition)
//		{
//			return false;
//		} else
//		{
//			return true;
//		}
	}

	public static boolean isValueDefinition(PDefinition def)
	{
		try
		{
			return def.apply(af.getDefintionValueChecker());
		} catch (AnalysisException e)
		{
			return false;
		}
//		if (def instanceof AImportedDefinition)
//		{
//			return isValueDefinition(((AImportedDefinition) def).getDef());
//		} else if (def instanceof AInheritedDefinition)
//		{
//			return isValueDefinition(((AInheritedDefinition) def).getSuperdef());
//		} else if (def instanceof ARenamedDefinition)
//		{
//			return isValueDefinition(((ARenamedDefinition) def).getDef());
//		} else if (def instanceof AValueDefinition)
//		{
//			return true;
//		} else
//		{
//			return false;
//		}
	}

	public static boolean isInstanceVariable(PDefinition def)
	{
		if (def instanceof AImportedDefinition)
		{
			return isInstanceVariable(((AImportedDefinition) def).getDef());
		} else if (def instanceof AInheritedDefinition)
		{
			return isInstanceVariable(((AInheritedDefinition) def).getSuperdef());
		} else if (def instanceof ARenamedDefinition)
		{
			return isInstanceVariable(((ARenamedDefinition) def).getDef());
		} else if (def instanceof AInstanceVariableDefinition)
		{
			return true;
		} else
		{
			return false;
		}
	}

	public static PStm findStatement(LinkedList<PDefinition> definitions,
			int lineno)
	{
		for (PDefinition d : definitions)
		{
			PStm found = findStatement(d, lineno);

			if (found != null)
			{
				return found;
			}
		}

		return null;
	}

	private static PStm findStatement(PDefinition def, int lineno)
	{
		if (def instanceof SClassDefinition)
		{
			return SClassDefinitionAssistantInterpreter.findStatement((SClassDefinition) def, lineno);
		} else if (def instanceof AExplicitOperationDefinition)
		{
			return AExplicitOperationDefinitionAssistantInterpreter.findStatement((AExplicitOperationDefinition) def, lineno);
		} else if (def instanceof AImplicitOperationDefinition)
		{
			return AImplicitOperationDefinitionAssistantInterpreter.findStatement((AImplicitOperationDefinition) def, lineno);
		} else if (def instanceof AThreadDefinition)
		{
			return AThreadDefinitionAssistantInterpreter.findStatement((AThreadDefinition) def, lineno);
		} else
		{
			return null;
		}
	}

}

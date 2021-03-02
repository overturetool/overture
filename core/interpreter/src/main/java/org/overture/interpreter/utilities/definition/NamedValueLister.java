package org.overture.interpreter.utilities.definition;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
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
import org.overture.ast.lex.Dialect;
import org.overture.ast.node.INode;
import org.overture.config.Settings;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.PatternMatchException;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.values.FunctionValue;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.OperationValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.interpreter.values.ValueSet;

/***************************************
 * @author gkanos
 ****************************************/
public class NamedValueLister extends
		QuestionAnswerAdaptor<Context, NameValuePairList>
{
	protected IInterpreterAssistantFactory af;

	public NamedValueLister(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public NameValuePairList caseAAssignmentDefinition(
			AAssignmentDefinition def, Context initialContext)
			throws AnalysisException
	{
		try
		{
			Value v = def.getExpression().apply(VdmRuntime.getExpressionEvaluator(), initialContext);

			if (!v.isUndefined())
			{
				v = v.convertTo(def.getType(), initialContext);
			}

			return new NameValuePairList(new NameValuePair(def.getName(), v.getUpdatable(null)));
		} catch (AnalysisException e)
		{
			if (e instanceof ValueException)
			{
				VdmRuntimeError.abort(def.getLocation(), (ValueException) e);
			}
			return null;
		}
	}

	@Override
	public NameValuePairList caseAEqualsDefinition(AEqualsDefinition def,
			Context initialContext) throws AnalysisException
	{
		Value v;
		try
		{
			v = def.getTest().apply(VdmRuntime.getExpressionEvaluator(), initialContext);
		} catch (AnalysisException e1)
		{
			if (e1 instanceof ValueException)
			{
				VdmRuntimeError.abort(def.getLocation(), (ValueException) e1);
			}
			return null;
		}

		NameValuePairList nvpl = null;

		if (def.getPattern() != null)
		{
			try
			{
				nvpl = af.createPPatternAssistant(def.getLocation().getModule()).getNamedValues(def.getPattern(), v, initialContext);
			} catch (PatternMatchException e)
			{
				VdmRuntimeError.abort(e, initialContext);
			}
		}
		else if (def.getTypebind() != null)
		{
			try
			{
				Value converted = v.convertTo(def.getTypebind().getType(), initialContext);
				nvpl = af.createPPatternAssistant(def.getLocation().getModule()).getNamedValues(def.getTypebind().getPattern(), converted, initialContext);
			} catch (PatternMatchException e)
			{
				VdmRuntimeError.abort(e, initialContext);
			} catch (ValueException e)
			{
				VdmRuntimeError.abort(def.getLocation(), e);
			}
		}
		else if (def.getSetbind() != null)
		{
			try
			{
				ValueSet set = def.getSetbind().getSet().apply(VdmRuntime.getExpressionEvaluator(), initialContext).setValue(initialContext);

				if (!set.contains(v))
				{
					VdmRuntimeError.abort(def.getLocation(), 4002, "Expression value is not in set bind", initialContext);
				}

				nvpl = af.createPPatternAssistant(def.getLocation().getModule()).getNamedValues(def.getSetbind().getPattern(), v, initialContext);
			}
			catch (AnalysisException e)
			{
				if (e instanceof PatternMatchException)
				{
					VdmRuntimeError.abort((PatternMatchException) e, initialContext);
				}

				if (e instanceof ValueException)
				{
					VdmRuntimeError.abort(def.getLocation(), (ValueException) e);
				}
			}
		}
		else if (def.getSeqbind() != null)
		{
			try
			{
				ValueList seq = def.getSeqbind().getSeq().apply(VdmRuntime.getExpressionEvaluator(), initialContext).seqValue(initialContext);

				if (!seq.contains(v))
				{
					VdmRuntimeError.abort(def.getLocation(), 4002, "Expression value is not in seq bind", initialContext);
				}

				nvpl = af.createPPatternAssistant(def.getLocation().getModule()).getNamedValues(def.getSeqbind().getPattern(), v, initialContext);
			}
			catch (AnalysisException e)
			{
				if (e instanceof PatternMatchException)
				{
					VdmRuntimeError.abort((PatternMatchException) e, initialContext);
				}

				if (e instanceof ValueException)
				{
					VdmRuntimeError.abort(def.getLocation(), (ValueException) e);
				}
			}
		}

		return nvpl;
	}

	@Override
	public NameValuePairList caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition def, Context initialContext)
			throws AnalysisException
	{
		NameValuePairList nvl = new NameValuePairList();
		Context free = null;	// initialContext.getVisibleVariables();

		FunctionValue prefunc = def.getPredef() == null ? null
				: new FunctionValue(def.getPredef(), null, null, free);

		FunctionValue postfunc = def.getPostdef() == null ? null
				: new FunctionValue(def.getPostdef(), null, null, free);

		FunctionValue func = new FunctionValue(def, prefunc, postfunc, free);
		func.isStatic = af.createPAccessSpecifierAssistant().isStatic(def.getAccess());
		func.uninstantiated = !def.getTypeParams().isEmpty();
		nvl.add(new NameValuePair(def.getName(), func));

		if (def.getPredef() != null)
		{
			nvl.add(new NameValuePair(def.getPredef().getName(), prefunc));
			prefunc.uninstantiated = !def.getTypeParams().isEmpty();
		}

		if (def.getPostdef() != null)
		{
			nvl.add(new NameValuePair(def.getPostdef().getName(), postfunc));
			postfunc.uninstantiated = !def.getTypeParams().isEmpty();
		}
		
		if (def.getMeasureDef() != null && def.getMeasureDef().getName().toString().startsWith("measure_"))
		{
			nvl.add(new NameValuePair(def.getMeasureName(), new FunctionValue(def.getMeasureDef(), null, null, null)));
		}

		if (Settings.dialect == Dialect.VDM_SL)
		{
			// This is needed for recursive local functions
			// free.putList(nvl);
		}

		return nvl;
	}

	@Override
	public NameValuePairList caseAExplicitOperationDefinition(
			AExplicitOperationDefinition def, Context initialContext)
			throws AnalysisException
	{
		NameValuePairList nvl = new NameValuePairList();

		FunctionValue prefunc = def.getPredef() == null ? null
				: new FunctionValue(def.getPredef(), null, null, null);

		FunctionValue postfunc = def.getPostdef() == null ? null
				: new FunctionValue(def.getPostdef(), null, null, null);

		OperationValue op = new OperationValue(def, prefunc, postfunc, def.getState(), af);
		op.isConstructor = def.getIsConstructor();
		op.isStatic = af.createPAccessSpecifierAssistant().isStatic(def.getAccess());
		nvl.add(new NameValuePair(def.getName(), op));

		if (def.getPredef() != null)
		{
			prefunc.isStatic = af.createPAccessSpecifierAssistant().isStatic(def.getAccess());
			nvl.add(new NameValuePair(def.getPredef().getName(), prefunc));
		}

		if (def.getPostdef() != null)
		{
			postfunc.isStatic = af.createPAccessSpecifierAssistant().isStatic(def.getAccess());
			nvl.add(new NameValuePair(def.getPostdef().getName(), postfunc));
		}

		return nvl;
	}

	@Override
	public NameValuePairList caseAImplicitFunctionDefinition(
			AImplicitFunctionDefinition def, Context initialContext)
			throws AnalysisException
	{
		NameValuePairList nvl = new NameValuePairList();
		Context free = null;	// initialContext.getVisibleVariables();

		FunctionValue prefunc = def.getPredef() == null ? null
				: new FunctionValue(def.getPredef(), null, null, free);

		FunctionValue postfunc = def.getPostdef() == null ? null
				: new FunctionValue(def.getPostdef(), null, null, free);

		// Note, body may be null if it is really implicit. This is caught
		// when the function is invoked. The value is needed to implement
		// the pre_() expression for implicit functions.

		FunctionValue func = new FunctionValue(def, prefunc, postfunc, free);
		func.isStatic = af.createPAccessSpecifierAssistant().isStatic(def.getAccess());
		func.uninstantiated = !def.getTypeParams().isEmpty();
		nvl.add(new NameValuePair(def.getName(), func));

		if (def.getPredef() != null)
		{
			nvl.add(new NameValuePair(def.getPredef().getName(), prefunc));
			prefunc.uninstantiated = !def.getTypeParams().isEmpty();
		}

		if (def.getPostdef() != null)
		{
			nvl.add(new NameValuePair(def.getPostdef().getName(), postfunc));
			postfunc.uninstantiated = !def.getTypeParams().isEmpty();
		}
		
		if (def.getMeasureDef() != null && def.getMeasureName().toString().startsWith("measure_"))
		{
			nvl.add(new NameValuePair(def.getMeasureName(), new FunctionValue(def.getMeasureDef(), null, null, null)));
		}

		if (Settings.dialect == Dialect.VDM_SL)
		{
			// This is needed for recursive local functions
			// free.putList(nvl);
		}

		return nvl;
	}

	@Override
	public NameValuePairList caseAImplicitOperationDefinition(
			AImplicitOperationDefinition def, Context initialContext)
			throws AnalysisException
	{
		NameValuePairList nvl = new NameValuePairList();

		FunctionValue prefunc = def.getPredef() == null ? null
				: new FunctionValue(def.getPredef(), null, null, null);

		FunctionValue postfunc = def.getPostdef() == null ? null
				: new FunctionValue(def.getPostdef(), null, null, null);

		// Note, body may be null if it is really implicit. This is caught
		// when the function is invoked. The value is needed to implement
		// the pre_() expression for implicit functions.

		OperationValue op = new OperationValue(def, prefunc, postfunc, def.getState(), af);
		op.isConstructor = def.getIsConstructor();
		op.isStatic = af.createPAccessSpecifierAssistant().isStatic(def.getAccess());
		nvl.add(new NameValuePair(def.getName(), op));

		if (def.getPredef() != null)
		{
			prefunc.isStatic = af.createPAccessSpecifierAssistant().isStatic(def.getAccess());
			nvl.add(new NameValuePair(def.getPredef().getName(), prefunc));
		}

		if (def.getPostdef() != null)
		{
			postfunc.isStatic = af.createPAccessSpecifierAssistant().isStatic(def.getAccess());
			nvl.add(new NameValuePair(def.getPostdef().getName(), postfunc));
		}

		return nvl;
	}

	@Override
	public NameValuePairList caseAImportedDefinition(AImportedDefinition def,
			Context initialContext) throws AnalysisException
	{
		NameValuePairList renamed = new NameValuePairList();

		for (NameValuePair nv : af.createPDefinitionAssistant().getNamedValues(def.getDef(), initialContext))
		{
			if (nv.name.equals(def.getDef().getName())) // NB. excludes pre/post/inv functions
			{
				renamed.add(new NameValuePair(def.getName(), nv.value));
			}
		}

		return renamed;
	}

	@Override
	public NameValuePairList caseAInheritedDefinition(AInheritedDefinition def,
			Context initialContext) throws AnalysisException
	{
		NameValuePairList renamed = new NameValuePairList();

		if (def.getSuperdef() instanceof AUntypedDefinition)
		{
			if (def.getClassDefinition() != null)
			{
				def.setSuperdef(af.createPDefinitionAssistant().findName(def.getClassDefinition(), def.getSuperdef().getName(), def.getNameScope()));
			}
		}

		for (NameValuePair nv : af.createPDefinitionAssistant().getNamedValues(def.getSuperdef(), initialContext))
		{
			renamed.add(new NameValuePair(nv.name.getModifiedName(def.getName().getModule()), nv.value));
		}

		return renamed;
	}

	@Override
	public NameValuePairList caseAInstanceVariableDefinition(
			AInstanceVariableDefinition def, Context initialContext)
			throws AnalysisException
	{
		try
		{
			Value v = def.getExpression().apply(VdmRuntime.getExpressionEvaluator(), initialContext);

			if (!v.isUndefined())
			{
				v = v.convertTo(def.getType(), initialContext);
			}

			return new NameValuePairList(new NameValuePair(def.getName(), v.getUpdatable(null)));
		} catch (ValueException e)
		{
			VdmRuntimeError.abort(def.getLocation(), e);
			return null;
		} catch (AnalysisException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public NameValuePairList caseALocalDefinition(ALocalDefinition def,
			Context initialContext) throws AnalysisException
	{
		NameValuePair nvp = new NameValuePair(def.getName(), initialContext.lookup(def.getName()));
		return new NameValuePairList(nvp);
	}

	@Override
	public NameValuePairList caseARenamedDefinition(ARenamedDefinition def,
			Context initialContext) throws AnalysisException
	{
		NameValuePairList renamed = new NameValuePairList();

		for (NameValuePair nv : af.createPDefinitionAssistant().getNamedValues(def.getDef(), initialContext))
		{
			// We exclude any name from the definition other than the one
			// explicitly renamed. Otherwise, generated names like pre_f
			// come through and are not renamed.

			if (nv.name.equals(def.getDef().getName()))
			{
				renamed.add(new NameValuePair(def.getName(), nv.value));
			}
		}

		return renamed;
	}

	@Override
	public NameValuePairList caseAThreadDefinition(AThreadDefinition def,
			Context initialContext) throws AnalysisException
	{
		return def.getOperationDef().apply(THIS, initialContext);
	}

	@Override
	public NameValuePairList caseATypeDefinition(ATypeDefinition def,
												 Context initialContext) throws AnalysisException
	{
		NameValuePairList nvl = new NameValuePairList();

		if (def.getInvdef() != null)
		{
			FunctionValue invfunc = new FunctionValue(def.getInvdef(), null, null, null);
			nvl.add(new NameValuePair(def.getInvdef().getName(), invfunc));
		}

		if (def.getEqRelation() != null && def.getEqRelation().getRelDef() != null)
		{
			FunctionValue func = new FunctionValue(def.getEqRelation().getRelDef(), null, null, null);
			nvl.add(new NameValuePair(def.getEqRelation().getRelDef().getName(), func));
		}

		if (def.getOrdRelation() != null && def.getOrdRelation().getRelDef() != null)
		{
			FunctionValue func = new FunctionValue(def.getOrdRelation().getRelDef(), null, null, null);
			nvl.add(new NameValuePair(def.getOrdRelation().getRelDef().getName(), func));
		}

		if (def.getOrdRelation() != null && def.getOrdRelation().getMinDef() != null)
		{
			FunctionValue func = new FunctionValue(def.getOrdRelation().getMinDef(), null, null, null);
			nvl.add(new NameValuePair(def.getOrdRelation().getMinDef().getName(), func));
		}

		if (def.getOrdRelation() != null && def.getOrdRelation().getMaxDef() != null)
		{
			FunctionValue func = new FunctionValue(def.getOrdRelation().getMaxDef(), null, null, null);
			nvl.add(new NameValuePair(def.getOrdRelation().getMaxDef().getName(), func));
		}

		return nvl;
	}

	@Override
	public NameValuePairList caseAUntypedDefinition(AUntypedDefinition def,
			Context initialContext) throws AnalysisException
	{
		assert false : "Can't get name/values of untyped definition?";
		return null;
	}

	@Override
	public NameValuePairList caseAValueDefinition(AValueDefinition def,
			Context initialContext) throws AnalysisException
	{
		Value v = null;

		try
		{
			// UpdatableValues are constantized as they cannot be updated.
			v = def.getExpression().apply(VdmRuntime.getExpressionEvaluator(), initialContext).convertTo(af.createPDefinitionAssistant().getType(def), initialContext).getConstant();
			return af.createPPatternAssistant(def.getLocation().getModule()).getNamedValues(def.getPattern(), v, initialContext);
		} catch (ValueException e)
		{
			VdmRuntimeError.abort(def.getLocation(), e);
		} catch (PatternMatchException e)
		{
			VdmRuntimeError.abort(e, initialContext);
		} catch (AnalysisException e)
		{

		}

		return null;
	}

	@Override
	public NameValuePairList defaultPDefinition(PDefinition def,
			Context initialContext) throws AnalysisException
	{
		return new NameValuePairList(); // Overridden
	}

	@Override
	public NameValuePairList createNewReturnValue(INode node, Context question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NameValuePairList createNewReturnValue(Object node, Context question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

}

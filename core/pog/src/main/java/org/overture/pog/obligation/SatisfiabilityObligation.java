/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overture.pog.obligation;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;
import org.overture.typechecker.assistant.definition.SFunctionDefinitionAssistantTC;

public class SatisfiabilityObligation extends ProofObligation
{
	private static final long serialVersionUID = -8922392508326253099L;

	private static final ILexNameToken OLD_STATE_ARG = new LexNameToken(null, "oldstate", null);
	private static final ILexNameToken OLD_SELF_ARG = new LexNameToken(null, "oldself", null);
	private static final ILexNameToken NEW_STATE_ARG = new LexNameToken(null, "newstate", null);
	private static final ILexNameToken NEW_SELF_ARG = new LexNameToken(null, "newself", null);

	public SatisfiabilityObligation(AImplicitFunctionDefinition func,
			IPOContextStack ctxt, IPogAssistantFactory af)
			throws AnalysisException
	{
		super(func, POType.FUNC_SATISFIABILITY, ctxt, func.getLocation(), af);

		/**
		 * f: A * B -> R [pre ...] post ... [pre_f(a, b) =>] exists r:R & post_f(a, b, r)
		 */

		List<PExp> arglist = new Vector<PExp>();

		SFunctionDefinitionAssistantTC assistant = af.createSFunctionDefinitionAssistant();
		// FIXME make these local definitions with unknown types -- so it goes through cg
		List<List<PPattern>> aux = new Vector<List<PPattern>>();
		for (APatternListTypePair p : func.getParamPatterns())
		{
			Vector<PPattern> aux2 = new Vector<PPattern>();
			for (PPattern p2 : p.getPatterns())
			{
				aux2.add(p2.clone());
			}
			aux.add(aux2);
		}

		List<List<PDefinition>> list = assistant.getParamDefinitions(func, func.getType(), aux, func.getLocation());

		for (int i = 0; i < func.getParamPatterns().size(); i++)
		{
			for (int j = 0; j < func.getParamPatterns().get(i).getPatterns().size(); j++)
			{
				PExp exp = patternToExp(func.getParamPatterns().get(i).getPatterns().get(j));
				if (exp instanceof AVariableExp)
				{
					((AVariableExp) exp).setVardef(list.get(i).get(j).clone());
				}
				arglist.add(exp);
			}
		}

		AApplyExp preApply = null;

		if (func.getPredef() != null)
		{
			AVariableExp var = new AVariableExp();
			var.setName(func.getPredef().getName().clone());
			var.setOriginal(func.getPredef().getName().getFullName());
			var.setVardef(func.getPredef().clone());

			AApplyExp apply = new AApplyExp();
			apply.setRoot(var);
			List<PExp> args = new Vector<PExp>();

			for (PExp arg : arglist)
			{
				args.add(arg.clone());
			}

			apply.setArgs(args);

			preApply = getApplyExp(var, arglist);
			preApply.setRoot(var);
			preApply.setType(new ABooleanBasicType());
			preApply.getRoot().setType(func.getPredef().getType().clone());
		}

		AExistsExp existsExp = new AExistsExp();
		existsExp.setType(new ABooleanBasicType());
		List<PExp> postArglist = new Vector<PExp>(arglist);

		if (func.getResult().getPattern() instanceof AIdentifierPattern)
		{
			AIdentifierPattern ip = (AIdentifierPattern) func.getResult().getPattern().clone();
			PExp rExp = patternToExp(func.getResult().getPattern());
			if (rExp instanceof AVariableExp)
			{
				ALocalDefinition l = AstFactory.newALocalDefinition(func.getLocation(), func.getName().clone(), NameScope.LOCAL, func.getResult().getType());
				((AVariableExp) rExp).setVardef(l);
			}
			postArglist.add(rExp);
			existsExp.setBindList(getMultipleTypeBindList(func.getResult().getType().clone(), ip.getName()));
		} else
		{
			throw new RuntimeException("Expecting identifier pattern in function result");
		}

		AVariableExp rootExp = getVarExp(func.getPostdef().getName());
		rootExp.setVardef(func.getPostdef().clone());
		AApplyExp postApply = getApplyExp(rootExp, postArglist);
		postApply.setType(new ABooleanBasicType());
		postApply.getRoot().setType(func.getPostdef().getType().clone());
		existsExp.setPredicate(postApply);

		if (preApply != null)
		{
			AImpliesBooleanBinaryExp implies = AstExpressionFactory.newAImpliesBooleanBinaryExp(preApply, existsExp);
			stitch = implies;
			valuetree.setPredicate(ctxt.getPredWithContext(implies));
		} else
		{
			stitch = existsExp;
			valuetree.setPredicate(ctxt.getPredWithContext(existsExp));
		}

		// valuetree.setContext(ctxt.getContextNodeList());
	}

	public SatisfiabilityObligation(AImplicitOperationDefinition op,
			PDefinition stateDefinition, IPOContextStack ctxt,
			IPogAssistantFactory af) throws AnalysisException
	{
		super(op, POType.OP_SATISFIABILITY, ctxt, op.getLocation(), af);

		/**
		 * op: A * B ==> R [pre ...] post ... [pre_op(a, b, state) =>] exists r:R, state:Sigma & post_op(a, b, r,
		 * state~, state) The state argument is either a Sigma(SL) or self(PP).
		 */

		PExp predExp = buildPredicate(op, stateDefinition);

		stitch = predExp;
		valuetree.setPredicate(ctxt.getPredWithContext(predExp));

	}

	public SatisfiabilityObligation(ATypeDefinition node, IPOContextStack ctxt,
			IPogAssistantFactory af) throws AnalysisException
	{
		super(node, POType.TYPE_INV_SAT, ctxt, node.getLocation(), af);

		AExistsExp exists_exp = new AExistsExp();
		exists_exp.setType(new ABooleanBasicType());

		ATypeMultipleBind tmb = new ATypeMultipleBind();
		List<PPattern> pats = new LinkedList<PPattern>();
		pats.add(node.getInvPattern().clone());
		tmb.setPlist(pats);
		tmb.setType(node.getInvType().clone());
		List<PMultipleBind> binds = new LinkedList<PMultipleBind>();
		binds.add(tmb);

		exists_exp.setBindList(binds);
		exists_exp.setPredicate(node.getInvExpression().clone());

		stitch = exists_exp;
		valuetree.setPredicate(ctxt.getPredWithContext(exists_exp));
	}

	public SatisfiabilityObligation(AClassInvariantDefinition node,
			IPOContextStack ctxt, IPogAssistantFactory af)
			throws AnalysisException
	{
		super(node, POType.STATE_INV_SAT, ctxt, node.getLocation(), af);

		AExistsExp exists_exp = new AExistsExp();
		List<PMultipleBind> binds = stateInvBinds(node);

		exists_exp.setBindList(binds);
		exists_exp.setPredicate(node.getExpression().clone());

		stitch = exists_exp;
		valuetree.setPredicate(ctxt.getPredWithContext(exists_exp));
	}

	protected List<PMultipleBind> stateInvBinds(AClassInvariantDefinition node)
	{
		List<PMultipleBind> binds = new LinkedList<PMultipleBind>();

		for (PDefinition p : node.getClassDefinition().getDefinitions())
		{
			if (p instanceof AInstanceVariableDefinition)
			{
				binds.add(getMultipleTypeBind(p.getType().clone(), p.getName().clone()));
			}
		}

		return binds;
	}

	PExp buildPredicate(AImplicitOperationDefinition op,
			PDefinition stateDefinition) throws AnalysisException
	{
		List<PExp> arglist = new Vector<PExp>();

		for (APatternListTypePair pltp : op.getParameterPatterns())
		{
			for (PPattern pattern : pltp.getPatterns())
			{
				arglist.add(patternToExp(pattern.clone()));
			}
		}

		if (stateDefinition != null)
		{
			stateInPre(arglist, stateDefinition);
		}
		AApplyExp preApply = null;

		if (op.getPredef() != null)
		{
			preApply = getApplyExp(getVarExp(op.getPredef().getName().clone()), arglist);
			preApply.setType(new ABooleanBasicType());
		}

		PExp mainExp;

		// Operation Has a Result. Add it in the post condition.
		if (op.getResult() != null)
		{

			AExistsExp existsExp = new AExistsExp();
			List<PExp> postArglist = new Vector<PExp>(arglist);

			if (op.getResult().getPattern() instanceof AIdentifierPattern)
			{
				AIdentifierPattern ip = (AIdentifierPattern) op.getResult().getPattern();
				postArglist.add(patternToExp(op.getResult().getPattern().clone()));

				if (stateDefinition != null)
				{

					if (stateDefinition instanceof AStateDefinition)
					{
						postArglist.add(getVarExp(OLD_STATE_ARG));
						postArglist.add(getVarExp(NEW_STATE_ARG));
					} else
					{
						postArglist.add(getVarExp(OLD_SELF_ARG));
						postArglist.add(getVarExp(NEW_SELF_ARG));
					}
				}

				existsExp.setBindList(getMultipleTypeBindList(op.getResult().getType().clone(), ip.getName().clone()));
			} else
			{
				throw new RuntimeException("Expecting single identifier pattern in operation result");
			}

			AApplyExp postApply = getApplyExp(getVarExp(op.getPostdef().getName()), postArglist);
			postApply.setType(new ABooleanBasicType());
			existsExp.setPredicate(postApply);
			mainExp = existsExp;
		}

		// No Result. Just add new state to post condition
		else
		{

			AExistsExp exists_exp = new AExistsExp();
			List<PExp> postArglist = new Vector<PExp>(arglist);

			List<PMultipleBind> exists_binds = new LinkedList<PMultipleBind>();
			if (stateDefinition != null)
			{
				stateInPost(exists_binds, postArglist, stateDefinition);
			}
			exists_exp.setBindList(exists_binds);
			exists_exp.setPredicate(getApplyExp(getVarExp(op.getPostdef().getName()), new Vector<PExp>(postArglist)));
			mainExp = exists_exp;
		}

		if (preApply != null)
		{
			return AstExpressionFactory.newAImpliesBooleanBinaryExp(preApply, mainExp);
		} else
		{
			return mainExp;
		}
	}

	protected void stateInPre(List<PExp> args, PDefinition stateDefinition)
	{

		if (stateDefinition instanceof AStateDefinition)
		{
			args.add(getVarExp(OLD_STATE_ARG));
		} else
		{
			args.add(getVarExp(OLD_SELF_ARG));
		}

	}

	protected void stateInPost(List<PMultipleBind> exists_binds,
			List<PExp> postArglist, PDefinition stateDefinition)
	{

		// replace with super call
		if (stateDefinition instanceof AStateDefinition)
		{
			postArglist.add(getVarExp(NEW_STATE_ARG));
			exists_binds.addAll(getMultipleTypeBindList(((AStateDefinition) stateDefinition).getRecordType().clone(), NEW_STATE_ARG));
		} else
		{
			postArglist.add(getVarExp(NEW_SELF_ARG));
			exists_binds.addAll(getMultipleTypeBindList(stateDefinition.getType().clone(), NEW_SELF_ARG));
		}
	}

}

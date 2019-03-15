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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstExpressionFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IPogAssistantFactory;
import org.overture.pog.pub.POType;

public class ParameterPatternObligation extends ProofObligation
{
	private static final long serialVersionUID = 6831031423902894299L;

	public IPogAssistantFactory assistantFactory; // gkanos:variable added by me to pass it as param to the method
													// generate.

	public ParameterPatternObligation(AExplicitFunctionDefinition def,
			IPOContextStack ctxt, IPogAssistantFactory af)
			throws AnalysisException
	{
		super(def, POType.FUNC_PATTERNS, ctxt, def.getLocation(), af);
		this.assistantFactory = af;
		// valuetree.setContext(ctxt.getContextNodeList());
		// cannot clone getPredef as it can be null. We protect the ast in
		// the generate method where it's used
		stitch = generate(def.getPredef(), cloneListPatternList(def.getParamPatternList()), cloneListType(((AFunctionType) def.getType()).getParameters()), ((AFunctionType) def.getType()).getResult().clone());
		valuetree.setPredicate(ctxt.getPredWithContext(stitch));
	}

	public ParameterPatternObligation(AImplicitFunctionDefinition def,
			IPOContextStack ctxt, IPogAssistantFactory af)
			throws AnalysisException
	{
		super(def, POType.FUNC_PATTERNS, ctxt, def.getLocation(), af);
		this.assistantFactory = af;

		stitch = generate(def.getPredef(), cloneListPatternList(assistantFactory.createAImplicitFunctionDefinitionAssistant().getParamPatternList(def)), cloneListType(((AFunctionType) def.getType()).getParameters()), ((AFunctionType) def.getType()).getResult().clone());
		valuetree.setPredicate(ctxt.getPredWithContext(stitch));
	}

	public ParameterPatternObligation(AExplicitOperationDefinition def,
			IPOContextStack ctxt, IPogAssistantFactory af)
			throws AnalysisException
	{
		super(def, POType.OPERATION_PATTERNS, ctxt, def.getLocation(), af);
		this.assistantFactory = af;

		stitch = generate(def.getPredef(), cloneListPatternList(assistantFactory.createAExplicitOperationDefinitionAssistant(null).getParamPatternList(def)), cloneListType(((AOperationType) def.getType()).getParameters()), ((AOperationType) def.getType()).getResult().clone());
		valuetree.setPredicate(ctxt.getPredWithContext(stitch));
	}

	public ParameterPatternObligation(AImplicitOperationDefinition def,
			IPOContextStack ctxt, IPogAssistantFactory af)
			throws AnalysisException
	{
		super(def, POType.OPERATION_PATTERNS, ctxt, def.getLocation(), af);
		this.assistantFactory = af;

		stitch = generate(def.getPredef(), cloneListPatternList(assistantFactory.createAImplicitOperationDefinitionAssistant().getListParamPatternList(def)), cloneListType(((AOperationType) def.getType()).getParameters()), ((AOperationType) def.getType()).getResult().clone());
		valuetree.setPredicate(ctxt.getPredWithContext(stitch));
	}

	private PExp generate(PDefinition predef, List<List<PPattern>> plist,
			List<PType> params, PType result) throws AnalysisException
	{
		AForAllExp forallExp = new AForAllExp();
		List<PMultipleBind> forallBindList = new Vector<PMultipleBind>();
		List<PExp> arglist = new Vector<PExp>();
		PExp forallPredicate = null;

		for (List<PPattern> paramList : plist)
		{
			Iterator<PType> titer = params.iterator();

			if (!paramList.isEmpty())
			{
				AExistsExp existsExp = new AExistsExp();
				List<PMultipleBind> existsBindList = new Vector<PMultipleBind>();
				PExp existsPredicate = null;

				Set<ILexNameToken> previousBindings = new HashSet<ILexNameToken>();

				for (PPattern param : paramList)
				{
					ILexNameToken aname = getUnique("arg");
					ILexNameToken bname = getUnique("bind");

					PType atype = titer.next();
					PExp pmatch = patternToExp(param);
					arglist.add(pmatch.clone());

					forallBindList.add(getMultipleTypeBind(atype, aname));
					existsBindList.add(getMultipleTypeBind(atype, bname));

					for (PDefinition def : assistantFactory.createPPatternAssistant(null).getDefinitions(param, atype, NameScope.LOCAL))
					{
						if (def.getName() != null
								&& !previousBindings.contains(def.getName()))
						{
							existsBindList.add(getMultipleTypeBind(def.getType(), def.getName()));
							previousBindings.add(def.getName());
						}
					}

					AEqualsBinaryExp eq1 = getEqualsExp(getVarExp(aname), getVarExp(bname));
					AEqualsBinaryExp eq2 = getEqualsExp(pmatch, getVarExp(bname));
					existsPredicate = makeAnd(existsPredicate, makeAnd(eq1, eq2));
				}
				existsExp.setBindList(existsBindList);
				existsExp.setPredicate(existsPredicate);

				forallPredicate = makeAnd(forallPredicate, existsExp);
			}

			if (result instanceof AFunctionType)
			{
				AFunctionType ft = (AFunctionType) result;
				result = ft.getResult();
				params = ft.getParameters();
			} else
			{
				break;
			}
		}

		forallExp.setBindList(forallBindList);

		if (predef != null)
		{
			AImpliesBooleanBinaryExp implies = AstExpressionFactory.newAImpliesBooleanBinaryExp(getApplyExp(getVarExp(predef.getName().clone()), arglist), forallPredicate);
			forallExp.setPredicate(implies);
		} else
		{
			forallExp.setPredicate(forallPredicate);
		}

		return forallExp;
	}

	private List<List<PPattern>> cloneListPatternList(List<List<PPattern>> list)
	{
		List<List<PPattern>> r = new LinkedList<List<PPattern>>();
		for (List<PPattern> list2 : list)
		{
			r.add(cloneList(list2));
		}
		return r;

	}

	private List<PPattern> cloneList(List<PPattern> list)
	{
		List<PPattern> r = new LinkedList<PPattern>();
		for (PPattern p : list)
		{
			r.add(p);
		}
		return r;
	}

}

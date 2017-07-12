/*******************************************************************************
 *
 *	Copyright (c) 2017 Fujitsu Services Ltd.
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

package org.overture.typechecker.utilities;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ACasesExp;
import org.overture.ast.expressions.ADefExp;
import org.overture.ast.expressions.AExists1Exp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AFieldExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.AIotaExp;
import org.overture.ast.expressions.AIsExp;
import org.overture.ast.expressions.ALambdaExp;
import org.overture.ast.expressions.ALetBeStExp;
import org.overture.ast.expressions.ALetDefExp;
import org.overture.ast.expressions.AMapCompMapExp;
import org.overture.ast.expressions.AMapEnumMapExp;
import org.overture.ast.expressions.AMapletExp;
import org.overture.ast.expressions.AMkBasicExp;
import org.overture.ast.expressions.AMkTypeExp;
import org.overture.ast.expressions.AMuExp;
import org.overture.ast.expressions.ANarrowExp;
import org.overture.ast.expressions.ARecordModifier;
import org.overture.ast.expressions.ASeqCompSeqExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.ASetCompSetExp;
import org.overture.ast.expressions.ASetEnumSetExp;
import org.overture.ast.expressions.ASetRangeSetExp;
import org.overture.ast.expressions.ASubseqExp;
import org.overture.ast.expressions.ATupleExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SBinaryExp;
import org.overture.ast.expressions.SBooleanBinaryExp;
import org.overture.ast.expressions.SUnaryExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexNameSet;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.ASeqBind;
import org.overture.ast.patterns.ASeqMultipleBind;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.AAlwaysStm;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.AAtomicStm;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.ACasesStm;
import org.overture.ast.statements.ACyclesStm;
import org.overture.ast.statements.ADurationStm;
import org.overture.ast.statements.AExitStm;
import org.overture.ast.statements.AForAllStm;
import org.overture.ast.statements.AForIndexStm;
import org.overture.ast.statements.AForPatternBindStm;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.ALetBeStStm;
import org.overture.ast.statements.ALetStm;
import org.overture.ast.statements.APeriodicStm;
import org.overture.ast.statements.AReturnStm;
import org.overture.ast.statements.ASporadicStm;
import org.overture.ast.statements.AStartStm;
import org.overture.ast.statements.AStopStm;
import org.overture.ast.statements.ATixeStm;
import org.overture.ast.statements.ATrapStm;
import org.overture.ast.statements.AWhileStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.statements.SSimpleBlockStm;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.ASet1SetType;
import org.overture.ast.types.ASetSetType;
import org.overture.ast.types.PType;
import org.overture.typechecker.FlatEnvironment;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Used to find the free variables in a definition, type or expression.
 */
public class FreeVariablesChecker extends QuestionAnswerAdaptor<FreeVarInfo, LexNameSet>
{
	protected ITypeCheckerAssistantFactory af;

	public FreeVariablesChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	/************************* Definitions ***************************/

	@Override
	public LexNameSet caseAClassInvariantDefinition(AClassInvariantDefinition node, FreeVarInfo info)
		throws AnalysisException
	{
		return node.getExpression().apply(this, info);
	}
	
	@Override
	public LexNameSet caseAEqualsDefinition(AEqualsDefinition node,	FreeVarInfo info)
		throws AnalysisException
	{
		FreeVarInfo local = info.set(new FlatEnvironment(af, node.getDefs(), info.env));
		return node.getTest().apply(this, local);
	}
	
	@Override
	public LexNameSet caseAValueDefinition(AValueDefinition node,
		FreeVarInfo info) throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		
		if (node.getType() != null)
		{
			names.addAll(node.getType().apply(this, info));
		}
		
		names.addAll(node.getExpression().apply(this, info));
		return names;
	}

	@Override
	public LexNameSet caseAExplicitFunctionDefinition(AExplicitFunctionDefinition node, FreeVarInfo info)
		throws AnalysisException
	{
		List<PDefinition> defs = new Vector<PDefinition>();
		List<List<PDefinition>> paramDefinitionList = af.createSFunctionDefinitionAssistant().
			getParamDefinitions(node, (AFunctionType) node.getType(), node.getParamPatternList(), node.getLocation());
		
		for (List<PDefinition> list: paramDefinitionList)
		{
			defs.addAll(list);
		}

		FreeVarInfo local = info.set(new FlatEnvironment(af, defs, info.env));
		LexNameSet names = node.getBody().apply(this, local);
		
		if (node.getPredef() != null)
		{
			names.addAll(node.getPredef().apply(this, local));
		}
		
		if (node.getPostdef() != null)
		{
			names.addAll(node.getPostdef().apply(this, local));
		}

		return names;
	}

	@Override
	public LexNameSet caseAImplicitFunctionDefinition(AImplicitFunctionDefinition node, FreeVarInfo info)
		throws AnalysisException
	{
		List<PDefinition> defs = new Vector<PDefinition>();
		
		for (APatternListTypePair pltp : node.getParamPatterns())
		{
			defs.addAll(getDefinitions(pltp, NameScope.LOCAL));
		}
		
		FreeVarInfo local = info.set(new FlatEnvironment(af, defs, info.env));
		LexNameSet names = new LexNameSet();
		
		if (node.getBody() != null)
		{
			names.addAll(node.getBody().apply(this, local));
		}
		
		if (node.getPredef() != null)
		{
			names.addAll(node.getPredef().apply(this, local));
		}
		
		if (node.getPostdef() != null)
		{
			names.addAll(node.getPostdef().apply(this, local));
		}

		return names;
	}
	
	@Override
	public LexNameSet caseATypeDefinition(ATypeDefinition node,	FreeVarInfo info)
		throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		
		if (node.getType() instanceof ANamedInvariantType)
		{
			ANamedInvariantType nt = (ANamedInvariantType)node.getType();
			names.addAll(nt.getType().apply(this, info));
		}
		
		if (node.getInvdef() != null)
		{
			names.addAll(node.getInvdef().apply(this, info));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseAInstanceVariableDefinition(AInstanceVariableDefinition node, FreeVarInfo info)
		throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		names.addAll(node.getType().apply(this, info));
		names.addAll(node.getExpression().apply(this, info));
		return names;
	}
	
	@Override
	public LexNameSet caseALocalDefinition(ALocalDefinition node, FreeVarInfo info)
		throws AnalysisException
	{
		LexNameSet names = node.getType().apply(this, info);
		
		if (node.getValueDefinition() != null)
		{
			names.addAll(node.getValueDefinition().apply(this, info));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseAStateDefinition(AStateDefinition node, FreeVarInfo info)
		throws AnalysisException
	{
		FreeVarInfo local = info.set(new FlatEnvironment(af, new Vector<PDefinition>(), info.env));
		LexNameSet names = new LexNameSet();
		
		if (node.getInvdef() != null)
		{
			names.addAll(node.getInvdef().apply(this, local));
		}
		
		if (node.getInitdef() != null)
		{
			names.addAll(node.getInitdef().apply(this, local));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseAExplicitOperationDefinition(AExplicitOperationDefinition node, FreeVarInfo info)
		throws AnalysisException
	{
		List<PDefinition> defs = new Vector<PDefinition>();

		if (node.getParamDefinitions() != null)
		{
			defs.addAll(node.getParamDefinitions());
		}

		FreeVarInfo local = info.set(new FlatEnvironment(af, defs, info.env));
		LexNameSet names = node.getBody().apply(this, local);

		if (node.getPredef() != null)
		{
			names.addAll(node.getPredef().apply(this, local));
		}

		if (node.getPostdef() != null)
		{
			names.addAll(node.getPostdef().apply(this, local));
		}

		return names;
	}
	
	@Override
	public LexNameSet caseAImplicitOperationDefinition(AImplicitOperationDefinition node, FreeVarInfo info)
		throws AnalysisException
	{
		List<PDefinition> defs = new Vector<PDefinition>();

		for (APatternListTypePair pltp : node.getParameterPatterns())
		{
			defs.addAll(getDefinitions(pltp, NameScope.LOCAL));
		}

		FreeVarInfo local = info.set(new FlatEnvironment(af, defs, info.env));
		LexNameSet names = new LexNameSet();

		if (node.getBody() != null)
		{
			names.addAll(node.getBody().apply(this, local));
		}

		if (node.getPredef() != null)
		{
			names.addAll(node.getPredef().apply(this, local));
		}

		if (node.getPostdef() != null)
		{
			names.addAll(node.getPostdef().apply(this, local));
		}

		return names;
	}
	
	/************************* Expressions ***************************/
	
	@Override
	public LexNameSet caseAVariableExp(AVariableExp node, FreeVarInfo info) throws AnalysisException
	{
		PDefinition d = info.globals.findName(node.getName(), NameScope.NAMESANDSTATE);
		
		if (d != null && af.createPDefinitionAssistant().isFunction(d))
		{
			return new LexNameSet();
		}

		if (info.env.findName(node.getName(), NameScope.NAMESANDSTATE) == null)
		{
			return new LexNameSet(node.getName().getExplicit(true));
		}
		else
		{
			return new LexNameSet();	// Already in local info.env, so not free 
		}
	}
	
	@Override
	public LexNameSet caseAApplyExp(AApplyExp node, FreeVarInfo info) throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		
		if (node.getRoot() instanceof AVariableExp && node.getRoot().getType() != null &&
			af.createPTypeAssistant().isFunction(node.getRoot().getType()))
		{
			// If this is a global call, then we depend on the function
			AVariableExp v = (AVariableExp)node.getRoot();
			
			if (info.globals.findName(v.getName(), NameScope.NAMESANDSTATE) != null)
			{
				names.add(v.getName());
			}
		}
		
		for (PExp exp: node.getArgs())
		{
			names.addAll(exp.apply(this, info));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseACasesExp(ACasesExp node, FreeVarInfo info) throws AnalysisException
	{
		return node.getExpression().apply(this, info);
	}
	
	@Override
	public LexNameSet caseAExistsExp(AExistsExp node, FreeVarInfo info)	throws AnalysisException
	{
		PDefinition def = AstFactory.newAMultiBindListDefinition(node.getLocation(), node.getBindList());
		FreeVarInfo local = info.set(new FlatEnvironment(af, def, info.env));
		LexNameSet names = new LexNameSet();
		
		if (node.getPredicate() != null)
		{
			names.addAll(node.getPredicate().apply(this, local));
		}
		
		for (PMultipleBind mb: node.getBindList())
		{
			names.addAll(mb.apply(this, local));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseAExists1Exp(AExists1Exp node, FreeVarInfo info) throws AnalysisException
	{
		PDefinition def = node.getDef();
		FreeVarInfo local = info.set(new FlatEnvironment(af, def, info.env));
		LexNameSet names = new LexNameSet();
		
		if (node.getPredicate() != null)
		{
			names.addAll(node.getPredicate().apply(this, local));
		}

		names.addAll(node.getBind().apply(this, local));
		return names;
	}
	
	@Override
	public LexNameSet caseAFieldExp(AFieldExp node, FreeVarInfo info) throws AnalysisException
	{
		return node.getObject().apply(this, info);
	}
	
	@Override
	public LexNameSet caseAForAllExp(AForAllExp node, FreeVarInfo info) throws AnalysisException
	{
		PDefinition def = AstFactory.newAMultiBindListDefinition(node.getLocation(), node.getBindList());
		FreeVarInfo local = info.set(new FlatEnvironment(af, def, info.env));
		LexNameSet names = new LexNameSet();
		
		if (node.getPredicate() != null)
		{
			names.addAll(node.getPredicate().apply(this, local));
		}
		
		for (PMultipleBind mb: node.getBindList())
		{
			names.addAll(mb.apply(this, local));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseAFuncInstatiationExp(AFuncInstatiationExp node, FreeVarInfo info)
		throws AnalysisException
	{
		return node.getFunction().apply(this, info);
	}
	
	@Override
	public LexNameSet caseAIfExp(AIfExp node, FreeVarInfo info)	throws AnalysisException
	{
		return node.getTest().apply(this, info);
	}
	
	@Override
	public LexNameSet caseAIotaExp(AIotaExp node, FreeVarInfo info)	throws AnalysisException
	{
		PDefinition def = AstFactory.newAMultiBindListDefinition(node.getLocation(), af.createPBindAssistant().getMultipleBindList(node.getBind()));
		FreeVarInfo local = info.set(new FlatEnvironment(af, def, info.env));
		LexNameSet names = new LexNameSet();
		
		if (node.getPredicate() != null)
		{
			names.addAll(node.getPredicate().apply(this, local));
		}

		names.addAll(node.getBind().apply(this, local));
		return names;
	}
	
	@Override
	public LexNameSet caseAIsExp(AIsExp node, FreeVarInfo info)	throws AnalysisException
	{
		return node.getTest().apply(this, info);
	}
	
	@Override
	public LexNameSet caseALambdaExp(ALambdaExp node, FreeVarInfo info) throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		
		for (ATypeBind mb: node.getBindList())
		{
			names.addAll(mb.apply(this, info));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseALetBeStExp(ALetBeStExp node, FreeVarInfo info) throws AnalysisException
	{
		PDefinition def = AstFactory.newAMultiBindListDefinition(node.getLocation(), af.createPMultipleBindAssistant().getMultipleBindList(node.getBind()));
		FreeVarInfo local = info.set(new FlatEnvironment(af, def, info.env));
		LexNameSet names = node.getBind().apply(this, local);
		
		if (node.getSuchThat() != null)
		{
			names.addAll(node.getSuchThat().apply(this, local));
		}
		
		names.addAll(node.getValue().apply(this, local));
		return names;
	}
	
	@Override
	public LexNameSet caseALetDefExp(ALetDefExp node, FreeVarInfo info) throws AnalysisException
	{
		FreeVarInfo local = info;
		LexNameSet names = new LexNameSet();

		for (PDefinition d : node.getLocalDefs())
		{
			if (d instanceof AExplicitFunctionDefinition)
			{
				// ignore
			}
			else
			{
				local = info.set(new FlatEnvironment(af, d, local.env));
				names.addAll(d.apply(this, local));
			}
		}

		names.addAll(node.getExpression().apply(this, local));
		return names;
	}
	
	@Override
	public LexNameSet caseADefExp(ADefExp node, FreeVarInfo info) throws AnalysisException
	{
		FreeVarInfo local = info;
		LexNameSet names = new LexNameSet();

		for (PDefinition d : node.getLocalDefs())
		{
			if (d instanceof AExplicitFunctionDefinition)
			{
				// ignore
			}
			else
			{
				local = info.set(new FlatEnvironment(af, d, local.env));
				names.addAll(d.apply(this, local));
			}
		}

		names.addAll(node.getExpression().apply(this, local));
		return names;
	}
	
	@Override
	public LexNameSet caseAMapCompMapExp(AMapCompMapExp node, FreeVarInfo info) throws AnalysisException
	{
		PDefinition def = AstFactory.newAMultiBindListDefinition(node.getLocation(), node.getBindings());
		FreeVarInfo local = info.set(new FlatEnvironment(af, def, info.env));
		LexNameSet names = new LexNameSet();	// Note "first" is conditional
		
		if (node.getPredicate() != null)
		{
			node.getPredicate().apply(this, local);
		}
		
		for (PMultipleBind mb: node.getBindings())
		{
			names.addAll(mb.apply(this, local));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseAMapEnumMapExp(AMapEnumMapExp node, FreeVarInfo info) throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		
		for (AMapletExp maplet: node.getMembers())
		{
			names.addAll(maplet.apply(this, info));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseAMapletExp(AMapletExp node, FreeVarInfo info) throws AnalysisException
	{
		LexNameSet names = node.getLeft().apply(this, info);
		names.addAll(node.getRight().apply(this, info));
		return names;
	}
	
	@Override
	public LexNameSet caseAMkBasicExp(AMkBasicExp node, FreeVarInfo info) throws AnalysisException
	{
		LexNameSet names = node.getType().apply(this, info);
		names.addAll(node.getArg().apply(this, info));
		return names;
	}
	
	@Override
	public LexNameSet caseAMkTypeExp(AMkTypeExp node, FreeVarInfo info) throws AnalysisException
	{
		LexNameSet names = new LexNameSet(node.getTypeName());
		
		for (PExp arg: node.getArgs())
		{
			names.addAll(arg.apply(this, info));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseAMuExp(AMuExp node, FreeVarInfo info) throws AnalysisException
	{
		LexNameSet names = node.getRecord().apply(this, info);
		
		for (ARecordModifier rm: node.getModifiers())
		{
			names.addAll(rm.getValue().apply(this, info));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseANarrowExp(ANarrowExp node, FreeVarInfo info) throws AnalysisException
	{
		LexNameSet names = node.getTest().apply(this, info);
		
		if (node.getTypeName() != null)
		{
			names.add(node.getTypeName());
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseASeqCompSeqExp(ASeqCompSeqExp node, FreeVarInfo info) throws AnalysisException
	{
		FreeVarInfo local = null;
		LexNameSet names = new LexNameSet();	// Note "first" is conditional
		
		if (node.getSeqBind() != null)
		{
			PDefinition def = AstFactory.newAMultiBindListDefinition(node.getLocation(), af.createPBindAssistant().getMultipleBindList(node.getSeqBind()));
			local = info.set(new FlatEnvironment(af, def, info.env));
			names.addAll(node.getSeqBind().apply(this, local));
		}
		else if (node.getSetBind() != null)
		{
			PDefinition def = AstFactory.newAMultiBindListDefinition(node.getLocation(), af.createPBindAssistant().getMultipleBindList(node.getSetBind()));
			local = info.set(new FlatEnvironment(af, def, info.env));
			names.addAll(node.getSetBind().apply(this, local));
		}
		
		if (node.getPredicate() != null)
		{
			node.getPredicate().apply(this, local);
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseASeqEnumSeqExp(ASeqEnumSeqExp node, FreeVarInfo info) throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		
		for (PExp exp: node.getMembers())
		{
			names.addAll(exp.apply(this, info));
		}
		
		return names;
	}

	@Override
	public LexNameSet caseASetCompSetExp(ASetCompSetExp node, FreeVarInfo info) throws AnalysisException
	{
		PDefinition def = AstFactory.newAMultiBindListDefinition(node.getLocation(), node.getBindings());
		FreeVarInfo local = info.set(new FlatEnvironment(af, def, info.env));
		LexNameSet names = new LexNameSet();
		
		if (node.getPredicate() != null)
		{
			names.addAll(node.getPredicate().apply(this, local));
		}
		
		for (PMultipleBind mb: node.getBindings())
		{
			names.addAll(mb.apply(this, local));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseASetEnumSetExp(ASetEnumSetExp node, FreeVarInfo info) throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		
		for (PExp exp: node.getMembers())
		{
			names.addAll(exp.apply(this, info));
		}
		
		return names;
	}

	@Override
	public LexNameSet caseASetRangeSetExp(ASetRangeSetExp node,	FreeVarInfo info) throws AnalysisException
	{
		LexNameSet names = node.getFirst().apply(this, info);
		names.addAll(node.getLast().apply(this, info));
		return names;
	}
	
	@Override
	public LexNameSet caseASubseqExp(ASubseqExp node, FreeVarInfo info) throws AnalysisException
	{
		LexNameSet names = node.getFrom().apply(this, info);
		names.addAll(node.getTo().apply(this, info));
		return names;
	}
	
	@Override
	public LexNameSet caseATupleExp(ATupleExp node, FreeVarInfo info) throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		
		for (PExp exp: node.getArgs())
		{
			names.addAll(exp.apply(this, info));
		}
		
		return names;
	}
	
	/************************* Binds ***************************/
	
	@Override
	public LexNameSet caseASeqMultipleBind(ASeqMultipleBind node, FreeVarInfo info) throws AnalysisException
	{
		return node.getSeq().apply(this, info);
	}
	
	@Override
	public LexNameSet caseASetMultipleBind(ASetMultipleBind node, FreeVarInfo info) throws AnalysisException
	{
		return node.getSet().apply(this, info);
	}
	
	@Override
	public LexNameSet caseATypeMultipleBind(ATypeMultipleBind node, FreeVarInfo info) throws AnalysisException
	{
		return node.getType().apply(this, info);
	}
	
	@Override
	public LexNameSet caseASeqBind(ASeqBind node, FreeVarInfo info) throws AnalysisException
	{
		return node.getSeq().apply(this, info);
	}
	
	@Override
	public LexNameSet caseASetBind(ASetBind node, FreeVarInfo info) throws AnalysisException
	{
		return node.getSet().apply(this, info);
	}
	
	@Override
	public LexNameSet caseATypeBind(ATypeBind node, FreeVarInfo info) throws AnalysisException
	{
		return node.getType().apply(this, info);
	}
	
	/************************* Types ***************************/
	
	@Override
	public LexNameSet caseABracketType(ABracketType node, FreeVarInfo info) throws AnalysisException
	{
		return node.getType().apply(this, info);
	}
	
	@Override
	public LexNameSet caseAFunctionType(AFunctionType node, FreeVarInfo info) throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		
		for (PType p: node.getParameters())
		{
			names.addAll(p.apply(this, info));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseAMapMapType(AMapMapType node, FreeVarInfo info) throws AnalysisException
	{
		LexNameSet names = node.getFrom().apply(this, info);
		names.addAll(node.getTo().apply(this, info));
		return names;
	}
	
	@Override
	public LexNameSet caseANamedInvariantType(ANamedInvariantType node, FreeVarInfo info)
		throws AnalysisException
	{
		if (info.env.findType(node.getName(), node.getLocation().getModule()) == null)
		{
			// Invariant values covered in TCTypeDefinition
			return new LexNameSet(node.getName().getExplicit(true));
		}
		else
		{
			return new LexNameSet();
		}
	}
	
	@Override
	public LexNameSet caseAOptionalType(AOptionalType node, FreeVarInfo info) throws AnalysisException
	{
		return node.getType().apply(this, info);
	}
	
	@Override
	public LexNameSet caseAProductType(AProductType node, FreeVarInfo info) throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		
		for (PType p: node.getTypes())
		{
			names.addAll(p.apply(this, info));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseARecordInvariantType(ARecordInvariantType node, FreeVarInfo info) throws AnalysisException
	{
		if (info.env.findType(node.getName(), node.getLocation().getModule()) == null)
		{
			// Invariant values covered in TCTypeDefinition
			return new LexNameSet(node.getName().getExplicit(true));
		}
		else
		{
			return new LexNameSet();
		}
	}
	
	@Override
	public LexNameSet caseASeqSeqType(ASeqSeqType node, FreeVarInfo info) throws AnalysisException
	{
		return node.getSeqof().apply(this, info);
	}

	@Override
	public LexNameSet caseASeq1SeqType(ASeq1SeqType node, FreeVarInfo info) throws AnalysisException
	{
		return node.getSeqof().apply(this, info);
	}
	
	@Override
	public LexNameSet caseASetSetType(ASetSetType node, FreeVarInfo info) throws AnalysisException
	{
		return node.getSetof().apply(this, info);
	}
	
	@Override
	public LexNameSet caseASet1SetType(ASet1SetType node, FreeVarInfo info) throws AnalysisException
	{
		return node.getSetof().apply(this, info);
	}
	
	/************************* Statements ***************************/
	
	@Override
	public LexNameSet caseAAlwaysStm(AAlwaysStm node, FreeVarInfo info)	throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		names.addAll(node.getAlways().apply(this, info));
		names.addAll(node.getBody().apply(this, info));
		return names;
	}
	
	@Override
	public LexNameSet caseAAssignmentStm(AAssignmentStm node, FreeVarInfo info) throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		names.addAll(node.getExp().apply(this, info));
		return names;
	}
	
	@Override
	public LexNameSet caseAAtomicStm(AAtomicStm node, FreeVarInfo info)	throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
				
		for (AAssignmentStm stmt: node.getAssignments())
		{
			names.addAll(stmt.apply(this, info));
		}
		
		return names;
	}

	@Override
	public LexNameSet caseABlockSimpleBlockStm(ABlockSimpleBlockStm node, FreeVarInfo info)
		throws AnalysisException
	{
		FreeVarInfo local = info;
		
		for (AAssignmentDefinition d: node.getAssignmentDefs())
		{
			local = local.set(new FlatEnvironment(af, d, local.env));	// cumulative
		}
		
		return defaultSSimpleBlockStm(node, local);
	}
	
	@Override
	public LexNameSet caseACallObjectStm(ACallObjectStm node, FreeVarInfo info)
		throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		
		for (PExp arg: node.getArgs())
		{
			names.addAll(arg.apply(this, info));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseACallStm(ACallStm node, FreeVarInfo info)	throws AnalysisException
	{
		LexNameSet names = new LexNameSet(node.getName().getExplicit(true));
		
		for (PExp arg: node.getArgs())
		{
			names.addAll(arg.apply(this, info));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseACasesStm(ACasesStm node, FreeVarInfo info) throws AnalysisException
	{
		return node.getExp().apply(this, info);
	}
	
	@Override
	public LexNameSet caseACyclesStm(ACyclesStm node, FreeVarInfo info) throws AnalysisException
	{
		LexNameSet names = node.getCycles().apply(this, info);
		names.addAll(node.getStatement().apply(this, info));
		return names;
	}
	
	@Override
	public LexNameSet caseADurationStm(ADurationStm node, FreeVarInfo info) throws AnalysisException
	{
		LexNameSet names = node.getDuration().apply(this, info);
		names.addAll(node.getStatement().apply(this, info));
		return names;
	}
	
	@Override
	public LexNameSet caseAExitStm(AExitStm node, FreeVarInfo info) throws AnalysisException
	{
		if (node.getExpression() == null)
		{
			return new LexNameSet();
		}
		
		info.returns.set(true);
		return node.getExpression().apply(this, info);
	}
	
	@Override
	public LexNameSet caseAForAllStm(AForAllStm node, FreeVarInfo info)	throws AnalysisException
	{
		return node.getSet().apply(this, info);
	}
	
	@Override
	public LexNameSet caseAForIndexStm(AForIndexStm node, FreeVarInfo info)	throws AnalysisException
	{
		LexNameSet names = node.getFrom().apply(this, info);
		names.addAll(node.getTo().apply(this, info));
		
		if (node.getBy() != null)
		{
			names.addAll(node.getBy().apply(this, info));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseAForPatternBindStm(AForPatternBindStm node, FreeVarInfo info)
		throws AnalysisException
	{
		return node.getExp().apply(this, info);
	}
	
	@Override
	public LexNameSet caseAIfStm(AIfStm node, FreeVarInfo info)	throws AnalysisException
	{
		return node.getIfExp().apply(this, info);
	}
	
	@Override
	public LexNameSet caseALetBeStStm(ALetBeStStm node, FreeVarInfo info) throws AnalysisException
	{
		PDefinition def = AstFactory.newAMultiBindListDefinition(node.getLocation(), af.createPMultipleBindAssistant().getMultipleBindList(node.getBind()));
		FreeVarInfo local = info.set(new FlatEnvironment(af, def, info.env));
		LexNameSet names = node.getBind().apply(this, local);
		
		if (node.getSuchThat() != null)
		{
			names.addAll(node.getSuchThat().apply(this, local));
		}
		
		names.addAll(node.getStatement().apply(this, local));
		return names;
	}
	
	@Override
	public LexNameSet caseALetStm(ALetStm node, FreeVarInfo info) throws AnalysisException
	{
		FreeVarInfo local = info;
		LexNameSet names = new LexNameSet();

		for (PDefinition d : node.getLocalDefs())
		{
			if (d instanceof AExplicitFunctionDefinition)
			{
				// ignore
			}
			else
			{
				local = info.set(new FlatEnvironment(af, d, local.env));
				names.addAll(d.apply(this, local));
			}
		}

		names.addAll(node.getStatement().apply(this, local));
		return names;
	}
	
	@Override
	public LexNameSet caseAPeriodicStm(APeriodicStm node, FreeVarInfo info) throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		
		for (PExp arg: node.getArgs())
		{
			names.addAll(arg.apply(this, info));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseASporadicStm(ASporadicStm node, FreeVarInfo info) throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		
		for (PExp arg: node.getArgs())
		{
			names.addAll(arg.apply(this, info));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseAReturnStm(AReturnStm node, FreeVarInfo info) throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		
		if (node.getExpression() != null)
		{
			names.addAll(node.getExpression().apply(this, info));
		}
		
		info.returns.set(true);
		return names;
	}
	
	@Override
	public LexNameSet caseAStartStm(AStartStm node, FreeVarInfo info) throws AnalysisException
	{
		return node.getObj().apply(this, info);
	}
	
	@Override
	public LexNameSet caseAStopStm(AStopStm node, FreeVarInfo info)	throws AnalysisException
	{
		return node.getObj().apply(this, info);
	}
	
	@Override
	public LexNameSet caseATixeStm(ATixeStm node, FreeVarInfo info) throws AnalysisException
	{
		return node.getBody().apply(this, info);
	}
	
	@Override
	public LexNameSet caseATrapStm(ATrapStm node, FreeVarInfo info) throws AnalysisException
	{
		return node.getBody().apply(this, info);
	}
	
	@Override
	public LexNameSet caseAWhileStm(AWhileStm node, FreeVarInfo info) throws AnalysisException
	{
		return node.getExp().apply(this, info);
	}
	
	/************************* Defaults ***************************/
	
	@Override
	public LexNameSet defaultSBinaryExp(SBinaryExp exp, FreeVarInfo info) throws AnalysisException
	{
		LexNameSet names = exp.getLeft().apply(this, info);
		names.addAll(exp.getRight().apply(this, info));
		return names;
	}
	
	public LexNameSet defaultSBooleanBinaryExp(SBooleanBinaryExp exp, FreeVarInfo info) throws AnalysisException
	{
		return exp.getLeft().apply(this, info);		// Only left is unconditional
	}

	@Override
	public LexNameSet defaultSUnaryExp(SUnaryExp exp, FreeVarInfo info) throws AnalysisException
	{
		return exp.getExp().apply(this, info);
	}
	
	@Override
	public LexNameSet defaultSSimpleBlockStm(SSimpleBlockStm node, FreeVarInfo info)
		throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		
		for (PStm stmt: node.getStatements())
		{
			if (!info.returns.get())
			{
				names.addAll(stmt.apply(this, info));
			}
		}
		
		return names;
	}

	/************************* New values etc ***************************/

	@Override
	public LexNameSet createNewReturnValue(INode node, FreeVarInfo info) throws AnalysisException
	{
		return new LexNameSet();	// None
	}

	@Override
	public LexNameSet createNewReturnValue(Object node, FreeVarInfo info) throws AnalysisException
	{
		return new LexNameSet();	// None
	}
	
	private List<PDefinition> getDefinitions(APatternListTypePair pltp, NameScope scope)
	{
		List<PDefinition> list = new Vector<PDefinition>();

		for (PPattern p : pltp.getPatterns())
		{
			list.addAll(af.createPPatternAssistant().getDefinitions(p, pltp.getType(), scope));
		}

		return list;
	}
}

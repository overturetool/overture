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
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ACasesExp;
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
import org.overture.typechecker.Environment;
import org.overture.typechecker.FlatEnvironment;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Used to find the free variables in a definition, type or expression.
 */
public class FreeVariablesChecker extends QuestionAnswerAdaptor<Environment, LexNameSet>
{
	protected ITypeCheckerAssistantFactory af;

	public FreeVariablesChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public LexNameSet caseAClassInvariantDefinition(AClassInvariantDefinition node, Environment question)
		throws AnalysisException
	{
		return node.getExpression().apply(this, question);
	}
	
	@Override
	public LexNameSet caseAEqualsDefinition(AEqualsDefinition node,	Environment question)
		throws AnalysisException
	{
		Environment env = new FlatEnvironment(af, node.getDefs(), question);
		return node.getTest().apply(this, env);
	}
	
	@Override
	public LexNameSet caseAValueDefinition(AValueDefinition node,
		Environment question) throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		
		if (node.getType() != null)
		{
			names.addAll(node.getType().apply(this, question));
		}
		
		names.addAll(node.getExpression().apply(this, question));
		return names;
	}

	@Override
	public LexNameSet caseAExplicitFunctionDefinition(AExplicitFunctionDefinition node, Environment question)
		throws AnalysisException
	{
		List<PDefinition> defs = new Vector<PDefinition>();
		List<List<PDefinition>> paramDefinitionList = af.createSFunctionDefinitionAssistant().
			getParamDefinitions(node, (AFunctionType) node.getType(), node.getParamPatternList(), node.getLocation());
		
		for (List<PDefinition> list: paramDefinitionList)
		{
			defs.addAll(list);
		}

		Environment local = new FlatEnvironment(af, defs, question);
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
	public LexNameSet caseAImplicitFunctionDefinition(AImplicitFunctionDefinition node, Environment question)
		throws AnalysisException
	{
		List<PDefinition> defs = new Vector<PDefinition>();
		
		for (APatternListTypePair pltp : node.getParamPatterns())
		{
			defs.addAll(getDefinitions(pltp, NameScope.LOCAL));
		}
		
		Environment local = new FlatEnvironment(af, defs, question);
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
	public LexNameSet caseATypeDefinition(ATypeDefinition node,	Environment env)
		throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		
		if (node.getType() instanceof ANamedInvariantType)
		{
			ANamedInvariantType nt = (ANamedInvariantType)node.getType();
			names.addAll(nt.getType().apply(this, env));
		}
		
		if (node.getInvdef() != null)
		{
			names.addAll(node.getInvdef().apply(this, env));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseAInstanceVariableDefinition(AInstanceVariableDefinition node, Environment env)
		throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		names.addAll(node.getType().apply(this, env));
		names.addAll(node.getExpression().apply(this, env));
		return names;
	}
	
	@Override
	public LexNameSet caseALocalDefinition(ALocalDefinition node, Environment env)
		throws AnalysisException
	{
		LexNameSet names = node.getType().apply(this, env);
		
		if (node.getValueDefinition() != null)
		{
			names.addAll(node.getValueDefinition().apply(this, env));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseAStateDefinition(AStateDefinition node, Environment env)
		throws AnalysisException
	{
		Environment local = new FlatEnvironment(af, new Vector<PDefinition>(), env);
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
	public LexNameSet caseAVariableExp(AVariableExp node, Environment question) throws AnalysisException
	{
		if (question.findName(node.getName(), NameScope.NAMES) == null)
		{
			return new LexNameSet(node.getName().getExplicit(true));
		}
		else
		{
			return new LexNameSet();	// Already in local env, so not free 
		}
	}
	
	@Override
	public LexNameSet caseAApplyExp(AApplyExp node, Environment env) throws AnalysisException
	{
		LexNameSet names = node.getRoot().apply(this, env);
		
		for (PExp exp: node.getArgs())
		{
			names.addAll(exp.apply(this, env));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseACasesExp(ACasesExp node, Environment env) throws AnalysisException
	{
		return node.getExpression().apply(this, env);
	}
	
	@Override
	public LexNameSet caseAExistsExp(AExistsExp node, Environment env)	throws AnalysisException
	{
		PDefinition def = AstFactory.newAMultiBindListDefinition(node.getLocation(), node.getBindList());
		Environment local = new FlatEnvironment(af, def, env);
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
	public LexNameSet caseAExists1Exp(AExists1Exp node, Environment env) throws AnalysisException
	{
		PDefinition def = node.getDef();
		Environment local = new FlatEnvironment(af, def, env);
		LexNameSet names = new LexNameSet();
		
		if (node.getPredicate() != null)
		{
			names.addAll(node.getPredicate().apply(this, local));
		}

		names.addAll(node.getBind().apply(this, local));
		return names;
	}
	
	@Override
	public LexNameSet caseAFieldExp(AFieldExp node, Environment question) throws AnalysisException
	{
		return node.getObject().apply(this, question);
	}
	
	@Override
	public LexNameSet caseAForAllExp(AForAllExp node, Environment env) throws AnalysisException
	{
		PDefinition def = AstFactory.newAMultiBindListDefinition(node.getLocation(), node.getBindList());
		Environment local = new FlatEnvironment(af, def, env);
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
	public LexNameSet caseAFuncInstatiationExp(AFuncInstatiationExp node, Environment question)
		throws AnalysisException
	{
		return node.getFunction().apply(this, question);
	}
	
	@Override
	public LexNameSet caseAIfExp(AIfExp node, Environment question)	throws AnalysisException
	{
		return node.getTest().apply(this, question);
	}
	
	@Override
	public LexNameSet caseAIotaExp(AIotaExp node, Environment env)	throws AnalysisException
	{
		PDefinition def = AstFactory.newAMultiBindListDefinition(node.getLocation(), af.createPBindAssistant().getMultipleBindList(node.getBind()));
		Environment local = new FlatEnvironment(af, def, env);
		LexNameSet names = new LexNameSet();
		
		if (node.getPredicate() != null)
		{
			names.addAll(node.getPredicate().apply(this, local));
		}

		names.addAll(node.getBind().apply(this, local));
		return names;
	}
	
	@Override
	public LexNameSet caseAIsExp(AIsExp node, Environment question)	throws AnalysisException
	{
		LexNameSet names = node.getTest().apply(this, question);
		
		if (node.getTypeName() != null)
		{
			names.add(node.getTypeName());
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseALambdaExp(ALambdaExp node, Environment question) throws AnalysisException
	{
		List<PMultipleBind> mbinds = new Vector<PMultipleBind>();
		
		for (ATypeBind tb : node.getBindList())
		{
			mbinds.addAll(tb.apply(af.getMultipleBindLister()));
		}
		
		PDefinition def = AstFactory.newAMultiBindListDefinition(node.getLocation(), mbinds);
		Environment local = new FlatEnvironment(af, def, question);
		LexNameSet names = node.getExpression().apply(this, local);
		
		for (ATypeBind mb: node.getBindList())
		{
			names.addAll(mb.apply(this, local));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseALetBeStExp(ALetBeStExp node, Environment question) throws AnalysisException
	{
		PDefinition def = AstFactory.newAMultiBindListDefinition(node.getLocation(), af.createPMultipleBindAssistant().getMultipleBindList(node.getBind()));
		Environment local = new FlatEnvironment(af, def, question);
		LexNameSet names = node.getBind().apply(this, local);
		
		if (node.getSuchThat() != null)
		{
			names.addAll(node.getSuchThat().apply(this, local));
		}
		
		names.addAll(node.getValue().apply(this, local));
		return names;
	}
	
	@Override
	public LexNameSet caseALetDefExp(ALetDefExp node, Environment env) throws AnalysisException
	{
		Environment local = env;
		LexNameSet names = new LexNameSet();

		for (PDefinition d : node.getLocalDefs())
		{
			if (d instanceof AExplicitFunctionDefinition)
			{
				// ignore
			}
			else
			{
				local = new FlatEnvironment(af, d, local);
				names.addAll(d.apply(this, local));
			}
		}

		names.addAll(node.getExpression().apply(this, local));
		return names;
	}
	
	@Override
	public LexNameSet caseAMapCompMapExp(AMapCompMapExp node, Environment question) throws AnalysisException
	{
		PDefinition def = AstFactory.newAMultiBindListDefinition(node.getLocation(), node.getBindings());
		Environment local = new FlatEnvironment(af, def, question);
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
	public LexNameSet caseAMapEnumMapExp(AMapEnumMapExp node, Environment env) throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		
		for (AMapletExp maplet: node.getMembers())
		{
			names.addAll(maplet.apply(this, env));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseAMapletExp(AMapletExp node, Environment question) throws AnalysisException
	{
		LexNameSet names = node.getLeft().apply(this, question);
		names.addAll(node.getRight().apply(this, question));
		return names;
	}
	
	@Override
	public LexNameSet caseAMkBasicExp(AMkBasicExp node, Environment question) throws AnalysisException
	{
		LexNameSet names = node.getType().apply(this, question);
		names.addAll(node.getArg().apply(this, question));
		return names;
	}
	
	@Override
	public LexNameSet caseAMkTypeExp(AMkTypeExp node, Environment question) throws AnalysisException
	{
		LexNameSet names = new LexNameSet(node.getTypeName());
		
		for (PExp arg: node.getArgs())
		{
			names.addAll(arg.apply(this, question));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseAMuExp(AMuExp node, Environment question) throws AnalysisException
	{
		LexNameSet names = node.getRecord().apply(this, question);
		
		for (ARecordModifier rm: node.getModifiers())
		{
			names.addAll(rm.getValue().apply(this, question));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseANarrowExp(ANarrowExp node, Environment question) throws AnalysisException
	{
		LexNameSet names = node.getTest().apply(this, question);
		
		if (node.getTypeName() != null)
		{
			names.add(node.getTypeName());
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseASeqCompSeqExp(ASeqCompSeqExp node, Environment env) throws AnalysisException
	{
		Environment local = null;
		LexNameSet names = new LexNameSet();	// Note "first" is conditional
		
		if (node.getSeqBind() != null)
		{
			PDefinition def = AstFactory.newAMultiBindListDefinition(node.getLocation(), af.createPBindAssistant().getMultipleBindList(node.getSeqBind()));
			local = new FlatEnvironment(af, def, env);
			names.addAll(node.getSeqBind().apply(this, local));
		}
		else if (node.getSetBind() != null)
		{
			PDefinition def = AstFactory.newAMultiBindListDefinition(node.getLocation(), af.createPBindAssistant().getMultipleBindList(node.getSetBind()));
			local = new FlatEnvironment(af, def, env);
			names.addAll(node.getSetBind().apply(this, local));
		}
		
		if (node.getPredicate() != null)
		{
			node.getPredicate().apply(this, local);
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseASeqEnumSeqExp(ASeqEnumSeqExp node, Environment question) throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		
		for (PExp exp: node.getMembers())
		{
			names.addAll(exp.apply(this, question));
		}
		
		return names;
	}

	@Override
	public LexNameSet caseASetCompSetExp(ASetCompSetExp node, Environment env) throws AnalysisException
	{
		PDefinition def = AstFactory.newAMultiBindListDefinition(node.getLocation(), node.getBindings());
		Environment local = new FlatEnvironment(af, def, env);
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
	public LexNameSet caseASetEnumSetExp(ASetEnumSetExp node, Environment question) throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		
		for (PExp exp: node.getMembers())
		{
			names.addAll(exp.apply(this, question));
		}
		
		return names;
	}

	@Override
	public LexNameSet caseASetRangeSetExp(ASetRangeSetExp node,	Environment env) throws AnalysisException
	{
		LexNameSet names = node.getFirst().apply(this, env);
		names.addAll(node.getLast().apply(this, env));
		return names;
	}
	
	@Override
	public LexNameSet caseASubseqExp(ASubseqExp node, Environment env) throws AnalysisException
	{
		LexNameSet names = node.getFrom().apply(this, env);
		names.addAll(node.getTo().apply(this, env));
		return names;
	}
	
	@Override
	public LexNameSet caseATupleExp(ATupleExp node, Environment env) throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		
		for (PExp exp: node.getArgs())
		{
			names.addAll(exp.apply(this, env));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseASeqMultipleBind(ASeqMultipleBind node, Environment question) throws AnalysisException
	{
		return node.getSeq().apply(this, question);
	}
	
	@Override
	public LexNameSet caseASetMultipleBind(ASetMultipleBind node, Environment question) throws AnalysisException
	{
		return node.getSet().apply(this, question);
	}
	
	@Override
	public LexNameSet caseATypeMultipleBind(ATypeMultipleBind node, Environment question) throws AnalysisException
	{
		return node.getType().apply(this, question);
	}
	
	@Override
	public LexNameSet caseASeqBind(ASeqBind node, Environment question) throws AnalysisException
	{
		return node.getSeq().apply(this, question);
	}
	
	@Override
	public LexNameSet caseASetBind(ASetBind node, Environment question) throws AnalysisException
	{
		return node.getSet().apply(this, question);
	}
	
	@Override
	public LexNameSet caseATypeBind(ATypeBind node, Environment question) throws AnalysisException
	{
		return node.getType().apply(this, question);
	}
	
	@Override
	public LexNameSet caseABracketType(ABracketType node, Environment question) throws AnalysisException
	{
		return node.getType().apply(this, question);
	}
	
	@Override
	public LexNameSet caseAFunctionType(AFunctionType node, Environment question) throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		
		for (PType p: node.getParameters())
		{
			names.addAll(p.apply(this, question));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseAMapMapType(AMapMapType node, Environment env) throws AnalysisException
	{
		LexNameSet names = node.getFrom().apply(this, env);
		names.addAll(node.getTo().apply(this, env));
		return names;
	}
	
	@Override
	public LexNameSet caseANamedInvariantType(ANamedInvariantType node, Environment env)
		throws AnalysisException
	{
		if (env.findType(node.getName(), node.getLocation().getModule()) == null)
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
	public LexNameSet caseAOptionalType(AOptionalType node, Environment question) throws AnalysisException
	{
		return node.getType().apply(this, question);
	}
	
	@Override
	public LexNameSet caseAProductType(AProductType node, Environment question) throws AnalysisException
	{
		LexNameSet names = new LexNameSet();
		
		for (PType p: node.getTypes())
		{
			names.addAll(p.apply(this, question));
		}
		
		return names;
	}
	
	@Override
	public LexNameSet caseARecordInvariantType(ARecordInvariantType node, Environment env) throws AnalysisException
	{
		if (env.findType(node.getName(), node.getLocation().getModule()) == null)
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
	public LexNameSet caseASeqSeqType(ASeqSeqType node, Environment question) throws AnalysisException
	{
		return node.getSeqof().apply(this, question);
	}

	@Override
	public LexNameSet caseASeq1SeqType(ASeq1SeqType node, Environment question) throws AnalysisException
	{
		return node.getSeqof().apply(this, question);
	}
	
	@Override
	public LexNameSet caseASetSetType(ASetSetType node, Environment question) throws AnalysisException
	{
		return node.getSetof().apply(this, question);
	}
	
	@Override
	public LexNameSet caseASet1SetType(ASet1SetType node, Environment question) throws AnalysisException
	{
		return node.getSetof().apply(this, question);
	}
	
	@Override
	public LexNameSet defaultSBinaryExp(SBinaryExp exp, Environment env) throws AnalysisException
	{
		LexNameSet names = exp.getLeft().apply(this, env);
		names.addAll(exp.getRight().apply(this, env));
		return names;
	}

	@Override
	public LexNameSet defaultSUnaryExp(SUnaryExp exp, Environment env) throws AnalysisException
	{
		return exp.getExp().apply(this, env);
	}

	@Override
	public LexNameSet createNewReturnValue(INode node, Environment question) throws AnalysisException
	{
		return new LexNameSet();	// None
	}

	@Override
	public LexNameSet createNewReturnValue(Object node, Environment question) throws AnalysisException
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

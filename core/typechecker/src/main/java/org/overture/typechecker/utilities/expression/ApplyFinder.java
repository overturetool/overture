/*******************************************************************************
 *
 *	Copyright (c) 2019 Nick Battle.
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

package org.overture.typechecker.utilities.expression;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.AAnnotatedUnaryExp;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.expressions.ACasesExp;
import org.overture.ast.expressions.ADefExp;
import org.overture.ast.expressions.AElementsUnaryExp;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AExists1Exp;
import org.overture.ast.expressions.AExistsExp;
import org.overture.ast.expressions.AFieldExp;
import org.overture.ast.expressions.AFieldNumberExp;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.AIotaExp;
import org.overture.ast.expressions.AIsExp;
import org.overture.ast.expressions.AIsOfBaseClassExp;
import org.overture.ast.expressions.AIsOfClassExp;
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
import org.overture.ast.expressions.ANewExp;
import org.overture.ast.expressions.APreExp;
import org.overture.ast.expressions.ARecordModifier;
import org.overture.ast.expressions.ASameBaseClassExp;
import org.overture.ast.expressions.ASameClassExp;
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
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameSet;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.ASeqBind;
import org.overture.ast.patterns.ASeqMultipleBind;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.typechecker.assistant.definition.SFunctionDefinitionAssistantTC;

/**
 */
public class ApplyFinder extends AnswerAdaptor<Set<ILexNameToken>>
{
	private final SFunctionDefinitionAssistantTC assistant;
	private List<SClassDefinition> classes = null;
	private List<AModuleModules> modules = null;
	
	public ApplyFinder(SFunctionDefinitionAssistantTC assistant)
	{
		this.assistant = assistant;
	}

	public void setClasses(List<SClassDefinition> classes)
	{
		this.classes = classes;
	}

	public void setModules(List<AModuleModules> modules)
	{
		this.modules = modules;
	}

	@Override
	public Set<ILexNameToken> caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition node) throws AnalysisException
	{
		Set<ILexNameToken> result = node.getBody().apply(this);

		if (node.getPredef() != null)
		{
			result.addAll(node.getPredef().apply(this));
		}

		if (node.getPostdef() != null)
		{
			result.addAll(node.getPostdef().apply(this));
		}

		return result;
	}

	@Override
	public Set<ILexNameToken> caseAImplicitFunctionDefinition(
			AImplicitFunctionDefinition node) throws AnalysisException
	{
		Set<ILexNameToken> result = new LexNameSet();

		if (node.getBody() != null)
		{
			result.addAll(node.getBody().apply(this));
		}

		if (node.getPredef() != null)
		{
			result.addAll(node.getPredef().apply(this));
		}

		if (node.getPostdef() != null)
		{
			result.addAll(node.getPostdef().apply(this));
		}

		return result;
	}

 	@Override
	public Set<ILexNameToken> caseAApplyExp(AApplyExp node) throws AnalysisException
	{
 		LexNameSet result = new LexNameSet();

		if (node.getRoot() instanceof AVariableExp)
		{
			AVariableExp vexp = (AVariableExp)node.getRoot();
			result.add(lookupDefName(vexp.getName()));
		}
		else if (node.getRoot() instanceof AFuncInstatiationExp)
		{
			AFuncInstatiationExp fie = (AFuncInstatiationExp)node.getRoot();

			if (fie.getFunction() instanceof AVariableExp)
			{
				AVariableExp vexp = (AVariableExp)fie.getFunction();
				result.add(lookupDefName(vexp.getName()));
			}
		}

		result.addAll(node.getRoot().apply(this));

		for (PExp a: node.getArgs())
		{
			result.addAll(a.apply(this));
		}

		return result;
	}

 	private ILexNameToken lookupDefName(ILexNameToken sought)
	{
 		if (classes != null)
 		{
 			PDefinition def = assistant.findClassDefinition(sought, classes);
 			return def == null ? sought : def.getName();
 		}
 		else if (modules != null)
 		{
 			PDefinition def = assistant.findModuleDefinition(sought, modules);
 			return def == null ? sought : def.getName();
 		}
 		else
 		{
 			return sought;
 		}
	}

	@Override
 	public Set<ILexNameToken> caseAAnnotatedUnaryExp(AAnnotatedUnaryExp node) throws AnalysisException
 	{
 		return node.getExp().apply(this);
 	}

 	@Override
	public Set<ILexNameToken> defaultSBinaryExp(SBinaryExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();
		all.addAll(node.getLeft().apply(this));
		all.addAll(node.getRight().apply(this));
		return all;
	}

 	@Override
	public Set<ILexNameToken> caseACasesExp(ACasesExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();
		all.addAll(node.getExpression().apply(this));

		for (ACaseAlternative a: node.getCases())
		{
			all.addAll(a.getResult().apply(this));
		}

		if (node.getOthers() != null)
		{
			all.addAll(node.getOthers().apply(this));
		}

		return all;
	}

 	@Override
	public Set<ILexNameToken> caseADefExp(ADefExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();

		for (PDefinition def: node.getLocalDefs())
 		{
 			if (def instanceof AEqualsDefinition)
 			{
 				AEqualsDefinition edef = (AEqualsDefinition)def;
 				all.addAll(edef.getTest().apply(this));
 			}
 		}

		all.addAll(node.getExpression().apply(this));
		return all;
	}

 	@Override
	public Set<ILexNameToken> caseAElementsUnaryExp(AElementsUnaryExp node) throws AnalysisException
	{
		return node.getExp().apply(this);
	}

 	@Override
	public Set<ILexNameToken> caseAElseIfExp(AElseIfExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();
		all.addAll(node.getElseIf().apply(this));
		all.addAll(node.getThen().apply(this));
		return all;
	}

 	@Override
	public Set<ILexNameToken> caseAExists1Exp(AExists1Exp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();
		all.addAll(caseBind(node.getBind()));

		if (node.getPredicate() != null)
		{
			all.addAll(node.getPredicate().apply(this));
		}

		return all;
	}

 	@Override
	public Set<ILexNameToken> caseAExistsExp(AExistsExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();

		for (PMultipleBind bind: node.getBindList())
		{
			all.addAll(caseMultipleBind(bind));
		}

		if (node.getPredicate() != null)
		{
			all.addAll(node.getPredicate().apply(this));
		}

		return all;
	}

	@Override
	public Set<ILexNameToken> caseAFieldExp(AFieldExp node) throws AnalysisException
	{
		return node.getObject().apply(this);
	}

 	@Override
	public Set<ILexNameToken> caseAFieldNumberExp(AFieldNumberExp node) throws AnalysisException
	{
 		return node.getTuple().apply(this);
	}

 	@Override
	public Set<ILexNameToken> caseAForAllExp(AForAllExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();

		for (PMultipleBind bind: node.getBindList())
		{
			all.addAll(caseMultipleBind(bind));
		}

		if (node.getPredicate() != null)
		{
			all.addAll(node.getPredicate().apply(this));
		}

		return all;
	}

 	@Override
	public Set<ILexNameToken> caseAFuncInstatiationExp(AFuncInstatiationExp node) throws AnalysisException
	{
		return node.getFunction().apply(this);
	}

 	@Override
	public Set<ILexNameToken> caseAIfExp(AIfExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();
		all.addAll(node.getTest().apply(this));
		all.addAll(node.getThen().apply(this));

		for (AElseIfExp elseif: node.getElseList())
		{
			all.addAll(elseif.apply(this));
		}

		all.addAll(node.getElse().apply(this));
		return all;
	}

 	@Override
	public Set<ILexNameToken> caseAIotaExp(AIotaExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();
		all.addAll(caseBind(node.getBind()));

		if (node.getPredicate() != null)
		{
			all.addAll(node.getPredicate().apply(this));
		}

		return all;
	}

 	@Override
	public Set<ILexNameToken> caseAIsExp(AIsExp node) throws AnalysisException
	{
		return node.getTest().apply(this);
	}

 	@Override
	public Set<ILexNameToken> caseAIsOfBaseClassExp(AIsOfBaseClassExp node) throws AnalysisException
	{
 		return node.getExp().apply(this);
	}

 	@Override
	public Set<ILexNameToken> caseAIsOfClassExp(AIsOfClassExp node) throws AnalysisException
	{
 		return node.getExp().apply(this);
	}

 	@Override
	public Set<ILexNameToken> caseALambdaExp(ALambdaExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();

		for (ATypeBind bind: node.getBindList())
		{
			all.addAll(caseBind(bind));
		}

		all.addAll(node.getExpression().apply(this));
		return all;
	}

 	@Override
	public Set<ILexNameToken> caseALetBeStExp(ALetBeStExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();
		all.addAll(caseMultipleBind(node.getBind()));

		if (node.getSuchThat() != null)
		{
			all.addAll(node.getSuchThat().apply(this));
		}

		all.addAll(node.getValue().apply(this));
		return all;
	}

 	@Override
	public Set<ILexNameToken> caseALetDefExp(ALetDefExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();

		for (PDefinition def: node.getLocalDefs())
 		{
 			if (def instanceof AValueDefinition)
 			{
 				AValueDefinition vdef = (AValueDefinition)def;
 				all.addAll(vdef.getExpression().apply(this));
 			}
 		}

		all.addAll(node.getExpression().apply(this));
		return all;
	}

 	@Override
	public Set<ILexNameToken> caseAMapCompMapExp(AMapCompMapExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();
		all.addAll(node.getFirst().getLeft().apply(this));
		all.addAll(node.getFirst().getRight().apply(this));

		for (PMultipleBind mbind: node.getBindings())
		{
			all.addAll(caseMultipleBind(mbind));
		}

		if (node.getPredicate() != null)
		{
			all.addAll(node.getPredicate().apply(this));
		}

		return all;
	}

 	@Override
	public Set<ILexNameToken> caseAMapEnumMapExp(AMapEnumMapExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();

		for (AMapletExp maplet: node.getMembers())
		{
			all.addAll(maplet.getLeft().apply(this));
			all.addAll(maplet.getRight().apply(this));
		}

		return all;
	}

 	@Override
	public Set<ILexNameToken> caseAMkBasicExp(AMkBasicExp node) throws AnalysisException
	{
		return node.getArg().apply(this);
	}

 	@Override
	public Set<ILexNameToken> caseAMkTypeExp(AMkTypeExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();

		for (PExp a: node.getArgs())
		{
			all.addAll(a.apply(this));
		}

		return all;
	}

 	@Override
	public Set<ILexNameToken> caseAMuExp(AMuExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();

		for (ARecordModifier modifier: node.getModifiers())
		{
			all.addAll(modifier.getValue().apply(this));
		}

		all.addAll(node.getRecord().apply(this));
		return all;
	}

 	@Override
	public Set<ILexNameToken> caseANarrowExp(ANarrowExp node) throws AnalysisException
	{
		return node.getTest().apply(this);
	}

 	@Override
	public Set<ILexNameToken> caseANewExp(ANewExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();

		for (PExp a: node.getArgs())
		{
			all.addAll(a.apply(this));
		}

		return all;
	}

 	@Override
	public Set<ILexNameToken> caseAPreExp(APreExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();
		all.addAll(node.getFunction().apply(this));

		for (PExp exp: node.getArgs())
		{
			all.addAll(exp.apply(this));
		}

		return all;
	}

 	@Override
	public Set<ILexNameToken> caseASameBaseClassExp(ASameBaseClassExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();
		all.addAll(node.getLeft().apply(this));
		all.addAll(node.getRight().apply(this));
		return all;
	}

 	@Override
	public Set<ILexNameToken> caseASameClassExp(ASameClassExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();
		all.addAll(node.getLeft().apply(this));
		all.addAll(node.getRight().apply(this));
		return all;
	}

 	@Override
	public Set<ILexNameToken> caseASeqCompSeqExp(ASeqCompSeqExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();
		all.addAll(node.getFirst().apply(this));

		if (node.getSeqBind() != null)
		{
			all.addAll(caseBind(node.getSeqBind()));
		}
		else if (node.getSetBind() != null)
		{
			all.addAll(caseBind(node.getSetBind()));
		}

		if (node.getPredicate() != null)
		{
			all.addAll(node.getPredicate().apply(this));
		}

		return all;
	}

 	@Override
	public Set<ILexNameToken> caseASeqEnumSeqExp(ASeqEnumSeqExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();

		for (PExp m: node.getMembers())
		{
			all.addAll(m.apply(this));
		}

		return all;
	}

 	@Override
	public Set<ILexNameToken> caseASetCompSetExp(ASetCompSetExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();
		all.addAll(node.getFirst().apply(this));

		for (PMultipleBind mbind: node.getBindings())
		{
			all.addAll(caseMultipleBind(mbind));
		}

		if (node.getPredicate() != null)
		{
			all.addAll(node.getPredicate().apply(this));
		}

		return all;
	}

 	@Override
	public Set<ILexNameToken> caseASetEnumSetExp(ASetEnumSetExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();

		for (PExp m: node.getMembers())
		{
			all.addAll(m.apply(this));
		}

		return all;
	}

 	@Override
	public Set<ILexNameToken> caseASetRangeSetExp(ASetRangeSetExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();
		all.addAll(node.getFirst().apply(this));
		all.addAll(node.getLast().apply(this));
		return all;
	}

 	@Override
	public Set<ILexNameToken> caseASubseqExp(ASubseqExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();
		all.addAll(node.getFrom().apply(this));
		all.addAll(node.getTo().apply(this));
		return all;
	}

 	@Override
	public Set<ILexNameToken> caseATupleExp(ATupleExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();

		for (PExp m: node.getArgs())
		{
			all.addAll(m.apply(this));
		}

		return all;
	}

 	@Override
	public Set<ILexNameToken> defaultSUnaryExp(SUnaryExp node) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();
		all.addAll(node.getExp().apply(this));
		return all;
	}

	private Set<ILexNameToken> caseBind(PBind bind) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();

		if (bind instanceof ASetBind)
		{
			ASetBind sbind = (ASetBind)bind;
			all.addAll(sbind.getSet().apply(this));
		}
		else if (bind instanceof ASeqBind)
		{
			ASeqBind sbind = (ASeqBind)bind;
			all.addAll(sbind.getSeq().apply(this));
		}

		return all;
	}

 	private Set<ILexNameToken> caseMultipleBind(PMultipleBind bind) throws AnalysisException
	{
		Set<ILexNameToken> all = new HashSet<ILexNameToken>();

		if (bind instanceof ASetMultipleBind)
		{
			ASetMultipleBind sbind = (ASetMultipleBind)bind;
			all.addAll(sbind.getSet().apply(this));
		}
		else if (bind instanceof ASeqMultipleBind)
		{
			ASeqMultipleBind sbind = (ASeqMultipleBind)bind;
			all.addAll(sbind.getSeq().apply(this));
		}

		return all;
	}

 	@Override
	public Set<ILexNameToken> createNewReturnValue(INode node) throws AnalysisException
	{
		return new HashSet<ILexNameToken>();
	}

	@Override
	public Set<ILexNameToken> createNewReturnValue(Object node) throws AnalysisException
	{
		return new HashSet<ILexNameToken>();
	}
}

/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.visitor;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.AMutexSyncDefinition;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.traces.ATraceDefinitionTerm;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.PType;
import org.overture.ast.util.ClonableString;
import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STermCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AFuncDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.AMutexSyncDeclCG;
import org.overture.codegen.cgast.declarations.ANamedTraceDeclCG;
import org.overture.codegen.cgast.declarations.ANamedTypeDeclCG;
import org.overture.codegen.cgast.declarations.APersyncDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.AStateDeclCG;
import org.overture.codegen.cgast.declarations.AThreadDeclCG;
import org.overture.codegen.cgast.declarations.ATypeDeclCG;
import org.overture.codegen.cgast.expressions.ALambdaExpCG;
import org.overture.codegen.cgast.expressions.ANotImplementedExpCG;
import org.overture.codegen.cgast.name.ATokenNameCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.statements.ANotImplementedStmCG;
import org.overture.codegen.cgast.traces.ATraceDeclTermCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.ATemplateTypeCG;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;

public class DeclVisitorCG extends AbstractVisitorCG<IRInfo, SDeclCG>
{
	@Override
	public SDeclCG caseAStateDefinition(AStateDefinition node, IRInfo question)
			throws AnalysisException
	{
		AAccessSpecifierAccessSpecifier access = node.getAccess();
		ILexNameToken name = node.getName();
		AExplicitFunctionDefinition initdef = node.getInitdef();
		PExp initExp = node.getInitExpression();
		PPattern initPattern = node.getInitPattern();
		AExplicitFunctionDefinition invdef = node.getInvdef();
		PExp invExp = node.getInvExpression();
		PPattern invPattern = node.getInvPattern();

		String accessCg = access.getAccess().toString();
		String nameCg = name != null ? name.getName() : null;
		SDeclCG initDeclCg = initdef != null ? initdef.apply(question.getDeclVisitor(), question)
				: null;
		SExpCG initExpCg = initExp != null ? initExp.apply(question.getExpVisitor(), question)
				: null;
		SPatternCG initPatternCg = initPattern != null ? initPattern.apply(question.getPatternVisitor(), question)
				: null;
		SDeclCG invDeclCg = invdef != null ? invdef.apply(question.getDeclVisitor(), question)
				: null;
		SExpCG invExpCg = invExp != null ? invExp.apply(question.getExpVisitor(), question)
				: null;
		SPatternCG invPatternCg = invPattern != null ? invPattern.apply(question.getPatternVisitor(), question)
				: null;

		AStateDeclCG stateDeclCg = new AStateDeclCG();
		stateDeclCg.setAccess(accessCg);
		stateDeclCg.setName(nameCg);

		if (initDeclCg instanceof AFuncDeclCG)
		{
			stateDeclCg.setInitDecl((AFuncDeclCG) initDeclCg);
		}
		stateDeclCg.setInitExp(initExpCg);
		stateDeclCg.setInitPattern(initPatternCg);

		if (invDeclCg instanceof AFuncDeclCG)
		{
			stateDeclCg.setInvDecl((AFuncDeclCG) invDeclCg);
		}
		stateDeclCg.setInvExp(invExpCg);
		stateDeclCg.setInvPattern(invPatternCg);

		for (AFieldField field : node.getFields())
		{
			SDeclCG fieldCg = field.apply(question.getDeclVisitor(), question);

			if (fieldCg instanceof AFieldDeclCG)
			{
				stateDeclCg.getFields().add((AFieldDeclCG) fieldCg);
			} else
			{
				return null;
			}
		}

		return stateDeclCg;
	}
	
	@Override
	public SDeclCG caseAClassInvariantDefinition(
			AClassInvariantDefinition node, IRInfo question)
			throws AnalysisException
	{
		// Do not report the node as unsupported and generate nothing
		return null;
	}

	@Override
	public SDeclCG caseATraceDefinitionTerm(ATraceDefinitionTerm node,
			IRInfo question) throws AnalysisException
	{
		// Do not report the node as unsupported and generate nothing
		return null;
	}

	@Override
	public SDeclCG caseANamedTraceDefinition(ANamedTraceDefinition node,
			IRInfo question) throws AnalysisException
	{
		if(!question.getSettings().generateTraces())
		{
			return null;
		}
		
		ANamedTraceDeclCG namedTraceDecl = new ANamedTraceDeclCG();

		for(ClonableString cloStr : node.getPathname())
		{
			ATokenNameCG name = new ATokenNameCG();
			name.setName(cloStr.value);
			
			namedTraceDecl.getPathname().add(name);
		}
		
		for(ATraceDefinitionTerm term : node.getTerms())
		{
			STermCG termCg = term.apply(question.getTermVisitor(), question);
			
			if(termCg instanceof ATraceDeclTermCG)
			{
				namedTraceDecl.getTerms().add((ATraceDeclTermCG) termCg);
			}
			else
			{
				Logger.getLog().printErrorln("Expected term to be of type ATraceDeclTermCG. Got: " + termCg);
				return null;
			}
		}
		
		return namedTraceDecl;
	}

	@Override
	public SDeclCG caseANamedInvariantType(ANamedInvariantType node,
			IRInfo question) throws AnalysisException
	{
		PType type = node.getType();
		
		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		
		ATypeNameCG typeName = new ATypeNameCG();
		typeName.setDefiningClass(node.getName().getModule());
		typeName.setName(node.getName().getName());
		
		ANamedTypeDeclCG namedTypeDecl = new ANamedTypeDeclCG();
		namedTypeDecl.setName(typeName);
		namedTypeDecl.setType(typeCg);
		
		return namedTypeDecl;
	}

	@Override
	public SDeclCG caseARecordInvariantType(ARecordInvariantType node,
			IRInfo question) throws AnalysisException
	{
		ILexNameToken name = node.getName();
		LinkedList<AFieldField> fields = node.getFields();

		ARecordDeclCG record = new ARecordDeclCG();
		record.setName(name.getName());
		
		if(node.getInvDef() != null)
		{
			SDeclCG invCg = node.getInvDef().apply(question.getDeclVisitor(), question);
			record.setInvariant(invCg);
		}

		LinkedList<AFieldDeclCG> recordFields = record.getFields();
		for (AFieldField aFieldField : fields)
		{
			SDeclCG res = aFieldField.apply(question.getDeclVisitor(), question);

			if (res instanceof AFieldDeclCG)
			{
				AFieldDeclCG fieldDecl = (AFieldDeclCG) res;
				recordFields.add(fieldDecl);
			} else
			{
				return null;
			}
		}

		return record;
	}

	@Override
	public SDeclCG caseAFieldField(AFieldField node, IRInfo question)
			throws AnalysisException
	{
		// Record fields are public
		String access = IRConstants.PUBLIC;
		String name = node.getTagname().getName();
		boolean isStatic = false;
		boolean isFinal = false;
		STypeCG type = node.getType().apply(question.getTypeVisitor(), question);
		SExpCG exp = null;

		return question.getDeclAssistant().constructField(access, name, isStatic, isFinal, type, exp);
	}

	@Override
	public SDeclCG caseATypeDefinition(ATypeDefinition node, IRInfo question)
			throws AnalysisException
	{
		String access = node.getAccess().getAccess().toString();
		PType type = node.getType();
		
		SDeclCG declCg = type.apply(question.getDeclVisitor(), question);

		SDeclCG invCg = node.getInvdef() != null ?
				node.getInvdef().apply(question.getDeclVisitor(), question)
				: null;
		
		ATypeDeclCG typeDecl = new ATypeDeclCG();
		typeDecl.setAccess(access);
		typeDecl.setDecl(declCg);
		typeDecl.setInv(invCg);
		
		return typeDecl;
	}

	@Override
	public SDeclCG caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition node, IRInfo question)
			throws AnalysisException
	{
		String accessCg = node.getAccess().getAccess().toString();
		String funcNameCg = node.getName().getName();

		STypeCG typeCg = node.getType().apply(question.getTypeVisitor(), question);

		if (!(typeCg instanceof AMethodTypeCG))
		{
			question.addUnsupportedNode(node, "Expected method type for explicit function. Got: "
					+ typeCg);
			return null;
		}

		AMethodTypeCG methodTypeCg = (AMethodTypeCG) typeCg;

		AFuncDeclCG method = new AFuncDeclCG();

		method.setAccess(accessCg);
		method.setMethodType(methodTypeCg);
		method.setName(funcNameCg);

		Iterator<List<PPattern>> iterator = node.getParamPatternList().iterator();
		List<PPattern> paramPatterns = iterator.next();

		LinkedList<AFormalParamLocalParamCG> formalParameters = method.getFormalParams();

		for (int i = 0; i < paramPatterns.size(); i++)
		{
			SPatternCG pattern = paramPatterns.get(i).apply(question.getPatternVisitor(), question);

			AFormalParamLocalParamCG param = new AFormalParamLocalParamCG();
			param.setType(methodTypeCg.getParams().get(i).clone());
			param.setPattern(pattern);

			formalParameters.add(param);
		}

		if (node.getIsUndefined())
		{
			method.setBody(new ANotImplementedExpCG());
		} else if (node.getIsCurried())
		{
			AMethodTypeCG nextLevel = (AMethodTypeCG) methodTypeCg;

			ALambdaExpCG currentLambda = new ALambdaExpCG();
			ALambdaExpCG topLambda = currentLambda;

			while (iterator.hasNext())
			{
				nextLevel = (AMethodTypeCG) nextLevel.getResult();
				paramPatterns = iterator.next();

				for (int i = 0; i < paramPatterns.size(); i++)
				{
					PPattern param = paramPatterns.get(i);

					SPatternCG patternCg = param.apply(question.getPatternVisitor(), question);

					AFormalParamLocalParamCG paramCg = new AFormalParamLocalParamCG();
					paramCg.setPattern(patternCg);
					paramCg.setType(nextLevel.getParams().get(i).clone());

					currentLambda.getParams().add(paramCg);
				}

				currentLambda.setType(nextLevel.clone());

				if (iterator.hasNext())
				{
					ALambdaExpCG nextLambda = new ALambdaExpCG();
					currentLambda.setExp(nextLambda);
					currentLambda = nextLambda;
				}

			}

			SExpCG bodyExp = node.getBody().apply(question.getExpVisitor(), question);
			currentLambda.setExp(bodyExp);
			method.setBody(topLambda);
		} else
		{
			SExpCG bodyCg = node.getBody().apply(question.getExpVisitor(), question);
			method.setBody(bodyCg);
		}

		boolean isAbstract = method.getBody() == null;
		method.setAbstract(isAbstract);

		// If the function uses any type parameters they will be
		// registered as part of the method declaration
		LinkedList<ILexNameToken> typeParams = node.getTypeParams();
		for (int i = 0; i < typeParams.size(); i++)
		{
			ILexNameToken typeParam = typeParams.get(i);
			ATemplateTypeCG templateType = new ATemplateTypeCG();
			templateType.setName(typeParam.getName());
			method.getTemplateTypes().add(templateType);
		}
		
		AExplicitFunctionDefinition preCond = node.getPredef();
		SDeclCG preCondCg = preCond != null ? preCond.apply(question.getDeclVisitor(), question) : null;
		method.setPreCond(preCondCg);
		
		AExplicitFunctionDefinition postCond = node.getPostdef();
		SDeclCG postCondCg = postCond != null ? postCond.apply(question.getDeclVisitor(), question) : null;
		method.setPostCond(postCondCg);

		method.setImplicit(false);
		
		return method;
	}
	
	@Override
	public SDeclCG caseAImplicitFunctionDefinition(
			AImplicitFunctionDefinition node, IRInfo question)
			throws AnalysisException
	{
		String accessCg = node.getAccess().getAccess().toString();
		String funcNameCg = node.getName().getName();
		
		STypeCG typeCg = node.getType().apply(question.getTypeVisitor(), question);
		
		if (!(typeCg instanceof AMethodTypeCG))
		{
			question.addUnsupportedNode(node, "Expected method type for implicit function. Got: "
					+ typeCg);
			return null;
		}

		AFuncDeclCG func = new AFuncDeclCG();
		AExplicitFunctionDefinition preCond = node.getPredef();
		SDeclCG preCondCg = preCond != null ? preCond.apply(question.getDeclVisitor(), question) : null;
		func.setPreCond(preCondCg);
		
		AExplicitFunctionDefinition postCond = node.getPostdef();
		SDeclCG postCondCg = postCond != null ? postCond.apply(question.getDeclVisitor(), question) : null;
		func.setPostCond(postCondCg);


		// If the function uses any type parameters they will be
		// registered as part of the method declaration
		List<ILexNameToken> typeParams = node.getTypeParams();
		for (int i = 0; i < typeParams.size(); i++)
		{
			ILexNameToken typeParam = typeParams.get(i);
			ATemplateTypeCG templateType = new ATemplateTypeCG();
			templateType.setName(typeParam.getName());
			func.getTemplateTypes().add(templateType);
		}

		func.setAbstract(false);
		func.setAccess(accessCg);
		func.setImplicit(true);
		func.setBody(new ANotImplementedExpCG());
		func.setFormalParams(question.getDeclAssistant().
				consFormalParams(node.getParamPatterns(), question));
		func.setMethodType((AMethodTypeCG) typeCg);
		func.setName(funcNameCg);
		
		// The implicit function is currently constructed without the result information:
		//SPatternCG resPatternCg = node.getResult().getPattern().apply(question.getPatternVisitor(), question);
		//STypeCG resTypeCg = node.getResult().getType().apply(question.getTypeVisitor(), question);

		
		return func;
	}
	
	@Override
	public SDeclCG caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node, IRInfo question)
			throws AnalysisException
	{
		AMethodDeclCG method = question.getDeclAssistant().initMethod(node, question);
		
		if(method == null)
		{
			question.addUnsupportedNode(node, "Expected method type for explicit operation. Got: "
					+ node.getType());
			return null;
		}
		
		List<PType> ptypes = ((AOperationType) node.getType()).getParameters();
		LinkedList<PPattern> paramPatterns = node.getParameterPatterns();
		
		LinkedList<AFormalParamLocalParamCG> formalParameters = method.getFormalParams();

		for (int i = 0; i < ptypes.size(); i++)
		{
			STypeCG paramType = ptypes.get(i).apply(question.getTypeVisitor(), question);
			SPatternCG patternCg = paramPatterns.get(i).apply(question.getPatternVisitor(), question);

			AFormalParamLocalParamCG param = new AFormalParamLocalParamCG();
			param.setType(paramType);
			param.setPattern(patternCg);

			formalParameters.add(param);
		}

		return method;
	}
	
	@Override
	public SDeclCG caseAImplicitOperationDefinition(
			AImplicitOperationDefinition node, IRInfo question)
			throws AnalysisException
	{
		AMethodDeclCG method = question.getDeclAssistant().initMethod(node, question);
		
		if(method == null)
		{
			question.addUnsupportedNode(node, "Expected method type for explicit operation. Got: "
					+ node.getType());
			return null;
		}

		// The curent IR construction does not include:
		//
		// Name of result and its type:
		// APatternTypePair res = node.getResult();
		// Ext clauses (read and write):
		// LinkedList<AExternalClause> externals = node.getExternals();
		// Exceptions thrown:
		// LinkedList<AErrorCase> errors = node.getErrors();
		
		method.setBody(new ANotImplementedStmCG());
		method.setFormalParams(question.getDeclAssistant().
				consFormalParams(node.getParameterPatterns(), question));
		return method;
	}

	@Override
	public SDeclCG caseAInstanceVariableDefinition(
			AInstanceVariableDefinition node, IRInfo question)
			throws AnalysisException
	{
		String access = node.getAccess().getAccess().toString();
		String name = node.getName().getName();
		boolean isStatic = node.getAccess().getStatic() != null;
		boolean isFinal = false;
		STypeCG type = node.getType().apply(question.getTypeVisitor(), question);
		SExpCG exp = node.getExpression().apply(question.getExpVisitor(), question);

		return question.getDeclAssistant().constructField(access, name, isStatic, isFinal, type, exp);
	}

	@Override
	public SDeclCG caseAValueDefinition(AValueDefinition node, IRInfo question)
			throws AnalysisException
	{
		String access = node.getAccess().getAccess().toString();
		String name = node.getPattern().toString();
		boolean isStatic = true;
		boolean isFinal = true;
		PType type = node.getType();
		PExp exp = node.getExpression();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		SExpCG expCg = exp.apply(question.getExpVisitor(), question);

		return question.getDeclAssistant().constructField(access, name, isStatic, isFinal, typeCg, expCg);
	}
	
	@Override
	public SDeclCG caseAThreadDefinition(AThreadDefinition node, IRInfo question)
			throws AnalysisException
	{

		PStm stm = node.getOperationDef().getBody();
		
		SStmCG stmCG = stm.apply(question.getStmVisitor(), question);
		
		AThreadDeclCG threaddcl = new AThreadDeclCG();
	
		threaddcl.setStm(stmCG);

		
		return threaddcl;
	}
	
	
	@Override
	public SDeclCG caseAPerSyncDefinition(APerSyncDefinition node,
			IRInfo question) throws AnalysisException
	{
		PExp guard = node.getGuard();
		ILexNameToken opname = node.getOpname();

		APersyncDeclCG predicate = new APersyncDeclCG();
		
		predicate.setPred(guard.apply(question.getExpVisitor(), question));
		predicate.setOpname(opname.getName());
		
		
		return predicate;
	}
	
	@Override
	public SDeclCG caseAMutexSyncDefinition(AMutexSyncDefinition node,
			IRInfo question) throws AnalysisException
	{
		LinkedList<ILexNameToken> operations = node.getOperations();
		
		AMutexSyncDeclCG mutexdef = new AMutexSyncDeclCG();
		
		for(ILexNameToken opname : operations)
		{
			ATokenNameCG token = new ATokenNameCG();
			token.setName(opname.getName());
			mutexdef.getOpnames().add(token);
		}
		
		return mutexdef;
	}
}

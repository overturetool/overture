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
import org.overture.codegen.ir.SDeclIR;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STermIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AFuncDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.AMutexSyncDeclIR;
import org.overture.codegen.ir.declarations.ANamedTraceDeclIR;
import org.overture.codegen.ir.declarations.ANamedTypeDeclIR;
import org.overture.codegen.ir.declarations.APersyncDeclIR;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.declarations.AStateDeclIR;
import org.overture.codegen.ir.declarations.AThreadDeclIR;
import org.overture.codegen.ir.declarations.ATypeDeclIR;
import org.overture.codegen.ir.expressions.ALambdaExpIR;
import org.overture.codegen.ir.expressions.ANotImplementedExpIR;
import org.overture.codegen.ir.name.ATokenNameIR;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.statements.ANotImplementedStmIR;
import org.overture.codegen.ir.traces.ATraceDeclTermIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.ATemplateTypeIR;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;

public class DeclVisitorIR extends AbstractVisitorIR<IRInfo, SDeclIR>
{
	@Override
	public SDeclIR caseAStateDefinition(AStateDefinition node, IRInfo question)
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
		SDeclIR initDeclCg = initdef != null ? initdef.apply(question.getDeclVisitor(), question)
				: null;
		SExpIR initExpCg = initExp != null ? initExp.apply(question.getExpVisitor(), question)
				: null;
		SPatternIR initPatternCg = initPattern != null ? initPattern.apply(question.getPatternVisitor(), question)
				: null;
		SDeclIR invDeclCg = invdef != null ? invdef.apply(question.getDeclVisitor(), question)
				: null;
		SExpIR invExpCg = invExp != null ? invExp.apply(question.getExpVisitor(), question)
				: null;
		SPatternIR invPatternCg = invPattern != null ? invPattern.apply(question.getPatternVisitor(), question)
				: null;

		AStateDeclIR stateDeclCg = new AStateDeclIR();
		stateDeclCg.setAccess(accessCg);
		stateDeclCg.setName(nameCg);

		if (initDeclCg instanceof AFuncDeclIR)
		{
			stateDeclCg.setInitDecl((AFuncDeclIR) initDeclCg);
		}
		stateDeclCg.setInitExp(initExpCg);
		stateDeclCg.setInitPattern(initPatternCg);

		if (invDeclCg instanceof AFuncDeclIR)
		{
			stateDeclCg.setInvDecl((AFuncDeclIR) invDeclCg);
		}
		stateDeclCg.setInvExp(invExpCg);
		stateDeclCg.setInvPattern(invPatternCg);
		stateDeclCg.setExecutable(node.getCanBeExecuted());

		for (AFieldField field : node.getFields())
		{
			SDeclIR fieldCg = field.apply(question.getDeclVisitor(), question);

			if (fieldCg instanceof AFieldDeclIR)
			{
				stateDeclCg.getFields().add((AFieldDeclIR) fieldCg);
			} else
			{
				return null;
			}
		}

		return stateDeclCg;
	}
	
	@Override
	public SDeclIR caseAClassInvariantDefinition(
			AClassInvariantDefinition node, IRInfo question)
			throws AnalysisException
	{
		// Do not report the node as unsupported and generate nothing
		return null;
	}

	@Override
	public SDeclIR caseATraceDefinitionTerm(ATraceDefinitionTerm node,
			IRInfo question) throws AnalysisException
	{
		// Do not report the node as unsupported and generate nothing
		return null;
	}

	@Override
	public SDeclIR caseANamedTraceDefinition(ANamedTraceDefinition node,
			IRInfo question) throws AnalysisException
	{
		if(!question.getSettings().generateTraces())
		{
			return null;
		}
		
		ANamedTraceDeclIR namedTraceDecl = new ANamedTraceDeclIR();

		for(ClonableString cloStr : node.getPathname())
		{
			ATokenNameIR name = new ATokenNameIR();
			name.setName(cloStr.value);
			
			namedTraceDecl.getPathname().add(name);
		}
		
		for(ATraceDefinitionTerm term : node.getTerms())
		{
			STermIR termCg = term.apply(question.getTermVisitor(), question);
			
			if(termCg instanceof ATraceDeclTermIR)
			{
				namedTraceDecl.getTerms().add((ATraceDeclTermIR) termCg);
			}
			else
			{
				Logger.getLog().printErrorln("Expected term to be of type ATraceDeclTermIR. Got: " + termCg);
				return null;
			}
		}
		
		return namedTraceDecl;
	}

	@Override
	public SDeclIR caseANamedInvariantType(ANamedInvariantType node,
			IRInfo question) throws AnalysisException
	{
		PType type = node.getType();
		
		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
		
		ATypeNameIR typeName = new ATypeNameIR();
		typeName.setDefiningClass(node.getName().getModule());
		typeName.setName(node.getName().getName());
		
		ANamedTypeDeclIR namedTypeDecl = new ANamedTypeDeclIR();
		namedTypeDecl.setName(typeName);
		namedTypeDecl.setType(typeCg);
		
		return namedTypeDecl;
	}

	@Override
	public SDeclIR caseARecordInvariantType(ARecordInvariantType node,
			IRInfo question) throws AnalysisException
	{
		ILexNameToken name = node.getName();
		LinkedList<AFieldField> fields = node.getFields();

		ARecordDeclIR record = new ARecordDeclIR();
		record.setName(name.getName());
		
		if(node.getInvDef() != null)
		{
			SDeclIR invCg = node.getInvDef().apply(question.getDeclVisitor(), question);
			record.setInvariant(invCg);
		}

		LinkedList<AFieldDeclIR> recordFields = record.getFields();
		for (AFieldField aFieldField : fields)
		{
			SDeclIR res = aFieldField.apply(question.getDeclVisitor(), question);

			if (res instanceof AFieldDeclIR)
			{
				AFieldDeclIR fieldDecl = (AFieldDeclIR) res;
				recordFields.add(fieldDecl);
			} else
			{
				return null;
			}
		}

		return record;
	}

	@Override
	public SDeclIR caseAFieldField(AFieldField node, IRInfo question)
			throws AnalysisException
	{
		// Record fields are public
		String access = IRConstants.PUBLIC;
		String name = node.getTagname().getName();
		boolean isStatic = false;
		boolean isFinal = false;
		STypeIR type = node.getType().apply(question.getTypeVisitor(), question);
		SExpIR exp = null;

		return question.getDeclAssistant().constructField(access, name, isStatic, isFinal, type, exp);
	}

	@Override
	public SDeclIR caseATypeDefinition(ATypeDefinition node, IRInfo question)
			throws AnalysisException
	{
		String access = node.getAccess().getAccess().toString();
		PType type = node.getType();
		
		SDeclIR declCg = type.apply(question.getDeclVisitor(), question);

		SDeclIR invCg = node.getInvdef() != null ?
				node.getInvdef().apply(question.getDeclVisitor(), question)
				: null;
		
		ATypeDeclIR typeDecl = new ATypeDeclIR();
		typeDecl.setAccess(access);
		typeDecl.setDecl(declCg);
		typeDecl.setInv(invCg);
		
		return typeDecl;
	}

	@Override
	public SDeclIR caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition node, IRInfo question)
			throws AnalysisException
	{
		String accessCg = node.getAccess().getAccess().toString();
		String funcNameCg = node.getName().getName();

		STypeIR typeCg = node.getType().apply(question.getTypeVisitor(), question);

		if (!(typeCg instanceof AMethodTypeIR))
		{
			question.addUnsupportedNode(node, "Expected method type for explicit function. Got: "
					+ typeCg);
			return null;
		}

		AMethodTypeIR methodTypeCg = (AMethodTypeIR) typeCg;

		AFuncDeclIR method = new AFuncDeclIR();

		method.setAccess(accessCg);
		method.setMethodType(methodTypeCg);
		method.setName(funcNameCg);

		Iterator<List<PPattern>> iterator = node.getParamPatternList().iterator();
		List<PPattern> paramPatterns = iterator.next();

		LinkedList<AFormalParamLocalParamIR> formalParameters = method.getFormalParams();

		for (int i = 0; i < paramPatterns.size(); i++)
		{
			SPatternIR pattern = paramPatterns.get(i).apply(question.getPatternVisitor(), question);

			AFormalParamLocalParamIR param = new AFormalParamLocalParamIR();
			param.setType(methodTypeCg.getParams().get(i).clone());
			param.setPattern(pattern);

			formalParameters.add(param);
		}

		if (node.getIsUndefined())
		{
			method.setBody(new ANotImplementedExpIR());
		} else if (node.getIsCurried())
		{
			AMethodTypeIR nextLevel = (AMethodTypeIR) methodTypeCg;

			ALambdaExpIR currentLambda = new ALambdaExpIR();
			ALambdaExpIR topLambda = currentLambda;

			while (iterator.hasNext())
			{
				nextLevel = (AMethodTypeIR) nextLevel.getResult();
				paramPatterns = iterator.next();

				for (int i = 0; i < paramPatterns.size(); i++)
				{
					PPattern param = paramPatterns.get(i);

					SPatternIR patternCg = param.apply(question.getPatternVisitor(), question);

					AFormalParamLocalParamIR paramCg = new AFormalParamLocalParamIR();
					paramCg.setPattern(patternCg);
					paramCg.setType(nextLevel.getParams().get(i).clone());

					currentLambda.getParams().add(paramCg);
				}

				currentLambda.setType(nextLevel.clone());

				if (iterator.hasNext())
				{
					ALambdaExpIR nextLambda = new ALambdaExpIR();
					currentLambda.setExp(nextLambda);
					currentLambda = nextLambda;
				}

			}

			SExpIR bodyExp = node.getBody().apply(question.getExpVisitor(), question);
			currentLambda.setExp(bodyExp);
			method.setBody(topLambda);
		} else
		{
			SExpIR bodyCg = node.getBody().apply(question.getExpVisitor(), question);
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
			ATemplateTypeIR templateType = new ATemplateTypeIR();
			templateType.setName(typeParam.getName());
			method.getTemplateTypes().add(templateType);
		}
		
		AExplicitFunctionDefinition preCond = node.getPredef();
		SDeclIR preCondCg = preCond != null ? preCond.apply(question.getDeclVisitor(), question) : null;
		method.setPreCond(preCondCg);
		
		AExplicitFunctionDefinition postCond = node.getPostdef();
		SDeclIR postCondCg = postCond != null ? postCond.apply(question.getDeclVisitor(), question) : null;
		method.setPostCond(postCondCg);

		method.setImplicit(false);
		
		return method;
	}
	
	@Override
	public SDeclIR caseAImplicitFunctionDefinition(
			AImplicitFunctionDefinition node, IRInfo question)
			throws AnalysisException
	{
		String accessCg = node.getAccess().getAccess().toString();
		String funcNameCg = node.getName().getName();
		
		STypeIR typeCg = node.getType().apply(question.getTypeVisitor(), question);
		
		if (!(typeCg instanceof AMethodTypeIR))
		{
			question.addUnsupportedNode(node, "Expected method type for implicit function. Got: "
					+ typeCg);
			return null;
		}

		AFuncDeclIR func = new AFuncDeclIR();
		AExplicitFunctionDefinition preCond = node.getPredef();
		SDeclIR preCondCg = preCond != null ? preCond.apply(question.getDeclVisitor(), question) : null;
		func.setPreCond(preCondCg);
		
		AExplicitFunctionDefinition postCond = node.getPostdef();
		SDeclIR postCondCg = postCond != null ? postCond.apply(question.getDeclVisitor(), question) : null;
		func.setPostCond(postCondCg);


		// If the function uses any type parameters they will be
		// registered as part of the method declaration
		List<ILexNameToken> typeParams = node.getTypeParams();
		for (int i = 0; i < typeParams.size(); i++)
		{
			ILexNameToken typeParam = typeParams.get(i);
			ATemplateTypeIR templateType = new ATemplateTypeIR();
			templateType.setName(typeParam.getName());
			func.getTemplateTypes().add(templateType);
		}

		func.setAbstract(false);
		func.setAccess(accessCg);
		func.setImplicit(true);
		func.setBody(new ANotImplementedExpIR());
		func.setFormalParams(question.getDeclAssistant().
				consFormalParams(node.getParamPatterns(), question));
		func.setMethodType((AMethodTypeIR) typeCg);
		func.setName(funcNameCg);
		
		// The implicit function is currently constructed without the result information:
		//SPatternIR resPatternCg = node.getResult().getPattern().apply(question.getPatternVisitor(), question);
		//STypeIR resTypeCg = node.getResult().getType().apply(question.getTypeVisitor(), question);

		
		return func;
	}
	
	@Override
	public SDeclIR caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node, IRInfo question)
			throws AnalysisException
	{
		AMethodDeclIR method = question.getDeclAssistant().initMethod(node, question);
		
		if(method == null)
		{
			question.addUnsupportedNode(node, "Expected method type for explicit operation. Got: "
					+ node.getType());
			return null;
		}
		
		if(!(node.getType() instanceof AOperationType))
		{
			question.addUnsupportedNode(node, "Node type should be an operation type. Got: " + node.getType());
			return null;
		}
		
		List<PType> ptypes = ((AOperationType) node.getType()).getParameters();
		LinkedList<PPattern> paramPatterns = node.getParameterPatterns();
		
		LinkedList<AFormalParamLocalParamIR> formalParameters = method.getFormalParams();

		for (int i = 0; i < ptypes.size(); i++)
		{
			STypeIR paramType = ptypes.get(i).apply(question.getTypeVisitor(), question);
			SPatternIR patternCg = paramPatterns.get(i).apply(question.getPatternVisitor(), question);

			AFormalParamLocalParamIR param = new AFormalParamLocalParamIR();
			param.setType(paramType);
			param.setPattern(patternCg);

			formalParameters.add(param);
		}

		return method;
	}
	
	@Override
	public SDeclIR caseAImplicitOperationDefinition(
			AImplicitOperationDefinition node, IRInfo question)
			throws AnalysisException
	{
		AMethodDeclIR method = question.getDeclAssistant().initMethod(node, question);
		
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
		
		method.setBody(new ANotImplementedStmIR());
		method.setFormalParams(question.getDeclAssistant().
				consFormalParams(node.getParameterPatterns(), question));
		return method;
	}

	@Override
	public SDeclIR caseAInstanceVariableDefinition(
			AInstanceVariableDefinition node, IRInfo question)
			throws AnalysisException
	{
		String access = node.getAccess().getAccess().toString();
		String name = node.getName().getName();
		boolean isStatic = node.getAccess().getStatic() != null;
		boolean isFinal = false;
		STypeIR type = node.getType().apply(question.getTypeVisitor(), question);
		SExpIR exp = node.getExpression().apply(question.getExpVisitor(), question);

		return question.getDeclAssistant().constructField(access, name, isStatic, isFinal, type, exp);
	}

	@Override
	public SDeclIR caseAValueDefinition(AValueDefinition node, IRInfo question)
			throws AnalysisException
	{
		String access = node.getAccess().getAccess().toString();
		String name = node.getPattern().toString();
		boolean isStatic = true;
		boolean isFinal = true;
		PType type = node.getType();
		PExp exp = node.getExpression();

		STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
		SExpIR expCg = exp.apply(question.getExpVisitor(), question);

		return question.getDeclAssistant().constructField(access, name, isStatic, isFinal, typeCg, expCg);
	}
	
	@Override
	public SDeclIR caseAThreadDefinition(AThreadDefinition node, IRInfo question)
			throws AnalysisException
	{

		PStm stm = node.getOperationDef().getBody();
		
		SStmIR stmIR = stm.apply(question.getStmVisitor(), question);
		
		AThreadDeclIR threaddcl = new AThreadDeclIR();
	
		threaddcl.setStm(stmIR);

		
		return threaddcl;
	}
	
	
	@Override
	public SDeclIR caseAPerSyncDefinition(APerSyncDefinition node,
			IRInfo question) throws AnalysisException
	{
		PExp guard = node.getGuard();
		ILexNameToken opname = node.getOpname();

		APersyncDeclIR predicate = new APersyncDeclIR();
		
		predicate.setPred(guard.apply(question.getExpVisitor(), question));
		predicate.setOpname(opname.getName());
		
		
		return predicate;
	}
	
	@Override
	public SDeclIR caseAMutexSyncDefinition(AMutexSyncDefinition node,
			IRInfo question) throws AnalysisException
	{
		LinkedList<ILexNameToken> operations = node.getOperations();
		
		AMutexSyncDeclIR mutexdef = new AMutexSyncDeclIR();
		
		for(ILexNameToken opname : operations)
		{
			ATokenNameIR token = new ATokenNameIR();
			token.setName(opname.getName());
			mutexdef.getOpnames().add(token);
		}
		
		return mutexdef;
	}
}

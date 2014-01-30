package org.overture.codegen.visitor;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.codegen.assistant.DeclAssistantCG;
import org.overture.codegen.assistant.TypeAssistantCG;
import org.overture.codegen.cgast.declarations.AEmptyDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.PDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.statements.ANotImplementedStmCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.ATemplateTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.constants.IOoAstConstants;
import org.overture.codegen.ooast.OoAstInfo;
import org.overture.codegen.utils.AnalysisExceptionCG;

public class DeclVisitorCG extends AbstractVisitorCG<OoAstInfo, PDeclCG>
{
	private DeclAssistantCG declAssistant;
	
	public DeclVisitorCG()
	{
		this.declAssistant = new DeclAssistantCG();
	}
		
	@Override
	public PDeclCG caseANamedInvariantType(ANamedInvariantType node,
			OoAstInfo question) throws AnalysisException
	{
		PType type = node.getType();
		
		if(type instanceof AUnionType)
		{
			AUnionType unionType = (AUnionType) type;
			
			if(TypeAssistantCG.isUnionOfQuotes(unionType))
				//The VDM translation ignores named invariant types that are not
				//union of quotes as they are represented as integers instead
				return new AEmptyDeclCG();
		}

		return null; //Currently the code generator only supports the union of quotes case
	}
	
	@Override
	public PDeclCG caseARecordInvariantType(ARecordInvariantType node,
			OoAstInfo question) throws AnalysisException
	{
		ILexNameToken name = node.getName();
		LinkedList<AFieldField> fields = node.getFields();
		
		ARecordDeclCG record = new ARecordDeclCG();
		//Set this public for now but it must be corrected as the access is specified
		//in the type definition instead:
		//		types
		//
		//		public R ::
		//		    x : nat
		//		    y : nat;
		record.setAccess(IOoAstConstants.PUBLIC);
		record.setName(name.getName());
		
		LinkedList<AFieldDeclCG> recordFields = record.getFields();
		for (AFieldField aFieldField : fields)
		{		
			PDeclCG res = aFieldField.apply(question.getDeclVisitor(), question);
			
			if(res instanceof AFieldDeclCG)
			{
				AFieldDeclCG fieldDecl = (AFieldDeclCG) res;
				recordFields.add(fieldDecl);
			}
			else
				throw new AnalysisExceptionCG("Could not generate fields of record: " + name, node.getLocation());
		}
		
		return record;
	}
	
	@Override
	public PDeclCG caseAFieldField(AFieldField node, OoAstInfo question)
			throws AnalysisException
	{
		//Record fields are public
		String access = IOoAstConstants.PUBLIC;
		String name = node.getTag();
		boolean isStatic = false;
		boolean isFinal = false;
		PTypeCG type = node.getType().apply(question.getTypeVisitor(), question);
		PExpCG exp = null;
		
		return declAssistant.constructField(access, name, isStatic, isFinal, type, exp);
	}
	
	@Override
	public PDeclCG caseATypeDefinition(ATypeDefinition node,
			OoAstInfo question) throws AnalysisException
	{
		String access = node.getAccess().getAccess().toString();
		
		PDeclCG dec = node.getType().apply(question.getDeclVisitor(), question);
		
		if(dec instanceof ARecordDeclCG)
		{
			ARecordDeclCG record = (ARecordDeclCG) dec;
			record.setAccess(access);
		}
		
		return dec;
	}
		
	@Override
	public PDeclCG caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition node, OoAstInfo question)
			throws AnalysisException
	{
		if(node.getIsCurried()|| node.getIsTypeInvariant())
			return null;
		
		String access = node.getAccess().getAccess().toString();
		boolean isStatic = true;
		String operationName = node.getName().getName();

		PTypeCG returnType = ((AFunctionType) node.getType()).getResult().apply(question.getTypeVisitor(), question);
		
		PStmCG body = null;
		if(node.getIsUndefined())
		   body = new ANotImplementedStmCG();
		else
			body = node.getBody().apply(question.getStatementVisitor(), question);
		
		boolean isAbstract = body == null;
		
		AMethodDeclCG method = new AMethodDeclCG();
		
		method.setAccess(access);
		method.setStatic(isStatic);
		method.setReturnType(returnType);
		method.setName(operationName);		
		method.setBody(body);
		method.setAbstract(isAbstract);
		
		List<PType> ptypes = ((AFunctionType) node.getType()).getParameters();
		List<PPattern> paramPatterns = node.getParamPatternList().get(0);
		
		LinkedList<AFormalParamLocalDeclCG> formalParameters = method.getFormalParams();
		
		for(int i = 0; i < ptypes.size(); i++)
		{
			PTypeCG type = ptypes.get(i).apply(question.getTypeVisitor(), question);
			String name = paramPatterns.get(i).toString();
			
			AFormalParamLocalDeclCG param = new AFormalParamLocalDeclCG();
			param.setType(type);
			param.setName(name);
			
			formalParameters.add(param);
		}
		
		//If the function uses any type parameters they will be
		//registered as part of the method declaration
		LinkedList<ILexNameToken> typeParams = node.getTypeParams();
		for(int i = 0; i < typeParams.size(); i++)
		{
			ILexNameToken typeParam = typeParams.get(i);
			ATemplateTypeCG templateType = new ATemplateTypeCG();
			templateType.setName(typeParam.getName());
			method.getTemplateTypes().add(templateType);
		}
		
		return method;
	}
	
	@Override
	public PDeclCG caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node, OoAstInfo question)
			throws AnalysisException
	{
		
		String access = node.getAccess().getAccess().toString();
		boolean isStatic = question.getTcFactory().createPDefinitionAssistant().isStatic(node);
		String operationName = node.getName().getName();
		PTypeCG returnType = node.getType().apply(question.getTypeVisitor(), question);		
		PStmCG body = node.getBody().apply(question.getStatementVisitor(), question);
		boolean isConstructor = node.getIsConstructor();
		boolean isAbstract = body == null;
		
		AMethodDeclCG method = new AMethodDeclCG();
		
		method.setAccess(access);
		method.setStatic(isStatic);
		method.setReturnType(returnType);
		method.setName(operationName);
		method.setBody(body);
		method.setIsConstructor(isConstructor);
		method.setAbstract(isAbstract);
		
		List<PType> ptypes = ((AOperationType) node.getType()).getParameters();
		LinkedList<PPattern> paramPatterns = node.getParameterPatterns();
		
		LinkedList<AFormalParamLocalDeclCG> formalParameters = method.getFormalParams();
		
		for(int i = 0; i < ptypes.size(); i++)
		{
			PTypeCG type = ptypes.get(i).apply(question.getTypeVisitor(), question);
			String name = paramPatterns.get(i).toString();
			
			AFormalParamLocalDeclCG param = new AFormalParamLocalDeclCG();
			param.setType(type);
			param.setName(name);
			
			formalParameters.add(param);
		}
		
		return method;
	}
	
	@Override
	public PDeclCG caseAInstanceVariableDefinition(
			AInstanceVariableDefinition node, OoAstInfo question)
			throws AnalysisException
	{
		String access = node.getAccess().getAccess().toString();
		String name = node.getName().getName();
		boolean isStatic = node.getAccess().getStatic() != null;
		boolean isFinal = false;
		PTypeCG type = node.getType().apply(question.getTypeVisitor(), question);
		PExpCG exp = node.getExpression().apply(question.getExpVisitor(), question);
		
		
		return declAssistant.constructField(access, name, isStatic, isFinal, type, exp);
	}
	
	@Override
	public PDeclCG caseAValueDefinition(AValueDefinition node, OoAstInfo question) throws AnalysisException
	{
		String access = node.getAccess().getAccess().toString();
		String name = node.getPattern().toString();
		boolean isStatic = true;
		boolean isFinal = true;
		PTypeCG type = node.getType().apply(question.getTypeVisitor(), question);
		PExpCG exp = node.getExpression().apply(question.getExpVisitor(), question);
		
		return declAssistant.constructField(access, name, isStatic, isFinal, type, exp);
	}
}

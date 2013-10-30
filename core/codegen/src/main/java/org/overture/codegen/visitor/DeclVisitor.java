package org.overture.codegen.visitor;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.codegen.assistant.DeclAssistant;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AEmptyDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.PDeclCG;
import org.overture.codegen.cgast.expressions.AVariableExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG;
import org.overture.codegen.cgast.statements.ANotImplementedStmCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.constants.OoAstInfo;
import org.overture.codegen.utils.VdmTransUtil;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;

public class DeclVisitor extends AbstractVisitorCG<CodeGenInfo, PDeclCG>
{
	private static final long serialVersionUID = -7968170190668212627L;
	
	private DeclAssistant declAssistant;
	
	public DeclVisitor()
	{
		this.declAssistant = new DeclAssistant();
	}
		
	@Override
	public PDeclCG caseANamedInvariantType(ANamedInvariantType node,
			CodeGenInfo question) throws AnalysisException
	{
		PType type = node.getType();
		
		if(type instanceof AUnionType)
		{
			AUnionType unionType = (AUnionType) type;
			
			if(VdmTransUtil.isUnionOfQuotes(unionType))
				//The VDM translation ignores named invariant types that are not
				//union of quotes as they are represented as integers instead
				return new AEmptyDeclCG();
		}

		return null; //Currently the code generator only supports the union of quotes case
	}
	
	@Override
	public PDeclCG caseARecordInvariantType(ARecordInvariantType node,
			CodeGenInfo question) throws AnalysisException
	{
		String name = node.getName().getName();
		LinkedList<AFieldField> fields = node.getFields();
		
		AClassDeclCG staticClass = new AClassDeclCG();
		staticClass.setAbstract(false);
		staticClass.setStatic(true);
		//Set this public for now but it must be corrected as the access is specified
		//in the type definition instead:
		//		types
		//
		//		public R ::
		//		    x : nat
		//		    y : nat;
		staticClass.setAccess(OoAstInfo.PUBLIC);
		
		staticClass.setInnerClasses(null);
		staticClass.setMethods(null);
		staticClass.setName(name);
		staticClass.setSuperName(null);
		
		AMethodDeclCG constructor = new AMethodDeclCG();
		constructor.setAbstract(false);
		constructor.setAccess(OoAstInfo.PUBLIC);
		
		ABlockStmCG body = new ABlockStmCG();
		LinkedList<PStmCG> bodyStms = body.getStatements();
		constructor.setBody(body); //TODO
		
		List<AFormalParamLocalDeclCG> formalParams = new LinkedList<AFormalParamLocalDeclCG>();
		constructor.setFormalParams(formalParams);//TODO
		
		constructor.setName(name);
		constructor.setReturnType(null);
		constructor.setStatic(false);
		
		List<AMethodDeclCG> recordConstructors = new LinkedList<AMethodDeclCG>();
		recordConstructors.add(constructor);
		staticClass.setMethods(recordConstructors);
		
		LinkedList<AFieldDeclCG> staticClassFields = staticClass.getFields();
		for (AFieldField aFieldField : fields)
		{		
			PDeclCG res = aFieldField.apply(question.getDeclVisitor(), question);
			
			if(res instanceof AFieldDeclCG)
			{
				AFieldDeclCG fieldDecl = (AFieldDeclCG) res;
				staticClassFields.add(fieldDecl);
				
				String formalName = OoAstInfo.CONSTRUCTOR_FORMAL_PREFIX + fieldDecl.getName();
				PTypeCG formalType = fieldDecl.getType();
				
				AFormalParamLocalDeclCG formal = new AFormalParamLocalDeclCG();
				formal.setName(formalName);
				formal.setType(formalType.clone());
				formalParams.add(formal);
				
				AAssignmentStmCG assignment = new AAssignmentStmCG();
				AIdentifierStateDesignatorCG field = new AIdentifierStateDesignatorCG();
				field.setName(fieldDecl.getName());
				assignment.setTarget(field);
				AVariableExpCG varExp = new AVariableExpCG();
				varExp.setOriginal(formalName);
				varExp.setType(formalType.clone());
				assignment.setExp(varExp);
				bodyStms.add(assignment);
			}
			else
				throw new AnalysisException("Could not generate fields of record: " + name);
		}
		
		constructor.setFormalParams(formalParams);
		
		return staticClass;
	}
	
	@Override
	public PDeclCG caseAFieldField(AFieldField node, CodeGenInfo question)
			throws AnalysisException
	{
		//Record fields are public
		String access = OoAstInfo.PUBLIC;
		String name = node.getTag();
		boolean isStatic = false;
		boolean isFinal = false;
		PTypeCG type = node.getType().apply(question.getTypeVisitor(), question);
		PExpCG exp = null;
		
		return declAssistant.constructField(access, name, isStatic, isFinal, type, exp);
	}
	
	@Override
	public PDeclCG caseATypeDefinition(ATypeDefinition node,
			CodeGenInfo question) throws AnalysisException
	{
//		System.out.println("*******");		
//		System.out.println("Access: " + node.getAccess());
//		System.out.println("Got a type definition: " + node.toString());
//		System.out.println("Invariant type: " + node.getInvType().toString());
//		System.out.println("Get type: " + node.getType());
//		System.out.println("Name: " + node.getName().getName());
//		System.out.println("Pattern: " + node.getInvPattern());
//		
//		System.out.println("Trying to apply type:");
//		System.out.println("The typ: " + typ.getClass().getName());	
//		System.out.println("*****");
		
		String access = node.getAccess().getAccess().toString();
		
		PDeclCG dec = node.getType().apply(question.getDeclVisitor(), question);
		
		if(dec instanceof AClassDeclCG)
		{
			AClassDeclCG staticClass = (AClassDeclCG) dec;
			staticClass.setAccess(access);
		}
		
		return dec;
	}
		
	@Override
	public PDeclCG caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition node, CodeGenInfo question)
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
		
		for(int i = 0; i < paramPatterns.size(); i++)
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
	public PDeclCG caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node, CodeGenInfo question)
			throws AnalysisException
	{	
		
		String access = node.getAccess().getAccess().toString();
		boolean isStatic = PDefinitionAssistantTC.isStatic(node);
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
		
		for(int i = 0; i < paramPatterns.size(); i++)
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
			AInstanceVariableDefinition node, CodeGenInfo question)
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
	public PDeclCG caseAValueDefinition(AValueDefinition node, CodeGenInfo question) throws AnalysisException
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

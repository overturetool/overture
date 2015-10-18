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
package org.overture.codegen.trans.uniontypes;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.codegen.assistant.TypeAssistantCG;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.declarations.SClassDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ACardUnaryExpCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.AElemsUnaryExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AFieldNumberExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.AInstanceofExpCG;
import org.overture.codegen.cgast.expressions.AIntDivNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ALenUnaryExpCG;
import org.overture.codegen.cgast.expressions.AMapDomainUnaryExpCG;
import org.overture.codegen.cgast.expressions.AMissingMemberRuntimeErrorExpCG;
import org.overture.codegen.cgast.expressions.AModNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.expressions.ANotUnaryExpCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.expressions.ARemNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ASeqConcatBinaryExpCG;
import org.overture.codegen.cgast.expressions.AUndefinedExpCG;
import org.overture.codegen.cgast.expressions.SNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.SUnaryExpCG;
import org.overture.codegen.cgast.expressions.SVarExpBase;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.AElseIfStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.APlainCallStmCG;
import org.overture.codegen.cgast.statements.ARaiseErrorStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.statements.ASuperCallStmCG;
import org.overture.codegen.cgast.statements.SCallStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AErrorTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.ATupleTypeCG;
import org.overture.codegen.cgast.types.AUnionTypeCG;
import org.overture.codegen.cgast.types.AUnknownTypeCG;
import org.overture.codegen.cgast.types.SMapTypeCG;
import org.overture.codegen.cgast.types.SSeqTypeCG;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class UnionTypeTrans extends DepthFirstAnalysisAdaptor
{
	public static final String MISSING_OP_MEMBER = "Missing operation member: ";
	public static final String MISSING_MEMBER = "Missing member: ";
	
	private TransAssistantCG transAssistant;

	private UnionTypeVarPrefixes unionTypePrefixes;
	
	private List<INode> cloneFreeNodes;
	
	public UnionTypeTrans(TransAssistantCG transAssistant, UnionTypeVarPrefixes unionTypePrefixes, List<INode> cloneFreeNodes)
	{
		this.transAssistant = transAssistant;
		this.unionTypePrefixes = unionTypePrefixes;
		this.cloneFreeNodes = cloneFreeNodes;
	}

	private interface TypeFinder<T extends STypeCG>
	{
		public T findType(PType type)
				throws org.overture.ast.analysis.AnalysisException;
	}

	public <T extends STypeCG> T searchType(SExpCG exp, TypeFinder<T> typeFinder)
	{
		if (exp == null || exp.getType() == null)
		{
			return null;
		}

		SourceNode sourceNode = exp.getType().getSourceNode();

		if (sourceNode == null)
		{
			return null;
		}

		org.overture.ast.node.INode vdmTypeNode = sourceNode.getVdmNode();

		if (vdmTypeNode instanceof PType)
		{
			try
			{
				PType vdmType = (PType) vdmTypeNode;

				return typeFinder.findType(vdmType);

			} catch (org.overture.ast.analysis.AnalysisException e)
			{
			}
		}

		return null;
	}

	private SExpCG correctTypes(SExpCG exp, STypeCG castedType)
			throws AnalysisException
	{
		if ((exp.getType() instanceof AUnknownTypeCG || exp.getType() instanceof AUnionTypeCG)
				&& !(exp instanceof ACastUnaryExpCG))
		{
			ACastUnaryExpCG casted = new ACastUnaryExpCG();
			casted.setType(castedType.clone());
			casted.setExp(exp.clone());

			transAssistant.replaceNodeWith(exp, casted);

			return casted;
		}

		return exp;
	}
	
	private boolean correctArgTypes(List<SExpCG> args, List<STypeCG> paramTypes)
			throws AnalysisException
	{
		if (transAssistant.getInfo().getAssistantManager().getTypeAssistant().checkArgTypes(transAssistant.getInfo(), args, paramTypes))
		{
			for (int k = 0; k < paramTypes.size(); k++)
			{
				SExpCG arg = args.get(k);
				
				if(!(arg instanceof ANullExpCG))
				{
					correctTypes(arg, paramTypes.get(k));
				}
			}
			return true;
		}

		return false;
	}

	private boolean handleUnaryExp(SUnaryExpCG exp) throws AnalysisException
	{
		STypeCG type = exp.getExp().getType();

		if (type instanceof AUnionTypeCG)
		{
			org.overture.ast.node.INode vdmNode = type.getSourceNode().getVdmNode();

			if (vdmNode instanceof PType)
			{
				return true;
			}
		}

		return false;
	}

	private AInstanceofExpCG consInstanceCheck(SExpCG copy, STypeCG type)
	{
		AInstanceofExpCG check = new AInstanceofExpCG();
		check.setType(new ABoolBasicTypeCG());
		check.setCheckedType(type.clone());
		check.setExp(copy.clone());
		
		return check;
	}

	@Override
	public void defaultInSNumericBinaryExpCG(SNumericBinaryExpCG node)
			throws AnalysisException
	{
		STypeCG expectedType;
		
		if (transAssistant.getInfo().getTypeAssistant().isNumericType(node.getType()))
		{
			expectedType = node.getType();
		} else
		{
			expectedType = getExpectedOperandType(node);
		}

		correctTypes(node.getLeft(), expectedType);
		correctTypes(node.getRight(), expectedType);
	}
	
	public STypeCG getExpectedOperandType(SNumericBinaryExpCG node)
	{
		if(node instanceof AIntDivNumericBinaryExpCG || node instanceof AModNumericBinaryExpCG || node instanceof ARemNumericBinaryExpCG)
		{
			return new AIntNumericBasicTypeCG();
		}
		else
		{
			return new ARealNumericBasicTypeCG();
		}
	}
	
	@Override
	public void caseAFieldDeclCG(AFieldDeclCG node) throws AnalysisException
	{
		if (node.getInitial() != null)
		{
			if (node.getInitial().getType() instanceof AUnionTypeCG)
			{
				correctTypes(node.getInitial(), node.getType());
			}
			
			node.getInitial().apply(this);
		}
	}
	
	@Override
	public void caseACardUnaryExpCG(ACardUnaryExpCG node)
			throws AnalysisException
	{
		STypeCG type = node.getExp().getType();
		
		if(type instanceof AUnionTypeCG)
		{
			STypeCG expectedType = transAssistant.getInfo().getTypeAssistant().getSetType((AUnionTypeCG) type);
			correctTypes(node.getExp(), expectedType);
		}
		
		node.getExp().apply(this);
		node.getType().apply(this);
	}
	
	@Override
	public void caseALenUnaryExpCG(ALenUnaryExpCG node)
			throws AnalysisException
	{
		STypeCG type = node.getExp().getType();
		
		if(type instanceof AUnionTypeCG)
		{
			STypeCG expectedType = transAssistant.getInfo().getTypeAssistant().getSeqType((AUnionTypeCG) type);
			correctTypes(node.getExp(), expectedType);
		}
		
		node.getExp().apply(this);
		node.getType().apply(this);
	}
	
	@Override
	public void caseASeqConcatBinaryExpCG(ASeqConcatBinaryExpCG node)
			throws AnalysisException
	{
		node.getLeft().apply(this);
		node.getRight().apply(this);
		node.getType().apply(this);
		
		if(!transAssistant.getInfo().getTypeAssistant().usesUnionType(node))
		{
			return;
		}
		
		STypeCG leftType = node.getLeft().getType();

		if (leftType instanceof AUnionTypeCG)
		{
			STypeCG expectedType = transAssistant.getInfo().getTypeAssistant().getSeqType((AUnionTypeCG) leftType);
			correctTypes(node.getLeft(), expectedType);
		}

		STypeCG rightType = node.getRight().getType();

		if (rightType instanceof AUnionTypeCG)
		{
			STypeCG expectedType = transAssistant.getInfo().getTypeAssistant().getSeqType((AUnionTypeCG) rightType);
			correctTypes(node.getRight(), expectedType);
		}
	}
	
	@Override
	public void caseAFieldNumberExpCG(AFieldNumberExpCG node)
			throws AnalysisException
	{
		SExpCG tuple = node.getTuple();
		STypeCG tupleType = tuple.getType();
		
		if(!(tupleType instanceof AUnionTypeCG))
		{
			tuple.apply(this);
			return;
		}
		
		handleFieldExp(node, "field number " + node.getField(), tuple, tupleType, node.getType().clone());
	}
	
	@Override
	public void caseAFieldExpCG(AFieldExpCG node) throws AnalysisException
	{
		SExpCG object = node.getObject();
		STypeCG objectType = object.getType();
		
		if(!(objectType instanceof AUnionTypeCG))
		{
			object.apply(this);
			return;
		}
		
		STypeCG resultType = getResultType(node, node.parent(), objectType, transAssistant.getInfo().getTypeAssistant());
		
		handleFieldExp(node, node.getMemberName(), object, objectType, resultType);
	}

	private void handleFieldExp(SExpCG node, String memberName, SExpCG subject, STypeCG fieldObjType, STypeCG resultType) throws AnalysisException
	{
		INode parent = node.parent();

		TypeAssistantCG typeAssistant = transAssistant.getInfo().getAssistantManager().getTypeAssistant();

		SStmCG enclosingStatement = transAssistant.getEnclosingStm(node, "field expression");

		String applyResultName = transAssistant.getInfo().getTempVarNameGen().nextVarName(unionTypePrefixes.applyExp());

		AIdentifierPatternCG id = new AIdentifierPatternCG();
		id.setName(applyResultName);

		AVarDeclCG resultDecl = transAssistant.getInfo().getDeclAssistant().
				consLocalVarDecl(node.getSourceNode().getVdmNode(), resultType, id, transAssistant.getInfo().getExpAssistant().consNullExp());
		
		AIdentifierVarExpCG resultVar = new AIdentifierVarExpCG();
		resultVar.setSourceNode(node.getSourceNode());
		resultVar.setIsLambda(false);
		resultVar.setIsLocal(true);
		resultVar.setName(applyResultName);
		resultVar.setType(resultDecl.getType().clone());

		ABlockStmCG replacementBlock = new ABlockStmCG();
		SExpCG obj = null;
		
		if (!(subject instanceof SVarExpBase))
		{
			String objName = transAssistant.getInfo().getTempVarNameGen().nextVarName(unionTypePrefixes.objExp());

			AIdentifierPatternCG objId = new AIdentifierPatternCG();
			objId.setName(objName);

			AVarDeclCG objectDecl = transAssistant.getInfo().getDeclAssistant().
					consLocalVarDecl(subject.getType().clone(), objId, subject.clone());
			
			replacementBlock.getLocalDefs().add(objectDecl);

			AIdentifierVarExpCG objectVar = new AIdentifierVarExpCG();
			objectVar.setIsLambda(false);
			objectVar.setIsLocal(true);
			objectVar.setName(objName);
			objectVar.setType(objectDecl.getType().clone());
			obj = objectVar;
		} else
		{
			obj = subject.clone();
		}

		List<STypeCG> possibleTypes = ((AUnionTypeCG) fieldObjType).getTypes();
		possibleTypes = typeAssistant.clearDuplicates(possibleTypes);

		AIfStmCG ifChecks = new AIfStmCG();

		int handledTypes = 0;
		for (int i = 0; i < possibleTypes.size(); i++)
		{
			SExpCG fieldExp = (SExpCG) node.clone();
			STypeCG currentType = possibleTypes.get(i);
			
			if(currentType instanceof AUnknownTypeCG)
			{
				// If we are accessing an element of (say) the sequence [new A(), new B(), nil] of type A | B | [?]
				// then the current IR type will be the unknown type at some point. This case is simply skipped.
				continue;
			}
			
			if (!(currentType instanceof AClassTypeCG)
					&& !(currentType instanceof ATupleTypeCG)
					&& !(currentType instanceof ARecordTypeCG))
			{
				// If the field cannot possibly exist then continue
				continue;
			}
			
			boolean memberExists = false;

			memberExists = memberExists(memberName, parent, typeAssistant, fieldExp, currentType);

			if (!memberExists)
			{
				// If the member does not exist then the case should not be treated
				continue;
			}
			
			ACastUnaryExpCG castedFieldExp = new ACastUnaryExpCG();
			castedFieldExp.setType(currentType.clone());
			castedFieldExp.setExp(obj.clone());

			setSubject(fieldExp, castedFieldExp);

			AAssignToExpStmCG assignment = new AAssignToExpStmCG();
			cloneFreeNodes.add(assignment);
			assignment.setTarget(resultVar.clone());
			assignment.setExp(getAssignmentExp(node, fieldExp));

			if (handledTypes == 0)
			{
				ifChecks.setIfExp(consInstanceCheck(obj, currentType));
				ifChecks.setThenStm(assignment);
			} else
			{
				AElseIfStmCG elseIf = new AElseIfStmCG();
				elseIf.setElseIf(consInstanceCheck(obj, currentType));
				elseIf.setThenStm(assignment);

				ifChecks.getElseIf().add(elseIf);
			}
			
			handledTypes++;
		}
		
		if(handledTypes == 0)
		{
			return;
		}
		
		ARaiseErrorStmCG raise = consRaiseStm(MISSING_MEMBER, memberName);
		ifChecks.setElseStm(raise);

		if(parent instanceof AApplyExpCG && ((AApplyExpCG) parent).getRoot() == node)
		{
			transAssistant.replaceNodeWith(parent, resultVar);
		}
		else
		{
			transAssistant.replaceNodeWith(node, resultVar);
		}
		
		replacementBlock.getLocalDefs().add(resultDecl);
		replacementBlock.getStatements().add(ifChecks);

		transAssistant.replaceNodeWith(enclosingStatement, replacementBlock);
		replacementBlock.getStatements().add(enclosingStatement);
		
		ifChecks.apply(this);
	}

	private void setSubject(SExpCG fieldExp, ACastUnaryExpCG castedFieldExp)
	{
		if(fieldExp instanceof AFieldExpCG)
		{
			((AFieldExpCG) fieldExp).setObject(castedFieldExp);
		}
		else if(fieldExp instanceof AFieldNumberExpCG)
		{
			((AFieldNumberExpCG) fieldExp).setTuple(castedFieldExp);
		}
	}

	private boolean memberExists(String memberName, INode parent,
			TypeAssistantCG typeAssistant, SExpCG fieldExp,
			STypeCG currentType) throws AnalysisException
	{
		if (fieldExp instanceof AFieldExpCG)
		{
			if (currentType instanceof AClassTypeCG)
			{
				String className = ((AClassTypeCG) currentType).getName();

				return memberExists(parent, typeAssistant, className, memberName);
			} else if (currentType instanceof ARecordTypeCG)
			{
				ARecordTypeCG recordType = (ARecordTypeCG) currentType;

				return transAssistant.getInfo().getDeclAssistant().getFieldDecl(transAssistant.getInfo().getClasses(), recordType, memberName) != null;
			}
		}
		else if(fieldExp instanceof AFieldNumberExpCG && currentType instanceof ATupleTypeCG)
		{
			return true;
			
			// Could possibly be strengthened
			// AFieldNumberExpCG fieldNumberExp = (AFieldNumberExpCG) fieldExp;
			// return  fieldNumberExp.getField() <= ((ATupleTypeCG) currentType).getTypes().size();
		}
		
		return false;
	}
	
	private boolean memberExists(INode parent, TypeAssistantCG typeAssistant,
			String className, String memberName) throws AnalysisException
	{
		if(typeAssistant.getFieldType(transAssistant.getInfo().getClasses(), className, memberName) != null)
		{
			return true;
		}
		
		List<SExpCG> args = ((AApplyExpCG) parent).getArgs();
		
		return typeAssistant.getMethodType(transAssistant.getInfo(), className, memberName, args) != null;
	}

	@Override
	public void caseAApplyExpCG(AApplyExpCG node) throws AnalysisException
	{
		for (SExpCG arg : node.getArgs())
		{
			arg.apply(this);
		}
		
		SExpCG root = node.getRoot();
		root.apply(this);

		if (root.getType() instanceof AUnionTypeCG) {
			STypeCG colType = searchType(root, new TypeFinder<SMapTypeCG>() {
				@Override
				public SMapTypeCG findType(PType type)
						throws org.overture.ast.analysis.AnalysisException {
					SMapType mapType = transAssistant.getInfo().getTcFactory()
							.createPTypeAssistant().getMap(type);

					return mapType != null ? (SMapTypeCG) mapType.apply(
							transAssistant.getInfo().getTypeVisitor(), transAssistant.getInfo()) : null;
				}
			});

			if (colType == null) {
				colType = searchType(root, new TypeFinder<SSeqTypeCG>() {
					@Override
					public SSeqTypeCG findType(PType type)
							throws org.overture.ast.analysis.AnalysisException {

						SSeqType seqType = transAssistant.getInfo().getTcFactory()
								.createPTypeAssistant().getSeq(type);

						return seqType != null ? (SSeqTypeCG) seqType.apply(
								transAssistant.getInfo().getTypeVisitor(), transAssistant.getInfo()) : null;
					}
				});
			}

			if (colType != null && node.getArgs().size() == 1) {
				correctTypes(root, colType);
				return;
			}

		} else if (root.getType() instanceof AMethodTypeCG)
		{
			AMethodTypeCG methodType = (AMethodTypeCG) root.getType();

			LinkedList<STypeCG> paramTypes = methodType.getParams();

			LinkedList<SExpCG> args = node.getArgs();

			correctArgTypes(args, paramTypes);
		}
	}

	@Override
	public void inANotUnaryExpCG(ANotUnaryExpCG node) throws AnalysisException
	{
		correctTypes(node.getExp(), new ABoolBasicTypeCG());
	}

	@Override
	public void inANewExpCG(ANewExpCG node) throws AnalysisException
	{
		LinkedList<SExpCG> args = node.getArgs();

		boolean hasUnionTypes = false;

		for (SExpCG arg : args)
		{
			if (arg.getType() instanceof AUnionTypeCG)
			{
				hasUnionTypes = true;
				break;
			}
		}

		if (!hasUnionTypes)
		{
			return;
		}

		STypeCG type = node.getType();

		if (type instanceof AClassTypeCG)
		{
			for (SClassDeclCG classCg : transAssistant.getInfo().getClasses())
			{
				for (AMethodDeclCG method : classCg.getMethods())
				{
					if (!method.getIsConstructor())
					{
						continue;
					}

					if (correctArgTypes(args, method.getMethodType().getParams()))
					{
						return;
					}
				}
			}
		} else if (type instanceof ARecordTypeCG)
		{
			ARecordTypeCG recordType = (ARecordTypeCG) type;
			String definingClassName = recordType.getName().getDefiningClass();
			String recordName = recordType.getName().getName();

			SClassDeclCG classDecl = transAssistant.getInfo().getAssistantManager().getDeclAssistant().findClass(transAssistant.getInfo().getClasses(), definingClassName);
			ARecordDeclCG record = transAssistant.getInfo().getAssistantManager().getDeclAssistant().findRecord(classDecl, recordName);

			List<STypeCG> fieldTypes = transAssistant.getInfo().getAssistantManager().getTypeAssistant().getFieldTypes(record);

			if (correctArgTypes(args, fieldTypes))
			{
				return;
			}
		}
	}

	@Override
	public void inAIfStmCG(AIfStmCG node) throws AnalysisException
	{
		ABoolBasicTypeCG expectedType = new ABoolBasicTypeCG();

		correctTypes(node.getIfExp(), expectedType);

		LinkedList<AElseIfStmCG> elseIfs = node.getElseIf();

		for (AElseIfStmCG currentElseIf : elseIfs)
		{
			correctTypes(currentElseIf.getElseIf(), expectedType);
		}
	}

	@Override
	public void caseAPlainCallStmCG(APlainCallStmCG node) throws AnalysisException
	{
		STypeCG classType = node.getClassType();
		
		String className = classType instanceof AClassTypeCG ? ((AClassTypeCG) classType).getName()
				: node.getAncestor(ADefaultClassDeclCG.class).getName();
		
		handleCallStm(node, className);
	}
	
	@Override
	public void caseASuperCallStmCG(ASuperCallStmCG node)
			throws AnalysisException
	{
		handleCallStm(node, transAssistant.getInfo().getStmAssistant().getSuperClassName(node));
	}

	private void handleCallStm(SCallStmCG node, String className) throws AnalysisException
	{
		for (SExpCG arg : node.getArgs())
		{
			arg.apply(this);
		}

		String fieldName = node.getName();
		LinkedList<SExpCG> args = node.getArgs();

		TypeAssistantCG typeAssistant = transAssistant.getInfo().getAssistantManager().getTypeAssistant();
		AMethodTypeCG methodType = typeAssistant.getMethodType(transAssistant.getInfo(), className, fieldName, args);

		if (methodType != null)
		{
			correctArgTypes(args, methodType.getParams());
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void inACallObjectExpStmCG(ACallObjectExpStmCG node)
			throws AnalysisException
	{
		for (SExpCG arg : node.getArgs())
		{
			arg.apply(this);
		}

		SExpCG objExp = node.getObj();

		STypeCG objType = objExp.getType();
		if (!(objType instanceof AUnionTypeCG))
		{
			return;
		}

		STypeCG type = node.getType();
		LinkedList<SExpCG> args = node.getArgs();
		//String className = node.getClassName();
		String fieldName = node.getFieldName();
		SourceNode sourceNode = node.getSourceNode();

		ACallObjectExpStmCG call = new ACallObjectExpStmCG();
		call.setObj(objExp);
		call.setType(type.clone());
		call.setArgs((List<? extends SExpCG>) args.clone());
		//call.setClassName(className);
		call.setFieldName(fieldName);
		call.setSourceNode(sourceNode);

		ABlockStmCG replacementBlock = new ABlockStmCG();

		if (!(objExp instanceof SVarExpCG))
		{
			String callStmObjName = transAssistant.getInfo().getTempVarNameGen().nextVarName(unionTypePrefixes.callStmObj());
			
			AIdentifierPatternCG id = new AIdentifierPatternCG();
			id.setName(callStmObjName);
			AVarDeclCG objDecl = transAssistant.getInfo().getDeclAssistant().
					consLocalVarDecl(node.getSourceNode().getVdmNode(),
							objType.clone(), id, objExp.clone());

			AIdentifierVarExpCG objVar = new AIdentifierVarExpCG();
			objVar.setSourceNode(node.getSourceNode());
			objVar.setIsLambda(false);
			objVar.setIsLocal(true);
			objVar.setName(callStmObjName);
			objVar.setType(objDecl.getType().clone());

			objExp = objVar;

			replacementBlock.getLocalDefs().add(objDecl);
		}

		TypeAssistantCG typeAssistant = transAssistant.getInfo().getAssistantManager().getTypeAssistant();

		LinkedList<STypeCG> possibleTypes = ((AUnionTypeCG) objType).getTypes();
		AIfStmCG ifChecks = new AIfStmCG();

		int handledTypes = 0;
		for (int i = 0; i < possibleTypes.size(); i++)
		{
			ACallObjectExpStmCG callCopy = call.clone();

			AClassTypeCG currentType = (AClassTypeCG) possibleTypes.get(i);

			AMethodTypeCG methodType = typeAssistant.getMethodType(transAssistant.getInfo(), currentType.getName(), fieldName, args);

			if (methodType != null)
			{
				correctArgTypes(callCopy.getArgs(), methodType.getParams());
			}
			else
			{
				//It's possible (due to the way union types work) that the method type for the
				//field in the object type does not exist. Let's say we are trying to invoke the
				//operation 'op' for an object type that is either A or B but it might be the
				//case that only 'A' has the operation 'op' defined.
				continue;
			}

			ACastUnaryExpCG castedVarExp = new ACastUnaryExpCG();
			castedVarExp.setType(currentType.clone());
			castedVarExp.setExp(objExp.clone());

			callCopy.setObj(castedVarExp);

			if (handledTypes == 0)
			{
				ifChecks.setIfExp(consInstanceCheck(objExp, currentType));
				ifChecks.setThenStm(callCopy);
			} else
			{
				AElseIfStmCG elseIf = new AElseIfStmCG();
				elseIf.setElseIf(consInstanceCheck(objExp, currentType));
				elseIf.setThenStm(callCopy);

				ifChecks.getElseIf().add(elseIf);
			}
			
			handledTypes++;
		}
		
		if(handledTypes == 0)
		{
			return;
		}
		
		ARaiseErrorStmCG raiseStm = consRaiseStm(MISSING_OP_MEMBER,fieldName);
		ifChecks.setElseStm(raiseStm);

		replacementBlock.getStatements().add(ifChecks);
		transAssistant.replaceNodeWith(node, replacementBlock);
		ifChecks.apply(this);
	}

	private ARaiseErrorStmCG consRaiseStm(String prefix, String fieldName)
	{
		AMissingMemberRuntimeErrorExpCG missingMember = new AMissingMemberRuntimeErrorExpCG();
		missingMember.setType(new AErrorTypeCG());
		missingMember.setMessage(prefix + fieldName);

		ARaiseErrorStmCG raise = new ARaiseErrorStmCG();
		raise.setError(missingMember);
		
		return raise;
	}

	@Override
	public void inAVarDeclCG(AVarDeclCG node)
			throws AnalysisException
	{
		SExpCG exp = node.getExp();
		
		if(exp != null)
		{
			exp.apply(this);
		}
		
		STypeCG type = node.getType();
		
		if(castNotNeeded(exp, type))
		{
			return;
		}
		
		if (!(type instanceof AUnionTypeCG))
		{
			correctTypes(exp, type);
		}
	}
	
	@Override
	public void caseAAssignToExpStmCG(AAssignToExpStmCG node) throws AnalysisException
	{
		SExpCG exp = node.getExp();
		
		if(exp != null)
		{
			exp.apply(this);
		}
		
		STypeCG type = node.getTarget().getType();
		
		if(castNotNeeded(exp, type))
		{
			return;
		}
		
		if (!(type instanceof AUnionTypeCG))
		{
			correctTypes(exp, type);
		}
	}

	private boolean castNotNeeded(SExpCG exp, STypeCG type)
	{
		return type instanceof AUnknownTypeCG || exp instanceof ANullExpCG || exp instanceof AUndefinedExpCG;
	}

	@Override
	public void caseAReturnStmCG(AReturnStmCG node) throws AnalysisException
	{
		if (node.getExp() == null)
		{
			return; // When the return type of the method is 'void'
		}
		
		if(node.getExp() instanceof ANullExpCG)
		{
			return;
		}

		node.getExp().apply(this);

		AMethodDeclCG methodDecl = node.getAncestor(AMethodDeclCG.class);

		STypeCG expectedType = methodDecl.getMethodType().getResult();
		
		if(expectedType instanceof AUnknownTypeCG)
		{
			return;
		}

		if (!(expectedType instanceof AUnionTypeCG))
		{
			correctTypes(node.getExp(), expectedType);
		}
	}

	@Override
	public void inAElemsUnaryExpCG(AElemsUnaryExpCG node)
			throws AnalysisException
	{
		if (handleUnaryExp(node))
		{
			SExpCG exp = node.getExp();
			PType vdmType = (PType) exp.getType().getSourceNode().getVdmNode();
			SSeqType seqType = transAssistant.getInfo().getTcFactory().createPTypeAssistant().getSeq(vdmType);

			try
			{
				STypeCG typeCg = seqType.apply(transAssistant.getInfo().getTypeVisitor(), transAssistant.getInfo());

				if (typeCg instanceof SSeqTypeCG)
				{
					correctTypes(exp, typeCg);
				}

			} catch (org.overture.ast.analysis.AnalysisException e)
			{
			}
		}
	}

	@Override
	public void inAMapDomainUnaryExpCG(AMapDomainUnaryExpCG node)
			throws AnalysisException
	{
		if (handleUnaryExp(node))
		{
			SExpCG exp = node.getExp();
			PType vdmType = (PType) exp.getType().getSourceNode().getVdmNode();
			SMapType mapType = transAssistant.getInfo().getTcFactory().createPTypeAssistant().getMap(vdmType);

			try
			{
				STypeCG typeCg = mapType.apply(transAssistant.getInfo().getTypeVisitor(), transAssistant.getInfo());

				if (typeCg instanceof SMapTypeCG)
				{
					correctTypes(exp, typeCg);
				}

			} catch (org.overture.ast.analysis.AnalysisException e)
			{
			}
		}
	}
	
	private SExpCG getAssignmentExp(INode node, SExpCG fieldExp)
	{
		INode parent = node.parent();
		
		if(parent instanceof AApplyExpCG && ((AApplyExpCG) parent).getRoot() == node)
		{
			AApplyExpCG applyExp = (AApplyExpCG) parent.clone();
			applyExp.setRoot(fieldExp);
			
			return applyExp;
		}
		else
		{
			return fieldExp;
		}
	}

	private STypeCG getResultType(AFieldExpCG node, INode parent,
			STypeCG fieldObjType, TypeAssistantCG typeAssistant)
	{
		if(parent instanceof SExpCG)
		{
			if (parent instanceof AApplyExpCG && ((AApplyExpCG) parent).getRoot() == node)
			{
				return ((SExpCG) parent).getType().clone();
			}
		}

		return fieldType(node, fieldObjType, typeAssistant);
	}

	private STypeCG fieldType(AFieldExpCG node, STypeCG objectType,
			TypeAssistantCG typeAssistant)
	{
		List<STypeCG> fieldTypes = new LinkedList<STypeCG>();
		List<STypeCG> types = ((AUnionTypeCG)objectType).getTypes();
		
		for(STypeCG currentType : types)
		{
			String memberName = node.getMemberName();
			STypeCG fieldType = null;
			
			if(currentType instanceof AClassTypeCG)
			{
				AClassTypeCG classType = (AClassTypeCG) currentType;
				fieldType = typeAssistant.getFieldType(transAssistant.getInfo().getClasses(), classType.getName(), memberName);
			}
			else if(currentType instanceof ARecordTypeCG)
			{
				ARecordTypeCG recordType = (ARecordTypeCG) currentType;
				fieldType = transAssistant.getInfo().getTypeAssistant().getFieldType(transAssistant.getInfo().getClasses(), recordType, memberName);
			}
			else{
				//Can be the unknown type
				continue;
			}

			if(fieldType == null)
			{
				// The field type may not be found if the member does not exist
				// For example:
				// 
				// types
				// R1 :: x : int;
				// R2 :: y : int;
				// ...
				//let inlines : seq of Inline = [mk_R1(4), mk_R2(5)]
				//in 
				//		  return inlines(1).x + inlines(2).y;
				continue;
			}
			
			if(!typeAssistant.containsType(fieldTypes, fieldType))
			{
				fieldTypes.add(fieldType);
			}
		}
		
		if(fieldTypes.size() == 1)
		{
			return fieldTypes.get(0);
		}
		else
		{
			AUnionTypeCG unionTypes = new AUnionTypeCG();
			unionTypes.setTypes(fieldTypes);
			
			return unionTypes;
		}
	}
}

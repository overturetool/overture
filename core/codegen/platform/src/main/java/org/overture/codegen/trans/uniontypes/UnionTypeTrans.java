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
import org.overture.codegen.assistant.TypeAssistantIR;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.*;
import org.overture.codegen.ir.expressions.AIsOfClassExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.ACallObjectExpStmIR;
import org.overture.codegen.ir.statements.AElseIfStmIR;
import org.overture.codegen.ir.statements.AIfStmIR;
import org.overture.codegen.ir.statements.APlainCallStmIR;
import org.overture.codegen.ir.statements.ARaiseErrorStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.statements.ASuperCallStmIR;
import org.overture.codegen.ir.statements.SCallStmIR;
import org.overture.codegen.ir.types.*;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class UnionTypeTrans extends DepthFirstAnalysisAdaptor
{
	public static final String MISSING_OP_MEMBER = "Missing operation member: ";
	public static final String MISSING_MEMBER = "Missing member: ";

	private TransAssistantIR transAssistant;

	private UnionTypeVarPrefixes unionTypePrefixes;

	private List<INode> cloneFreeNodes;

	public UnionTypeTrans(TransAssistantIR transAssistant,
			UnionTypeVarPrefixes unionTypePrefixes, List<INode> cloneFreeNodes)
	{
		this.transAssistant = transAssistant;
		this.unionTypePrefixes = unionTypePrefixes;
		this.cloneFreeNodes = cloneFreeNodes;
	}

	private interface TypeFinder<T extends STypeIR>
	{
		public T findType(PType type)
				throws org.overture.ast.analysis.AnalysisException;
	}

	public <T extends STypeIR> T searchType(SExpIR exp,
			TypeFinder<T> typeFinder)
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

	private SExpIR correctTypes(SExpIR exp, STypeIR castedType)
			throws AnalysisException
	{
		if ((exp.getType() instanceof AUnknownTypeIR ||
				exp.getType() instanceof AUnionTypeIR ||
				castedType instanceof ATemplateTypeIR) &&
				!(exp instanceof ACastUnaryExpIR) &&
				!exp.getType().equals(castedType))
		{
			ACastUnaryExpIR casted = new ACastUnaryExpIR();
			casted.setType(castedType.clone());
			casted.setExp(exp.clone());

			transAssistant.replaceNodeWith(exp, casted);

			return casted;
		}

		return exp;
	}

	private boolean correctArgTypes(List<SExpIR> args, List<STypeIR> paramTypes, boolean sameSize)
			throws AnalysisException
	{
		if (sameSize && args.size() != paramTypes.size())
		{
			return false;
		}

		if (transAssistant.getInfo().getAssistantManager().getTypeAssistant().checkArgTypes(transAssistant.getInfo(), args, paramTypes))
		{
			for (int k = 0; k < paramTypes.size(); k++)
			{
				SExpIR arg = args.get(k);

				if (!(arg instanceof ANullExpIR))
				{
					correctTypes(arg, paramTypes.get(k));
				}
			}
			return true;
		}

		return false;
	}

	private boolean handleUnaryExp(SUnaryExpIR exp) throws AnalysisException
	{
		STypeIR type = exp.getExp().getType();

		if (type instanceof AUnionTypeIR)
		{
			org.overture.ast.node.INode vdmNode = type.getSourceNode().getVdmNode();

			if (vdmNode instanceof PType)
			{
				return true;
			}
		}

		return false;
	}

	private AIsOfClassExpIR consInstanceCheck(SExpIR copy, STypeIR type)
	{
		AIsOfClassExpIR check = new AIsOfClassExpIR();
		check.setType(new ABoolBasicTypeIR());
		check.setCheckedType(type.clone());
		check.setExp(copy.clone());

		return check;
	}

	@Override
	public void defaultInSNumericBinaryExpIR(SNumericBinaryExpIR node)
			throws AnalysisException
	{
		STypeIR expectedType;

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

	public STypeIR getExpectedOperandType(SNumericBinaryExpIR node)
	{
		if (node instanceof AIntDivNumericBinaryExpIR
				|| node instanceof AModNumericBinaryExpIR
				|| node instanceof ARemNumericBinaryExpIR)
		{
			return new AIntNumericBasicTypeIR();
		} else
		{
			return new ARealNumericBasicTypeIR();
		}
	}

	@Override
	public void caseAFieldDeclIR(AFieldDeclIR node) throws AnalysisException
	{
		if (node.getInitial() != null)
		{
			if (node.getInitial().getType() instanceof AUnionTypeIR)
			{
				correctTypes(node.getInitial(), node.getType());
			}

			node.getInitial().apply(this);
		}
	}

	@Override
	public void caseACardUnaryExpIR(ACardUnaryExpIR node)
			throws AnalysisException
	{
		STypeIR type = node.getExp().getType();

		if (type instanceof AUnionTypeIR)
		{
			STypeIR expectedType = transAssistant.getInfo().getTypeAssistant().getSetType((AUnionTypeIR) type);
			correctTypes(node.getExp(), expectedType);
		}

		node.getExp().apply(this);
		node.getType().apply(this);
	}

	@Override
	public void caseALenUnaryExpIR(ALenUnaryExpIR node) throws AnalysisException
	{
		STypeIR type = node.getExp().getType();

		if (type instanceof AUnionTypeIR)
		{
			STypeIR expectedType = transAssistant.getInfo().getTypeAssistant().getSeqType((AUnionTypeIR) type);
			correctTypes(node.getExp(), expectedType);
		}

		node.getExp().apply(this);
		node.getType().apply(this);
	}

	@Override
	public void caseASeqConcatBinaryExpIR(ASeqConcatBinaryExpIR node)
			throws AnalysisException
	{
		node.getLeft().apply(this);
		node.getRight().apply(this);
		node.getType().apply(this);

		if (!transAssistant.getInfo().getTypeAssistant().usesUnionType(node))
		{
			return;
		}

		STypeIR leftType = node.getLeft().getType();

		if (leftType instanceof AUnionTypeIR)
		{
			STypeIR expectedType = transAssistant.getInfo().getTypeAssistant().getSeqType((AUnionTypeIR) leftType);
			correctTypes(node.getLeft(), expectedType);
		}

		STypeIR rightType = node.getRight().getType();

		if (rightType instanceof AUnionTypeIR)
		{
			STypeIR expectedType = transAssistant.getInfo().getTypeAssistant().getSeqType((AUnionTypeIR) rightType);
			correctTypes(node.getRight(), expectedType);
		}
	}

	@Override
	public void caseAFieldNumberExpIR(AFieldNumberExpIR node)
			throws AnalysisException
	{
		SExpIR tuple = node.getTuple();
		STypeIR tupleType = tuple.getType();

		if (!(tupleType instanceof AUnionTypeIR))
		{
			tuple.apply(this);
			return;
		}

		handleFieldExp(node, "field number "
				+ node.getField(), tuple, tupleType, node.getType().clone());
	}

	@Override
	public void caseAFieldExpIR(AFieldExpIR node) throws AnalysisException
	{
		SExpIR object = node.getObject();
		STypeIR objectType = object.getType();

		if (!(objectType instanceof AUnionTypeIR))
		{
			object.apply(this);
			return;
		}

		STypeIR resultType = getResultType(node, node.parent(), objectType, transAssistant.getInfo().getTypeAssistant());

		handleFieldExp(node, node.getMemberName(), object, objectType, resultType);
	}

	private void handleFieldExp(SExpIR node, String memberName, SExpIR subject,
			STypeIR fieldObjType, STypeIR resultType) throws AnalysisException
	{
		INode parent = node.parent();

		TypeAssistantIR typeAssistant = transAssistant.getInfo().getAssistantManager().getTypeAssistant();

		SStmIR enclosingStatement = transAssistant.getEnclosingStm(node, "field expression");

		String applyResultName = transAssistant.getInfo().getTempVarNameGen().nextVarName(unionTypePrefixes.applyExp());

		AIdentifierPatternIR id = new AIdentifierPatternIR();
		id.setName(applyResultName);

		AVarDeclIR resultDecl = transAssistant.getInfo().getDeclAssistant().consLocalVarDecl(node.getSourceNode().getVdmNode(), resultType, id, transAssistant.getInfo().getExpAssistant().consUndefinedExp());

		AIdentifierVarExpIR resultVar = new AIdentifierVarExpIR();
		resultVar.setSourceNode(node.getSourceNode());
		resultVar.setIsLambda(false);
		resultVar.setIsLocal(true);
		resultVar.setName(applyResultName);
		resultVar.setType(resultDecl.getType().clone());

		ABlockStmIR replacementBlock = new ABlockStmIR();
		SExpIR obj = null;

		if (!(subject instanceof SVarExpBase))
		{
			String objName = transAssistant.getInfo().getTempVarNameGen().nextVarName(unionTypePrefixes.objExp());

			AIdentifierPatternIR objId = new AIdentifierPatternIR();
			objId.setName(objName);

			AVarDeclIR objectDecl = transAssistant.getInfo().getDeclAssistant().consLocalVarDecl(subject.getType().clone(), objId, subject.clone());

			replacementBlock.getLocalDefs().add(objectDecl);

			AIdentifierVarExpIR objectVar = new AIdentifierVarExpIR();
			objectVar.setIsLambda(false);
			objectVar.setIsLocal(true);
			objectVar.setName(objName);
			objectVar.setType(objectDecl.getType().clone());
			obj = objectVar;
		} else
		{
			obj = subject.clone();
		}

		List<STypeIR> possibleTypes = ((AUnionTypeIR) fieldObjType).getTypes();
		possibleTypes = typeAssistant.clearDuplicates(possibleTypes);

		AIfStmIR ifChecks = new AIfStmIR();

		int handledTypes = 0;
		for (int i = 0; i < possibleTypes.size(); i++)
		{
			SExpIR fieldExp = (SExpIR) node.clone();
			STypeIR currentType = possibleTypes.get(i);

			if (currentType instanceof AUnknownTypeIR)
			{
				// If we are accessing an element of (say) the sequence [new A(), new B(), nil] of type A | B | [?]
				// then the current IR type will be the unknown type at some point. This case is simply skipped.
				continue;
			}

			if (!(currentType instanceof AClassTypeIR)
					&& !(currentType instanceof ATupleTypeIR)
					&& !(currentType instanceof ARecordTypeIR))
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

			ACastUnaryExpIR castedFieldExp = new ACastUnaryExpIR();
			castedFieldExp.setType(currentType.clone());
			castedFieldExp.setExp(obj.clone());

			setSubject(fieldExp, castedFieldExp);

			AAssignToExpStmIR assignment = new AAssignToExpStmIR();
			cloneFreeNodes.add(assignment);
			assignment.setTarget(resultVar.clone());
			assignment.setExp(getAssignmentExp(node, fieldExp));

			if (handledTypes == 0)
			{
				ifChecks.setIfExp(consInstanceCheck(obj, currentType));
				ifChecks.setThenStm(assignment);
			} else
			{
				AElseIfStmIR elseIf = new AElseIfStmIR();
				elseIf.setElseIf(consInstanceCheck(obj, currentType));
				elseIf.setThenStm(assignment);

				ifChecks.getElseIf().add(elseIf);
			}

			handledTypes++;
		}

		if (handledTypes == 0)
		{
			return;
		}

		ARaiseErrorStmIR raise = consRaiseStm(MISSING_MEMBER, memberName);
		ifChecks.setElseStm(raise);

		if (parent instanceof AApplyExpIR
				&& ((AApplyExpIR) parent).getRoot() == node)
		{
			transAssistant.replaceNodeWith(parent, resultVar);
		} else
		{
			transAssistant.replaceNodeWith(node, resultVar);
		}

		replacementBlock.getLocalDefs().add(resultDecl);
		replacementBlock.getStatements().add(ifChecks);

		transAssistant.replaceNodeWith(enclosingStatement, replacementBlock);
		replacementBlock.getStatements().add(enclosingStatement);

		ifChecks.apply(this);
	}

	private void setSubject(SExpIR fieldExp, ACastUnaryExpIR castedFieldExp)
	{
		if (fieldExp instanceof AFieldExpIR)
		{
			((AFieldExpIR) fieldExp).setObject(castedFieldExp);
		} else if (fieldExp instanceof AFieldNumberExpIR)
		{
			((AFieldNumberExpIR) fieldExp).setTuple(castedFieldExp);
		}
	}

	private boolean memberExists(String memberName, INode parent,
			TypeAssistantIR typeAssistant, SExpIR fieldExp, STypeIR currentType)
			throws AnalysisException
	{
		if (fieldExp instanceof AFieldExpIR)
		{
			if (currentType instanceof AClassTypeIR)
			{
				String className = ((AClassTypeIR) currentType).getName();

				return memberExists(parent, typeAssistant, className, memberName);
			} else if (currentType instanceof ARecordTypeIR)
			{
				ARecordTypeIR recordType = (ARecordTypeIR) currentType;

				return transAssistant.getInfo().getDeclAssistant().getFieldDecl(transAssistant.getInfo().getClasses(), recordType, memberName) != null;
			}
		} else if (fieldExp instanceof AFieldNumberExpIR
				&& currentType instanceof ATupleTypeIR)
		{
			return true;

			// Could possibly be strengthened
			// AFieldNumberExpIR fieldNumberExp = (AFieldNumberExpIR) fieldExp;
			// return fieldNumberExp.getField() <= ((ATupleTypeIR) currentType).getTypes().size();
		}

		return false;
	}

	private boolean memberExists(INode parent, TypeAssistantIR typeAssistant,
			String className, String memberName) throws AnalysisException
	{
		if (typeAssistant.getFieldType(transAssistant.getInfo().getClasses(), className, memberName) != null)
		{
			return true;
		}

		List<SExpIR> args = ((AApplyExpIR) parent).getArgs();

		return typeAssistant.getMethodType(transAssistant.getInfo(), className, memberName, args) != null;
	}

	@Override
	public void caseAApplyExpIR(AApplyExpIR node) throws AnalysisException
	{
		for (SExpIR arg : node.getArgs())
		{
			arg.apply(this);
		}

		SExpIR root = node.getRoot();
		root.apply(this);

		if (root.getType() instanceof AUnionTypeIR)
		{
			STypeIR colType = searchType(root, new TypeFinder<SMapTypeIR>()
			{
				@Override
				public SMapTypeIR findType(PType type)
						throws org.overture.ast.analysis.AnalysisException
				{
					SMapType mapType = transAssistant.getInfo().getTcFactory().createPTypeAssistant().getMap(type);

					return mapType != null
							? (SMapTypeIR) mapType.apply(transAssistant.getInfo().getTypeVisitor(), transAssistant.getInfo())
							: null;
				}
			});

			if (colType == null)
			{
				colType = searchType(root, new TypeFinder<SSeqTypeIR>()
				{
					@Override
					public SSeqTypeIR findType(PType type)
							throws org.overture.ast.analysis.AnalysisException
					{

						SSeqType seqType = transAssistant.getInfo().getTcFactory().createPTypeAssistant().getSeq(type);

						return seqType != null
								? (SSeqTypeIR) seqType.apply(transAssistant.getInfo().getTypeVisitor(), transAssistant.getInfo())
								: null;
					}
				});
			}

			if (colType != null && node.getArgs().size() == 1)
			{
				correctTypes(root, colType);
				return;
			}

		} else if (root.getType() instanceof AMethodTypeIR)
		{
			AMethodTypeIR methodType = (AMethodTypeIR) root.getType();

			LinkedList<STypeIR> paramTypes = methodType.getParams();

			LinkedList<SExpIR> args = node.getArgs();
			correctArgTypes(args, paramTypes, false);
		}
	}

	@Override
	public void inANotUnaryExpIR(ANotUnaryExpIR node) throws AnalysisException
	{
		correctTypes(node.getExp(), new ABoolBasicTypeIR());
	}

	@Override
	public void inANewExpIR(ANewExpIR node) throws AnalysisException
	{
		LinkedList<SExpIR> args = node.getArgs();

		boolean hasUnionTypes = false;

		for (SExpIR arg : args)
		{
			if (arg.getType() instanceof AUnionTypeIR)
			{
				hasUnionTypes = true;
				break;
			}
		}

		if (!hasUnionTypes)
		{
			return;
		}

		STypeIR type = node.getType();

		if (type instanceof AClassTypeIR)
		{
			for (SClassDeclIR classCg : transAssistant.getInfo().getClasses())
			{
				for (AMethodDeclIR method : classCg.getMethods())
				{
					if (!method.getIsConstructor())
					{
						continue;
					}

					if (correctArgTypes(args, method.getMethodType().getParams(), true))
					{
						return;
					}
				}
			}
		} else if (type instanceof ARecordTypeIR)
		{
			ARecordTypeIR recordType = (ARecordTypeIR) type;
			String definingClassName = recordType.getName().getDefiningClass();
			String recordName = recordType.getName().getName();

			SClassDeclIR classDecl = transAssistant.getInfo().getAssistantManager().getDeclAssistant().findClass(transAssistant.getInfo().getClasses(), definingClassName);
			
			if(classDecl == null)
			{
				return;
			}
			
			ARecordDeclIR record = transAssistant.getInfo().getAssistantManager().getDeclAssistant().findRecord(classDecl, recordName);

			List<STypeIR> fieldTypes = transAssistant.getInfo().getAssistantManager().getTypeAssistant().getFieldTypes(record);

			if (correctArgTypes(args, fieldTypes, true))
			{
				return;
			}
		}
	}

	@Override
	public void inAIfStmIR(AIfStmIR node) throws AnalysisException
	{
		ABoolBasicTypeIR expectedType = new ABoolBasicTypeIR();

		correctTypes(node.getIfExp(), expectedType);

		LinkedList<AElseIfStmIR> elseIfs = node.getElseIf();

		for (AElseIfStmIR currentElseIf : elseIfs)
		{
			correctTypes(currentElseIf.getElseIf(), expectedType);
		}
	}

	@Override
	public void caseAPlainCallStmIR(APlainCallStmIR node)
			throws AnalysisException
	{
		STypeIR classType = node.getClassType();

		String className = classType instanceof AClassTypeIR
				? ((AClassTypeIR) classType).getName()
				: node.getAncestor(ADefaultClassDeclIR.class).getName();

		handleCallStm(node, className);
	}

	@Override
	public void caseASuperCallStmIR(ASuperCallStmIR node)
			throws AnalysisException
	{
		handleCallStm(node, transAssistant.getInfo().getStmAssistant().getSuperClassName(node));
	}

	private void handleCallStm(SCallStmIR node, String className)
			throws AnalysisException
	{
		for (SExpIR arg : node.getArgs())
		{
			arg.apply(this);
		}

		String fieldName = node.getName();
		LinkedList<SExpIR> args = node.getArgs();

		TypeAssistantIR typeAssistant = transAssistant.getInfo().getAssistantManager().getTypeAssistant();
		AMethodTypeIR methodType = typeAssistant.getMethodType(transAssistant.getInfo(), className, fieldName, args);

		if (methodType != null)
		{
			correctArgTypes(args, methodType.getParams(), true);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void inACallObjectExpStmIR(ACallObjectExpStmIR node)
			throws AnalysisException
	{
		for (SExpIR arg : node.getArgs())
		{
			arg.apply(this);
		}

		SExpIR objExp = node.getObj();

		STypeIR objType = objExp.getType();
		if (!(objType instanceof AUnionTypeIR))
		{
			return;
		}

		STypeIR type = node.getType();
		LinkedList<SExpIR> args = node.getArgs();
		// String className = node.getClassName();
		String fieldName = node.getFieldName();
		SourceNode sourceNode = node.getSourceNode();

		ACallObjectExpStmIR call = new ACallObjectExpStmIR();
		call.setObj(objExp);
		call.setType(type.clone());
		call.setArgs((List<? extends SExpIR>) args.clone());
		// call.setClassName(className);
		call.setFieldName(fieldName);
		call.setSourceNode(sourceNode);

		ABlockStmIR replacementBlock = new ABlockStmIR();

		if (!(objExp instanceof SVarExpIR))
		{
			String callStmObjName = transAssistant.getInfo().getTempVarNameGen().nextVarName(unionTypePrefixes.callStmObj());

			AIdentifierPatternIR id = new AIdentifierPatternIR();
			id.setName(callStmObjName);
			AVarDeclIR objDecl = transAssistant.getInfo().getDeclAssistant().consLocalVarDecl(node.getSourceNode().getVdmNode(), objType.clone(), id, objExp.clone());

			AIdentifierVarExpIR objVar = new AIdentifierVarExpIR();
			objVar.setSourceNode(node.getSourceNode());
			objVar.setIsLambda(false);
			objVar.setIsLocal(true);
			objVar.setName(callStmObjName);
			objVar.setType(objDecl.getType().clone());

			objExp = objVar;

			replacementBlock.getLocalDefs().add(objDecl);
		}

		TypeAssistantIR typeAssistant = transAssistant.getInfo().getAssistantManager().getTypeAssistant();

		LinkedList<STypeIR> possibleTypes = ((AUnionTypeIR) objType).getTypes();
		AIfStmIR ifChecks = new AIfStmIR();

		int handledTypes = 0;
		for (int i = 0; i < possibleTypes.size(); i++)
		{
			ACallObjectExpStmIR callCopy = call.clone();

			AClassTypeIR currentType = (AClassTypeIR) possibleTypes.get(i);

			AMethodTypeIR methodType = typeAssistant.getMethodType(transAssistant.getInfo(), currentType.getName(), fieldName, args);

			if (methodType != null)
			{
				correctArgTypes(callCopy.getArgs(), methodType.getParams(), true);
			} else
			{
				// It's possible (due to the way union types work) that the method type for the
				// field in the object type does not exist. Let's say we are trying to invoke the
				// operation 'op' for an object type that is either A or B but it might be the
				// case that only 'A' has the operation 'op' defined.
				continue;
			}

			ACastUnaryExpIR castedVarExp = new ACastUnaryExpIR();
			castedVarExp.setType(currentType.clone());
			castedVarExp.setExp(objExp.clone());

			callCopy.setObj(castedVarExp);

			if (handledTypes == 0)
			{
				ifChecks.setIfExp(consInstanceCheck(objExp, currentType));
				ifChecks.setThenStm(callCopy);
			} else
			{
				AElseIfStmIR elseIf = new AElseIfStmIR();
				elseIf.setElseIf(consInstanceCheck(objExp, currentType));
				elseIf.setThenStm(callCopy);

				ifChecks.getElseIf().add(elseIf);
			}

			handledTypes++;
		}

		if (handledTypes == 0)
		{
			return;
		}

		ARaiseErrorStmIR raiseStm = consRaiseStm(MISSING_OP_MEMBER, fieldName);
		ifChecks.setElseStm(raiseStm);

		replacementBlock.getStatements().add(ifChecks);
		transAssistant.replaceNodeWith(node, replacementBlock);
		ifChecks.apply(this);
	}

	private ARaiseErrorStmIR consRaiseStm(String prefix, String fieldName)
	{
		AMissingMemberRuntimeErrorExpIR missingMember = new AMissingMemberRuntimeErrorExpIR();
		missingMember.setType(new AErrorTypeIR());
		missingMember.setMessage(prefix + fieldName);

		ARaiseErrorStmIR raise = new ARaiseErrorStmIR();
		raise.setError(missingMember);

		return raise;
	}

	@Override
	public void inAVarDeclIR(AVarDeclIR node) throws AnalysisException
	{
		SExpIR exp = node.getExp();

		if (exp != null)
		{
			exp.apply(this);
		}

		STypeIR type = node.getType();

		if (castNotNeeded(exp, type))
		{
			return;
		}

		if (!(type instanceof AUnionTypeIR))
		{
			correctTypes(exp, type);
		}
	}

	@Override
	public void caseAAssignToExpStmIR(AAssignToExpStmIR node)
			throws AnalysisException
	{
		handAssignRighHandSide(node);
		handleAssignTarget(node);
	}

	public void handleAssignTarget(AAssignToExpStmIR node)
			throws AnalysisException
	{
		if (node.getTarget() instanceof AFieldExpIR)
		{
			AFieldExpIR field = (AFieldExpIR) node.getTarget();

			if (field.getObject().getType() instanceof AUnionTypeIR)
			{
				LinkedList<STypeIR> types = ((AUnionTypeIR) field.getObject().getType()).getTypes();

				AIfStmIR ifChecks = new AIfStmIR();

				for (int i = 0; i < types.size(); i++)
				{
					STypeIR currentType = types.get(i);

					AIsOfClassExpIR cond = consInstanceCheck(field.getObject(), currentType);
					AAssignToExpStmIR castFieldObj = castFieldObj(node, field, currentType);

					if (i == 0)
					{
						ifChecks.setIfExp(cond);
						ifChecks.setThenStm(castFieldObj);
					} else
					{
						AElseIfStmIR elseIf = new AElseIfStmIR();
						elseIf.setElseIf(cond);
						elseIf.setThenStm(castFieldObj);

						ifChecks.getElseIf().add(elseIf);
					}
				}

				ifChecks.setElseStm(consRaiseStm(MISSING_MEMBER, field.getMemberName()));

				transAssistant.replaceNodeWith(node, ifChecks);
				ifChecks.apply(this);
			}
		}
	}

	public void handAssignRighHandSide(AAssignToExpStmIR node)
			throws AnalysisException
	{
		if (node.getExp() != null)
		{
			node.getExp().apply(this);
		}

		if (!castNotNeeded(node.getExp(), node.getTarget().getType()))
		{
			if (!(node.getTarget().getType() instanceof AUnionTypeIR))
			{
				correctTypes(node.getExp(), node.getTarget().getType());
			}
		}
	}

	public AAssignToExpStmIR castFieldObj(AAssignToExpStmIR assign,
			AFieldExpIR target, STypeIR possibleType)
	{
		ACastUnaryExpIR cast = new ACastUnaryExpIR();
		cast.setType(possibleType.clone());
		cast.setExp(target.getObject().clone());

		AAssignToExpStmIR assignCopy = assign.clone();
		AFieldExpIR fieldCopy = target.clone();

		transAssistant.replaceNodeWith(fieldCopy.getObject(), cast);
		transAssistant.replaceNodeWith(assignCopy.getTarget(), fieldCopy);

		return assignCopy;
	}

	private boolean castNotNeeded(SExpIR exp, STypeIR type)
	{
		return type instanceof AUnknownTypeIR || exp instanceof ANullExpIR
				|| exp instanceof AUndefinedExpIR;
	}

	@Override
	public void caseAReturnStmIR(AReturnStmIR node) throws AnalysisException
	{
		if (node.getExp() == null)
		{
			return; // When the return type of the method is 'void'
		}

		if (node.getExp() instanceof ANullExpIR)
		{
			return;
		}

		node.getExp().apply(this);

		AMethodDeclIR methodDecl = node.getAncestor(AMethodDeclIR.class);

		STypeIR expectedType = methodDecl.getMethodType().getResult();

		if (expectedType instanceof AUnknownTypeIR)
		{
			return;
		}

		if (!(expectedType instanceof AUnionTypeIR))
		{
			correctTypes(node.getExp(), expectedType);
		}
	}

	@Override
	public void inAElemsUnaryExpIR(AElemsUnaryExpIR node)
			throws AnalysisException
	{
		if (handleUnaryExp(node))
		{
			SExpIR exp = node.getExp();
			PType vdmType = (PType) exp.getType().getSourceNode().getVdmNode();
			SSeqType seqType = transAssistant.getInfo().getTcFactory().createPTypeAssistant().getSeq(vdmType);

			try
			{
				STypeIR typeCg = seqType.apply(transAssistant.getInfo().getTypeVisitor(), transAssistant.getInfo());

				if (typeCg instanceof SSeqTypeIR)
				{
					correctTypes(exp, typeCg);
				}

			} catch (org.overture.ast.analysis.AnalysisException e)
			{
			}
		}
	}

	@Override
	public void inAMapDomainUnaryExpIR(AMapDomainUnaryExpIR node)
			throws AnalysisException
	{
		if (handleUnaryExp(node))
		{
			SExpIR exp = node.getExp();
			PType vdmType = (PType) exp.getType().getSourceNode().getVdmNode();
			SMapType mapType = transAssistant.getInfo().getTcFactory().createPTypeAssistant().getMap(vdmType);

			try
			{
				STypeIR typeCg = mapType.apply(transAssistant.getInfo().getTypeVisitor(), transAssistant.getInfo());

				if (typeCg instanceof SMapTypeIR)
				{
					correctTypes(exp, typeCg);
				}

			} catch (org.overture.ast.analysis.AnalysisException e)
			{
			}
		}
	}

	private SExpIR getAssignmentExp(INode node, SExpIR fieldExp)
	{
		INode parent = node.parent();

		if (parent instanceof AApplyExpIR
				&& ((AApplyExpIR) parent).getRoot() == node)
		{
			AApplyExpIR applyExp = (AApplyExpIR) parent.clone();
			applyExp.setRoot(fieldExp);

			return applyExp;
		} else
		{
			return fieldExp;
		}
	}

	private STypeIR getResultType(AFieldExpIR node, INode parent,
			STypeIR fieldObjType, TypeAssistantIR typeAssistant)
	{
		if (parent instanceof SExpIR)
		{
			if (parent instanceof AApplyExpIR
					&& ((AApplyExpIR) parent).getRoot() == node)
			{
				return ((SExpIR) parent).getType().clone();
			}
		}

		return fieldType(node, fieldObjType, typeAssistant);
	}

	private STypeIR fieldType(AFieldExpIR node, STypeIR objectType,
			TypeAssistantIR typeAssistant)
	{
		List<STypeIR> fieldTypes = new LinkedList<STypeIR>();
		List<STypeIR> types = ((AUnionTypeIR) objectType).getTypes();

		for (STypeIR currentType : types)
		{
			String memberName = node.getMemberName();
			STypeIR fieldType = null;

			if (currentType instanceof AClassTypeIR)
			{
				AClassTypeIR classType = (AClassTypeIR) currentType;
				fieldType = typeAssistant.getFieldType(transAssistant.getInfo().getClasses(), classType.getName(), memberName);
			} else if (currentType instanceof ARecordTypeIR)
			{
				ARecordTypeIR recordType = (ARecordTypeIR) currentType;
				fieldType = transAssistant.getInfo().getTypeAssistant().getFieldType(transAssistant.getInfo().getClasses(), recordType, memberName);
			} else
			{
				// Can be the unknown type
				continue;
			}

			if (fieldType == null)
			{
				// The field type may not be found if the member does not exist
				// For example:
				//
				// types
				// R1 :: x : int;
				// R2 :: y : int;
				// ...
				// let inlines : seq of Inline = [mk_R1(4), mk_R2(5)]
				// in
				// return inlines(1).x + inlines(2).y;
				continue;
			}

			if (!typeAssistant.containsType(fieldTypes, fieldType))
			{
				fieldTypes.add(fieldType);
			}
		}

		if (fieldTypes.size() == 1)
		{
			return fieldTypes.get(0);
		} else
		{
			AUnionTypeIR unionTypes = new AUnionTypeIR();
			unionTypes.setTypes(fieldTypes);

			return unionTypes;
		}
	}
}

package org.overture.codegen.merging;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.assistant.TypeAssistantCG;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalDeclCG;
import org.overture.codegen.cgast.expressions.AEnumSeqExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.ANotEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ATupleTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.cgast.types.SBasicTypeCGBase;
import org.overture.codegen.cgast.types.SSeqTypeCGBase;
import org.overture.codegen.vdm2java.JavaCodeGen;

//TODO: Factor out Java specific generation code and put in the appropriate package
public class CG
{
	public static String format(INode field) throws AnalysisException
	{		
		MergeVisitor mergeVisitor = new MergeVisitor(JavaCodeGen.TEMPLATE_CALLABLES);
		StringWriter writer = new StringWriter();
		field.apply(mergeVisitor, writer);

		return writer.toString();
	}
	
	private static String getTupleStr(ATupleTypeCG type) throws AnalysisException
	{
		String tuple = "";
		
		switch (type.getTypes().size())
		{
			case 1:
				tuple = "Unit";
				break;
			case 2:
				tuple = "Pair";
				break;
			case 3:
				tuple = "Triplet";
				break;
			case 4:
				tuple = "Quartet";
				break;
			case 5:
				tuple = "Quintet";
				break;
			case 6:
				tuple = "Sextet";
				break;
			case 7:
				tuple = "Septet";
				break;
			case 8:
				tuple = "Octet";
				break;
			case 9:
				tuple = "Ennead";
				break;
			case 10:
				tuple = "Decade";
				break;
			default:
				throw new AnalysisException("Tuple types do not support more than 10 types!");
		}
	
		return tuple;
	}
	
	public static String formatTupleType(ATupleTypeCG type) throws AnalysisException
	{
		return getTupleStr(type) + CG.formatTemplateTypes(type.getTypes());
	}
	
	public static String formatTemplateTypes(LinkedList<PTypeCG> types) throws AnalysisException
	{
		StringWriter writer = new StringWriter();
		
		if(types.size() <= 0)
			return "";
		
		PTypeCG firstType = types.get(0);
		
		if(TypeAssistantCG.isBasicType(firstType))
			firstType = TypeAssistantCG.getWrapperType((SBasicTypeCGBase) firstType);
		
		writer.append(CG.format(firstType));
		
		for(int i = 1; i < types.size(); i++)
		{
			PTypeCG currentType = types.get(i);
			
			if(TypeAssistantCG.isBasicType(currentType))
				currentType = TypeAssistantCG.getWrapperType((SBasicTypeCGBase) currentType);
			
			writer.append(", " + CG.format(currentType));
		}
		
		return "<" + writer.toString() + ">";
	}
	
	public static String formatEqualsBinaryExp(AEqualsBinaryExpCG node) throws AnalysisException
	{
		//FIXME: Only works for simple types, i.e. not references
		//Operator pec?
		
		/*
		 * Things to consider:
		 * 
		 * Collections: sets, sequences and maps
		 * Classes: Maps to == 
		 * Type defs: Not supported anyway
		 * Records: Not supported anyway
		 * Primitive types: Maps to == 
		 * 
		 */
		PTypeCG leftNodeType = node.getLeft().getType();
		
		if(leftNodeType instanceof SSeqTypeCGBase)
		{
			//In VDM the types of the equals are compatible when the AST passes the type check
			PExpCG leftNode = node.getLeft();
			PExpCG rightNode = node.getRight();
			
			if(isEmptySeq(leftNode))
			{
				return CG.format(node.getRight()) + ".isEmpty()";
			}
			else if(isEmptySeq(rightNode))
			{
				return CG.format(node.getLeft()) + ".isEmpty()";
			}
		
			return "Utils.seqEquals(" + CG.format(node.getLeft()) + ", " + CG.format(node.getRight()) + ")";
		}
		//else if(..)
		
		return CG.format(node.getLeft()) + " == " + CG.format(node.getRight());
	}
	
	public static String formatNotEqualsBinaryExp(ANotEqualsBinaryExpCG node) throws AnalysisException
	{
		//FIXME: Same problems as for equals. In addition, this method lacks support for collections
		return CG.format(node.getLeft()) + " != " + CG.format(node.getRight());
	}
	
	private static boolean isEmptySeq(PExpCG exp)
	{
		if(exp instanceof AEnumSeqExpCG)
		{
			AEnumSeqExpCG v = (AEnumSeqExpCG) exp;

			return v.getMembers().size() == 0;
		}
		
		return false;
	}
	
	public static String format(List<AFormalParamLocalDeclCG> params) throws AnalysisException
	{
		StringWriter writer = new StringWriter();
		
		if(params.size() <= 0)
			return "";
		
		AFormalParamLocalDeclCG firstParam = params.get(0);
		writer.append(CG.format(firstParam.getType()) + " " + firstParam.getName());
		
		for(int i = 1; i < params.size(); i++)
		{
			AFormalParamLocalDeclCG param = params.get(i);
			writer.append(", " + CG.format(param.getType()) + " " + param.getName());
		}
		return writer.toString();
	}
	
	public static String formatSuperType(AClassDeclCG classDecl)
	{
		if(classDecl.getSuperName() == null)
			return "";
		else
			return "extends " + classDecl.getSuperName();
	}
	
	public static String formatArgs(List<PExpCG> exps) throws AnalysisException
	{
		StringWriter writer = new StringWriter();
		
		if(exps.size() <= 0)
			return "";
		
		PExpCG firstExp = exps.get(0);
		writer.append(CG.format(firstExp));
		
		for(int i = 1; i < exps.size(); i++)
		{
			PExpCG exp = exps.get(i);
			writer.append(", " + CG.format(exp));
		}
		
		return writer.toString();
	}
	
	public static boolean isNull(INode node)
	{
		return node == null;
	}
	
	public static boolean isVoidType(PTypeCG node)
	{
		return node instanceof AVoidTypeCG;
	}
	
	public static String formatInitialExp(PExpCG exp) throws AnalysisException
	{
		//private int a = 2; (when exp != null)
		//private int a; (when exp == null)
		
		if(exp == null)
			return "";
		else
			return " = " + CG.format(exp).toString();
		
	}
	
	public static String formatOperationBody(PStmCG body) throws AnalysisException
	{
		if(body == null)
			return ";";
		
		StringWriter generatedBody = new StringWriter();
		
		generatedBody.append("{\r\n\r\n");//TODO: USE PROPER CONSTANT
		generatedBody.append(CG.format(body));
		generatedBody.append("\r\n}");//TODO: USE PROPER CONSTANT
		
		return generatedBody.toString();
	}
	
	public static String formatTemplateParam(INode potentialBasicType) throws AnalysisException
	{
		if(potentialBasicType == null)
			return "";
		
		if(potentialBasicType instanceof AIntNumericBasicTypeCG)
			return "Integer";
		else if(potentialBasicType instanceof ARealNumericBasicTypeCG)
			return "Double";
		else if(potentialBasicType instanceof ABoolBasicTypeCG)
			return "Boolean";
		else if(potentialBasicType instanceof ACharBasicTypeCG)
			return "Character";
		else
			return CG.format(potentialBasicType);
		
		//TODO: Put in the others: What are they?
	}
}

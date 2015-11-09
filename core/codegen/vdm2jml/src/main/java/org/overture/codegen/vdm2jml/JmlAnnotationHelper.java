package org.overture.codegen.vdm2jml;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.PCG;
import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;

public class JmlAnnotationHelper
{
	private JmlGenerator jmlGen;
	
	public JmlAnnotationHelper(JmlGenerator jmlGen)
	{
		this.jmlGen = jmlGen;
	}
	
	public void makeNullableByDefault(ADefaultClassDeclCG clazz)
	{
		clazz.setGlobalMetaData(consMetaData(JmlGenerator.JML_NULLABLE_BY_DEFAULT));
	}
	
	public void makeNamedTypeInvFuncsPublic(ADefaultClassDeclCG clazz)
	{
		List<AMethodDeclCG> nameInvMethods = jmlGen.getUtil().getNamedTypeInvMethods(clazz);

		for (AMethodDeclCG method : nameInvMethods)
		{
			makeCondPublic(method);
		}
	}
	
	public void makeRecMethodsPure(List<IRStatus<INode>> ast)
	{
		List<ARecordDeclCG> records = jmlGen.getUtil().getRecords(ast);

		for (ARecordDeclCG rec : records)
		{
			for (AMethodDeclCG method : rec.getMethods())
			{
				if (!method.getIsConstructor())
				{
					makePure(method);
				}
			}
		}
	}
	
	public List<ClonableString> consAnno(String jmlAnno, String pred,
			List<String> fieldNames)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("//@ %s %s", jmlAnno, pred));

		appendFieldNames(fieldNames, sb);

		return consMetaData(sb);
	}

	private void appendFieldNames(List<String> fieldNames, StringBuilder sb) {
		
		sb.append("(");
		
		String sep = "";
		for (String fName : fieldNames)
		{
			sb.append(sep).append(fName);
			sep = ",";
		}

		sb.append(");");
	}
	
	public void addInvCheckGhostVarDecl(ADefaultClassDeclCG owner)
	{
		String metaStr = String.format(JmlGenerator.JML_INV_CHECKS_ON_DECL, JmlGenerator.INV_CHECKS_ON_GHOST_VAR_NAME);
		
		appendMetaData(owner, consMetaData(metaStr));
	}
	
	private String consInvChecksOnName(ADefaultClassDeclCG owner)
	{
		StringBuilder prefix = new StringBuilder();
		
		if(JavaCodeGenUtil.isValidJavaPackage(owner.getPackage()))
		{
			prefix.append(owner.getPackage());
			prefix.append(".");
		}
		
		prefix.append(owner.getName());
		prefix.append(".");
		prefix.append(JmlGenerator.INV_CHECKS_ON_GHOST_VAR_NAME);
		
		return prefix.toString();
	}

	public void addRecInv(ARecordDeclCG r) {

		List<String> args = jmlGen.getUtil().getRecFieldNames(r);

		String jmlAnno = "public " + JmlGenerator.JML_INSTANCE_INV_ANNOTATION;
		
		StringBuilder pred = new StringBuilder();
		pred.append(consInvChecksOnNameEncClass());
		pred.append(JmlGenerator.JML_IMPLIES);
		pred.append(JmlGenerator.INV_PREFIX);
		pred.append(r.getName());
		
		appendMetaData(r, consAnno(jmlAnno, pred.toString(), args));
	}

	public String consInvChecksOnNameEncClass()
	{
		return consInvChecksOnNameEncClass(null);
	}
	
	public String consInvChecksOnNameEncClass(ADefaultClassDeclCG enclosingClass)
	{
		if(enclosingClass != null && enclosingClass == jmlGen.getInvChecksFlagOwner())
		{
			return JmlGenerator.INV_CHECKS_ON_GHOST_VAR_NAME;
		}
		else
		{
			return consInvChecksOnName(jmlGen.getInvChecksFlagOwner());
		}
	}

	
	public void makePure(SDeclCG cond)
	{
		if (cond != null)
		{
			appendMetaData(cond, consMetaData(JmlGenerator.JML_PURE));
		}
	}
	
	public void makeHelper(SDeclCG cond)
	{
		if(cond != null)
		{
			appendMetaData(cond, consMetaData(JmlGenerator.JML_HELPER));
		}
	}
	
	public void makeCondPublic(SDeclCG cond)
	{
		if (cond instanceof AMethodDeclCG)
		{
			((AMethodDeclCG) cond).setAccess(IRConstants.PUBLIC);
		} else
		{
			Logger.getLog().printErrorln("Expected method declaration but got "
					+ cond + " in makePCondPublic");
		}
	}
	
	public void addMetaData(PCG node, List<ClonableString> extraMetaData, boolean prepend)
	{
		if (extraMetaData == null || extraMetaData.isEmpty())
		{
			return;
		}

		List<ClonableString> allMetaData = new LinkedList<ClonableString>();

		if(prepend)
		{
			allMetaData.addAll(extraMetaData);
			allMetaData.addAll(node.getMetaData());
		}
		else
		{
			allMetaData.addAll(node.getMetaData());
			allMetaData.addAll(extraMetaData);
		}

		node.setMetaData(allMetaData);
	}
	
	public void appendMetaData(PCG node, List<ClonableString> extraMetaData)
	{
		addMetaData(node, extraMetaData, false);
	}
	
	public void makeSpecPublic(AFieldDeclCG f)
	{
		appendMetaData(f, consMetaData(JmlGenerator.JML_SPEC_PUBLIC));
	}
	
	public List<ClonableString> consMetaData(StringBuilder sb)
	{
		return consMetaData(sb.toString());
	}

	public List<ClonableString> consMetaData(String str)
	{
		List<ClonableString> inv = new LinkedList<ClonableString>();

		inv.add(new ClonableString(str));

		return inv;
	}
}

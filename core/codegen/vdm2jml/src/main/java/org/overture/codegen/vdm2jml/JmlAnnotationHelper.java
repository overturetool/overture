package org.overture.codegen.vdm2jml;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.ir.PIR;
import org.overture.codegen.ir.SDeclIR;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
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
	
	public void makeNullableByDefault(ADefaultClassDeclIR clazz)
	{
		clazz.setGlobalMetaData(consMetaData(JmlGenerator.JML_NULLABLE_BY_DEFAULT));
	}
	
	public void makeNamedTypeInvFuncsPublic(ADefaultClassDeclIR clazz)
	{
		List<AMethodDeclIR> nameInvMethods = jmlGen.getUtil().getNamedTypeInvMethods(clazz);

		for (AMethodDeclIR method : nameInvMethods)
		{
			makeCondPublic(method);
		}
	}
	
	public void makeRecMethodsPure(List<IRStatus<PIR>> ast)
	{
		List<ARecordDeclIR> records = jmlGen.getUtil().getRecords(ast);

		for (ARecordDeclIR rec : records)
		{
			for (AMethodDeclIR method : rec.getMethods())
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
	
	public void addInvCheckGhostVarDecl(ADefaultClassDeclIR owner)
	{
		String metaStr = String.format(JmlGenerator.JML_INV_CHECKS_ON_DECL, JmlGenerator.INV_CHECKS_ON_GHOST_VAR_NAME);
		
		appendMetaData(owner, consMetaData(metaStr));
	}
	
	private String consInvChecksOnName(ADefaultClassDeclIR owner)
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

	public void addRecInv(ARecordDeclIR r) {

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
	
	public String consInvChecksOnNameEncClass(ADefaultClassDeclIR enclosingClass)
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

	
	public void makePure(SDeclIR cond)
	{
		if (cond != null)
		{
			appendMetaData(cond, consMetaData(JmlGenerator.JML_PURE));
		}
	}
	
	public void makeHelper(SDeclIR cond)
	{
		if(cond != null)
		{
			appendMetaData(cond, consMetaData(JmlGenerator.JML_HELPER));
		}
	}
	
	public void makeCondPublic(SDeclIR cond)
	{
		if (cond instanceof AMethodDeclIR)
		{
			((AMethodDeclIR) cond).setAccess(IRConstants.PUBLIC);
		} else
		{
			Logger.getLog().printErrorln("Expected method declaration but got "
					+ cond + " in makePCondPublic");
		}
	}
	
	public void addMetaData(PIR node, List<ClonableString> extraMetaData, boolean prepend)
	{
		this.jmlGen.getJavaGen().getInfo().getNodeAssistant().addMetaData(node, extraMetaData, prepend);
	}
	
	public void appendMetaData(PIR node, List<ClonableString> extraMetaData)
	{
		addMetaData(node, extraMetaData, false);
	}
	
	public void makeSpecPublic(AFieldDeclIR f)
	{
		appendMetaData(f, consMetaData(JmlGenerator.JML_SPEC_PUBLIC));
	}
	
	public List<ClonableString> consMetaData(StringBuilder sb)
	{
		return consMetaData(sb.toString());
	}

	public List<ClonableString> consMetaData(String str)
	{
		List<ClonableString> inv = new LinkedList<>();

		inv.add(new ClonableString(str));

		return inv;
	}
}

package org.overture.codegen.vdm2jml;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.assistant.ExpAssistantCG;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.PCG;
import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ANamedTypeDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.ATypeDeclCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.AOrBoolBinaryExpCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.logging.Logger;

public class JmlAnnotationHelper
{
	private JmlGenerator jmlGen;
	
	public JmlAnnotationHelper(JmlGenerator jmlGen)
	{
		this.jmlGen = jmlGen;
	}
	
	public void makeNamedTypeInvFuncsPublic(AClassDeclCG clazz)
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
	
	public List<ClonableString> consAnno(String jmlAnno, String name,
			List<String> fieldNames)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("//@ %s %s", jmlAnno, name));
		sb.append("(");

		String sep = "";
		for (String fName : fieldNames)
		{
			sb.append(sep).append(fName);
			sep = ",";
		}

		sb.append(");");

		return consMetaData(sb);
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
	
	public void adjustNamedTypeInvFuncs(AClassDeclCG clazz)
	{
		for (ATypeDeclCG typeDecl : clazz.getTypeDecls())
		{
			if (typeDecl.getDecl() instanceof ANamedTypeDeclCG)
			{
				ANamedTypeDeclCG namedTypeDecl = (ANamedTypeDeclCG) typeDecl.getDecl();

				AMethodDeclCG method = jmlGen.getUtil().getInvMethod(typeDecl);

				if (method == null)
				{
					continue;
				}
				
				AIdentifierVarExpCG paramExp = jmlGen.getUtil().getInvParamVar(method);
				
				if(paramExp == null)
				{
					continue;
				}

				String defModule = namedTypeDecl.getName().getDefiningClass();
				String typeName = namedTypeDecl.getName().getName();
				NamedTypeInfo findTypeInfo = NamedTypeInvDepCalculator.findTypeInfo(jmlGen.getTypeInfoList(), defModule, typeName);

				List<LeafTypeInfo> leafTypes = findTypeInfo.getLeafTypesRecursively();

				if (leafTypes.isEmpty())
				{
					Logger.getLog().printErrorln("Could not find any leaf types for named invariant type "
							+ findTypeInfo.getDefModule()
							+ "."
							+ findTypeInfo.getTypeName()
							+ " in '"
							+ this.getClass().getSimpleName() + "'");
					continue;
				}

				// The idea is to construct a dynamic type check to make sure that the parameter value
				// matches one of the leaf types, e.g.
				// Utils.is_char(n) || Utils.is_nat(n)
				SExpCG typeCond = null;
				
				ExpAssistantCG expAssist = jmlGen.getJavaGen().getInfo().getExpAssistant();

				if (leafTypes.size() == 1)
				{
					STypeCG typeCg = leafTypes.get(0).toIrType(jmlGen.getJavaGen().getInfo());

					if (typeCg == null)
					{
						continue;
					}

					typeCond = expAssist.consIsExp(paramExp, typeCg);
				} else
				{
					// There are two or more leaf types
					STypeCG typeCg = leafTypes.get(0).toIrType(jmlGen.getJavaGen().getInfo());

					if (typeCg == null)
					{
						continue;
					}

					AOrBoolBinaryExpCG topOr = new AOrBoolBinaryExpCG();
					topOr.setType(new ABoolBasicTypeCG());
					topOr.setLeft(expAssist.consIsExp(paramExp, typeCg));

					AOrBoolBinaryExpCG next = topOr;

					// Iterate all leaf types - except for the first and last ones
					for (int i = 1; i < leafTypes.size() - 1; i++)
					{
						typeCg = leafTypes.get(i).toIrType(jmlGen.getJavaGen().getInfo());

						if (typeCg == null)
						{
							continue;
						}

						AOrBoolBinaryExpCG tmp = new AOrBoolBinaryExpCG();
						tmp.setType(new ABoolBasicTypeCG());
						tmp.setLeft(expAssist.consIsExp(paramExp, typeCg));

						next.setRight(tmp);
						next = tmp;
					}

					typeCg = leafTypes.get(leafTypes.size() - 1).toIrType(jmlGen.getJavaGen().getInfo());

					if (typeCg == null)
					{
						continue;
					}

					next.setRight(expAssist.consIsExp(paramExp, typeCg));

					typeCond = topOr;
				}

				// We will negate the type check and return false if the type of the parameter
				// is not any of the leaf types, e.g
				// if (!(Utils.is_char(n) || Utils.is_nat(n))) { return false;}
				typeCond = expAssist.negate(typeCond);

				boolean nullAllowed = findTypeInfo.allowsNull();

				if (!nullAllowed)
				{
					// If 'null' is not allowed as a value we have to update the dynamic
					// type check to also take this into account too, e.g.
					// if ((Utils.equals(n, null)) || !(Utils.is_char(n) || Utils.is_nat(n))) { return false;}
					AEqualsBinaryExpCG notNull = new AEqualsBinaryExpCG();
					notNull.setType(new ABoolBasicTypeCG());
					notNull.setLeft(paramExp.clone());
					notNull.setRight(jmlGen.getJavaGen().getTransformationAssistant().consNullExp());

					AOrBoolBinaryExpCG nullCheckOr = new AOrBoolBinaryExpCG();
					nullCheckOr.setType(new ABoolBasicTypeCG());
					nullCheckOr.setLeft(notNull);
					nullCheckOr.setRight(typeCond);

					typeCond = nullCheckOr;
				}

				AReturnStmCG returnFalse = new AReturnStmCG();
				returnFalse.setExp(jmlGen.getJavaGen().getInfo().getExpAssistant().consBoolLiteral(false));

				AIfStmCG dynTypeCheck = new AIfStmCG();
				dynTypeCheck.setIfExp(typeCond);
				dynTypeCheck.setThenStm(returnFalse);

				SStmCG body = method.getBody();

				ABlockStmCG repBlock = new ABlockStmCG();
				jmlGen.getJavaGen().getTransformationAssistant().replaceNodeWith(body, repBlock);

				repBlock.getStatements().add(dynTypeCheck);
				repBlock.getStatements().add(body);
			}
		}
	}
}

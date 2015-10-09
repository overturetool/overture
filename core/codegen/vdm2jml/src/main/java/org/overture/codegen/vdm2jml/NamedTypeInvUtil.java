package org.overture.codegen.vdm2jml;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.ANamedTypeDeclCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;
import org.overture.codegen.cgast.types.AMapMapTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.ASeqSeqTypeCG;
import org.overture.codegen.cgast.types.ASetSetTypeCG;
import org.overture.codegen.cgast.types.ATupleTypeCG;
import org.overture.codegen.cgast.types.AUnionTypeCG;
import org.overture.codegen.cgast.types.AUnknownTypeCG;
import org.overture.codegen.logging.Logger;

public class NamedTypeInvUtil
{
	private NamedTypeInvHandler handler;
	
	public NamedTypeInvUtil(NamedTypeInvHandler handler)
	{
		this.handler = handler;
	}
	
	public List<String> consJmlCheck(String enclosingClass, String jmlVisibility,
			String annotationType, boolean invChecksGuard, List<AbstractTypeInfo> typeInfoMatches,
			SVarExpCG var)
	{
		List<String> predStrs = new LinkedList<>();
		
		if(handler.getInvAssertTrans().buildRecValidChecks())
		{
			appendRecValidChecks(invChecksGuard, typeInfoMatches, var, predStrs);
		}
		
		StringBuilder inv = new StringBuilder();
		inv.append("//@ ");

		if (jmlVisibility != null)
		{
			inv.append(jmlVisibility);
			inv.append(' ');
		}

		inv.append(annotationType);
		inv.append(' ');
		
		if(invChecksGuard)
		{
			inv.append(consInvChecksGuard());
			inv.append('(');
		}

		String or = "";
		for (AbstractTypeInfo match : typeInfoMatches)
		{
			String javaPackage = handler.getJmlGen().getJavaSettings().getJavaRootPackage();
			String consCheckExp = match.consCheckExp(enclosingClass, javaPackage);
			inv.append(or);
			if(consCheckExp != null)
			{
				inv.append(consCheckExp);
			}
			else
			{
				Logger.getLog().printErrorln("Expression could not be checked in '"
						+ this.getClass().getSimpleName() + "'");
				//TODO: Consider better handling
				inv.append("true");
			}
			or = JmlGenerator.JML_OR;
		}

		if(invChecksGuard)
		{
			inv.append(')');
		}
		
		inv.append(';');
		
		// Inject the name of the field into the expression
		predStrs.add(String.format(inv.toString(), var.getName()));
		
		return predStrs;
	}

	private String consInvChecksGuard()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(handler.getJmlGen().getAnnotator().consInvChecksOnName(handler.getJmlGen().getInvChecksFlagOwner()));
		sb.append(JmlGenerator.JML_IMPLIES);
		
		return sb.toString();
	}

	private void appendRecValidChecks(boolean invChecksGuard,
			List<AbstractTypeInfo> typeInfoMatches, SVarExpCG var,
			List<String> predStrs)
	{
		List<ARecordTypeCG> recordTypes = getRecTypes(typeInfoMatches);

		if (!recordTypes.isEmpty())
		{
			for (ARecordTypeCG rt : recordTypes)
			{
				StringBuilder inv = new StringBuilder();
				String fullyQualifiedRecType = fullyQualifiedRecType(rt);

				inv.append("//@ ");
				inv.append(JmlGenerator.JML_ASSERT_ANNOTATION);
				inv.append(' ');

				if (invChecksGuard)
				{
					inv.append(consInvChecksGuard());
				}

				if (var.getType() instanceof ARecordTypeCG)
				{
					inv.append(var.getName());
					inv.append('.');
					inv.append(JmlGenerator.REC_VALID_METHOD_CALL);
					// e.g. r1.valid()
				} else
				{
					inv.append(var.getName());
					inv.append(" instanceof ");
					inv.append(fullyQualifiedRecType);
					inv.append(JmlGenerator.JML_IMPLIES);
					inv.append("((" + fullyQualifiedRecType + ") " + var.getName() + ").");
					inv.append(JmlGenerator.REC_VALID_METHOD_CALL);
					// e.g. r1 instanceof project.Entrytypes.R3 ==> ((project.Entrytypes.R3) r1).valid()
				}

				inv.append(';');
				predStrs.add(inv.toString());
			}
		}
	}

	public String fullyQualifiedRecType(ARecordTypeCG rt)
	{
		String defClass = rt.getName().getDefiningClass();
		String recPackage = JmlGenUtil.consRecPackage(defClass, handler.getJmlGen().getJavaGen().getJavaSettings().getJavaRootPackage());
		String fullyQualifiedRecType = recPackage + "."
				+ rt.getName().getName();
		return fullyQualifiedRecType;
	}
	
	private List<ARecordTypeCG> getRecTypes(
			List<AbstractTypeInfo> typeInfoMatches)
	{
		List<ARecordTypeCG> recTypes = new LinkedList<>();
		
		for (AbstractTypeInfo match : typeInfoMatches)
		{
			List<LeafTypeInfo> leaves = match.getLeafTypesRecursively();

			for (LeafTypeInfo leaf : leaves)
			{
				if (leaf.getType() instanceof ARecordTypeCG)
				{
					recTypes.add((ARecordTypeCG) leaf.getType());
				}
			}
		}

		return recTypes;
	}

	public List<AMetaStmCG> consAssertStm(List<AbstractTypeInfo> invTypes,
			String encClassName, SVarExpCG var, INode node, RecClassInfo recInfo)
	{
		boolean inAccessor = node != null && recInfo != null && recInfo.inAccessor(node);

		List<AMetaStmCG> asserts = new LinkedList<>();
		List<String> assertStrs = consJmlCheck(encClassName, null, JmlGenerator.JML_ASSERT_ANNOTATION, inAccessor, invTypes, var);
		
		for(String a : assertStrs)
		{
			AMetaStmCG assertStm = new AMetaStmCG();
			List<ClonableString> assertMetaData = handler.getJmlGen().getAnnotator().consMetaData(a);
			handler.getJmlGen().getAnnotator().appendMetaData(assertStm, assertMetaData);
			asserts.add(assertStm);
		}

		return asserts;
	}
	
	public AMetaStmCG consVarNotNullAssert(String varName)
	{
		AMetaStmCG assertStm = new AMetaStmCG();
		List<ClonableString> assertMetaData = handler.getJmlGen().getAnnotator().consMetaData("//@ assert " + varName + " != null;");
		handler.getJmlGen().getAnnotator().appendMetaData(assertStm, assertMetaData);

		return assertStm;
	}
	
//	public List<ClonableString> consValNotNullInvariant(String fieldName)
//	{
//		return handler.getJmlGen().getAnnotator().consMetaData("//@ static invariant " + fieldName + " != null;");
//	}
	

	public List<AbstractTypeInfo> findTypeInfo(STypeCG type)
	{
		List<AbstractTypeInfo> posTypes = new LinkedList<>();

		if (type.getNamedInvType() != null)
		{
			ANamedTypeDeclCG namedInv = type.getNamedInvType();

			String defModule = namedInv.getName().getDefiningClass();
			String typeName = namedInv.getName().getName();

			NamedTypeInfo info = NamedTypeInvDepCalculator.findTypeInfo(handler.getJmlGen().getTypeInfoList(), defModule, typeName);

			if (info != null)
			{
				posTypes.add(info);
			} else
			{
				Logger.getLog().printErrorln("Could not find info for named type '"
						+ typeName + "' defined in module '" + defModule
						+ "' in '" + this.getClass().getSimpleName() + "'");
			}

			// We do not need to collect sub named invariant types
		} else if (type instanceof AUnionTypeCG)
		{
			for (STypeCG t : ((AUnionTypeCG) type).getTypes())
			{
				posTypes.addAll(findTypeInfo(t));
			}
		}
		else if(type instanceof AUnknownTypeCG)
		{
			return posTypes;
		}
		else if(type instanceof ASetSetTypeCG || type instanceof AMapMapTypeCG || type instanceof ASeqSeqTypeCG || type instanceof ATupleTypeCG)
		{
			// Can't do anything for these right now...
			// TODO: implement handling
			return posTypes;
		}
		else
		{
			posTypes.add(new LeafTypeInfo(type, handler.getJmlGen().getJavaGen().getInfo().getTypeAssistant().allowsNull(type)));
		}

		// We will only consider types that are disjoint. As an example consider
		// the type definitions below:
		//
		// C = ...; N = ...; CN = C|N;
		//
		// Say we have the following value definition:
		//
		// val : CN|N
		//
		// Then we only want to have the type info for CN returned since N is already
		// contained in CN.
		return NamedTypeInvDepCalculator.onlyDisjointTypes(posTypes);
	}
}

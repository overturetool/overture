package org.overture.codegen.vdm2jml;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.PType;
import org.overture.ast.util.ClonableString;
import org.overture.ast.util.PTypeSet;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.ANamedTypeDeclCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.AUnionTypeCG;
import org.overture.codegen.logging.Logger;

public class NamedTypeInvUtil
{
	private NamedTypeInvHandler handler;
	
	public NamedTypeInvUtil(NamedTypeInvHandler handler)
	{
		this.handler = handler;
	}
	
	public String consJmlCheck(String enclosingClass, String jmlVisibility,
			String annotationType, boolean invChecksGuard, List<NamedTypeInfo> typeInfoMatches,
			SVarExpCG var)
	{
		StringBuilder inv = new StringBuilder();
		
		appendRecValidChecks(invChecksGuard, typeInfoMatches, var, inv);
		
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
		for (NamedTypeInfo match : typeInfoMatches)
		{
			inv.append(or);
			inv.append(match.consCheckExp(enclosingClass, handler.getJmlGen().getJavaSettings().getJavaRootPackage()));
			or = JmlGenerator.JML_OR;
		}

		if(invChecksGuard)
		{
			inv.append(')');
		}
		
		inv.append(';');
		
		// Inject the name of the field into the expression
		return String.format(inv.toString(), var.getName());
	}

	private String consInvChecksGuard()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(handler.getJmlGen().getAnnotator().consInvChecksOnName(handler.getJmlGen().getInvChecksFlagOwner()));
		sb.append(JmlGenerator.JML_IMPLIES);
		
		return sb.toString();
	}

	private void appendRecValidChecks(boolean invChecksGuard,
			List<NamedTypeInfo> typeInfoMatches, SVarExpCG var,
			StringBuilder inv)
	{
		List<ARecordTypeCG> recordTypes = getRecTypes(typeInfoMatches);

		if (!recordTypes.isEmpty())
		{
			for (ARecordTypeCG rt : recordTypes)
			{
				String defClass = rt.getName().getDefiningClass();
				String recPackage = handler.getJmlGen().getUtil().consRecPackage(defClass);
				String fullyQualifiedRecType = recPackage + "."
						+ rt.getName().getName();

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
				inv.append('\n');
			}
		}
	}
	
	private List<ARecordTypeCG> getRecTypes(
			List<NamedTypeInfo> typeInfoMatches)
	{
		PTypeSet typeSet = new PTypeSet(handler.getJmlGen().getJavaGen().getInfo().getTcFactory());

		for (NamedTypeInfo match : typeInfoMatches)
		{
			List<LeafTypeInfo> leaves = match.getLeafTypesRecursively();

			for (LeafTypeInfo leaf : leaves)
			{
				if (leaf.getType() instanceof ARecordInvariantType)
				{
					typeSet.add(leaf.getType());
				}
			}
		}

		List<ARecordTypeCG> recTypes = new LinkedList<>();

		for (PType type : typeSet)
		{
			STypeCG irType = LeafTypeInfo.toIrType(type, handler.getJmlGen().getJavaGen().getInfo());

			if (irType instanceof ARecordTypeCG)
			{
				recTypes.add((ARecordTypeCG) irType);
			} else
			{
				Logger.getLog().printErrorln("Expected " + type
						+ " to convert to a "
						+ ARecordTypeCG.class.getSimpleName() + " in '"
						+ this.getClass().getSimpleName() + "'");
			}
		}

		return recTypes;
	}

	public AMetaStmCG consAssertStm(List<NamedTypeInfo> invTypes,
			String encClassName, SVarExpCG var, INode node, RecClassInfo recInfo)
	{
		boolean inAccessor = node != null && recInfo != null && recInfo.inAccessor(node);
		
		
		AMetaStmCG assertStm = new AMetaStmCG();
		String assertStr = consJmlCheck(encClassName, null, JmlGenerator.JML_ASSERT_ANNOTATION, inAccessor, invTypes, var);
		List<ClonableString> assertMetaData = handler.getJmlGen().getAnnotator().consMetaData(assertStr);
		handler.getJmlGen().getAnnotator().appendMetaData(assertStm, assertMetaData);

		return assertStm;
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
	

	public List<NamedTypeInfo> findNamedInvTypes(STypeCG type)
	{
		List<NamedTypeInfo> posTypes = new LinkedList<NamedTypeInfo>();

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
				posTypes.addAll(findNamedInvTypes(t));
			}
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

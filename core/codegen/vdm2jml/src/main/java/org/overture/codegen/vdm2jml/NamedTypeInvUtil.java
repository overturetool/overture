package org.overture.codegen.vdm2jml;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.ANamedTypeDeclCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;
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
			String varName)
	{
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
			inv.append(handler.getJmlGen().getAnnotator().consInvChecksOnName(handler.getJmlGen().getInvChecksFlagOwner()));
			inv.append(JmlGenerator.JML_IMPLIES);
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
		return String.format(inv.toString(), varName);
	}
	
	public AMetaStmCG consAssertStm(List<NamedTypeInfo> invTypes,
			String enclosingClassName, String varNameStr, INode node, RecClassInfo recInfo)
	{
		boolean inAccessor = node != null && recInfo != null && recInfo.inAccessor(node);
		
		
		AMetaStmCG assertStm = new AMetaStmCG();
		String assertStr = consJmlCheck(enclosingClassName, null, JmlGenerator.JML_ASSERT_ANNOTATION, inAccessor, invTypes, varNameStr);
		List<ClonableString> assertMetaData = handler.getJmlGen().getAnnotator().consMetaData(assertStr);
		handler.getJmlGen().getAnnotator().appendMetaData(assertStm, assertMetaData);

		return assertStm;
	}

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

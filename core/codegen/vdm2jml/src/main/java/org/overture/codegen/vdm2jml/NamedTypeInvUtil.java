package org.overture.codegen.vdm2jml;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.ANamedTypeDeclCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
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
			String annotationType, List<NamedTypeInfo> typeInfoMatches,
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

		String or = "";
		for (NamedTypeInfo match : typeInfoMatches)
		{
			inv.append(or);
			inv.append(match.consCheckExp(enclosingClass, handler.getJmlGen().getJavaSettings().getJavaRootPackage()));
			or = JmlGenerator.JML_OR;
		}

		inv.append(';');

		// Inject the name of the field into the expression
		return String.format(inv.toString(), varName);
	}

	public void injectAssertion(SStmCG node, List<NamedTypeInfo> invTypes,
			String enclosingClassName, String varNameStr, boolean append)
	{
		AMetaStmCG assertStm = consAssertStm(invTypes, enclosingClassName, varNameStr);

		ABlockStmCG replStm = new ABlockStmCG();

		handler.getJmlGen().getJavaGen().getTransAssistant().replaceNodeWith(node, replStm);

		replStm.getStatements().add(node);

		if (append)
		{
			replStm.getStatements().add(assertStm);
		} else
		{
			replStm.getStatements().addFirst(assertStm);
		}
	}

	public String consJmlCheck(String enclosingClass, String annotationType,
			List<NamedTypeInfo> typeInfoMatches, String varName)
	{
		return consJmlCheck(enclosingClass, null, annotationType, typeInfoMatches, varName);
	}

	public AMetaStmCG consAssertStm(List<NamedTypeInfo> invTypes,
			String enclosingClassName, String varNameStr)
	{
		AMetaStmCG assertStm = new AMetaStmCG();
		String assertStr = consJmlCheck(enclosingClassName, JmlGenerator.JML_ASSERT_ANNOTATION, invTypes, varNameStr);
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

	public String consVarName(AAssignToExpStmCG node)
	{
		// Must be field or variable expression
		SExpCG next = node.getTarget();

		if (next instanceof AFieldExpCG)
		{
			if (((AFieldExpCG) next).getObject().getType() instanceof ARecordTypeCG)
			{
				// rec.field = ...
				// No need to take record modifications into account. The invariant
				// should handle this (if it is needed).
				return null;
			}
		}

		List<String> names = new LinkedList<String>();

		// Consider the field a.b.c
		while (next instanceof AFieldExpCG)
		{
			AFieldExpCG fieldExpCG = (AFieldExpCG) next;

			// First 'c' will be visited, then 'b' and then 'a'.
			names.add(fieldExpCG.getMemberName());
			next = fieldExpCG.getObject();
		}

		if (next instanceof SVarExpCG)
		{
			SVarExpCG var = (SVarExpCG) next;
			names.add(var.getName());
		} else
		{
			Logger.getLog().printErrorln("Expected target to a variable expression at this point. Got "
					+ next + " in '" + this.getClass().getSimpleName() + "'");
			return null;
		}

		// By reversing the list we get the original order, i.e. 'a' then 'b' then 'c'
		Collections.reverse(names);

		if (names.isEmpty())
		{
			Logger.getLog().printErrorln("Expected the naming list not to be empty in '"
					+ this.getClass().getSimpleName() + "'");
			return null;
		}

		StringBuilder varName = new StringBuilder();

		varName.append(names.get(0));

		// Iterate over all names - except for the first
		for (int i = 1; i < names.size(); i++)
		{
			varName.append(".");
			varName.append(names.get(i));
		}
		// So for our example varName will be a.b.c

		return varName.toString();
	}
}

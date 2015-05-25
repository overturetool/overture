package org.overture.codegen.vdm2jml;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ANamedTypeDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.AMapSeqUpdateStmCG;
import org.overture.codegen.cgast.types.AUnionTypeCG;
import org.overture.codegen.logging.Logger;

// TODO: remember to take optional types into account
public class NamedTypeInvariantTransformation extends DepthFirstAnalysisAdaptor
{
	private JmlGenerator jmlGen;
	
	public NamedTypeInvariantTransformation(JmlGenerator jmlGen)
	{
		this.jmlGen = jmlGen;
	}

	public String consJmlCheck(String jmlVisibility, String annotationType,
			List<NamedTypeInfo> typeInfoMatches, String varName)
	{
		StringBuilder inv = new StringBuilder();
		inv.append("//@ ");
		inv.append(jmlVisibility);
		inv.append(' ');
		inv.append(annotationType);
		inv.append(' ');

		String or = "";
		for (NamedTypeInfo match : typeInfoMatches)
		{
			inv.append(or);
			inv.append(match.consCheckExp());
			or = JmlGenerator.JML_OR;
		}

		inv.append(';');

		// Inject the name of the field into the expression
		return String.format(inv.toString(), varName);
	}

	public List<NamedTypeInfo> findNamedInvTypes(STypeCG type)
	{
		List<NamedTypeInfo> posTypes = new LinkedList<NamedTypeInfo>();

		if (type.getNamedInvType() != null)
		{
			ANamedTypeDeclCG namedInv = type.getNamedInvType();

			String defModule = namedInv.getName().getDefiningClass();
			String typeName = namedInv.getName().getName();

			NamedTypeInfo info = NamedTypeInvDepCalculator.findTypeInfo(jmlGen.getTypeInfoList(), defModule, typeName);

			if (info != null)
			{
				posTypes.add(info);
			} else
			{
				Logger.getLog().printErrorln("Could not find info for named type '"
						+ typeName
						+ "' defined in module '"
						+ defModule
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

	@Override
	public void caseAFieldDeclCG(AFieldDeclCG node) throws AnalysisException
	{
		// Examples:
		// val : char | Even = 5;
		// stateField : char | Even;

		List<NamedTypeInfo> invTypes = findNamedInvTypes(node.getType());

		if (invTypes.isEmpty())
		{
			return;
		}

		// Because we code generate VDM-SL all fields will be static
		String inv = consJmlCheck(JmlGenerator.JML_PUBLIC, JmlGenerator.JML_STATIC_INV_ANNOTATION, invTypes, node.getName());

		jmlGen.getAnnotator().appendMetaData(node, jmlGen.getAnnotator().consMetaData(inv));
	}

	@Override
	public void caseAVarDeclCG(AVarDeclCG node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseAVarDeclCG(node);
	}

	@Override
	public void caseAAssignToExpStmCG(AAssignToExpStmCG node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseAAssignToExpStmCG(node);
	}

	@Override
	public void caseAMapSeqUpdateStmCG(AMapSeqUpdateStmCG node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		super.caseAMapSeqUpdateStmCG(node);
	}

	@Override
	public void caseAMethodDeclCG(AMethodDeclCG node) throws AnalysisException
	{
		// Upon entering the method, assert that the parameters are valid
		// wrt. their named invariant types

		// TODO Auto-generated method stub
		super.caseAMethodDeclCG(node);
	}

}

package org.overture.codegen.vdm2jml;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ANamedTypeDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AMapSeqUpdateStmCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;
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

	public String consJmlCheck(String enclosingClass, String annotationType,
			List<NamedTypeInfo> typeInfoMatches, String varName)
	{
		return consJmlCheck(enclosingClass, null, annotationType, typeInfoMatches, varName);
	}
	
	@Override
	public void caseAClassDeclCG(AClassDeclCG node) throws AnalysisException
	{
		// We want only to treat fields and methods specified by the user.
		// This case helps us avoiding visiting invariant methods
		
		for(AFieldDeclCG f : node.getFields())
		{
			f.apply(this);
		}
		
		for(AMethodDeclCG m : node.getMethods())
		{
			m.apply(this);
		}
	}
	
	public String consJmlCheck(String enclosingClass, String jmlVisibility, String annotationType,
			List<NamedTypeInfo> typeInfoMatches, String varName)
	{
		StringBuilder inv = new StringBuilder();
		inv.append("//@ ");
		
		if(jmlVisibility != null)
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
			inv.append(match.consCheckExp(enclosingClass, jmlGen.getJavaSettings().getJavaRootPackage()));
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

		AClassDeclCG enclosingClass = jmlGen.getUtil().getEnclosingClass(node);
		
		if(enclosingClass == null)
		{
			return;
		}
		
		// Because we code generate VDM-SL all fields will be static
		String inv = consJmlCheck(enclosingClass.getName(), JmlGenerator.JML_PUBLIC, JmlGenerator.JML_STATIC_INV_ANNOTATION, invTypes, node.getName());

		jmlGen.getAnnotator().appendMetaData(node, jmlGen.getAnnotator().consMetaData(inv));
	}

	@Override
	public void caseAVarDeclCG(AVarDeclCG node) throws AnalysisException
	{
		// Examples:
		// let x : Even = 1 in ...
		// (dcl y : Even | nat := 2; ...)

		List<NamedTypeInfo> invTypes = findNamedInvTypes(node.getType());

		if (invTypes.isEmpty())
		{
			return;
		}

		String name = jmlGen.getUtil().getName(node.getPattern());

		if (name == null)
		{
			return;
		}

		if (node.parent() instanceof ABlockStmCG)
		{
			ABlockStmCG parentBlock = (ABlockStmCG) node.parent();

			if (!parentBlock.getLocalDefs().contains(node))
			{
				Logger.getLog().printErrorln("Expected local variable declaration to be "
						+ "one of the local variable declarations of "
						+ "the parent statement block in '"
						+ this.getClass().getSimpleName() + "'");
				return;
			}

			if (parentBlock.getLocalDefs().size() > 1)
			{
				ABlockStmCG declBlock = new ABlockStmCG();
				while (parentBlock.getLocalDefs().getLast() != node)
				{
					declBlock.getLocalDefs().addFirst(parentBlock.getLocalDefs().getLast());
				}
				parentBlock.getStatements().addFirst(declBlock);
			}

			AMetaStmCG assertInv = new AMetaStmCG();

			AClassDeclCG enclosingClass = node.getAncestor(AClassDeclCG.class);
			
			if(enclosingClass == null)
			{
				return;
			}
			
			String inv = consJmlCheck(enclosingClass.getName(), JmlGenerator.JML_ASSERT_ANNOTATION, invTypes, name);
			
			jmlGen.getAnnotator().appendMetaData(assertInv, jmlGen.getAnnotator().consMetaData(inv));
			
			parentBlock.getStatements().addFirst(assertInv);
		} else
		{
			Logger.getLog().printErrorln("Expected parent of local variable "
					+ "declaration to be a statement block. Got: "
					+ node.parent() + " in '" + this.getClass().getSimpleName()
					+ "'");
		}
	}

	@Override
	public void caseAAssignToExpStmCG(AAssignToExpStmCG node)
			throws AnalysisException
	{
		List<NamedTypeInfo> invTypes = findNamedInvTypes(node.getTarget().getType());

		if (invTypes.isEmpty())
		{
			return;
		}
		
		// Must be field or variable expression
		SExpCG next = node.getTarget();
		
		List<String> names = new LinkedList<String>();
		
		// Consider the field a.b.c
		while(next instanceof AFieldExpCG)
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
			return;
		}
		
		// By reversing the list we get the original order, i.e.  'a' then 'b' then 'c'
		Collections.reverse(names);

		if (names.isEmpty())
		{
			Logger.getLog().printErrorln("Expected the naming list not to be empty in '"
					+ this.getClass().getSimpleName() + "'");
			return;
		}
		
		StringBuilder varName = new StringBuilder();
		
		varName.append(names.get(0));
		
		// Iterate over all names - except for the first
		for(int i = 1; i < names.size(); i++)
		{
			varName.append(".");
			varName.append(names.get(i));
		}
		// So for our example varName wil be a.b.c
		
		AClassDeclCG enclosingClass = node.getAncestor(AClassDeclCG.class);
		
		if(enclosingClass == null)
		{
			return;
		}
		
		String assertStr = consJmlCheck(enclosingClass.getName(), JmlGenerator.JML_ASSERT_ANNOTATION, invTypes, varName.toString());
		List<ClonableString> assertMetaData = jmlGen.getAnnotator().consMetaData(assertStr);
		AMetaStmCG assertStm = new AMetaStmCG();
		jmlGen.getAnnotator().appendMetaData(assertStm, assertMetaData);
		
		ABlockStmCG replStm = new ABlockStmCG();
		
		jmlGen.getJavaGen().getTransformationAssistant().replaceNodeWith(node, replStm);
		
		replStm.getStatements().add(node);
		replStm.getStatements().add(assertStm);
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

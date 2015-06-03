package org.overture.codegen.vdm2jml;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.assistant.DeclAssistantCG;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ANamedTypeDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AMapSeqUpdateStmCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.AUnionTypeCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.assistants.TransAssistantCG;

// TODO: remember to take optional types into account
public class NamedTypeInvariantTransformation extends DepthFirstAnalysisAdaptor
{
	public static final String RET_VAR_NAME_PREFIX = "ret_";
	public static final String MAP_SEQ_NAME_PREFIX = "col_";
	
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

	private void injectAssertion(SStmCG node,
			List<NamedTypeInfo> invTypes, String enclosingClassName,
			String varNameStr, boolean append)
	{
		AMetaStmCG assertStm = consAssertStm(invTypes, enclosingClassName, varNameStr);
		
		ABlockStmCG replStm = new ABlockStmCG();
		
		jmlGen.getJavaGen().getTransformationAssistant().replaceNodeWith(node, replStm);
		
		replStm.getStatements().add(node);
		
		if(append)
		{
			replStm.getStatements().add(assertStm);
		}
		else 
		{
			replStm.getStatements().addFirst(assertStm);
		}
	}

	private AMetaStmCG consAssertStm(List<NamedTypeInfo> invTypes,
			String enclosingClassName, String varNameStr)
	{
		AMetaStmCG assertStm = new AMetaStmCG();
		String assertStr = consJmlCheck(enclosingClassName, JmlGenerator.JML_ASSERT_ANNOTATION, invTypes, varNameStr);
		List<ClonableString> assertMetaData = jmlGen.getAnnotator().consMetaData(assertStr);
		jmlGen.getAnnotator().appendMetaData(assertStm, assertMetaData);
		
		return assertStm;
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
		
		// In classes that originate from VDM-SL modules the state field
		// and the values are static. However record fields are not.
		String scope = node.getStatic() ? JmlGenerator.JML_STATIC_INV_ANNOTATION
				: JmlGenerator.JML_INSTANCE_INV_ANNOTATION;
		
		String inv = consJmlCheck(enclosingClass.getName(), JmlGenerator.JML_PUBLIC, scope, invTypes, node.getName());

		jmlGen.getAnnotator().appendMetaData(node, jmlGen.getAnnotator().consMetaData(inv));
	}
	
	@Override
	public void caseABlockStmCG(ABlockStmCG node) throws AnalysisException
	{
		if (node.getLocalDefs().size() > 1)
		{
			LinkedList<AVarDeclCG> origDecls = new LinkedList<AVarDeclCG>(node.getLocalDefs());
			
			for(int i = origDecls.size() - 1; i >= 0; i--)
			{
				AVarDeclCG nextDecl = origDecls.get(i);
				
				ABlockStmCG block = new ABlockStmCG();
				block.getLocalDefs().add(nextDecl);
				
				node.getStatements().addFirst(block);
			}
			
			for(SStmCG stm : node.getStatements())
			{
				stm.apply(this);
			}
			
		} else
		{
			for(AVarDeclCG dec : node.getLocalDefs())
			{
				dec.apply(this);
			}
			
			for (SStmCG stm : node.getStatements())
			{
				stm.apply(this);
			}
		}
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
				// The block statement case method should have ensured that the size == 1
				Logger.getLog().printErrorln("Expected only a single local declaration in "
						+ "the parent block at this point in '" + this.getClass().getSimpleName() + "'");
				return;
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
		// Must be field or variable expression
		SExpCG next = node.getTarget();
		
		if(next instanceof AFieldExpCG)
		{
			if(((AFieldExpCG) next).getObject().getType() instanceof ARecordTypeCG)
			{
				// rec.field = ...
				// No need to take record modifications into account. The invariant
				// should handle this (if it is needed).
				return;
			}
		}
		
		List<NamedTypeInfo> invTypes = findNamedInvTypes(node.getTarget().getType());

		if (invTypes.isEmpty())
		{
			return;
		}
		
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
		
		AClassDeclCG enclosingClass = jmlGen.getUtil().getEnclosingClass(node);
		
		if(enclosingClass == null)
		{
			return;
		}
		
		String enclosingClassName = enclosingClass.getName();
		String varNameStr = varName.toString();
		
		injectAssertion(node, invTypes, enclosingClassName, varNameStr, true);
	}

	@Override
	public void caseAMapSeqUpdateStmCG(AMapSeqUpdateStmCG node)
			throws AnalysisException
	{
		SExpCG col = node.getCol();

		List<NamedTypeInfo> invTypes = findNamedInvTypes(col.getType());

		if (!invTypes.isEmpty())
		{
			AClassDeclCG enclosingClass = jmlGen.getUtil().getEnclosingClass(node);

			if (enclosingClass == null)
			{
				return;
			}

			String name;
			if (col instanceof SVarExpCG)
			{
				name = ((SVarExpCG) col).getName();
			} else
			{
				// Should never into this case. The collection must be a variable expression
				// else it could not be updated.
				Logger.getLog().printErrorln("Expected collection to be a variable expression at this point. Got: "
						+ col + " in '" + this.getClass().getSimpleName() + "'");

				TransAssistantCG trans = jmlGen.getJavaGen().getTransformationAssistant();
				DeclAssistantCG declAs = jmlGen.getJavaGen().getInfo().getDeclAssistant();

				STypeCG type = col.getType();

				ITempVarGen nameGen = jmlGen.getJavaGen().getInfo().getTempVarNameGen();
				name = nameGen.nextVarName(MAP_SEQ_NAME_PREFIX);
				AIdentifierPatternCG id = trans.consIdPattern(name);

				AVarDeclCG varDecl = declAs.consLocalVarDecl(type, id, col.clone());
				AIdentifierVarExpCG varExp = trans.consIdentifierVar(name, type.clone());

				trans.replaceNodeWith(col, varExp);

				ABlockStmCG replBlock = new ABlockStmCG();

				trans.replaceNodeWith(node, replBlock);
				replBlock.getLocalDefs().add(varDecl);
				replBlock.getStatements().add(node);
			}

			injectAssertion(node, invTypes, enclosingClass.getName(), name, true);
		}
	}

	@Override
	public void caseAMethodDeclCG(AMethodDeclCG node) throws AnalysisException
	{
		// Upon entering the method, assert that the parameters are valid
		// wrt. their named invariant types
		
		// This transformation also makes sure that the return statement is
		// properly handled for cases where the method returns a value of a
		// named invariant type. In particular this transformation ensures
		// that such values are extracted to local variable declarations
		// and then the assertion check is performed on those.
		
		ABlockStmCG replBody = new ABlockStmCG();
		for(AFormalParamLocalParamCG param : node.getFormalParams())
		{
			List<NamedTypeInfo> invTypes = findNamedInvTypes(param.getType());

			if (!invTypes.isEmpty())
			{
				AClassDeclCG encClass = jmlGen.getUtil().getEnclosingClass(node);
				
				if(encClass == null)
				{
					continue;
				}
				
				String enclosingClassName = encClass.getName();
				
				String varNameStr = jmlGen.getUtil().getName(param.getPattern());
				
				if(varNameStr == null)
				{
					continue;
				}
				
				replBody.getStatements().add(consAssertStm(invTypes, enclosingClassName, varNameStr));
			}
		}
		
		SStmCG body = node.getBody();
		jmlGen.getJavaGen().getTransformationAssistant().replaceNodeWith(body, replBody);
		replBody.getStatements().add(body);
		body.apply(this);
	}
	
	@Override
	public void caseAReturnStmCG(AReturnStmCG node) throws AnalysisException
	{
		SExpCG exp = node.getExp();
		
		if(exp instanceof SVarExpCG)
		{
			return;
		}
		
		ITempVarGen nameGen = jmlGen.getJavaGen().getInfo().getTempVarNameGen();
		String name = nameGen.nextVarName(RET_VAR_NAME_PREFIX);
		TransAssistantCG trans = jmlGen.getJavaGen().getTransformationAssistant();
		
		AMethodDeclCG enclosingMethod = jmlGen.getUtil().getEnclosingMethod(node);
		
		if(enclosingMethod == null)
		{
			return;
		}
		
		STypeCG returnType = enclosingMethod.getMethodType().getResult();
		
		List<NamedTypeInfo> invTypes = findNamedInvTypes(returnType);

		if (invTypes.isEmpty())
		{
			return;
		}
		
		AIdentifierPatternCG id = trans.consIdPattern(name);
		
		AVarDeclCG var = jmlGen.getJavaGen().getInfo().getDeclAssistant().consLocalVarDecl(returnType.clone(), id, exp.clone());

		AIdentifierVarExpCG varExp = trans.consIdentifierVar(name, returnType.clone());
		
		trans.replaceNodeWith(exp, varExp);
		
		ABlockStmCG replBlock = new ABlockStmCG();
		replBlock.getLocalDefs().add(var);
		
		trans.replaceNodeWith(node, replBlock);
		
		replBlock.getStatements().add(node);
		var.apply(this);
	}
}

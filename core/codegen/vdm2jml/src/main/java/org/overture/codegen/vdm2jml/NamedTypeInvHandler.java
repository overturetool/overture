package org.overture.codegen.vdm2jml;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.AMapSeqUpdateStmCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class NamedTypeInvHandler implements IAssert
{
	public static final String RET_VAR_NAME_PREFIX = "ret_";
	public static final String MAP_SEQ_NAME_PREFIX = "col_";

	private InvAssertionTrans invTrans;
	private NamedTypeInvUtil util;
	
	public NamedTypeInvHandler(InvAssertionTrans invTrans)
	{
		this.invTrans = invTrans;
		this.util = new NamedTypeInvUtil(this);
	}

	public void handleClass(AClassDeclCG node) throws AnalysisException
	{
		// We want only to treat fields and methods specified by the user.
		// This case helps us avoiding visiting invariant methods

		for (AFieldDeclCG f : node.getFields())
		{
			f.apply(invTrans);
		}

		for (AMethodDeclCG m : node.getMethods())
		{
			m.apply(invTrans);
		}
	}
	
	public void handleField(AFieldDeclCG node)
	{
		// Examples:
		// val : char | Even = 5;
		// stateField : char | Even;

		List<NamedTypeInfo> invTypes = util.findNamedInvTypes(node.getType());

		if (invTypes.isEmpty())
		{
			return;
		}

		AClassDeclCG enclosingClass = invTrans.getJmlGen().getUtil().getEnclosingClass(node);

		if (enclosingClass == null)
		{
			return;
		}

		// In classes that originate from VDM-SL modules the state field
		// and the values are static. However record fields are not.
		String scope = node.getStatic() ? JmlGenerator.JML_STATIC_INV_ANNOTATION
				: JmlGenerator.JML_INSTANCE_INV_ANNOTATION;

		String inv = util.consJmlCheck(enclosingClass.getName(), JmlGenerator.JML_PUBLIC, scope, invTypes, node.getName());

		invTrans.getJmlGen().getAnnotator().appendMetaData(node, invTrans.getJmlGen().getAnnotator().consMetaData(inv));
	}

	public void handleBlock(ABlockStmCG node) throws AnalysisException
	{
		if (node.getLocalDefs().size() > 1)
		{
			LinkedList<AVarDeclCG> origDecls = new LinkedList<AVarDeclCG>(node.getLocalDefs());

			for (int i = origDecls.size() - 1; i >= 0; i--)
			{
				AVarDeclCG nextDecl = origDecls.get(i);

				ABlockStmCG block = new ABlockStmCG();
				block.getLocalDefs().add(nextDecl);

				node.getStatements().addFirst(block);
			}

			for (SStmCG stm : node.getStatements())
			{
				stm.apply(invTrans);
			}

		} else
		{
			for (AVarDeclCG dec : node.getLocalDefs())
			{
				dec.apply(invTrans);
			}

			for (SStmCG stm : node.getStatements())
			{
				stm.apply(invTrans);
			}
		}
	}
	
	public void handleReturn(AReturnStmCG node) throws AnalysisException
	{
		SExpCG exp = node.getExp();

		if (exp instanceof SVarExpCG)
		{
			return;
		}

		ITempVarGen nameGen = invTrans.getJmlGen().getJavaGen().getInfo().getTempVarNameGen();
		String name = nameGen.nextVarName(RET_VAR_NAME_PREFIX);
		TransAssistantCG trans = invTrans.getJmlGen().getJavaGen().getTransAssistant();

		AMethodDeclCG enclosingMethod = invTrans.getJmlGen().getUtil().getEnclosingMethod(node);

		if (enclosingMethod == null)
		{
			return;
		}

		STypeCG returnType = enclosingMethod.getMethodType().getResult();

		List<NamedTypeInfo> invTypes = util.findNamedInvTypes(returnType);

		if (invTypes.isEmpty())
		{
			return;
		}

		AIdentifierPatternCG id = invTrans.getJmlGen().getJavaGen().getInfo().getPatternAssistant().consIdPattern(name);

		AVarDeclCG var = invTrans.getJmlGen().getJavaGen().getInfo().getDeclAssistant().consLocalVarDecl(returnType.clone(), id, exp.clone());

		AIdentifierVarExpCG varExp = invTrans.getJmlGen().getJavaGen().getInfo().getExpAssistant().consIdVar(name, returnType.clone());

		trans.replaceNodeWith(exp, varExp);

		ABlockStmCG replBlock = new ABlockStmCG();
		replBlock.getLocalDefs().add(var);

		trans.replaceNodeWith(node, replBlock);

		replBlock.getStatements().add(node);
		var.apply(invTrans);
	}

	public void handleMethod(AMethodDeclCG node) throws AnalysisException
	{
		// Upon entering the method, assert that the parameters are valid
		// wrt. their named invariant types

		// This transformation also makes sure that the return statement is
		// properly handled for cases where the method returns a value of a
		// named invariant type. In particular this transformation ensures
		// that such values are extracted to local variable declarations
		// and then the assertion check is performed on those.

		ABlockStmCG replBody = new ABlockStmCG();
		for (AFormalParamLocalParamCG param : node.getFormalParams())
		{
			List<NamedTypeInfo> invTypes = util.findNamedInvTypes(param.getType());

			if (!invTypes.isEmpty())
			{
				AClassDeclCG encClass = invTrans.getJmlGen().getUtil().getEnclosingClass(node);

				if (encClass == null)
				{
					continue;
				}

				String enclosingClassName = encClass.getName();

				String varNameStr = invTrans.getJmlGen().getUtil().getName(param.getPattern());

				if (varNameStr == null)
				{
					continue;
				}

				replBody.getStatements().add(util.consAssertStm(invTypes, enclosingClassName, varNameStr));
			}
		}

		SStmCG body = node.getBody();
		invTrans.getJmlGen().getJavaGen().getTransAssistant().replaceNodeWith(body, replBody);
		replBody.getStatements().add(body);
		body.apply(invTrans);
	}

	public AMetaStmCG handleMapSeq(AMapSeqUpdateStmCG node)
	{
		// TODO: Consider this for the atomic statement

		SExpCG col = node.getCol();

		List<NamedTypeInfo> invTypes = util.findNamedInvTypes(col.getType());

		if (!invTypes.isEmpty())
		{
			AClassDeclCG enclosingClass = invTrans.getJmlGen().getUtil().getEnclosingClass(node);

			if (enclosingClass == null)
			{
				return null;
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
						+ col + " in '" + this.getClass().getSimpleName()
						+ "'");
				return null;
			}

			return util.consAssertStm(invTypes, enclosingClass.getName(), name);
		}
		
		return null;
	}

	public void handleVarDecl(AVarDeclCG node)
	{
		// Examples:
		// let x : Even = 1 in ...
		// (dcl y : Even | nat := 2; ...)

		List<NamedTypeInfo> invTypes = util.findNamedInvTypes(node.getType());

		if (invTypes.isEmpty())
		{
			return;
		}

		String name = invTrans.getJmlGen().getUtil().getName(node.getPattern());

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
						+ "the parent block at this point in '"
						+ this.getClass().getSimpleName() + "'");
				return;
			}

			AMetaStmCG assertInv = new AMetaStmCG();

			AClassDeclCG enclosingClass = node.getAncestor(AClassDeclCG.class);

			if (enclosingClass == null)
			{
				return;
			}

			String inv = util.consJmlCheck(enclosingClass.getName(), JmlGenerator.JML_ASSERT_ANNOTATION, invTypes, name);

			invTrans.getJmlGen().getAnnotator().appendMetaData(assertInv, invTrans.getJmlGen().getAnnotator().consMetaData(inv));

			parentBlock.getStatements().addFirst(assertInv);
		} else
		{
			Logger.getLog().printErrorln("Expected parent of local variable "
					+ "declaration to be a statement block. Got: "
					+ node.parent() + " in '" + this.getClass().getSimpleName()
					+ "'");
		}
	}

	public AMetaStmCG handleCallObj(ACallObjectExpStmCG node)
	{
		// TODO: Handle setter calls to records
		// Consider collecting them in the RecAccessorTrans

		// RecAccessorTrans removed all direct field assignments to records, i.e. <rec_obj>.field := <exp>
		// which are now on the form on the form rec.set_field(4).
		// This case simply means we will have to check a record invariant.

		SExpCG recObj = node.getObj();

		if (recObj instanceof SVarExpCG)
		{
			// rec.set_field(atomic_tmp)
			SVarExpCG recObjVar = (SVarExpCG) recObj;

			List<NamedTypeInfo> invTypes = util.findNamedInvTypes(recObj.getType());

			// This will happen for cases like T = R; R :: x : int;
			if (!invTypes.isEmpty())
			{
				AClassDeclCG encClass = invTrans.getJmlGen().getUtil().getEnclosingClass(node);

				if (encClass == null)
				{
					return null;
				}

				return util.consAssertStm(invTypes, encClass.getName(), recObjVar.getName());
			}

		} else if (recObj instanceof AFieldExpCG)
		{
			// Should not happen...
			Logger.getLog().printErrorln("Did not expect record object of call object expression to be a field expression at this point: "
					+ recObj);
		} else
		{
			// TODO: implement proper handling
			// Must also take othe kinds of state designators into account: r1.get_r2().get_r3().set_field(atomic_tmp)

			Logger.getLog().printErrorln("Found unexpected record object of call expression "
					+ " statement inside atomic statement block in '"
					+ this.getClass().getSimpleName() + "'. Target found: "
					+ recObj);
		}
		
		return null;
	}
	
	public void handleAssign(AAssignToExpStmCG node)
	{
		// <target> := atomic_tmp;

		/*
		 * Note that assignment to targets that are of type AFieldNumberExpCG, i.e. tuples (e.g. tup.#1 := 5) is not
		 * allowed in VDM.
		 */
		/*
		 * No need to assert anything since the violation would already have been detected in the temporary variable
		 * section
		 */

		SExpCG target = node.getTarget();

		if (!(target instanceof SVarExpCG))
		{
			Logger.getLog().printErrorln("By now all assignments should have simple variable expression as target. Got: "
					+ target);
			return;
		}

		List<NamedTypeInfo> invTypes = util.findNamedInvTypes(node.getTarget().getType());

		if (invTypes.isEmpty())
		{
			return;
		}

		String varName = util.consVarName(node);

		if (varName == null)
		{
			return;
		}

		AClassDeclCG encClass = invTrans.getJmlGen().getUtil().getEnclosingClass(node);

		if (encClass == null)
		{
			return;
		}

		String enclosingClassName = encClass.getName();
		String varNameStr = varName.toString();

		util.injectAssertion(node, invTypes, enclosingClassName, varNameStr, true);
	}
	
	public JmlGenerator getJmlGen()
	{
		return invTrans.getJmlGen();
	}

	@Override
	public AMetaStmCG consAssert(AIdentifierVarExpCG var)
	{
		List<NamedTypeInfo> invTypes = util.findNamedInvTypes(var.getType());

		if (invTypes.isEmpty())
		{
			return null;
		}

		AClassDeclCG encClass = invTrans.getJmlGen().getUtil().getEnclosingClass(var);

		if (encClass == null)
		{
			return null;
		}
		
		return util.consAssertStm(invTypes, encClass.getName(), var.getName());
	}
}

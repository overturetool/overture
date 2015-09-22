package org.overture.codegen.vdm2jml;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.AMapSeqUpdateStmCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.ir.IRInfo;
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

	public void handleClass(ADefaultClassDeclCG node) throws AnalysisException
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
		/**
		 * Values and record fields will be handled by this handler (not the state component field since its type is a
		 * record type) Example: val : char | Even = 5;
		 */
		List<NamedTypeInfo> invTypes = util.findNamedInvTypes(node.getType());

		if (invTypes.isEmpty())
		{
			return;
		}

		ADefaultClassDeclCG encClass = invTrans.getJmlGen().getUtil().getEnclosingClass(node);

		if (encClass == null)
		{
			return;
		}

		if (!invTrans.getRecInfo().isRecField(node))
		{
			/**
			 * So at this point it must be a value defined in a module. No need to check if invariant checks are enabled.
			 */
			
			AIdentifierVarExpCG var = getJmlGen().getJavaGen().getInfo().getExpAssistant().consIdVar(node.getName(), node.getType().clone());
			
			String inv = util.consJmlCheck(encClass.getName(), JmlGenerator.JML_PUBLIC, JmlGenerator.JML_STATIC_INV_ANNOTATION, false, invTypes, var);
			invTrans.getJmlGen().getAnnotator().appendMetaData(node, invTrans.getJmlGen().getAnnotator().consMetaData(inv));
		}
		/**
		 * No need to assert type consistency of record fields since this is handled by the record setter
		 */
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
			if(!node.getLocalDefs().isEmpty())
			{
				node.getLocalDefs().getFirst().apply(invTrans);
			}

			for (SStmCG stm : node.getStatements())
			{
				stm.apply(invTrans);
			}
		}
	}
	
	public void handleReturn(AReturnStmCG node) throws AnalysisException
	{
		/**
		 * The idea is to extract the return value to variable and return that variable. Then it becomes the
		 * responsibility of the variable declaration case to assert if the named invariant type is violated.
		 */
		SExpCG exp = node.getExp();
		
		AMethodDeclCG encMethod = invTrans.getJmlGen().getUtil().getEnclosingMethod(node);

		if (encMethod == null)
		{
			return;
		}

		STypeCG returnType = encMethod.getMethodType().getResult();

		List<NamedTypeInfo> invTypes = util.findNamedInvTypes(returnType);

		if (invTypes.isEmpty())
		{
			return;
		}

		String name = getInfo().getTempVarNameGen().nextVarName(RET_VAR_NAME_PREFIX);
		AIdentifierPatternCG id = getInfo().getPatternAssistant().consIdPattern(name);

		AIdentifierVarExpCG varExp = getInfo().getExpAssistant().consIdVar(name, returnType.clone());
		getTransAssist().replaceNodeWith(exp, varExp);

		AVarDeclCG varDecl = getInfo().getDeclAssistant().consLocalVarDecl(returnType.clone(), id, exp.clone());
		ABlockStmCG replBlock = new ABlockStmCG();
		replBlock.getLocalDefs().add(varDecl);

		getTransAssist().replaceNodeWith(node, replBlock);

		replBlock.getStatements().add(node);
		varDecl.apply(invTrans);
	}

	public void handleMethod(AMethodDeclCG node) throws AnalysisException
	{
		// Upon entering the method, assert that the parameters are valid wrt. their named invariant types.
		
		ABlockStmCG replBody = new ABlockStmCG();
		for (AFormalParamLocalParamCG param : node.getFormalParams())
		{
			List<NamedTypeInfo> invTypes = util.findNamedInvTypes(param.getType());

			if (!invTypes.isEmpty())
			{
				ADefaultClassDeclCG encClass = invTrans.getJmlGen().getUtil().getEnclosingClass(node);

				if (encClass == null)
				{
					continue;
				}

				String encClassName = encClass.getName();

				String varNameStr = invTrans.getJmlGen().getUtil().getName(param.getPattern());

				if (varNameStr == null)
				{
					continue;
				}
				
				SVarExpCG var = getJmlGen().getJavaGen().getInfo().getExpAssistant().consIdVar(varNameStr, param.getType().clone());

				/**
				 * Upon entering a record setter it is necessary to check if invariants checks are enabled before
				 * checking the parameter
				 */
				AMetaStmCG as = util.consAssertStm(invTypes, encClassName, var, node, invTrans.getRecInfo());
				replBody.getStatements().add(as);
			}
		}

		SStmCG body = node.getBody();
		getTransAssist().replaceNodeWith(body, replBody);
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
			ADefaultClassDeclCG enclosingClass = invTrans.getJmlGen().getUtil().getEnclosingClass(node);

			if (enclosingClass == null)
			{
				return null;
			}

			if (col instanceof SVarExpCG)
			{
				/**
				 * Updates to fields in record setters need to check if invariants checks are enabled
				 */
				return util.consAssertStm(invTypes, enclosingClass.getName(), ((SVarExpCG) col), node,  invTrans.getRecInfo());
			} else
			{
				Logger.getLog().printErrorln("Expected collection to be a variable expression at this point. Got: "
						+ col + " in '" + this.getClass().getSimpleName()
						+ "'");
			}
		}
		
		return null;
	}
	
	public AMetaStmCG handleVarDecl(AVarDeclCG node)
	{
		// Examples:
		// let x : Even = 1 in ...
		// (dcl y : Even | nat := 2; ...)

		List<NamedTypeInfo> invTypes = util.findNamedInvTypes(node.getType());

		if (invTypes.isEmpty())
		{
			return null;
		}

		String name = invTrans.getJmlGen().getUtil().getName(node.getPattern());

		if (name == null)
		{
			return null;
		}

		ADefaultClassDeclCG enclosingClass = node.getAncestor(ADefaultClassDeclCG.class);

		if (enclosingClass == null)
		{
			return null;
		}
		
		AIdentifierVarExpCG var = getJmlGen().getJavaGen().getInfo().getExpAssistant().consIdVar(name, node.getType().clone());

		/**
		 * We do not really need to check if invariant checks are enabled because local variable declarations are not
		 * expected to be found inside record accessors
		 */
		return util.consAssertStm(invTypes, enclosingClass.getName(), var, node, invTrans.getRecInfo());
	}

	public AMetaStmCG handleCallObj(ACallObjectExpStmCG node)
	{
		/**
		 * Handling of setter calls to masked records. This will happen for cases like T = R ... ; R :: x : int;
		 */
		SExpCG recObj = node.getObj();

		if (recObj instanceof SVarExpCG)
		{
			SVarExpCG recObjVar = (SVarExpCG) recObj;

			List<NamedTypeInfo> invTypes = util.findNamedInvTypes(recObj.getType());

			if (!invTypes.isEmpty())
			{
				ADefaultClassDeclCG encClass = invTrans.getJmlGen().getUtil().getEnclosingClass(node);

				if (encClass == null)
				{
					return null;
				}

				/**
				 * Since setter calls can occur inside a record in the context of an atomic statement blocks we need to
				 * check if invariant checks are enabled
				 */
				return util.consAssertStm(invTypes, encClass.getName(), recObjVar, node, invTrans.getRecInfo());
			}

		}
		else
		{
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
		 * TODO: Missing handling of atomic...
		 * No need to assert anything since the violation would already have been detected in the temporary variable
		 * section
		 */
		/*
		 * Note that assignment to targets that are of type AFieldNumberExpCG, i.e. tuples (e.g. tup.#1 := 5) is not
		 * allowed in VDM.
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

		ADefaultClassDeclCG encClass = invTrans.getJmlGen().getUtil().getEnclosingClass(node);

		if (encClass == null)
		{
			return;
		}

		/**
		 * Since assignments can occur inside record setters in the context of an atomic statement block we need to
		 * check if invariant checks are enabled
		 */
		AMetaStmCG assertStm = util.consAssertStm(invTypes, encClass.getName(), ((SVarExpCG) target), node, invTrans.getRecInfo());
		ABlockStmCG replStm = new ABlockStmCG();
		getJmlGen().getJavaGen().getTransAssistant().replaceNodeWith(node, replStm);
		replStm.getStatements().add(node);
		replStm.getStatements().add(assertStm);

	}
	
	public ABlockStmCG getEncBlockStm(AVarDeclCG varDecl)
	{
		if (varDecl.parent() instanceof ABlockStmCG)
		{
			ABlockStmCG parentBlock = (ABlockStmCG) varDecl.parent();

			if (!parentBlock.getLocalDefs().contains(varDecl))
			{
				Logger.getLog().printErrorln("Expected local variable declaration to be "
						+ "one of the local variable declarations of "
						+ "the parent statement block in '"
						+ this.getClass().getSimpleName() + "'");
				return null;
			}

			if (parentBlock.getLocalDefs().size() > 1)
			{
				// The block statement case method should have ensured that the size == 1
				Logger.getLog().printErrorln("Expected only a single local declaration in "
						+ "the parent block at this point in '"
						+ this.getClass().getSimpleName() + "'");
				return null;
			}
			
			return parentBlock;
		}
		else
		{
			Logger.getLog().printErrorln("Expected parent of local variable "
					+ "declaration to be a statement block. Got: "
					+ varDecl.parent() + " in '" + this.getClass().getSimpleName()
					+ "'");
			return null;
		}
	}
	
	private TransAssistantCG getTransAssist()
	{
		return invTrans.getJmlGen().getJavaGen().getTransAssistant();
	}

	private IRInfo getInfo()
	{
		return invTrans.getJmlGen().getJavaGen().getInfo();
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

		ADefaultClassDeclCG encClass = invTrans.getStateDesInfo().getEnclosingClass(var);

		if (encClass == null)
		{
			return null;
		}
		
		/**
		 * Normalisation of state designators will never occur inside record classes so really there is no need to check
		 * if invariant checks are enabled
		 */
		return util.consAssertStm(invTypes, encClass.getName(), var, var, invTrans.getRecInfo());
	}
}

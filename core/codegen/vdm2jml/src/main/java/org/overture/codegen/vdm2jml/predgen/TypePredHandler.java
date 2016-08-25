package org.overture.codegen.vdm2jml.predgen;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.IRGeneratedTag;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.ACastUnaryExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.SVarExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.ACallObjectExpStmIR;
import org.overture.codegen.ir.statements.AForLoopStmIR;
import org.overture.codegen.ir.statements.AMapSeqUpdateStmIR;
import org.overture.codegen.ir.statements.AMetaStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.vdm2jml.JmlAnnotationHelper;
import org.overture.codegen.vdm2jml.JmlGenerator;
import org.overture.codegen.vdm2jml.predgen.info.AbstractTypeInfo;
import org.overture.codegen.vdm2jml.predgen.info.UnknownLeaf;
import org.overture.codegen.vdm2jml.util.IsValChecker;

public class TypePredHandler
{
	public static final String RET_VAR_NAME_PREFIX = "ret_";
	public static final String MAP_SEQ_NAME_PREFIX = "col_";

	private TypePredDecorator decorator;
	private TypePredUtil util;

	private Logger log = Logger.getLogger(this.getClass().getName());

	public TypePredDecorator getDecorator()
	{
		return decorator;
	}

	public TypePredHandler(TypePredDecorator decorator)
	{
		this.decorator = decorator;
		this.util = new TypePredUtil(this);
	}

	public void handleClass(ADefaultClassDeclIR node) throws AnalysisException
	{
		// We want only to treat fields and methods specified by the user.
		// This case helps us avoiding visiting invariant methods

		for (AFieldDeclIR f : node.getFields())
		{
			f.apply(decorator);
		}

		for (AMethodDeclIR m : node.getMethods())
		{
			m.apply(decorator);
		}
	}

	public void handleField(AFieldDeclIR node)
	{
		/**
		 * Values and record fields will be handled by this handler (not the state component field since its type is a
		 * record type) Example: val : char | Even = 5;
		 */
		ADefaultClassDeclIR encClass = decorator.getJmlGen().getUtil().getEnclosingClass(node);

		if (encClass == null)
		{
			return;
		}

		if (!decorator.getRecInfo().isRecField(node) && node.getFinal())
		{
			/**
			 * So at this point it must be a value defined in a module. No need to check if invariant checks are
			 * enabled.
			 */

			AbstractTypeInfo typeInfo = util.findTypeInfo(node.getType());

			/**
			 * Since we only allow value definitions to be initialized using literals they must be different from
			 * 'null'. However, there is a bug in OpenJML that sometimes cause the invariant check for a field to
			 * trigger before the field is properly initialized. As a work-around for this OpenJML bug, this trick
			 * guards against this bug, i.e. the static invariant check triggering pre-maturely. Since </br>
			 * fieldInitialised ==> invariant</br>
			 * =</br>
			 * !fieldInitialized || invariant</br>
			 * =</br>
			 * !(field != null) || invariant</br>
			 * =</br>
			 * field == null || invariant</br>
			 * </br>
			 * .. it suffices to simply consider the type as being optional.
			 */
			typeInfo.setOptional(true);

			if (proceed(typeInfo))
			{
				AIdentifierVarExpIR var = getJmlGen().getJavaGen().getInfo().getExpAssistant().consIdVar(node.getName(), node.getType().clone());

				List<String> invStrings = util.consJmlCheck(encClass, JmlGenerator.JML_PUBLIC, JmlGenerator.JML_STATIC_INV_ANNOTATION, false, typeInfo, var);
				for (String invStr : invStrings)
				{
					getAnnotator().appendMetaData(node, getAnnotator().consMetaData(invStr));
				}
			} else
			{
				// Since value definitions can only be initialised with literals there is no
				// need to guard against null (see JmlGenerator.initialIRConstructed)
				// if(varMayBeNull(node.getType()) && rightHandSideMayBeNull(node.getInitial()))
				// {
				// getAnnotator().appendMetaData(node, util.consValNotNullInvariant(node.getName()));
				// }
			}

		}
		/**
		 * No need to assert type consistency of record fields since this is handled by the record setter
		 */
	}

	private JmlAnnotationHelper getAnnotator()
	{
		return decorator.getJmlGen().getAnnotator();
	}

	public void handleBlock(ABlockStmIR node) throws AnalysisException
	{
		if (node.getLocalDefs().size() > 1)
		{
			LinkedList<AVarDeclIR> origDecls = new LinkedList<AVarDeclIR>(node.getLocalDefs());

			for (int i = origDecls.size() - 1; i >= 0; i--)
			{
				AVarDeclIR nextDecl = origDecls.get(i);

				ABlockStmIR block = new ABlockStmIR();
				block.getLocalDefs().add(nextDecl);

				node.getStatements().addFirst(block);
			}

			for (SStmIR stm : node.getStatements())
			{
				stm.apply(decorator);
			}

		} else
		{
			if (!node.getLocalDefs().isEmpty())
			{
				node.getLocalDefs().getFirst().apply(decorator);
			}

			for (SStmIR stm : node.getStatements())
			{
				stm.apply(decorator);
			}
		}
	}

	public void handleReturn(AReturnStmIR node) throws AnalysisException
	{
		/**
		 * The idea is to extract the return value to variable and return that variable. Then it becomes the
		 * responsibility of the variable declaration case to assert if the named invariant type is violated.
		 */
		SExpIR exp = node.getExp();

		AMethodDeclIR encMethod = decorator.getJmlGen().getUtil().getEnclosingMethod(node);

		if (encMethod == null)
		{
			return;
		}

		STypeIR returnType = encMethod.getMethodType().getResult();

		AbstractTypeInfo typeInfo = util.findTypeInfo(returnType);

		if (!proceed(typeInfo))
		{
			return;
		}

		String name = getInfo().getTempVarNameGen().nextVarName(RET_VAR_NAME_PREFIX);
		AIdentifierPatternIR id = getInfo().getPatternAssistant().consIdPattern(name);

		AIdentifierVarExpIR varExp = getInfo().getExpAssistant().consIdVar(name, returnType.clone());
		getTransAssist().replaceNodeWith(exp, varExp);

		AVarDeclIR varDecl = getInfo().getDeclAssistant().consLocalVarDecl(returnType.clone(), id, exp.clone());
		ABlockStmIR replBlock = new ABlockStmIR();
		replBlock.getLocalDefs().add(varDecl);

		getTransAssist().replaceNodeWith(node, replBlock);

		replBlock.getStatements().add(node);
		varDecl.apply(decorator);
	}

	public void handleMethod(AMethodDeclIR node) throws AnalysisException
	{
		if (!treatMethod(node))
		{
			return;
		}

		// Upon entering the method, assert that the parameters are valid wrt. their named invariant types.

		ABlockStmIR replBody = new ABlockStmIR();
		for (AFormalParamLocalParamIR param : node.getFormalParams())
		{
			AbstractTypeInfo typeInfo = util.findTypeInfo(param.getType());

			if (proceed(typeInfo))
			{
				ADefaultClassDeclIR encClass = decorator.getJmlGen().getUtil().getEnclosingClass(node);

				if (encClass == null)
				{
					continue;
				}

				String varNameStr = decorator.getJmlGen().getUtil().getName(param.getPattern());

				if (varNameStr == null)
				{
					continue;
				}

				SVarExpIR var = getInfo().getExpAssistant().consIdVar(varNameStr, param.getType().clone());

				/**
				 * Upon entering a record setter it is necessary to check if invariants checks are enabled before
				 * checking the parameter
				 */
				List<AMetaStmIR> as = util.consAssertStm(typeInfo, encClass, var, node, decorator.getRecInfo());
				for (AMetaStmIR a : as)
				{
					replBody.getStatements().add(a);
				}
			}
		}

		SStmIR body = node.getBody();
		getTransAssist().replaceNodeWith(body, replBody);
		replBody.getStatements().add(body);
		body.apply(decorator);
	}

	public List<AMetaStmIR> handleMapSeq(AMapSeqUpdateStmIR node)
	{
		// TODO: Consider this for the atomic statement

		SExpIR col = node.getCol();

		if (!(col instanceof SVarExpIR))
		{
			log.error("Expected collection to be a variable expression at this point. Got: "
					+ col);
			return null;
		}

		SVarExpIR var = (SVarExpIR) col;

		if (varMayBeNull(var.getType()))
		{
			// The best we can do is to assert that the map/seq subject to modification is
			// not null although we eventually get the null pointer exception, e.g.
			//
			// //@ azzert m != null
			// Utils.mapSeqUpdate(m,1L,1L);
			AMetaStmIR assertNotNull = util.consVarNotNullAssert(var.getName());

			ABlockStmIR replStm = new ABlockStmIR();
			getTransAssist().replaceNodeWith(node, replStm);
			replStm.getStatements().add(assertNotNull);
			replStm.getStatements().add(node);
		}

		AbstractTypeInfo typeInfo = util.findTypeInfo(var.getType());

		if (proceed(typeInfo))
		{
			ADefaultClassDeclIR enclosingClass = decorator.getJmlGen().getUtil().getEnclosingClass(node);

			if (enclosingClass == null)
			{
				return null;
			}

			if (col instanceof SVarExpIR)
			{
				/**
				 * Updates to fields in record setters need to check if invariants checks are enabled
				 */
				return util.consAssertStm(typeInfo, enclosingClass, var, node, decorator.getRecInfo());
			}
		}

		return null;
	}

	public List<AMetaStmIR> handleVarDecl(AVarDeclIR node)
	{
		// Examples:
		// let x : Even = 1 in ...
		// (dcl y : Even | nat := 2; ...)

		if (getInfo().getExpAssistant().isUndefined(node.getExp()))
		{
			return null;
		}

		AbstractTypeInfo typeInfo = util.findTypeInfo(node.getType());

		if (proceed(typeInfo))
		{
			String name = decorator.getJmlGen().getUtil().getName(node.getPattern());

			if (name == null)
			{
				return null;
			}

			ADefaultClassDeclIR enclosingClass = node.getAncestor(ADefaultClassDeclIR.class);

			if (enclosingClass == null)
			{
				return null;
			}

			AIdentifierVarExpIR var = getJmlGen().getJavaGen().getInfo().getExpAssistant().consIdVar(name, node.getType().clone());

			/**
			 * We do not really need to check if invariant checks are enabled because local variable declarations are
			 * not expected to be found inside record accessors
			 */
			return util.consAssertStm(typeInfo, enclosingClass, var, node, decorator.getRecInfo());
		}

		return null;
	}

	public List<AMetaStmIR> handleCallObj(ACallObjectExpStmIR node)
	{
		/**
		 * Handling of setter calls to masked records. This will happen for cases like T = R ... ; R :: x : int;
		 */
		SExpIR recObj = node.getObj();

		if (recObj instanceof ACastUnaryExpIR)
		{
			recObj = ((ACastUnaryExpIR) recObj).getExp();
		}

		if (recObj instanceof SVarExpIR)
		{
			SVarExpIR recObjVar = (SVarExpIR) recObj;

			if (varMayBeNull(recObj.getType()))
			{
				// The best we can do is to assert that the record subject to modification is
				// not null although we eventually get the null pointer exception, e.g.
				//
				// //@ azzert rec != null
				// rec.set_x(5);
				AMetaStmIR assertNotNull = util.consVarNotNullAssert(recObjVar.getName());

				ABlockStmIR replStm = new ABlockStmIR();
				getTransAssist().replaceNodeWith(node, replStm);
				replStm.getStatements().add(assertNotNull);
				replStm.getStatements().add(node);

				return null;
			}

			AbstractTypeInfo typeInfo = util.findTypeInfo(recObj.getType());

			if (proceed(typeInfo))
			{
				ADefaultClassDeclIR encClass = decorator.getJmlGen().getUtil().getEnclosingClass(node);

				if (encClass == null)
				{
					return null;
				}

				/**
				 * Since setter calls can occur inside a record in the context of an atomic statement blocks we need to
				 * check if invariant checks are enabled
				 */
				return util.consAssertStm(typeInfo, encClass, recObjVar, node, decorator.getRecInfo());
			}
		} else
		{
			log.error("Found unexpected record object of call expression statement inside atomic statement block. Target found: "
					+ recObj);
		}

		return null;
	}

	public void handleAssign(AAssignToExpStmIR node)
	{
		// <target> := atomic_tmp;

		/*
		 * Note that assignment to targets that are of type AFieldNumberExpIR, i.e. tuples (e.g. tup.#1 := 5) is not
		 * allowed in VDM.
		 */

		SExpIR target = node.getTarget();

		if (!(target instanceof SVarExpIR))
		{
			log.error("By now all assignments should have simple variable expression as target. Got: "
					+ target);
			return;
		}

		SVarExpIR var = (SVarExpIR) target;

		AbstractTypeInfo typeInfo = util.findTypeInfo(node.getTarget().getType());

		if (proceed(typeInfo))
		{
			ADefaultClassDeclIR encClass = decorator.getJmlGen().getUtil().getEnclosingClass(node);

			if (encClass == null)
			{
				return;
			}

			/**
			 * Since assignments can occur inside record setters in the context of an atomic statement block we need to
			 * check if invariant checks are enabled
			 */
			List<AMetaStmIR> asserts = util.consAssertStm(typeInfo, encClass, var, node, decorator.getRecInfo());

			for (AMetaStmIR a : asserts)
			{
				addAssert(node, a);
			}
		}
	}

	public ABlockStmIR getEncBlockStm(AVarDeclIR varDecl)
	{
		INode parent = varDecl.parent();

		if (parent instanceof ABlockStmIR)
		{
			ABlockStmIR parentBlock = (ABlockStmIR) varDecl.parent();

			if (!parentBlock.getLocalDefs().contains(varDecl))
			{
				log.error("Expected local variable declaration to be "
						+ "one of the local variable declarations of "
						+ "the parent statement block");
				return null;
			}

			if (parentBlock.getLocalDefs().size() > 1)
			{
				// The block statement case method should have ensured that the size == 1
				log.error("Expected only a single local declaration in "
						+ "the parent block at this point");
				return null;
			}

			return parentBlock;
		} else if (parent instanceof AForLoopStmIR)
		{
			// Do nothing
			return null;
		} else
		{
			log.error("Expected parent of local variable "
					+ "declaration to be a statement block. Got: "
					+ varDecl.parent());
			return null;
		}
	}

	private TransAssistantIR getTransAssist()
	{
		return decorator.getJmlGen().getJavaGen().getTransAssistant();
	}

	private IRInfo getInfo()
	{
		return decorator.getJmlGen().getJavaGen().getInfo();
	}

	public JmlGenerator getJmlGen()
	{
		return decorator.getJmlGen();
	}

	public List<AMetaStmIR> consAsserts(AIdentifierVarExpIR var)
	{
		AbstractTypeInfo typeInfo = util.findTypeInfo(var.getType());

		if (!proceed(typeInfo))
		{
			return null;
		}

		ADefaultClassDeclIR encClass = decorator.getStateDesInfo().getEnclosingClass(var);

		if (encClass == null)
		{
			return null;
		}

		/**
		 * Normalisation of state designators will never occur inside record classes so really there is no need to check
		 * if invariant checks are enabled
		 */
		return util.consAssertStm(typeInfo, encClass, var, var, decorator.getRecInfo());
	}

	public boolean rightHandSideMayBeNull(SExpIR exp)
	{
		IsValChecker checker = new IsValChecker();

		try
		{
			return !exp.apply(checker);
		} catch (AnalysisException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	private boolean varMayBeNull(STypeIR type)
	{
		return treatMethod(type)
				&& !getInfo().getTypeAssistant().allowsNull(type);
	}

	private boolean treatMethod(INode node)
	{
		if (inModuleToStringMethod(node))
		{
			return false;
		}

		// Some of the record methods inherited from object use native java type that can never be null

		if (decorator.getRecInfo().inRec(node))
		{
			if (!(decorator.getRecInfo().inAccessor(node)
					|| decorator.getRecInfo().inRecConstructor(node)))
			{
				return false;
			}
		}

		return true;
	}

	private boolean inModuleToStringMethod(INode type)
	{
		AMethodDeclIR m = type.getAncestor(AMethodDeclIR.class);

		if (m == null)
		{
			return false;
		}

		if (m.getTag() instanceof IRGeneratedTag
				&& m.getName().equals("toString"))
		{
			return true;
		}

		return false;
	}

	private void addAssert(AAssignToExpStmIR node, AMetaStmIR assertStm)
	{
		ABlockStmIR replStm = new ABlockStmIR();
		getJmlGen().getJavaGen().getTransAssistant().replaceNodeWith(node, replStm);
		replStm.getStatements().add(node);
		replStm.getStatements().add(assertStm);
	}

	public TypePredUtil getTypePredUtil()
	{
		return util;
	}

	private boolean proceed(AbstractTypeInfo typeInfo)
	{
		return !(typeInfo instanceof UnknownLeaf);
	}
}

package org.overture.codegen.trans;

import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SDeclIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AFuncDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.AStateDeclIR;
import org.overture.codegen.ir.expressions.AFieldExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.statements.AFieldStateDesignatorIR;
import org.overture.codegen.ir.statements.AIdentifierStateDesignatorIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class SlStateAccessTrans extends DepthFirstAnalysisAdaptor
{
	private final AStateDeclIR stateDecl;
	private IRInfo info;
	private TransAssistantIR transAssistant;
	private boolean inPreOrPost;

	public SlStateAccessTrans(AStateDeclIR stateDecl, IRInfo info,
			TransAssistantIR transAssistant)
	{
		this.stateDecl = stateDecl;
		this.info = info;
		this.transAssistant = transAssistant;
		this.inPreOrPost = false;
	}

	@Override
	public void caseAFuncDeclIR(AFuncDeclIR node) throws AnalysisException
	{
		handleMethodOrFunc(node.getBody(), node.getPreCond(), node.getPostCond());
	}

	@Override
	public void caseAMethodDeclIR(AMethodDeclIR node) throws AnalysisException
	{
		handleMethodOrFunc(node.getBody(), node.getPreCond(), node.getPostCond());
	}

	@Override
	public void caseAIdentifierVarExpIR(AIdentifierVarExpIR node)
			throws AnalysisException
	{
		if (isOldFieldRead(stateDecl, node) /* 1 */
				|| isFieldRead(stateDecl, node) /* 2 */
				|| isFieldReadInPreOrPost(stateDecl, node) /* 3 */)
		{
			// Given a state named 'St' with a field named 'f'
			// Note: If a variable is 'old' then it is local since state is passed
			// as an argument to the post condition function

			// Also note that the IR uses underscore to denote old names: _St corresponds to St~ (in VDM)
			// and _f corresponds to f~ (in VDM)

			// /*1*/ In case we are in a post condition the variable expression may represent a
			// field of the old state, i.e._f (or f~ in VDM)
			//
			// /*2*/ Any other place in the model:
			// The variable expression represents a field of the current state
			//
			// /*3*/ Another possibility is that the variable expression represents
			// a field of the current state in a pre or post condition of a function or
			// an operation. This is a special case since the state field can be
			// read as 'f' although it is only the entire state that is being passed as an argument
			// to the function. Below is a Java example showing this (result, old state,
			// current state):
			//
			// public static Boolean post_op(final Number RESULT, final St _St, final St St)
			// If /*1*/ or /*2*/ or /*3*/ is true we make the field access explicit since IR modules
			// use a single field to represent state.
			//
			AFieldExpIR fieldExp = new AFieldExpIR();
			fieldExp.setSourceNode(node.getSourceNode());
			fieldExp.setTag(node.getTag());
			fieldExp.setType(node.getType().clone());
			setFieldNames(fieldExp, stateDecl, node.getName());

			transAssistant.replaceNodeWith(node, fieldExp);
		}
	}

	@Override
	public void caseAIdentifierStateDesignatorIR(
			AIdentifierStateDesignatorIR node) throws AnalysisException
	{
		if (!node.getIsLocal() && !node.getName().equals(stateDecl.getName()))
		{
			ARecordTypeIR stateType = transAssistant.getRecType(stateDecl);

			AIdentifierStateDesignatorIR idState = new AIdentifierStateDesignatorIR();
			idState.setClassName(null);
			idState.setExplicit(false);
			idState.setIsLocal(false);
			idState.setName(stateDecl.getName());
			idState.setType(stateType);

			AFieldStateDesignatorIR field = new AFieldStateDesignatorIR();
			field.setField(node.getName());
			field.setObject(idState);
			for (AFieldDeclIR f : stateDecl.getFields())
			{
				if (f.getName().equals(node.getName()))
				{
					field.setType(f.getType().clone());
				}
			}

			transAssistant.replaceNodeWith(node, field);
		}
	}

	private void handleMethodOrFunc(INode body, SDeclIR preCond,
			SDeclIR postCond) throws AnalysisException
	{
		if (body != null)
		{
			body.apply(this);
		}

		handleCond(preCond);
		handleCond(postCond);
	}

	private void handleCond(SDeclIR cond) throws AnalysisException
	{
		if (cond != null)
		{
			inPreOrPost = true;
			cond.apply(this);
			inPreOrPost = false;
		}
	}

	private boolean isFieldReadInPreOrPost(final AStateDeclIR stateDecl,
			AIdentifierVarExpIR node)
	{
		if (!inPreOrPost)
		{
			return false;
		}

		boolean matches = false;
		for (AFieldDeclIR f : stateDecl.getFields())
		{
			if (f.getName().equals(node.getName()))
			{
				matches = true;
				break;
			}
		}

		return matches;
	}

	private boolean isFieldRead(final AStateDeclIR stateDecl, AIdentifierVarExpIR node) {
		return !info.isSlStateRead(node) && !node.getName().equals(stateDecl.getName())
				&& !node.getName().equals("_" + stateDecl.getName());
	}

	private boolean isOldFieldRead(final AStateDeclIR stateDecl,
			AIdentifierVarExpIR node)
	{
		return info.getExpAssistant().isOld(node.getName())
				&& !node.getName().equals("_" + stateDecl.getName());
	}

	private void setFieldNames(AFieldExpIR field, AStateDeclIR stateDecl,
			String name)
	{
		ARecordTypeIR recType = transAssistant.getRecType(stateDecl);
		String stateName = stateDecl.getName();

		if (info.getExpAssistant().isOld(name))
		{
			field.setObject(transAssistant.getInfo().getExpAssistant().consIdVar("_"
					+ stateName, recType));
			field.setMemberName(info.getExpAssistant().oldNameToCurrentName(name));
		} else
		{
			field.setObject(transAssistant.getInfo().getExpAssistant().consIdVar(stateName, recType));
			field.setMemberName(name);
		}
	}
}

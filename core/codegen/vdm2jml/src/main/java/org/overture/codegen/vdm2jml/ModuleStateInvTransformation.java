package org.overture.codegen.vdm2jml;

import org.overture.ast.lex.Dialect;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.AAtomicStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AMapSeqUpdateStmCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;
import org.overture.codegen.logging.Logger;
import org.overture.config.Settings;

public class ModuleStateInvTransformation extends DepthFirstAnalysisAdaptor
{
	private JmlGenerator jmlGen;

	public ModuleStateInvTransformation(JmlGenerator jmlGen)
	{
		// This transformation only works for VDM-SL. For example, it does not
		// take 'self' into account
		if (Settings.dialect != Dialect.VDM_SL)
		{
			Logger.getLog().printErrorln("This transformation is targeting VDM-SL. The dialect is set to: "
					+ Settings.dialect);
		}

		this.jmlGen = jmlGen;
	}
	
	@Override
	public void caseAAtomicStmCG(AAtomicStmCG node) throws AnalysisException
	{
		if(!isLastStmInMethod(node))
		{
			appendAssertion(node, consAssertStr(node));
		}
	}

	@Override
	public void caseAAssignToExpStmCG(AAssignToExpStmCG node)
			throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		// So by now we know that 1) the statement does not occur inside an atomic statement
		// and 2) the enclosing class has an invariant

		SExpCG subject = jmlGen.getJavaGen().getInfo().getExpAssistant().findSubject(node.getTarget());

		// Note that this case method does not have to consider
		// state updates on the form stateComp(52) := 4
		// since they get transformed into AMapSeqUpdateStmCGs
		// which are treated using a separate case method in this
		// visitor
		while (subject instanceof AFieldExpCG)
		{
			subject = ((AFieldExpCG) subject).getObject();
		}

		if (subject instanceof SVarExpCG)
		{
			SVarExpCG var = (SVarExpCG) subject;

			if (!var.getIsLocal())
			{
				// The variable is NOT local so the invariant needs to be checked
				appendAssertion(node, consAssertStr(node));
			}
		} else
		{
			Logger.getLog().printErrorln("Expected target to a variable expression at this point. Got "
					+ subject + " in '" + this.getClass().getSimpleName() + "'");

			// Append the assertion just in case...
			appendAssertion(node, consAssertStr(node));
		}
	}

	@Override
	public void caseAMapSeqUpdateStmCG(AMapSeqUpdateStmCG node)
			throws AnalysisException
	{
		if (!proceed(node))
		{
			return;
		}

		SExpCG subject = jmlGen.getJavaGen().getInfo().getExpAssistant().findSubject(node.getCol());

		if (subject instanceof SVarExpCG)
		{
			if (!((SVarExpCG) subject).getIsLocal())
			{
				// The map/seq update is NOT an update to a local variable so we'll assert the invariant check
				appendAssertion(node, consAssertStr(node));
			}
		} else
		{
			Logger.getLog().printErrorln("Expected 'next' to be a variable expression at this point. Got: "
					+ subject + " in '" + this.getClass().getSimpleName() + "'");

			// Append the assertion just in case...
			appendAssertion(node, consAssertStr(node));
		}
	}

	public void appendAssertion(SStmCG stm, String str)
	{
		if (stm.parent() != null)
		{
			AMetaStmCG assertion = new AMetaStmCG();
			jmlGen.getAnnotator().appendMetaData(assertion, jmlGen.getAnnotator().consMetaData(str));

			ABlockStmCG replacementBlock = new ABlockStmCG();

			jmlGen.getJavaGen().getTransAssistant().replaceNodeWith(stm, replacementBlock);

			replacementBlock.getStatements().add(stm);
			replacementBlock.getStatements().add(assertion);

		} else
		{
			Logger.getLog().printErrorln("Could not find parent node of " + stm
					+ " and therefore no assertion could be inserted (in"
					+ this.getClass().getSimpleName() + ")");
		}
	}

	private AClassDeclCG getEnclosingClass(SStmCG stm)
	{
		AClassDeclCG encClass = stm.getAncestor(AClassDeclCG.class);

		if (encClass != null)
		{
			return encClass;
		} else
		{
			Logger.getLog().printErrorln("Could not find enclosing class of statement "
					+ stm + " in '" + this.getClass().getSimpleName() + "'");
			return null;
		}
	}

	private boolean proceed(SStmCG stm)
	{
		AClassDeclCG encClass = getEnclosingClass(stm);

		if (encClass != null)
		{
			// We'll proceed if
			// 1) the class has an invariant and
			// 2) the statement does not occur inside an atomic statement and
			// 3) the statement is not the last statement of a method
			return encClass.getInvariant() != null
					&& !jmlGen.getJavaGen().getInfo().getStmAssistant().inAtomic(stm)
					&& !isLastStmInMethod(stm);
		} else
		{
			// Erroneous case: we can't really check anything..
			return false;
		}

	}

	private String consAssertStr(SStmCG stm)
	{
		String invExpStr = consInvExpStr(stm);

		if (invExpStr != null)
		{
			return "//@ " + JmlGenerator.JML_ASSERT_ANNOTATION + " " + invExpStr + ";";
		} else
		{
			return null;
		}
	}

	private String consInvExpStr(SStmCG stm)
	{
		AClassDeclCG encClass = getEnclosingClass(stm);

		if (encClass != null)
		{
			// In addition to the state field there may be other fields that originate from values.
			// However, values are final so we simply need to find the only non-final field in order
			// to construct the invariant expression
			for(AFieldDeclCG field : encClass.getFields())
			{
				if(field.getFinal() == false)
				{
					String stateName = field.getName();

					// E.g. inv_St(St)
					return JmlGenerator.INV_PREFIX + stateName + "(" + stateName + ")";
				}
			}
		}
		
		return null;
	}
	
	private boolean isLastStmInMethod(org.overture.codegen.cgast.INode node)
	{
		org.overture.codegen.cgast.INode parent = node.parent();
		org.overture.codegen.cgast.INode child = node;
		
		while(lastStmInBlock(parent, child) || isSingleStmBlock(parent))
		{
			child = parent;
			parent = parent.parent();
		}
		
		return parent instanceof AMethodDeclCG;
	}
	
	private boolean lastStmInBlock(INode posBlock, INode posStm)
	{
		if(!(posBlock instanceof ABlockStmCG) || !(posStm instanceof SStmCG))
		{
			return false;
		}
		
		ABlockStmCG block = (ABlockStmCG) posBlock;
		
		if(!block.getStatements().isEmpty())
		{
			return block.getStatements().getLast() == posStm;
		}
		
		return false;
	}

	private boolean isSingleStmBlock(org.overture.codegen.cgast.INode posBlock)
	{
		if(!(posBlock instanceof ABlockStmCG))
		{
			return false;
		}
		
		ABlockStmCG block = (ABlockStmCG) posBlock;
		
		int counter = 0;
		
		for(SStmCG s : block.getStatements())
		{
			if(!isEmptyBlock(s))
			{
				counter++;
			}
		}
		
		return counter == 1;
	}
	
	private boolean isEmptyBlock(org.overture.codegen.cgast.INode node)
	{
		if(node instanceof ABlockStmCG)
		{
			ABlockStmCG block = (ABlockStmCG) node;
			
			return block.getStatements().isEmpty() && block.getLocalDefs().isEmpty();
		}
		else
		{
			return false;
		}
	}
}

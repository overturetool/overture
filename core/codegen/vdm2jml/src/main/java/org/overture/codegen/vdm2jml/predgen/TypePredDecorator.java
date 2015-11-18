package org.overture.codegen.vdm2jml.predgen;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.AMapSeqUpdateStmCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.vdm2jml.JmlGenerator;
import org.overture.codegen.vdm2jml.data.RecClassInfo;
import org.overture.codegen.vdm2jml.data.StateDesInfo;
import org.overture.codegen.vdm2jml.trans.RecAccessorTrans;
import org.overture.codegen.vdm2jml.trans.TargetNormaliserTrans;

/**
 * This class is responsible for adding additional checks, like assertions, to the IR to preserve the semantics of the
 * contract-based notations of VDM-SL when they are translated to JML annotated Java.
 * 
 * @see RecAccessorTrans
 * @see TargetNormaliserTrans
 */
public class TypePredDecorator extends AtomicAssertTrans
{
	private RecModHandler recHandler;
	private TypePredHandler namedTypeHandler;
	private StateDesInfo stateDesInfo;
	private RecClassInfo recInfo;
	
	private boolean buildRecChecks = false;
	
	public TypePredDecorator(JmlGenerator jmlGen, StateDesInfo stateDesInfo,
			RecClassInfo recInfo)
	{
		super(jmlGen);
		this.recHandler = new RecModHandler(this);
		this.namedTypeHandler = new TypePredHandler(this);
		this.stateDesInfo = stateDesInfo;
		this.recInfo = recInfo;
	}

	@Override
	public void caseACallObjectExpStmCG(ACallObjectExpStmCG node)
			throws AnalysisException
	{
		if (node.getObj() instanceof SVarExpCG)
		{
			SVarExpCG obj = (SVarExpCG) node.getObj();
			handleStateUpdate(node, obj);
		}
		else if(node.getObj() instanceof ACastUnaryExpCG)
		{
			ACastUnaryExpCG cast = (ACastUnaryExpCG) node.getObj();
			
			if(cast.getExp() instanceof SVarExpCG)
			{
				SVarExpCG obj = (SVarExpCG) cast.getExp();
				handleStateUpdate(node, obj);
			}
			else
			{
				Logger.getLog().printErrorln("Expected subject of cast expression to be a variable expression at this point in '"
						+ this.getClass().getSimpleName() + "'. Got: " + cast.getExp());
			}
		}
		else
		{
			Logger.getLog().printErrorln("Expected object of call object statement "
					+ " to be a variable or cast expression by now in '"
					+ this.getClass().getSimpleName() + "'. Got: "
					+ node.getObj());
		}
	}

	private void handleStateUpdate(ACallObjectExpStmCG node, SVarExpCG obj)
	{
		handleStateUpdate(node, obj, stateDesInfo.getStateDesVars(node), recHandler.handleCallObj(node), namedTypeHandler.handleCallObj(node));
	}

	@Override
	public void caseAFieldDeclCG(AFieldDeclCG node) throws AnalysisException
	{
		namedTypeHandler.handleField(node);
	}

	@Override
	public void caseABlockStmCG(ABlockStmCG node) throws AnalysisException
	{
		namedTypeHandler.handleBlock(node);
	}

	@Override
	public void caseAVarDeclCG(AVarDeclCG node) throws AnalysisException
	{
		/**
		 * Variable declarations occurring inside an atomic statement do not need handling. The reason for this is that
		 * the call statement and the map/seq update cases currently take care of generating the assertions for
		 * variable expressions used to represent state designators.
		 * 
		 * TODO: Make this case handle state designators
		 */
		if(inAtomic())
		{
			return;
		}
		
		if(stateDesInfo.isStateDesDecl(node))
		{
			return;
		}
		
		/**
		 * Since the target of map/seq updates (e.g. Utils.mapsSeqUpdate(stateDes_3, 4, 'a')) and call object statements
		 * (e.g. stateDes_3.set_x("a")) (i.e. assignments in the VDM-SL model) are split into variables named stateDes_
		 * <n> we can also expect local variable declarations in atomic statement blocks
		 */
		List<AMetaStmCG> as = namedTypeHandler.handleVarDecl(node);
		
		if(as == null || as.isEmpty())
		{
			return;
		}
		
		ABlockStmCG encBlock = namedTypeHandler.getEncBlockStm(node);
		
		if(encBlock == null)
		{
			return;
		}
		
		/**
		 * The variable declaration is a local declaration of the statement block. Therefore, making the assertion the
		 * first statement in the statement block makes the assertion appear immediately right after the variable
		 * declaration.
		 */
		if(inAtomic())
		{
			for(AMetaStmCG a : as)
			{
				addPostAtomicCheck(a);
			}
		}
		else
		{
			for(int i = as.size() - 1; i >= 0; i--)
			{
				encBlock.getStatements().addFirst(as.get(i));
			}
		}
	}

	@Override
	public void caseAAssignToExpStmCG(AAssignToExpStmCG node)
			throws AnalysisException
	{
		/**
		 * Record modifications are now all on the form E.g. St = <recvalue>, i.e. node.getTarget() instanceof SVarExpCG
		 * && node.getTarget().getType() instanceof ARecordTypeCG. Invariant violations will therefore be detected when
		 * the record value is constructed or it appears in the temporary variable section if the assignment occurs in
		 * the context of an atomic statement block. Therefore, no record invariant needs to be asserted. Note that more
		 * complicated record modifications (e.g. rec1.rec2.f := 5) appear as nodes of type caseACallObjectExpStmCG
		 */
		
		if(!inAtomic())
		{
			namedTypeHandler.handleAssign(node);
		}
	}

	@Override
	public void caseAMapSeqUpdateStmCG(AMapSeqUpdateStmCG node)
			throws AnalysisException
	{
		if (node.getCol() instanceof SVarExpCG)
		{
			SVarExpCG col = (SVarExpCG) node.getCol();
			handleStateUpdate(node, col, stateDesInfo.getStateDesVars(node), null, namedTypeHandler.handleMapSeq(node));
		} else
		{
			Logger.getLog().printErrorln("Expected collection of map/sequence"
					+ " update to be a variable expression by now in '"
					+ this.getClass().getSimpleName() + "'. Got: "
					+ node.getCol());
		}
	}

	@Override
	public void caseAMethodDeclCG(AMethodDeclCG node) throws AnalysisException
	{
		namedTypeHandler.handleMethod(node);
	}

	@Override
	public void caseAReturnStmCG(AReturnStmCG node) throws AnalysisException
	{
		namedTypeHandler.handleReturn(node);
	}

	@Override
	public void caseADefaultClassDeclCG(ADefaultClassDeclCG node) throws AnalysisException
	{
		namedTypeHandler.handleClass(node);
	}

	private void handleStateUpdate(SStmCG node, SVarExpCG target,
			List<AIdentifierVarExpCG> objVars, AMetaStmCG recAssert,
			List<AMetaStmCG> namedTypeInvAssert)
	{
		if (recAssert == null && namedTypeInvAssert == null && objVars == null)
		{
			return;
		}

		if (!inAtomic())
		{
			// NOT inside atomic statement block
			ABlockStmCG replBlock = new ABlockStmCG();
			jmlGen.getJavaGen().getTransAssistant().replaceNodeWith(node, replBlock);
			replBlock.getStatements().add(node);

			addSubjectAsserts(recAssert, namedTypeInvAssert, replBlock);
			addStateDesAsserts(target, objVars, replBlock);
		} else
		{
			// We'll store the checks and let the atomic statement case handle the assert insertion
			addSubjectAssertAtomic(recAssert, namedTypeInvAssert);
			addStateDesAssertsAtomic(target, objVars);
		}
	}

	private void addSubjectAssertAtomic(AMetaStmCG recAssert,
			List<AMetaStmCG> namedTypeInvAssert)
	{
		for (AMetaStmCG a : consSubjectAsserts(recAssert, namedTypeInvAssert))
		{
			addPostAtomicCheck(a);
		}
	}

	private void addSubjectAsserts(AMetaStmCG recAssert,
			List<AMetaStmCG> namedTypeInvAssert, ABlockStmCG replBlock)
	{
		for (AMetaStmCG a : consSubjectAsserts(recAssert, namedTypeInvAssert))
		{
			replBlock.getStatements().add(a);
		}
	}

	private List<AMetaStmCG> consSubjectAsserts(AMetaStmCG recAssert,
			List<AMetaStmCG> namedTypeInvAsserts)
	{
		List<AMetaStmCG> allAsserts = new LinkedList<AMetaStmCG>();

		add(allAsserts, recAssert);
		
		if(namedTypeInvAsserts != null)
		{
			for(AMetaStmCG a : namedTypeInvAsserts)
			{
				add(allAsserts, a);
			}
		}

		return allAsserts;
	}

	private void addStateDesAssertsAtomic(SVarExpCG target, List<AIdentifierVarExpCG> objVars)
	{
		for (AMetaStmCG a : consObjVarAsserts(target, objVars))
		{
			addPostAtomicCheck(a);
		}
	}

	private void addStateDesAsserts(SVarExpCG target, List<AIdentifierVarExpCG> objVars,
			ABlockStmCG replBlock)
	{
		for (AMetaStmCG a : consObjVarAsserts(target, objVars))
		{
			add(replBlock, a);
		}
	}

	private List<AMetaStmCG> consObjVarAsserts(
			SVarExpCG target, List<AIdentifierVarExpCG> objVars)
	{
		List<AMetaStmCG> objVarAsserts = new LinkedList<AMetaStmCG>();

		if (objVars != null)
		{
			// All of them except the last
			for (int i = 0; i < objVars.size() - 1; i++)
			{
				addAsserts(objVarAsserts, objVars.get(i));
			}
			
			if(!objVarAsserts.isEmpty())
			{
				AIdentifierVarExpCG last = objVars.get(objVars.size() - 1);
				
				if(!target.getName().equals(last.getName()))
				{
					addAsserts(objVarAsserts, last);
				}
			}
		}

		Collections.reverse(objVarAsserts);
		return objVarAsserts;
	}

	private void addAsserts(List<AMetaStmCG> objVarAsserts,
			AIdentifierVarExpCG var)
	{
		buildRecChecks = true;
		List<AMetaStmCG> asserts = namedTypeHandler.consAsserts(var);
		buildRecChecks = false;
		
		if(asserts != null)
		{
			for(AMetaStmCG a : asserts)
			{
				add(objVarAsserts, a);
			}
		}
	}

	private void add(List<AMetaStmCG> asserts, AMetaStmCG as)
	{
		if (as != null)
		{
			asserts.add(as);
		}
	}

	private void add(ABlockStmCG block, AMetaStmCG as)
	{
		if (as != null)
		{
			block.getStatements().add(as);
		}
	}
	
	public RecClassInfo getRecInfo()
	{
		return recInfo;
	}
	
	public StateDesInfo getStateDesInfo()
	{
		return stateDesInfo;
	}

	public boolean buildRecValidChecks()
	{
		return buildRecChecks;
	}

	public TypePredUtil getTypePredUtil()
	{
		return this.namedTypeHandler.getTypePredUtil();
	}
}

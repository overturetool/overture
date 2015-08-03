package org.overture.codegen.vdm2jml;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.AMapSeqUpdateStmCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;

/**
 * This class is responsible for adding additional assertions to the IR to preserve the semantics of the contract-based
 * notations of VDM-SL when they are translated to JML annotated Java.
 * 
 * @see RecAccessorTrans
 * @see TargetNormaliserTrans
 */
public class InvAssertionTrans extends AtomicAssertTrans
{
	private RecModHandler recHandler;
	private NamedTypeInvHandler namedTypeHandler;
	private Map<SStmCG, List<AIdentifierVarExpCG>> stateDesVars;

	public InvAssertionTrans(JmlGenerator jmlGen,
			Map<SStmCG, List<AIdentifierVarExpCG>> stateDesVars)
	{
		super(jmlGen);
		this.recHandler = new RecModHandler(this);
		this.namedTypeHandler = new NamedTypeInvHandler(this);
		this.stateDesVars = stateDesVars;
	}

	@Override
	public void caseACallObjectExpStmCG(ACallObjectExpStmCG node)
			throws AnalysisException
	{
		handleStateUpdate(node, stateDesVars.get(node), recHandler.handleCallObj(node), namedTypeHandler.handleCallObj(node));
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
		namedTypeHandler.handleVarDecl(node);
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
		namedTypeHandler.handleAssign(node);
	}

	@Override
	public void caseAMapSeqUpdateStmCG(AMapSeqUpdateStmCG node)
			throws AnalysisException
	{
		handleStateUpdate(node, stateDesVars.get(node), recHandler.handleMapSeq(node), namedTypeHandler.handleMapSeq(node));
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
	public void caseAClassDeclCG(AClassDeclCG node) throws AnalysisException
	{
		namedTypeHandler.handleClass(node);
	}

	private void handleStateUpdate(SStmCG node,
			List<AIdentifierVarExpCG> objVars, AMetaStmCG recAssert,
			AMetaStmCG namedTypeInvAssert)
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
			addStateDesAsserts(objVars, replBlock);
		} else
		{
			// We'll store the checks and let the atomic statement case handle the assert insertion
			addSubjectAssertAtomic(recAssert, namedTypeInvAssert);
			addStateDesAssertsAtomic(objVars);
		}
	}

	private void addSubjectAssertAtomic(AMetaStmCG recAssert,
			AMetaStmCG namedTypeInvAssert)
	{
		for (AMetaStmCG a : consSubjectAsserts(recAssert, namedTypeInvAssert))
		{
			addPostAtomicCheck(a);
		}
	}

	private void addSubjectAsserts(AMetaStmCG recAssert,
			AMetaStmCG namedTypeInvAssert, ABlockStmCG replBlock)
	{
		for (AMetaStmCG a : consSubjectAsserts(recAssert, namedTypeInvAssert))
		{
			replBlock.getStatements().add(a);
		}
	}

	private List<AMetaStmCG> consSubjectAsserts(AMetaStmCG recAssert,
			AMetaStmCG namedTypeInvAssert)
	{
		List<AMetaStmCG> asserts = new LinkedList<AMetaStmCG>();

		add(asserts, recAssert);
		add(asserts, namedTypeInvAssert);

		return asserts;
	}

	private void addStateDesAssertsAtomic(List<AIdentifierVarExpCG> objVars)
	{
		for (AMetaStmCG a : consObjVarAsserts(objVars))
		{
			addPostAtomicCheck(a);
		}
	}

	private void addStateDesAsserts(List<AIdentifierVarExpCG> objVars,
			ABlockStmCG replBlock)
	{
		for (AMetaStmCG a : consObjVarAsserts(objVars))
		{
			add(replBlock, a);
		}
	}

	private List<AMetaStmCG> consObjVarAsserts(
			List<AIdentifierVarExpCG> objVars)
	{
		List<AMetaStmCG> objVarAsserts = new LinkedList<AMetaStmCG>();

		if (objVars != null)
		{
			// Everyone except the last
			for (int i = 0; i < objVars.size() - 1; i++)
			{
				AIdentifierVarExpCG var = objVars.get(i);

				add(objVarAsserts, recHandler.consAssert(var));
				// TODO: Will the named type invariants not get handled automatically since they are local variable
				// decls.
				add(objVarAsserts, namedTypeHandler.consAssert(var));
			}
		}

		Collections.reverse(objVarAsserts);
		return objVarAsserts;
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
}

package org.overture.codegen.vdm2jml;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.AMapSeqUpdateStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.trans.AtomicStmTrans;

/**
 * In the original VDM AST assignments are on the form <stateDesignator> := <exp>; Note that the left hand side is a
 * look-up and it is side-effect free When the atomic statement ends we need to check if any type invariants (which also
 * include state invariants) have been broken.
 * 
 * @see AtomicStmTrans
 */
public class InvAssertionTrans extends AtomicAssertTrans
{
	private RecModHandler recHandler;
	private NamedTypeInvHandler namedTypeHandler;

	public InvAssertionTrans(JmlGenerator jmlGen)
	{
		super(jmlGen);
		this.recHandler = new RecModHandler(this);
		this.namedTypeHandler = new NamedTypeInvHandler(this);
	}

	@Override
	public void caseACallObjectExpStmCG(ACallObjectExpStmCG node)
			throws AnalysisException
	{
		recHandler.handleCallObj(node);
		namedTypeHandler.handleCallObj(node);
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
		 * Regarding record modifications, which will now all be on the form E.g. St = new St(..), i.e. node.getTarget()
		 * instanceof SVarExpCG && node.getTarget().getType() instanceof ARecordTypeCG Violation will be detected when
		 * constructing the record value or in the temporary variable section if the assignment occurs in the context of
		 * an atomic statement block. Therefore, there is no need to assert anything. Note that more complicated record
		 * modifications (e.g. rec1.rec2.f := 5) appear as nodes of type caseACallObjectExpStmCG
		 */

		namedTypeHandler.handleAssign(node);
	}

	@Override
	public void caseAMapSeqUpdateStmCG(AMapSeqUpdateStmCG node)
			throws AnalysisException
	{
		recHandler.handleMapSeq(node);
		namedTypeHandler.handleMapSeq(node);
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
}

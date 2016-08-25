package org.overture.codegen.trans;

import org.apache.log4j.Logger;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.AAssignmentStmIR;
import org.overture.codegen.ir.statements.AAtomicStmIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class AtomicStmTrans extends DepthFirstAnalysisAdaptor
{
	// In VDM...
	// atomic
	// (
	// sd1 := ec1;
	// ...;
	// sdN := ecN
	// )
	//
	// ..Is evaluated as:
	//
	// let t1 : T1 = ec1,
	// ...
	// tN : TN = ecN in
	// (
	// -- turn off invariants, threading and durations
	// sd1 := t1;
	// ...
	// sdN := tN;
	// -- turn on invariants, threading and durations
	// -- and check that invariants hold.
	// );

	private TransAssistantIR transAssistant;
	private String atomicPrefix;

	private Logger log = Logger.getLogger(this.getClass().getName());

	public AtomicStmTrans(TransAssistantIR transAssistant, String atomicPrefix)
	{
		this.transAssistant = transAssistant;
		this.atomicPrefix = atomicPrefix;
	}

	@Override
	public void caseAAtomicStmIR(AAtomicStmIR node) throws AnalysisException
	{
		ABlockStmIR tmpBlock = new ABlockStmIR();

		for (SStmIR stm : node.getStatements())
		{
			if (stm instanceof AAssignmentStmIR)
			{
				AAssignmentStmIR assign = (AAssignmentStmIR) stm;

				// Build temporary variable
				String name = transAssistant.getInfo().getTempVarNameGen().nextVarName(atomicPrefix);
				STypeIR type = assign.getTarget().getType().clone();
				AIdentifierPatternIR pattern = transAssistant.getInfo().getPatternAssistant().consIdPattern(name);
				SExpIR exp = assign.getExp().clone();

				AVarDeclIR varDecl = transAssistant.getInfo().getDeclAssistant().consLocalVarDecl(type, pattern, exp);

				// Add temporary variable to the block
				tmpBlock.getLocalDefs().add(varDecl);

				// Assign state designator to temporary variable
				AIdentifierVarExpIR tmpVarExp = transAssistant.getInfo().getExpAssistant().consIdVar(name, type.clone());
				transAssistant.replaceNodeWith(assign.getExp(), tmpVarExp);
			} else
			{
				log.error("Expected all statements in atomic statement block to be assignments. Got: "
						+ stm);
			}
		}

		// Replace the atomic statement with the 'let' statement and make
		// the atomic statement its body
		transAssistant.replaceNodeWith(node, tmpBlock);
		tmpBlock.getStatements().add(node);
	}
}

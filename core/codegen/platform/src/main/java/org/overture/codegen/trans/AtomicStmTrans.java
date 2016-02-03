package org.overture.codegen.trans;

import org.overture.codegen.ir.SExpCG;
import org.overture.codegen.ir.SStmCG;
import org.overture.codegen.ir.STypeCG;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AVarDeclCG;
import org.overture.codegen.ir.expressions.AIdentifierVarExpCG;
import org.overture.codegen.ir.patterns.AIdentifierPatternCG;
import org.overture.codegen.ir.statements.AAssignmentStmCG;
import org.overture.codegen.ir.statements.AAtomicStmCG;
import org.overture.codegen.ir.statements.ABlockStmCG;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class AtomicStmTrans extends DepthFirstAnalysisAdaptor
{
	// In VDM...
	// atomic
	// (
	//   sd1 := ec1;
	//   ...;
	//   sdN := ecN
	// )
	//
	// ..Is evaluated as:
	//
	// let t1 : T1 = ec1,
	// ...
	// tN : TN = ecN in
	// (
	//   -- turn off invariants, threading and durations
	//   sd1 := t1;
	//   ...
	//   sdN := tN;
	//   -- turn on invariants, threading and durations
	//   -- and check that invariants hold.
	// );

	private TransAssistantCG transAssistant;
	private String atomicPrefix;

	public AtomicStmTrans(TransAssistantCG transAssistant, String atomicPrefix)
	{
		this.transAssistant = transAssistant;
		this.atomicPrefix = atomicPrefix;
	}

	@Override
	public void caseAAtomicStmCG(AAtomicStmCG node) throws AnalysisException
	{
		ABlockStmCG tmpBlock = new ABlockStmCG();

		for (SStmCG stm : node.getStatements())
		{
			if (stm instanceof AAssignmentStmCG)
			{
				AAssignmentStmCG assign = (AAssignmentStmCG) stm;
				
				// Build temporary variable
				String name = transAssistant.getInfo().getTempVarNameGen().nextVarName(atomicPrefix);
				STypeCG type = assign.getTarget().getType().clone();
				AIdentifierPatternCG pattern = transAssistant.getInfo().getPatternAssistant().consIdPattern(name);
				SExpCG exp = assign.getExp().clone();

				AVarDeclCG varDecl = transAssistant.getInfo().getDeclAssistant().consLocalVarDecl(type, pattern, exp);
				
				// Add temporary variable to the block
				tmpBlock.getLocalDefs().add(varDecl);
				
				// Assign state designator to temporary variable
				AIdentifierVarExpCG tmpVarExp = transAssistant.getInfo().getExpAssistant().consIdVar(name, type.clone());
				transAssistant.replaceNodeWith(assign.getExp(), tmpVarExp);
			} else
			{
				Logger.getLog().printErrorln("Expected all statements in atomic "
						+ "statement block to be assignments. Got: " + stm);
			}
		}
		
		// Replace the atomic statement with the 'let' statement and make
		// the atomic statement its body
		transAssistant.replaceNodeWith(node, tmpBlock);
		tmpBlock.getStatements().add(node);
	}
}

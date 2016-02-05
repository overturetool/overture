package org.overturetool.cgisa.transformations;

import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.AModuleDeclIR;
import org.overture.codegen.ir.declarations.AStateDeclIR;
import org.overture.codegen.ir.expressions.AEqualsBinaryExpIR;
import org.overture.codegen.ir.statements.AAtomicStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.IRInfo;

public class StateInit extends DepthFirstAnalysisAdaptor
{
	private IRInfo info;

	public StateInit(IRInfo info)
	{
		this.info = info;
	}

	@Override
	public void caseAStateDeclIR(AStateDeclIR node) throws AnalysisException
	{
		if (node.getInitDecl() != null)
		{
			AMethodDeclIR initOp;
			if (node.getExecutable())
			{
				initOp = makeExecutableInit(node);
			} else
			{
				initOp = info.getDeclAssistant().funcToMethod(node.getInitDecl());
			}

			AModuleDeclIR module = node.getAncestor(AModuleDeclIR.class);
			module.getDecls().add(initOp);
			node.setInitDecl(null);
		}
	}

	private AMethodDeclIR makeExecutableInit(AStateDeclIR node)
	{
		AMethodDeclIR meth = info.getDeclAssistant().funcToMethod(node.getInitDecl());
		AReturnStmIR ret = new AReturnStmIR();

		AEqualsBinaryExpIR initExp = (AEqualsBinaryExpIR) node.getInitExp();
		ret.setExp(initExp.getRight().clone());

		meth.setBody(ret);
		return meth;
	}
}

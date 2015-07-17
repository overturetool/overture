package org.overturetool.cgisa.transformations;

import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.AModuleDeclCG;
import org.overture.codegen.cgast.declarations.AStateDeclCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.statements.AAtomicStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.ir.IRInfo;

public class StateInit extends DepthFirstAnalysisAdaptor
{
	private IRInfo info;

	public StateInit(IRInfo info)
	{
		this.info = info;
	}

	@Override
	public void caseAStateDeclCG(AStateDeclCG node) throws AnalysisException
	{
		if (node.getInitDecl() != null)
		{
			AMethodDeclCG initOp;
			if (node.getExecutable())
			{
				initOp = makeExecutableInit(node);
			} else
			{
				initOp = info.getDeclAssistant().funcToMethod(node.getInitDecl());
			}

			AModuleDeclCG module = node.getAncestor(AModuleDeclCG.class);
			module.getDecls().add(initOp);
			node.setInitDecl(null);
		}
	}

	private AMethodDeclCG makeExecutableInit(AStateDeclCG node)
	{
		AMethodDeclCG meth = info.getDeclAssistant().funcToMethod(node.getInitDecl());
		AReturnStmCG ret = new AReturnStmCG();

		AEqualsBinaryExpCG initExp = (AEqualsBinaryExpCG) node.getInitExp();
		ret.setExp(initExp.getRight().clone());

		meth.setBody(ret);
		return meth;
	}
}

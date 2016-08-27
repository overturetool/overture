package org.overture.codegen.vdm2java;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.ASystemClassDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;

public class ImportsTrans extends DepthFirstAnalysisAdaptor
{
	private IRInfo info;

	public ImportsTrans(IRInfo info)
	{
		this.info = info;
	}

	@Override
	public void caseADefaultClassDeclIR(ADefaultClassDeclIR node)
			throws AnalysisException
	{
		handleClass(node);
	}

	@Override
	public void caseASystemClassDeclIR(ASystemClassDeclIR node)
			throws AnalysisException
	{
		handleClass(node);
	}

	private void handleClass(SClassDeclIR node)
	{
		List<ClonableString> dep = new LinkedList<>();

		if (!info.getDeclAssistant().isInnerClass(node))
		{
			dep.add(new ClonableString(JavaCodeGen.JAVA_UTIL));
			dep.add(new ClonableString(JavaCodeGen.RUNTIME_IMPORT));
		} else if (!info.getDeclAssistant().isInnerClass(node) && isQuote(node))
		{
			dep.add(new ClonableString(JavaCodeGen.RUNTIME_IMPORT));
		}

		if (importTraceSupport(node))
		{
			dep.add(new ClonableString(JavaCodeGen.TRACE_IMPORT));
		}

		node.setDependencies(dep);
	}

	public static boolean isQuote(SClassDeclIR classCg)
	{
		return classCg != null
				&& JavaCodeGen.JAVA_QUOTES_PACKAGE.equals(classCg.getPackage());
	}

	public boolean importTraceSupport(SClassDeclIR node)
	{
		return info.getSettings().generateTraces()
				&& !node.getTraces().isEmpty();
	}
}

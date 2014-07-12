package org.overture.codegen.trans.patterns;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.patterns.AIgnorePatternCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.trans.assistants.BaseTransformationAssistant;

public class IgnorePatternTransformation extends DepthFirstAnalysisAdaptor
{
	private IRInfo info;
	private BaseTransformationAssistant baseAssistant;
	private final String NAME_PREFIX;
	
	public IgnorePatternTransformation(IRInfo info, BaseTransformationAssistant baseAssistant, String namePrefix)
	{
		this.info = info;
		this.baseAssistant = baseAssistant;
		this.NAME_PREFIX = namePrefix;
	}

	@Override
	public void inAIgnorePatternCG(AIgnorePatternCG node)
			throws AnalysisException
	{
		String name = info.getTempVarNameGen().nextVarName(NAME_PREFIX);
		
		AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
		idPattern.setName(name);
		
		baseAssistant.replaceNodeWith(node, idPattern);
	}
}

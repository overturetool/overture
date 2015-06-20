package org.overture.codegen.vdm2cpp.vdmtools;

import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SObjectDesignatorCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.ACallObjectStmCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.conv.ObjectDesignatorToExpCG;

public class CallObjStmConverter extends DepthFirstAnalysisAdaptor {
	private TransAssistantCG transAssistant;
	private ObjectDesignatorToExpCG converter;

	public CallObjStmConverter(TransAssistantCG transAssistant, IRInfo info,
			List<AClassDeclCG> classes) {
		this.transAssistant = transAssistant;
		this.converter = new ObjectDesignatorToExpCG(info, classes);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void caseACallObjectStmCG(ACallObjectStmCG node)
			throws AnalysisException {
		STypeCG type = node.getType().clone();
		List<SExpCG> args = (List<SExpCG>) node.getArgs().clone();
		SObjectDesignatorCG obj = node.getDesignator();
		String fieldName = node.getFieldName();
		SourceNode sourceNode = node.getSourceNode();
		Object tag = node.getTag();

		SExpCG convertedObj = obj.apply(converter);
		ACallObjectExpStmCG callStm = new ACallObjectExpStmCG();
		callStm.setType(type.clone());
		callStm.setArgs(args);
		callStm.setObj(convertedObj);
		callStm.setFieldName(fieldName);
		callStm.setSourceNode(sourceNode);
		callStm.setTag(tag);
		transAssistant.replaceNodeWith(node, callStm);
	}
}
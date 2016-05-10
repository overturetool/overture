package org.overture.codegen.mojocg.util;

import java.util.Map;

import org.apache.maven.plugin.logging.Log;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.APlainCallStmIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class DelegateTrans extends DepthFirstAnalysisAdaptor
{
	private static final String FALLBACK_PARAM_NAME_PREFIX = "_param_";

	private Map<String, String> delegateMap;
	private TransAssistantIR assist;
	private Log log;

	public DelegateTrans(Map<String, String> buidDelegateMap, TransAssistantIR assist, Log log)
	{
		this.delegateMap = buidDelegateMap;
		this.assist = assist;
		this.log = log;
	}

	@Override
	public void caseADefaultClassDeclIR(ADefaultClassDeclIR node) throws AnalysisException
	{
		if (isBridgeClass(node))
		{
			for (AMethodDeclIR m : node.getMethods())
			{
				if (isDelegateCall(m.getBody()))
				{
					log.info("Updating " + node.getName() + "." + m.getName() + " to use delegate method "
							+ getFullDelegateName(node) + "." + m.getName());
					assist.replaceNodeWith(m.getBody(), consDelegateCall(node.getName(), m));
				}
			}
		}
	}

	private boolean isBridgeClass(ADefaultClassDeclIR node)
	{
		return delegateMap.containsKey(node.getName());
	}

	private String getFullDelegateName(ADefaultClassDeclIR node)
	{
		return delegateMap.get(node.getName());
	}

	private boolean isDelegateCall(SStmIR body)
	{
		DelegateSearch search = new DelegateSearch();

		if (body != null)
		{
			try
			{
				body.apply(search);
				return search.isDelegateCall();
			} catch (AnalysisException e)
			{
				e.printStackTrace();
				log.error("Unexpected error encountered when checking "
						+ "if method is a delegate call: " + e.getMessage());
			}
		}

		return false;
	}

	private APlainCallStmIR consDelegateCall(String className, AMethodDeclIR m)
	{
		AExternalTypeIR delegateType = new AExternalTypeIR();
		delegateType.setName(delegateMap.get(className));

		APlainCallStmIR call = new APlainCallStmIR();
		call.setClassType(delegateType);
		call.setIsStatic(m.getStatic());
		call.setName(m.getName());
		call.setSourceNode(m.getBody().getSourceNode());
		call.setTag(m.getBody().getTag());
		call.setType(m.getMethodType().getResult().clone());

		for (int i = 0; i < m.getFormalParams().size(); i++)
		{
			AFormalParamLocalParamIR param = m.getFormalParams().get(i);

			SPatternIR pattern = param.getPattern();

			String argName;

			if (pattern instanceof AIdentifierPatternIR)
			{
				argName = ((AIdentifierPatternIR) pattern).getName();
			} else
			{
				// Should not happen...
				argName = FALLBACK_PARAM_NAME_PREFIX + i;
			}

			call.getArgs().add(assist.getInfo().getExpAssistant().consIdVar(argName, param.getType().clone()));
		}

		return call;
	}
}

package org.overture.codegen.mojocg.util;

import java.util.Map;

import org.apache.maven.plugin.logging.Log;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.APlainCallStmCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class DelegateTrans extends DepthFirstAnalysisAdaptor
{
	private static final String FALLBACK_PARAM_NAME_PREFIX = "_param_";

	private Map<String, String> delegateMap;
	private TransAssistantCG assist;
	private Log log;

	public DelegateTrans(Map<String, String> buidDelegateMap, TransAssistantCG assist, Log log)
	{
		this.delegateMap = buidDelegateMap;
		this.assist = assist;
		this.log = log;
	}

	@Override
	public void caseADefaultClassDeclCG(ADefaultClassDeclCG node) throws AnalysisException
	{
		if (isBridgeClass(node))
		{
			for (AMethodDeclCG m : node.getMethods())
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

	private boolean isBridgeClass(ADefaultClassDeclCG node)
	{
		return delegateMap.containsKey(node.getName());
	}

	private String getFullDelegateName(ADefaultClassDeclCG node)
	{
		return delegateMap.get(node.getName());
	}

	private boolean isDelegateCall(SStmCG body)
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

	private APlainCallStmCG consDelegateCall(String className, AMethodDeclCG m)
	{
		AExternalTypeCG delegateType = new AExternalTypeCG();
		delegateType.setName(delegateMap.get(className));

		APlainCallStmCG call = new APlainCallStmCG();
		call.setClassType(delegateType);
		call.setIsStatic(m.getStatic());
		call.setName(m.getName());
		call.setSourceNode(m.getBody().getSourceNode());
		call.setTag(m.getBody().getTag());
		call.setType(m.getMethodType().getResult().clone());

		for (int i = 0; i < m.getFormalParams().size(); i++)
		{
			AFormalParamLocalParamCG param = m.getFormalParams().get(i);

			SPatternCG pattern = param.getPattern();

			String argName = null;

			if (pattern instanceof AIdentifierPatternCG)
			{
				argName = ((AIdentifierPatternCG) pattern).getName();
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

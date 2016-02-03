package org.overture.codegen.vdm2java;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.overture.ast.util.ClonableString;
import org.overture.codegen.assistant.NodeAssistantCG;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclCG;
import org.overture.codegen.ir.declarations.AMethodDeclCG;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRGeneratedTag;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class JUnit4Trans extends DepthFirstAnalysisAdaptor
{
	private static final String TEST_NAME_PREFIX = "test";
	public static final String TEST_ANNOTATION = "@Test";
	public static final String JUNI4_IMPORT = "org.junit.*";

	public TransAssistantCG assist;
	private JavaCodeGen javaCg;

	public JUnit4Trans(TransAssistantCG assist, JavaCodeGen javaCg)
	{
		this.assist = assist;
		this.javaCg = javaCg;
	}

	@Override
	public void caseADefaultClassDeclCG(ADefaultClassDeclCG node) throws AnalysisException
	{
		if (!javaCg.getJavaSettings().genJUnit4tests())
		{
			return;
		}

		if (!isTest(node))
		{
			return;
		}

		/**
		 * 1) Delete runFullSuite method with no parameters
		 */
		removeRunFullSuiteMethod(node);
		/**
		 * 2) Remove TestCase super class
		 */
		node.getSuperNames().clear();
		/**
		 * 3) Import JUnit4
		 */
		importJunit4(node);
		/**
		 * 4) Add the '@Test' annotation to public non-static, non-constructor methods that take no arguements and start
		 * with 'test'
		 */
		addTestAnnotations(node);
	}

	public boolean isTest(ADefaultClassDeclCG node)
	{
		return !node.getSuperNames().isEmpty() && node.getSuperNames().get(0).getName().equals(IRConstants.TEST_CASE);
	}

	public void addTestAnnotations(ADefaultClassDeclCG node)
	{
		NodeAssistantCG nodeAssist = assist.getInfo().getNodeAssistant();

		for (AMethodDeclCG m : node.getMethods())
		{
			if (m.getName().startsWith(TEST_NAME_PREFIX) && m.getAccess().equals(IRConstants.PUBLIC) && BooleanUtils.isFalse(m.getStatic())
					&& BooleanUtils.isFalse(m.getIsConstructor()) && m.getFormalParams().isEmpty()
					&& !(m.getTag() instanceof IRGeneratedTag))
			{
				nodeAssist.addMetaData(m, str2meta(TEST_ANNOTATION), false);
			}
		}
	}

	public void importJunit4(ADefaultClassDeclCG node)
	{
		assist.getInfo().getDeclAssistant().addDependencies(node, str2meta(JUNI4_IMPORT), false);
	}

	public void removeRunFullSuiteMethod(ADefaultClassDeclCG node)
	{
		for (int i = 0; i < node.getMethods().size(); i++)
		{
			AMethodDeclCG currentMethod = node.getMethods().get(i);

			if (currentMethod.getName().equals(IRConstants.TEST_CASE_RUN_FULL_SUITE)
					&& currentMethod.getFormalParams().isEmpty())
			{
				node.getMethods().remove(i);
				break;
			}
		}
	}

	public List<ClonableString> str2meta(String str)
	{
		List<ClonableString> extraMetaData = new LinkedList<>();

		extraMetaData.add(new ClonableString(str));
		return extraMetaData;
	}
}

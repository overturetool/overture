package org.overture.codegen.tests;

import java.io.File;

import org.apache.velocity.runtime.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.declarations.ADefaultClassDeclCG;
import org.overture.codegen.ir.expressions.APlusNumericBinaryExpCG;
import org.overture.codegen.merging.TemplateData;
import org.overture.codegen.merging.TemplateManager;

public class TemplateManagerTest
{
	public static final String TEST_ROOT = "myRoot";
	private static final String TEST_TEMPLATE = TEST_ROOT + File.separator + "TestTemplate.vm";

	@Test
	public void derivePath()
	{
		Class<? extends INode> nodeClass = ADefaultClassDeclCG.class;

		String relPath = TemplateManager.derivePath(TEST_ROOT, nodeClass);

		Assert.assertEquals("Got unexpected relative path for " + nodeClass, nodePath(nodeClass), relPath);
	}

	@Test
	public void templateExists()
	{
		TemplateManager manager = new TemplateManager(TEST_ROOT, TemplateManagerTest.class);

		Class<? extends INode> nodeClass = ADefaultClassDeclCG.class;

		String expectNoTemplate = "Expected no template to be found";

		try
		{
			Assert.assertNull(expectNoTemplate, manager.getTemplate(nodeClass));
		} catch (ParseException e)
		{
			Assert.fail(expectNoTemplate);
		}
		
		manager.setUserTemplatePath(manager.getTemplateLoaderRef(), nodeClass, TEST_TEMPLATE);

		String expectTemplate = "Expected template to be found";
		try
		{
			Assert.assertNotNull(expectTemplate, manager.getTemplate(nodeClass));
		} catch (ParseException e)
		{
			Assert.fail(expectTemplate);
		}
	}

	@Test
	public void reuseDerivedTemplate()
	{
		TemplateManager manager = new TemplateManager(TEST_ROOT);

		Class<ADefaultClassDeclCG> clazz = ADefaultClassDeclCG.class;

		Assert.assertFalse("Expected no user-defined template file for node "
				+ clazz, manager.isUserDefined(clazz));

		manager.setUserTemplatePath(manager.getTemplateLoaderRef(), clazz, TEST_TEMPLATE);

		Assert.assertTrue("Expected a user-defined template file for node " + clazz
				+ " by now", manager.isUserDefined(clazz));

		Class<APlusNumericBinaryExpCG> plus = APlusNumericBinaryExpCG.class;

		Assert.assertFalse("Expected no user-defined template file for node "
				+ plus, manager.isUserDefined(plus));

		TemplateData t = manager.getTemplateData(clazz);
		
		manager.setUserTemplatePath(manager.getTemplateLoaderRef(), plus, t.getTemplatePath());

		Assert.assertTrue("Expected node " + plus + " to have reused " + clazz
				+ "'s template", manager.getTemplateData(plus).getTemplatePath() == TEST_TEMPLATE);
	}

	private String nodePath(Class<? extends INode> node)
	{
		return TEST_ROOT + File.separator + node.getName().replace('.', File.separatorChar)
				+ TemplateManager.TEMPLATE_FILE_EXTENSION;
	}
}

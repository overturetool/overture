package org.overture.codegen.tests;

import java.io.File;

import org.apache.velocity.runtime.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.expressions.APlusNumericBinaryExpCG;
import org.overture.codegen.merging.TemplateManager;

public class TemplateManagerTest
{
	public static final String TEST_ROOT = "myRoot";
	private static final String REL_PATH = TEST_ROOT + File.separator + "TestTemplate.vm";

	@Test
	public void derivePath()
	{
		TemplateManager manager = new TemplateManager(TEST_ROOT);

		Class<? extends INode> nodeClass = ADefaultClassDeclCG.class;

		String relPath = manager.derivePath(nodeClass);

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
		
		manager.setUserDefinedPath(nodeClass, REL_PATH);

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

		manager.setUserDefinedPath(clazz, REL_PATH);

		Assert.assertTrue("Expected a user-defined template file for node " + clazz
				+ " by now", manager.isUserDefined(clazz));

		Class<APlusNumericBinaryExpCG> plus = APlusNumericBinaryExpCG.class;

		Assert.assertFalse("Expected no user-defined template file for node "
				+ plus, manager.isUserDefined(plus));

		manager.setUserDefinedPath(plus, manager.getRelativePath(clazz));

		Assert.assertTrue("Expected node " + plus + " to have reused " + clazz
				+ "'s template", manager.getRelativePath(plus) == REL_PATH);
	}

	private String nodePath(Class<? extends INode> node)
	{
		return TEST_ROOT + File.separator + node.getName().replace('.', File.separatorChar)
				+ TemplateManager.TEMPLATE_FILE_EXTENSION;
	}
}

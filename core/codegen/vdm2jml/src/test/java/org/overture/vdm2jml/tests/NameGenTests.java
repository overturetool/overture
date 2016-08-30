package org.overture.vdm2jml.tests;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.vdm2jml.util.NameGen;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class NameGenTests
{
	public static final String MSG = "Got unexpected name suggestion from name generator";

	public static final String TEST_FILE = "src" + File.separatorChar + "test"
			+ File.separatorChar + "resources" + File.separatorChar
			+ "NameGenTests.vdmpp";

	private NameGen nameGen;

	@Before
	public void init()
	{
		Settings.dialect = Dialect.VDM_PP;
		TypeCheckResult<List<SClassDefinition>> res = TypeCheckerUtil.typeCheckPp(new File(TEST_FILE));

		Assert.assertTrue("Expected model to type check without errors: "
				+ res, res.parserResult.errors.isEmpty()
						&& res.errors.isEmpty());

		Assert.assertTrue("Expected only a single class definition", res.result.size() == 1);

		this.nameGen = new NameGen(res.result.get(0));
	}

	@Test
	public void notTaken()
	{
		Assert.assertEquals(MSG, "x", nameGen.getName("x"));
	}

	@Test
	public void suggestSameNameTwice()
	{
		nameGen.getName("x");
		Assert.assertEquals(MSG, "x_1", nameGen.getName("x"));
	}

	@Test
	public void suggestSameNameThreeTimes()
	{
		nameGen.getName("x");
		nameGen.getName("x");
		Assert.assertEquals(MSG, "x_2", nameGen.getName("x"));
	}

	@Test
	public void suggestField()
	{
		Assert.assertEquals(MSG, "field_1", nameGen.getName("field"));
	}

	@Test
	public void addExtraName()
	{
		nameGen.addName("x");
		Assert.assertEquals(MSG, "x_1", nameGen.getName("x"));
	}

	@Test
	public void suggestLocalName()
	{
		Assert.assertEquals(MSG, "i_1", nameGen.getName("i"));
	}
}

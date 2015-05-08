package org.overture.vdm2jml.tests;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.vdm2java.JavaToolsUtils;
import org.overture.codegen.vdm2jml.IOpenJmlConsts;

@RunWith(Parameterized.class)
public class JmlPassTypeCheckTests extends OpenJmlValidationBase
{
	private static final String TEST_INPUT_FOLDER = "src" + File.separatorChar
			+ "test" + File.separatorChar + "resources";

	public JmlPassTypeCheckTests(File inputFile)
	{
		this.inputFile = inputFile;
	}

	@Parameters(name = "{index}: {0}")
	public static Collection<Object[]> data()
	{
		List<File> files = GeneralUtils.getFiles(new File(TEST_INPUT_FOLDER));

		return collectVdmslFiles(files);
	}
	
	@Test
	public void typeCheckJml()
	{
		tcFiles();
	}
	
	@Override
	public String[] getOpenJmlConfig(File openJml, File cgRuntime)
	{
		return new String[] { JavaToolsUtils.JAVA, JavaToolsUtils.JAR_ARG,
				openJml.getAbsolutePath(), IOpenJmlConsts.CP_ARG,
				cgRuntime.getAbsolutePath(), IOpenJmlConsts.TC };
	}
}

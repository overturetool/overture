package org.overture.codegen.tests.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.overture.codegen.constants.IRConstants;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.config.Release;

public class ExecutableSpecTestHandler extends EntryBasedTestHandler
{
	public ExecutableSpecTestHandler(Release release)
	{
		super(release);
	}

	@Override
	public void writeGeneratedCode(File parent, File resultFile) throws IOException
	{
		injectArgIntoMainClassFile(parent, JAVA_ENTRY_CALL);
		
		List<StringBuffer> content = TestUtils.readJavaModulesFromResultFile(resultFile);

		if (content.isEmpty())
		{
			System.out.println("Got no clases for: " + resultFile.getName());
			return;
		}

		parent.mkdirs();

		for (StringBuffer classCgStr : content)
		{
			String className = TestUtils.getJavaModuleName(classCgStr);
			File tempFile = consTempFile(className, parent, classCgStr);
			
			injectSerializableInterface(classCgStr, className);

			writeToFile(classCgStr.toString(), tempFile);
		}		
	}

	private void injectSerializableInterface(StringBuffer classCgStr, String className)
	{
		if(!className.equals(IRConstants.QUOTES_INTERFACE_NAME) && !className.startsWith(JavaCodeGen.INTERFACE_NAME_PREFIX))
		{
			int classNameIdx = classCgStr.indexOf(className);
			
			int prv = classCgStr.indexOf("private");
			int pub = classCgStr.indexOf("public");
			int abstr = classCgStr.indexOf("abstract");
			
			int min = prv >= 0 && prv < pub ? prv : pub;
			min = abstr >= 0  && abstr < min ? abstr : min;
			
			if(min < 0)
			{
				min = classNameIdx;
			}
			
			int firstLeftBraceIdx = classCgStr.indexOf("{", classNameIdx);
			
			String toReplace = classCgStr.substring(min, firstLeftBraceIdx);
			
			String replacement = "import java.io.*;\n\n" + 
								 toReplace + " implements Serializable";
			
			classCgStr.replace(min, firstLeftBraceIdx, replacement);
		}
	}
}

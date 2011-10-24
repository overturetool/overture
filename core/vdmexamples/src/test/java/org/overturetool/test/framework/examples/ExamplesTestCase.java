package org.overturetool.test.framework.examples;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.overturetool.test.framework.BaseTestCase;
import org.overturetool.vdmj.definitions.ClassDefinition;

public class ExamplesTestCase extends BaseTestCase
{
	public ExamplesTestCase()
	{
	}

	public ExamplesTestCase(File file)
	{
		super(file);
	}

	@Override
	public void test() throws Exception
	{
		if (mode != ContentModed.File)
		{
			return;
		}
		System.out.println(file.getName());
		System.out.println(getReadme());
	}

	public Set<File> getSpecFiles(String extension, File root)
	{
		Set<File> files = new HashSet<File>();
		for (File chld : root.listFiles())
		{
			if (chld.isDirectory())
			{
				files.addAll(getSpecFiles(extension, chld));
			} else if (chld.getName().endsWith(extension))
			{
				files.add(chld);
			}
		}
		return files;
	}

	protected VdmReadme getReadme()
	{
		File readme = null;

		if (file != null)
		{

			for (File chld : file.listFiles())
			{
				if (chld.getName().equals("README.txt"))
				{
					readme = chld;
				}
			}
		}
		if (readme == null)
		{
			return null;
		}
		return new VdmReadme(readme, name, true);
	}

	
	protected void compareResults(Set<Result<List<ClassDefinition>>> parse)
	{
		
		
	}
}

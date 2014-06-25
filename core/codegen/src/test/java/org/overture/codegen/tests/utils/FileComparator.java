package org.overture.codegen.tests.utils;

import java.io.File;
import java.util.Comparator;

public class FileComparator implements Comparator<File>
{
	@Override
	public int compare(File leftFile, File rightFile)

	{
		return leftFile.getName().compareTo(rightFile.getName());
	}
}
package org.overture.refactor.tests.base;

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

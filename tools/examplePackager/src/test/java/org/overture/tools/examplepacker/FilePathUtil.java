package org.overture.tools.examplepacker;

import java.io.File;

public class FilePathUtil
{
public static String getPlatformPath(String path)
{
	return path.replace('\\', '/').replace('/', File.separatorChar);
}
}

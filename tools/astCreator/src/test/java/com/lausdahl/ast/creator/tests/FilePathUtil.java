package com.lausdahl.ast.creator.tests;

import java.io.File;

public class FilePathUtil
{
public static String getPlatformPath(String path)
{
	return path.replace('\\', '/').replace('/', File.separatorChar);
}
}

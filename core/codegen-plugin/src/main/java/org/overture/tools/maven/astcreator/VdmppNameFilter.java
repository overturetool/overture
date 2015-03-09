package org.overture.tools.maven.astcreator;

import java.io.File;
import java.io.FilenameFilter;

public class VdmppNameFilter implements FilenameFilter
{

	@Override
	public boolean accept(File arg0, String arg1)
	{
		return arg1.endsWith(".vdmpp");
	}

}

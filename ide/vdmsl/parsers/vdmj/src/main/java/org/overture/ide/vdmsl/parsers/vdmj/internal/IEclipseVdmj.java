package org.overture.ide.vdmsl.parsers.vdmj.internal;

import java.io.File;
import java.util.List;

import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.modules.ModuleList;

public interface IEclipseVdmj
{
public ExitStatus parse(List<File> files);
	
	//public ExitStatus parse(String content);
public ExitStatus parse(File fileName);
	
	public ExitStatus typeCheck();
	
	public List<VDMError> getTypeErrors();
	
	public List<VDMWarning> getTypeWarnings();
	
	public List<VDMError> getParseErrors();
	
	public List<VDMWarning> getParseWarnings();
	
	public ModuleList getModules();
}

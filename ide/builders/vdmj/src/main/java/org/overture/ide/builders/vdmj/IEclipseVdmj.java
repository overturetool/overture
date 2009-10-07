package org.overture.ide.builders.vdmj;

import java.util.List;

import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;

public interface IEclipseVdmj {

//	public ExitStatus parse(List<File> files);
//	
//	public ExitStatus parse(String content);
	
	public ExitStatus typeCheck();
	
	public List<VDMError> getTypeErrors();
	
	public List<VDMWarning> getTypeWarnings();
	
	public List<VDMError> getParseErrors();
	
	public List<VDMWarning> getParseWarnings();
}

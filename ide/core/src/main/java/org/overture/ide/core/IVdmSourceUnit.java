package org.overture.ide.core;
//ICompilationUnit
import java.io.File;
import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.overture.ide.core.ast.NotAllowedException;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.modules.ModuleList;

public interface IVdmSourceUnit extends IVdmElement{

	public static final int VDM_DEFAULT  = 1;
	public static final int VDM_CLASS_SPEC  = 1;
	public static final int VDM_MODULE_SPEC = 2;
	
	public int getType();
	public void setType(int type);
	
	public IFile getFile();
	
	public File getSystemFile();
	
	public  void reconcile(List parseResult,boolean parseErrors);
	
	public  List getParseList();

	public boolean exists();

}
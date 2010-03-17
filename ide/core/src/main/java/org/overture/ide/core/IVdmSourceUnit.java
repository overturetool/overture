package org.overture.ide.core;
//ICompilationUnit
import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;

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
	public void clean();
	public abstract IVdmProject getProject();

}
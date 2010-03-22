package org.overture.ide.core;
//ICompilationUnit
import java.io.File;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.overturetool.vdmj.ast.IAstNode;
import org.overturetool.vdmj.lex.LexLocation;

public interface IVdmSourceUnit extends IVdmElement{

	public static final int VDM_DEFAULT  = 1;
	public static final int VDM_CLASS_SPEC  = 1;
	public static final int VDM_MODULE_SPEC = 2;
	public static final int VDM_MODULE_SPEC_FLAT = 3;
	
	public int getType();
	public void setType(int type);
	
	public IFile getFile();
	
	public File getSystemFile();
	
	public  void reconcile(List<IAstNode> parseResult,List<LexLocation> allLocation, Map<LexLocation,IAstNode> locationToAstNodeMap,boolean parseErrors);
	
	public  List<IAstNode> getParseList();

	public boolean exists();
	public void clean();
	public abstract IVdmProject getProject();
	

	public abstract boolean hasParseTree();

	public abstract Map<LexLocation, IAstNode> getLocationToAstNodeMap();
}
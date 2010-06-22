package org.overture.ide.core.resources;

import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.overture.ide.core.ElementChangedEvent;
import org.overture.ide.core.IVdmElementDelta;
import org.overture.ide.core.VdmCore;
import org.overture.ide.core.VdmElementDelta;
import org.overturetool.vdmj.ast.IAstNode;
import org.overturetool.vdmj.lex.LexLocation;

public class VdmSourceUnit implements IVdmSourceUnit 
{
	protected IVdmProject project;
	protected IFile file;
	protected int type;
	protected	List<LexLocation> allLocation = new Vector<LexLocation>();
	protected	Map<LexLocation, IAstNode> locationToAstNodeMap = new Hashtable<LexLocation, IAstNode>();
	protected boolean parseErrors= false;
	


	protected List<IAstNode> parseList = new Vector<IAstNode>();

	public VdmSourceUnit(IVdmProject project, IFile file) {
		this.project = project;
		this.file = file;
		this.type = IVdmSourceUnit.VDM_DEFAULT;
		
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public IFile getFile()
	{
		return file;
	}

	public File getSystemFile()
	{
		return project.getFile(file);
	}

	public synchronized void reconcile(List<IAstNode> parseResult,
			List<LexLocation> allLocation,
			Map<LexLocation, IAstNode> locationToAstNodeMap, boolean parseErrors)
	{
		this.parseList.clear();
		this.allLocation.clear();
		this.locationToAstNodeMap.clear();
this.parseErrors = parseErrors;
		if (!parseErrors)
		{
			this.parseList.addAll(parseResult);
			this.allLocation.addAll(allLocation);
			this.locationToAstNodeMap.putAll(locationToAstNodeMap);
		}

		// for (LexLocation lexLocation : allLocation)
		// {
		// if (locationToAstNodeMap.containsKey((lexLocation)))
		// System.out.println(locationToAstNodeMap.get(lexLocation)
		// .getName()
		// + " - "
		// + lexLocation.startLine
		// + ":"
		// + lexLocation.startPos
		// + " - "
		// + lexLocation.endLine
		// + ":" + lexLocation.endPos);
		// else
		// System.out.println(lexLocation.startLine + ":"
		// + lexLocation.startPos + " - " + lexLocation.endLine
		// + ":" + lexLocation.endPos);
		// }

		// VdmModelManager.getInstance().update(project, parseList);
		fireChangedEvent();
		// file.getLocation().toFile().getAbsolutePath()
		// IVdmModelManager astManager = VdmModelManager.instance();
		// astManager.update(project, project.getVdmNature(), ast);
		// IVdmSourceUnit rootNode = astManager.getRootNode(project, natureId);
		// if (rootNode != null)
		// {
		//
		// rootNode.setParseCorrect(filePath, !parseErrorsOccured);
		//
		// }
	}

	protected void fireChangedEvent()
	{
		VdmCore.getDeltaProcessor().fire(this,
				new ElementChangedEvent(new VdmElementDelta(this,
						IVdmElementDelta.CHANGED),
						ElementChangedEvent.DeltaType.POST_RECONCILE));
	}

	public synchronized List<IAstNode> getParseList()
	{
		return this.parseList;
	}

	public boolean exists()
	{
		return this.file.exists();
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public int getElementType()
	{
		return getType();
	}

	@Override
	public String toString()
	{
		return file.toString();
	}

	public synchronized void clean()
	{
		this.parseList.clear();

	}

	public IVdmProject getProject()
	{
		return project;
	}

	

	
	public boolean hasParseTree()
	{
		return parseList.size() > 0;
	}

	public synchronized Map<LexLocation, IAstNode> getLocationToAstNodeMap()
	{
		return locationToAstNodeMap;
	}

	public boolean hasParseErrors()
	{
		return this.parseErrors;
	}

	public VdmSourceUnitWorkingCopy getWorkingCopy()
	{
		return new VdmSourceUnitWorkingCopy(this);
	}


}

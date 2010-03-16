package org.overture.ide.core;

import java.io.File;
import java.lang.annotation.ElementType;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IFile;

import org.overture.ide.internal.core.ast.VdmModelManager;

public class VdmSourceUnit implements IVdmSourceUnit
{
	private IVdmProject project;
	private IFile file;
	private int type;

	private List parseList = new Vector();

	public VdmSourceUnit(IVdmProject project, IFile file) {
		this.project = project;
		this.file = file;
		this.type = IVdmSourceUnit.VDM_DEFAULT;
	
	}

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

	public synchronized void reconcile(List parseResult, boolean parseErrors)
	{
		this.type = type;
		this.parseList.clear();
		this.parseList.addAll(parseResult);

		VdmModelManager.getInstance().update(project, parseList);
		VdmCore.getDeltaProcessor().fire(this,
				new ElementChangedEvent(new VdmElementDelta(this,
						IVdmElementDelta.CHANGED),
						ElementChangedEvent.DeltaType.POST_RECONCILE));
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

	public synchronized List getParseList()
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
	

	

}

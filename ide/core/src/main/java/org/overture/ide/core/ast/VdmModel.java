package org.overture.ide.core.ast;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.overture.ide.core.ElementChangedEvent;
import org.overture.ide.core.IVdmElement;
import org.overture.ide.core.IVdmElementDelta;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.VdmCore;
import org.overture.ide.core.VdmElementDelta;
import org.overture.ide.core.parser.SourceParserManager;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overturetool.vdmj.ast.IAstNode;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.modules.ModuleList;

public class VdmModel implements IVdmModel
{
	static int count = 0;
	int id;
	protected boolean typeChecked = false;
	//private Hashtable<String, Boolean> parseCurrectTable = new Hashtable<String, Boolean>();

	protected Date checkedTime;

	protected List<IVdmSourceUnit> vdmSourceUnits = new Vector<IVdmSourceUnit>();

	public VdmModel()
	{
		// TODO Auto-generated constructor stub
		count++;
		id = count;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.ide.core.ast.IVdmElement#getRootElementList()
	 */
	public synchronized List<IAstNode> getRootElementList()
	{
		List<IAstNode> list = new Vector<IAstNode>();
		for (IVdmSourceUnit unit : vdmSourceUnits)
		{
			list.addAll(unit.getParseList());
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.ide.core.ast.IVdmElement#getCheckedTime()
	 */
	public synchronized Date getCheckedTime()
	{
		return checkedTime;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.ide.core.ast.IVdmElement#setChecked(boolean)
	 */
	public synchronized void setChecked(boolean checked)
	{

		this.typeChecked = checked;
		this.checkedTime = new Date();
		if (checked == true)
		{
			fireModelCheckedEvent();
		}
	}

	protected void fireModelCheckedEvent()
	{
		VdmCore.getDeltaProcessor().fire(this, new ElementChangedEvent(new VdmElementDelta(this, IVdmElementDelta.F_TYPE_CHECKED), ElementChangedEvent.DeltaType.POST_RECONCILE));
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.ide.core.ast.IVdmElement#isChecked()
	 */
	public synchronized boolean isTypeCorrect()
	{
		return typeChecked;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.ide.core.ast.IVdmElement#hasFile(java.io.File)
	 */
	public synchronized boolean hasFile(File file)
	{
		Assert.isNotNull(null);
		return false;
	}

	public synchronized boolean hasFile(IVdmSourceUnit file)
	{
		return this.vdmSourceUnits.contains(file);

	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.ide.core.ast.IVdmElement#getModuleList()
	 */
	public synchronized ModuleList getModuleList() throws NotAllowedException
	{
		ModuleList modules = new ModuleList();
		for (Object definition : getRootElementList())
		{
			if (definition instanceof Module)
				modules.add((Module) definition);
			else
				throw new NotAllowedException("Other definition than Module is found: "
						+ definition.getClass().getName());
		}
		return modules;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.ide.core.ast.IVdmElement#getClassList()
	 */
	public synchronized ClassList getClassList() throws NotAllowedException
	{
		ClassList classes = new ClassList();
		for (Object definition : getRootElementList())
		{
			if (definition instanceof ClassDefinition)
				classes.add((ClassDefinition) definition);
			else
				throw new NotAllowedException("Other definition than ClassDefinition is found: "
						+ definition.getClass().getName());
		}
		return classes;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.ide.core.ast.IVdmElement#hasClassList()
	 */
	public synchronized boolean hasClassList()
	{
		for (Object definition : getRootElementList())
		{
			if (definition instanceof ClassDefinition)
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.ide.core.ast.IVdmElement#hasModuleList()
	 */
	public synchronized boolean hasModuleList()
	{
		for (Object definition : getRootElementList())
		{
			if (definition instanceof Module)
				return true;
		}
		return false;
	}

//	/*
//	 * (non-Javadoc)
//	 * @see org.overture.ide.core.ast.IVdmElement#setParseCorrect(java.lang.String, java.lang.Boolean)
//	 */
//	public synchronized void setParseCorrect(String file, Boolean isParseCorrect)
//	{
//		if (parseCurrectTable.containsKey(file))
//			parseCurrectTable.remove(file);
//
//		parseCurrectTable.put(file, isParseCorrect);
//		typeChecked = false;
//	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.ide.core.ast.IVdmElement#isParseCorrect()
	 */
	public synchronized boolean isParseCorrect()
	{
		boolean isParseCorrect = true;
		for (IVdmSourceUnit source : vdmSourceUnits)
		{
			if (source.hasParseErrors())
			{
				isParseCorrect = false;
				break;
			}
		}
		return isParseCorrect;
		// vdmSourceUnits.get(0).
		// if(parseCurrectTable.size()==0)
		// {
		// return false;//we don't want to type check if empty
		// }
		//		
		//		
		// for (Boolean isCurrect : parseCurrectTable.values())
		// {
		// if (!isCurrect)
		// {
		// return false;
		// }
		// }
		// return true;
	}

	public boolean exists()
	{
		return getRootElementList().size() > 0;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public synchronized void addVdmSourceUnit(IVdmSourceUnit unit)
	{
		if (!vdmSourceUnits.contains(unit))
			this.vdmSourceUnits.add(unit);
		else
			System.err.println("Add error: " + unit);

	}

	public synchronized IVdmSourceUnit getVdmSourceUnit(IFile file)
	{
		for (IVdmSourceUnit unit : vdmSourceUnits)
		{
			if (unit.getFile().equals(file))
				return unit;
		}
		return null;
	}

	public int getElementType()
	{
		return IVdmElement.VDM_MODEL;
	}

	public void clean()
	{
		for (IVdmSourceUnit unit : vdmSourceUnits)
		{
			unit.clean();
		}
		//this.parseCurrectTable.clear();
		this.typeChecked = false;

	}

	public void refresh(boolean completeRefresh, IProgressMonitor monitor)
	{
		int worked = 1;
		if (monitor != null)
		{

			monitor.beginTask("Refreshing model", vdmSourceUnits.size());
		}
		for (IVdmSourceUnit source : vdmSourceUnits)
		{
			if (!completeRefresh && source.getParseList().size() > 0)
			{
				continue;
			}

			try
			{
				SourceParserManager.parseFile(source);
			} catch (CoreException e)
			{
				if (VdmCore.DEBUG)
				{
					e.printStackTrace();
				}
			} catch (IOException e)
			{
				if (VdmCore.DEBUG)
				{
					e.printStackTrace();
				}
			}
			if (monitor != null)
			{
				monitor.worked(++worked);
			}
		}

		if (monitor != null)
		{
			monitor.done();
		}

	}

	public List<IVdmSourceUnit> getSourceUnits()
	{
		return this.vdmSourceUnits;
	}

	public VdmModelWorkingCopy getWorkingCopy()
	{
		return new VdmModelWorkingCopy(this);
	}
}

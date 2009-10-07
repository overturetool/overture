package org.overture.ide.ast;

import java.util.Date;
import java.util.List;

public class RootNode
{
	private boolean checked;
	private Date checkedTime;
	private List rootElementList;

	public RootNode(List modules)
	{
		this.rootElementList = modules;
	}

	public void setRootElementList(List rootElementList)
	{
		this.rootElementList = rootElementList;
	}

	public List getRootElementList()
	{
		return rootElementList;
	}

	public void setCheckedTime(Date checkedTime)
	{
		this.checkedTime = checkedTime;
	}

	public Date getCheckedTime()
	{
		return checkedTime;
	}

	public void setChecked(boolean checked)
	{
		this.checked = checked;
	}

	public boolean isChecked()
	{
		return checked;
	}
}

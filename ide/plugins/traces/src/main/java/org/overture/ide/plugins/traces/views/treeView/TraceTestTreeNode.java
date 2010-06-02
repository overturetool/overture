package org.overture.ide.plugins.traces.views.treeView;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.overturetool.traces.utility.TraceTestResult;
import org.overturetool.vdmj.traces.Verdict;

public class TraceTestTreeNode implements IAdaptable ,ITreeNode{
	private TraceTestResult result;
	protected ITreeNode parent;
	
	private boolean runTimeError = false;

	public TraceTestTreeNode(TraceTestResult status) {
	
		this.result = status;
	}

	public boolean hasRunTimeError() {
		return runTimeError;
	}

	public void setRunTimeError() {
		this.runTimeError = true;
	}

	public void setStatus(Verdict status) {
		this.runTimeError = false;
		this.result.setStatus(status);
	}
	
	public void setResult(TraceTestResult result) {
		
		this.result=result;
	}

	public Verdict getStatus() {
		return this.result.getStatus();
	}

	public ITreeNode getParent() {
		return parent;
	}
	
	public TraceTestResult getResult()
	{
		return result;
	}

	@Override
	public String toString() {
		String tmp = result.getNumber().toString();
		while (tmp.length() < 6)
			tmp = "0" + tmp;
		return "Test " + tmp;
	}

	public String getName() {
		return toString();
	}
	
	public Integer getNumber()
	{
		return result.getNumber();
	}

	public void setParent(ITreeNode parent) {
		this.parent = parent;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

	public void addChild(ITreeNode child)
	{
		// TODO Auto-generated method stub
		
	}

	public List<ITreeNode> getChildren()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasChild(String name)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasChildren()
	{
		// TODO Auto-generated method stub
		return false;
	}



	// public boolean equals(Object obj)
	// {
	// if(obj instanceof TraceTestCaseTreeNode)
	// return this.name.equals(((TraceTestCaseTreeNode)obj).getName());
	// else
	// return false;
	// }

}
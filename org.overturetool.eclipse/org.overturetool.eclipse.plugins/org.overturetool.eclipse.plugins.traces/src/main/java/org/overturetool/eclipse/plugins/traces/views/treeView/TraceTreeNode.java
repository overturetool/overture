package org.overturetool.eclipse.plugins.traces.views.treeView;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.overturetool.traces.utility.ITracesHelper;
import org.overturetool.traces.utility.TraceTestResult;
import org.overturetool.vdmj.definitions.NamedTraceDefinition;

public class TraceTreeNode implements IAdaptable,ITreeNode
{

	private NamedTraceDefinition traceDefinition;
	private ITreeNode parent;
	private List<ITreeNode> children;
	private int testSkippedCount = 0;
	private int testTotal = 0;
	private ITracesHelper traceHelper;

	public TraceTreeNode(NamedTraceDefinition traceDef,
			ITracesHelper traceHelper)
	{
		this.traceDefinition = traceDef;
		this.setTraceHelper(traceHelper);
		this.children = new ArrayList<ITreeNode>();
	}

	public ITreeNode getParent()
	{
		return parent;
	}

	public NamedTraceDefinition GetTraceDefinition()
	{
		return traceDefinition;
	}

	public void SetSkippedCount(int skippedCount)
	{
		testSkippedCount = skippedCount;
	}

	@Override
	public String toString()
	{
		if (testSkippedCount != 0)
			return getName() + " (" + getTestTotal() + " skipped "
					+ testSkippedCount + ")";
		else
			return getName() + " (" + getTestTotal() + ")";
	}

	public String getName()
	{

		return traceDefinition.name.name;

	}

	public void setParent(ITreeNode parent)
	{
		this.parent = parent;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter)
	{
		return null;
	}

	public void addChild(ITreeNode child)
	{
		if (!children.contains(child))
		{
			boolean contains = false;
			for (ITreeNode node : getChildren())
			{
				if (node.getName().equals(child.getName()))
					contains = true;
			}
			if (!contains)
			{
				children.add(child);
				child.setParent(this);
			}
		}
	}

	public void removeChild(TraceTestTreeNode child)
	{
		children.remove(child);
		child.setParent(null);
	}

	public List<ITreeNode> getChildren()
	{
		return children;
	}

	public boolean hasChildren()
	{
		return children.size() > 0;
	}

	public boolean hasChild(String name)
	{
		for (ITreeNode node : children)
		{
			if (node.getName().equals(name))
				return true;

		}
		return false;
	}

	/**
	 * @param testTotal
	 *            the testTotal to set
	 */
	public void setTestTotal(int testTotal)
	{
		this.testTotal = testTotal;
	}

	/**
	 * @return the testTotal
	 */
	public int getTestTotal()
	{
		return testTotal;
	}

	public void LoadTests() throws Exception
	{
		children.clear();

		Long size = new Long(getTraceHelper().GetTraceTestCount(
				parent.getName(),
				getName()));

		if (size <= TraceTestGroup.GROUP_SIZE)
		{
			List<TraceTestResult> traceStatus = getTraceHelper().GetTraceTests(
					parent.getName(),
					getName());
			for (TraceTestResult traceTestStatus : traceStatus)
			{
				this.addChild(new TraceTestTreeNode(traceTestStatus));
			}
		} else
		{
			Double numberOfGroups = Math.ceil(size.doubleValue()
					/ TraceTestGroup.GROUP_SIZE);
			// Double t = TraceTestGroup.NumberOfLevels(size,
			// TraceTestGroup.GROUP_SIZE);

			if (numberOfGroups > TraceTestGroup.GROUP_SIZE)
				numberOfGroups = TraceTestGroup.GROUP_SIZE.doubleValue();

			Long testCountInGroup = (size) / numberOfGroups.longValue();
			Long currentCount = new Long(0);
			for (int i = 0; i < numberOfGroups - 1; i++)
			{
				TraceTestGroup group = new TraceTestGroup(currentCount + 1,
						currentCount + testCountInGroup.longValue() + 1);
				currentCount += testCountInGroup;
				this.addChild(group);
			}
			if (!currentCount.equals( size))
			{
				TraceTestGroup group = new TraceTestGroup(currentCount + 1,
						size + 1);
				this.addChild(group);

			}
		}
	}

	public void UnloadTests()
	{
		children.clear();
		children.add(new NotYetReadyTreeNode());
	}

	/**
	 * @param traceHelper
	 *            the traceHelper to set
	 */
	public void setTraceHelper(ITracesHelper traceHelper)
	{
		this.traceHelper = traceHelper;
	}

	/**
	 * @return the traceHelper
	 */
	public ITracesHelper getTraceHelper()
	{
		return traceHelper;
	}

	

}
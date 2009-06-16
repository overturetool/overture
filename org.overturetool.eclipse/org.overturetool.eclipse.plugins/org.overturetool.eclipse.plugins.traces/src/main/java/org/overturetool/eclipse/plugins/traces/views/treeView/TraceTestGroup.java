package org.overturetool.eclipse.plugins.traces.views.treeView;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.overturetool.traces.utility.TraceTestResult;
import org.overturetool.traces.utility.ITracesHelper.TestResultType;

public class TraceTestGroup extends TraceTestTreeNode
{
	public static final Long GROUP_SIZE = new Long(200);
	Long startNumber;
	Long stopNumber;
	TestResultType lastKnownStatus = TestResultType.Unknown;

	public TraceTestGroup(Long startNumber, Long stopNumber)
	{
		super(null);
		this.startNumber = startNumber;
		this.stopNumber = stopNumber - 1;
		children = new ArrayList<ITreeNode>();
		addChild(new NotYetReadyTreeNode());
	}

	private List<ITreeNode> children;

	// TraceTreeNode parent;

	// public ITreeNode getParent()
	// {
	// return parent;
	// }

	@Override
	public String toString()
	{
		return "[" + startNumber + "..." + (stopNumber) + "]";
	}

	public String getName()
	{
		return toString();
	}

	// public void setParent(TraceTreeNode parent)
	// {
	// this.parent = parent;
	// }

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

	public void removeChild(ITreeNode child)
	{
		children.remove(child);
		if (child instanceof TraceTestGroup)
			((TraceTestGroup) child).RemoveChildern();
		child.setParent(null);
		// child.SetResult(null);
	}

	public List<ITreeNode> getChildren()
	{
		return children;
	}

	public boolean hasChildren()
	{
		// return children.size() > 0;
		return stopNumber - startNumber > 0;
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

	public TestResultType GetStatus()
	{
		if (lastKnownStatus != TestResultType.Unknown)
			return lastKnownStatus;

		TestResultType status = TestResultType.Unknown;

		for (ITreeNode n : children)
		{
			if (n instanceof TraceTestTreeNode)
			{
				TraceTestTreeNode node = ((TraceTestTreeNode) n);
				if (node.GetStatus() == TestResultType.Fail)
					status = node.GetStatus();
				else if (node.GetStatus() == TestResultType.Inconclusive
						&& status != TestResultType.Fail)
					status = node.GetStatus();
				else if (node.GetStatus() == TestResultType.Ok
						&& status == TestResultType.Unknown)
					status = node.GetStatus();
			}
		}
		lastKnownStatus = status;
		return status;
	}

	private void LoadTestNodes() throws Exception
	{
		TraceTreeNode t = GetTraceParent(this);
		if (t!=null)
		{

			List<TraceTestResult> traceStatus = t.getTraceHelper().GetTraceTests(
					t.getParent().getName(),
					t.getName(),
					startNumber.intValue(),
					stopNumber.intValue());

			for (TraceTestResult traceTestStatus : traceStatus)
			{
				this.addChild(new TraceTestTreeNode(traceTestStatus));
			}
		}
	}

	private TraceTreeNode GetTraceParent(ITreeNode node)
	{
		if (node.getParent() == null)
			return null;
		else if (node.getParent() instanceof TraceTreeNode)
			return (TraceTreeNode) (node.getParent());
		else
			return GetTraceParent(node.getParent());
	}

	public void LoadTests() throws Exception
	{
		RemoveChildern();

		// for (int i = startNumber; i < stopNumber && i < traceStatus.size();
		// i++)
		// {
		// this.addChild(new TraceTestTreeNode(traceStatus.get(i)));
		// }

		Long size = stopNumber - startNumber;

		if (size <= TraceTestGroup.GROUP_SIZE)
		{
			LoadTestNodes();
		} else
		{
			Double numberOfGroups = new Double(size)
					/ TraceTestGroup.GROUP_SIZE;
			// Double t = TraceTestGroup.NumberOfLevels(new Long(size),
			// TraceTestGroup.GROUP_SIZE);

			if (numberOfGroups >= TraceTestGroup.GROUP_SIZE)
				numberOfGroups = TraceTestGroup.GROUP_SIZE.doubleValue();

			Double testCountInGroup = (size.doubleValue())
					/ TraceTestGroup.GROUP_SIZE;

			if (testCountInGroup.longValue() == 1)
			{
				testCountInGroup = TraceTestGroup.GROUP_SIZE.doubleValue();
			}

			Long currentCount = startNumber - 1;
			for (int i = 0; i < numberOfGroups - 1; i++)
			{
				TraceTestGroup group = new TraceTestGroup(currentCount + 1,
						currentCount + testCountInGroup.longValue());
				currentCount += testCountInGroup;
				this.addChild(group);
			}

			TraceTestGroup group = new TraceTestGroup(currentCount + 1,
					stopNumber + 1);
			this.addChild(group);

		}

	}

	public void UnloadTests()
	{
		RemoveChildern();
		addChild(new NotYetReadyTreeNode());
	}

	public void RemoveChildern()
	{
		for (int i = 0; i < children.size(); i++)
		{

			ITreeNode tmp = children.get(i);
			removeChild(tmp);

			tmp = null;
		}
		children = new Vector<ITreeNode>();
		System.gc();
	}

	public static Double NumberOfLevels(Long count, Long groupSize)
	{
		// levels(x,y)
		// if x / y > y then 1+ levels(x/y,y)
		// else 1;
		if ((count / groupSize) > groupSize)
			return new Double(1) + NumberOfLevels(count / groupSize, groupSize);
		else
			return new Double(1);

	}

}

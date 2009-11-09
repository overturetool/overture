package org.overture.ide.plugins.traces.views.treeView;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.overturetool.traces.utility.TraceTestResult;
import org.overturetool.vdmj.traces.Verdict;

public class TraceTestGroup extends TraceTestTreeNode
{
	public static final Long GROUP_SIZE = new Long(200);
	Long startNumber;
	Long stopNumber;
	Verdict lastKnownStatus = null;

	public TraceTestGroup(Long startNumber, Long stopNumber)
	{
		super(null);
		this.startNumber = startNumber;
		this.stopNumber = stopNumber - 1;
		children = new ArrayList<ITreeNode>();
		addChild(new NotYetReadyTreeNode());
	}

	private List<ITreeNode> children;

	

	@Override
	public String toString()
	{
		return "[" + startNumber + "..." + (stopNumber) + "]";
	}

	@Override
	public String getName()
	{
		return toString();
	}

	// public void setParent(TraceTreeNode parent)
	// {
	// this.parent = parent;
	// }

	@Override
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter)
	{
		return null;
	}

	@Override
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

	@Override
	public List<ITreeNode> getChildren()
	{
		return children;
	}

	@Override
	public boolean hasChildren()
	{
		// return children.size() > 0;
		return stopNumber - startNumber > 0;
	}

	@Override
	public boolean hasChild(String name)
	{
		for (ITreeNode node : children)
		{
			if (node.getName().equals(name))
				return true;

		}
		return false;
	}

	@Override
	public Verdict GetStatus()
	{
		if (lastKnownStatus != null)
			return lastKnownStatus;

		Verdict status = null;

		for (ITreeNode n : children)
		{
			if (n instanceof TraceTestTreeNode)
			{
				TraceTestTreeNode node = ((TraceTestTreeNode) n);
				if (node.GetStatus() == Verdict.FAILED)
					status = node.GetStatus();
				else if (node.GetStatus() == Verdict.INCONCLUSIVE
						&& status != Verdict.FAILED)
					status = node.GetStatus();
				else if (node.GetStatus() == Verdict.PASSED
						&& status == null)
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
		
		GroupSizeCalculator gs = new GroupSizeCalculator(size);

		if (!gs.hasGroups())
		{
			LoadTestNodes();
		} else
		{
//			if (testCountInGroup.longValue() == 1)
//			{
//				testCountInGroup = TraceTestGroup.GROUP_SIZE.doubleValue();
//			}

			Long currentCount = startNumber - 1;
			for (int i = 0; i < gs.getNumberOfGroups()- 1; i++)
			{
				TraceTestGroup group = new TraceTestGroup(currentCount + 1,
						currentCount + gs.getGroupSize());
				currentCount += gs.getGroupSize();
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

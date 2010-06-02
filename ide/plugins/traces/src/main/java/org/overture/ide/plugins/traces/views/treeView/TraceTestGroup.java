package org.overture.ide.plugins.traces.views.treeView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.overturetool.traces.utility.TraceHelperNotInitializedException;
import org.overturetool.traces.utility.TraceTestResult;
import org.overturetool.vdmj.traces.Verdict;
import org.xml.sax.SAXException;

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
			((TraceTestGroup) child).removeChildern();
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
	public Verdict getStatus()
	{
		if (lastKnownStatus == null)
		{
			Verdict status = null;

			for (ITreeNode n : children)
			{
				if (n instanceof TraceTestTreeNode)
				{
					TraceTestTreeNode node = ((TraceTestTreeNode) n);
					// if (node.getStatus() == Verdict.FAILED)
					// status = node.getStatus();
					// else if (node.getStatus() == Verdict.INCONCLUSIVE
					// && status != Verdict.FAILED)
					// status = node.getStatus();
					// else if (node.getStatus() == Verdict.PASSED
					// && status == null)
					// status = node.getStatus();
					status = calculateStatus(status, node.getStatus());
				}
			}
			lastKnownStatus = status;
		}
		return lastKnownStatus;
	}

	private Verdict calculateStatus(Verdict status, Verdict nextStatus)
	{

		if (nextStatus == Verdict.FAILED)
			return nextStatus;
		else if (nextStatus == Verdict.INCONCLUSIVE && status != Verdict.FAILED)
			return nextStatus;
		else if (nextStatus == Verdict.PASSED && status == null)
			status = nextStatus;

		return status;
	}

	public void loadGroupStatus() throws SAXException, IOException,
			ClassNotFoundException, TraceHelperNotInitializedException
	{
		TraceTreeNode t = getTraceParent(this);
		if (t != null)
		{

			List<TraceTestResult> traceStatus = t.getTraceHelper().getTraceTests(t.getParent().getName(), t.getName(), startNumber.intValue(), stopNumber.intValue());
			Verdict status = null;
			for (TraceTestResult traceTestStatus : traceStatus)
			{
				lastKnownStatus = calculateStatus(status, traceTestStatus.getStatus());
				if(lastKnownStatus == Verdict.FAILED)
				{
					break;
				}
			}
//			System.out.println(getName()+ " "+ lastKnownStatus);
//			int i = 0;
			
		}
	}

	private void loadTestNodes() throws Exception
	{
		TraceTreeNode t = getTraceParent(this);
		if (t != null)
		{

			List<TraceTestResult> traceStatus = t.getTraceHelper().getTraceTests(t.getParent().getName(), t.getName(), startNumber.intValue(), stopNumber.intValue());

			for (TraceTestResult traceTestStatus : traceStatus)
			{
				this.addChild(new TraceTestTreeNode(traceTestStatus));
			}
		}
	}

	private TraceTreeNode getTraceParent(ITreeNode node)
	{
		if (node.getParent() == null)
			return null;
		else if (node.getParent() instanceof TraceTreeNode)
			return (TraceTreeNode) (node.getParent());
		else
			return getTraceParent(node.getParent());
	}

	public void loadTests() throws Exception
	{
		removeChildern();

		// for (int i = startNumber; i < stopNumber && i < traceStatus.size();
		// i++)
		// {
		// this.addChild(new TraceTestTreeNode(traceStatus.get(i)));
		// }

		Long size = stopNumber - startNumber;

		GroupSizeCalculator gs = new GroupSizeCalculator(size);

		if (!gs.hasGroups())
		{
			loadTestNodes();
		} else
		{
			// if (testCountInGroup.longValue() == 1)
			// {
			// testCountInGroup = TraceTestGroup.GROUP_SIZE.doubleValue();
			// }

			Long currentCount = startNumber - 1;
			for (int i = 0; i < gs.getNumberOfGroups() - 1; i++)
			{
				TraceTestGroup group = new TraceTestGroup(currentCount + 1, currentCount
						+ gs.getGroupSize());
				currentCount += gs.getGroupSize();
				this.addChild(group);
			}

			TraceTestGroup group = new TraceTestGroup(currentCount + 1, stopNumber + 1);
			this.addChild(group);

		}

	}

	public void unloadTests()
	{
		removeChildern();
		addChild(new NotYetReadyTreeNode());
	}

	public void removeChildern()
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

	public static Double numberOfLevels(Long count, Long groupSize)
	{
		// levels(x,y)
		// if x / y > y then 1+ levels(x/y,y)
		// else 1;
		if ((count / groupSize) > groupSize)
			return new Double(1) + numberOfLevels(count / groupSize, groupSize);
		else
			return new Double(1);

	}

}

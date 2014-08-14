package ctruntime;

import java.util.Iterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeIterator implements Iterator<Node>, Iterable<Node>
{
	private final NodeList list;
	private int index = 0;

	public NodeIterator(NodeList list)
	{
		this.list = list;
	}

	@Override
	public boolean hasNext()
	{
		return index < list.getLength();
	}

	@Override
	public Node next()
	{
		return list.item(index++);
	}

	@Override
	public void remove()
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Iterator<Node> iterator()
	{
		return this;
	}

}

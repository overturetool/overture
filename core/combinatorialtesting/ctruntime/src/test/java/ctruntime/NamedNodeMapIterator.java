package ctruntime;

import java.util.Iterator;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NamedNodeMapIterator implements Iterator<Node>, Iterable<Node>
{
	private final NamedNodeMap list;
	private int index = 0;

	public NamedNodeMapIterator(NamedNodeMap list)
	{
		this.list = list;
	}

	@Override
	public boolean hasNext()
	{
		return list != null && index < list.getLength();
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

package org.overture.ast.preview;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.overture.ast.analysis.QuestionAdaptor;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.node.ExternalNode;
import org.overture.ast.node.INode;
import org.overture.ast.node.IToken;
import org.overture.ast.node.NodeList;

public class DotGraphVisitor extends QuestionAdaptor<DotGraphVisitor.DotPair>
{

	/**
	 * generated serial version
	 */
	private static final long serialVersionUID = 637147601437624885L;

	public static class DotPair
	{
		public DotNode parent;
		public String childKey;

		public DotPair(DotNode parent, String fieldKey)
		{
			this.parent = parent;
			this.childKey = fieldKey;
		}
	}

	public static class DotNode
	{
		public static int runningId = 0;

		public DotNode()
		{
			runningId++;
			this.id = "n" + runningId;
		}

		public String id;
		public String name;
		public Map<String, Object> childToId = new HashMap<String, Object>();
	}

	private StringBuilder resultString;
	public boolean showNullPointers = false;
	Set<INode> visitedNodes= null;

	public DotGraphVisitor()
	{
		DotNode.runningId = 0;
		resultString = new StringBuilder();
		visitedNodes = new HashSet<INode>();
		resultString.append("\tnode [shape=record];\n");
	}

	public String getResultString()
	{
		return "\ndigraph ast\n{\n" + resultString.toString() + "\n}";

	}

	public DotNode createDotNode(DotPair pair, INode node)
	{
		DotNode dn = new DotNode();
		dn.name = node.getClass().getSimpleName();

		String tmp = " [label=\"{" + dn.name + " |{";

		boolean firstChild = true;
		for (Entry<String, Object> s : node.getChildren(true).entrySet())
		{
			String id = dn.id + s.getKey();
			dn.childToId.put(id, s.getValue());
			if (!firstChild)
			{
				tmp += " | ";
			}
			firstChild = false;
			tmp += " <" + id + "> " + s.getKey();
		}

		resultString.append("\t" + dn.id + tmp + "}}\"];\n");
		if (pair != null && pair.parent != null)
		{
			String fieldId = pair.childKey;
			resultString.append("\t" + pair.parent.id + ":" + fieldId + " -> "
					+ dn.id + "\n");
		}
		return dn;
	}

	private void createDotNode(DotPair pair, Object node)
	{
		DotNode dn = new DotNode();
		String colour = "lightgray";
		if (node == null)
		{
			colour = "red2";
			dn.name = "null";
		} else
		{
			dn.name = node.getClass().getSimpleName();
		}

		if (node instanceof ExternalNode)
		{
			colour = "lightblue";
		}

		String tmp = " [color=" + colour + ",style=filled,label=\"{" + dn.name
				;

		if(node!=null)
		{
			tmp+= " |{";
		boolean firstChild = true;
		Map<String, Object> children = new HashMap<String, Object>();
		String nt = node.toString();
		if (nt.length() > 150)
		{
			nt = nt.substring(0, 150);
		}
		children.put("" + nt.replaceAll("[^a-zA-Z0-9 ]","") + "", null);
		for (Entry<String, Object> s : children.entrySet())
		{
			String id = dn.id + s.getKey();
			id = id.replaceAll("[^a-zA-Z0-9]","");
			dn.childToId.put(id, s.getValue());
			if (!firstChild)
			{
				tmp += " | ";
			}
			firstChild = false;
			tmp += " <" + id + "> " + s.getKey();
		}
		tmp+="}";
		}

		resultString.append("\t" + dn.id + tmp + "}\"];\n");
		if (pair != null && pair.parent != null)
		{

			String fieldId = pair.childKey;
			resultString.append("\t" + pair.parent.id + ":" + fieldId + " -> "
					+ dn.id + "\n");
		}
		// return dn;
	}

	@Override
	public void defaultINode(INode node, DotPair question) throws Throwable
	{
		if(!(node instanceof LexNameToken )&&visitedNodes.contains(node)|| node == null)
		{
			return;
		}
		
		if(!(node instanceof LexNameToken))
		{
		visitedNodes.add(node);
		}
		
		DotPair parentNode = new DotPair(createDotNode(question, node), null);

		for (Entry<String, Object> field : node.getChildren(true).entrySet())
		{

			Object fieldObject = field.getValue();
			if (fieldObject == null && !showNullPointers)
			{
				continue;// do not show on diagram
			}
			parentNode = new DotPair(parentNode.parent, parentNode.parent.id
					+ field.getKey());
			if (fieldObject instanceof INode)
			{
				INode childNode = (INode) fieldObject;
				childNode.apply(this, parentNode);
			} else if (fieldObject instanceof NodeList)
			{
				@SuppressWarnings("unchecked")
				NodeList<INode> childNodes = (NodeList<INode>) fieldObject;
				for (INode childNode : childNodes)
				{
					childNode.apply(this, parentNode);
				}
			} else
			// if (fieldObject instanceof ExternalNode)
			{
				createDotNode(parentNode, fieldObject);
			}
		}
	}

	@Override
	public void defaultIToken(IToken node, DotPair question)
	{
		createDotNode(question, node);
	}
}

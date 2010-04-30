package org.overture.ide.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.internal.resources.IManager;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overturetool.vdmj.ast.IAstNode;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.expressions.AndExpression;
import org.overturetool.vdmj.expressions.BinaryExpression;
import org.overturetool.vdmj.expressions.CaseAlternative;
import org.overturetool.vdmj.expressions.CasesExpression;
import org.overturetool.vdmj.expressions.ElseIfExpression;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.IfExpression;
import org.overturetool.vdmj.expressions.LetDefExpression;
import org.overturetool.vdmj.expressions.UnaryExpression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.statements.Statement;

/*
 * public abstract IAstNode getNodeAt(int pos); public int getLineOffset(int lineIndex);
 */

@SuppressWarnings("restriction")
public class SourceReferenceManager implements IManager
{
	protected class ElementChangedListener implements IElementChangedListener
	{
		public void elementChanged(final ElementChangedEvent e)
		{
			IVdmElementDelta delta = e.getDelta();
			if (delta.getKind() == IVdmElementDelta.ADDED
					|| delta.getKind() == IVdmElementDelta.CHANGED)
				refresh();
		}
	}

	Map<Integer, IAstNode> offsetToAstNodeMap = new Hashtable<Integer, IAstNode>();
	List<SourceReference> sourceReferences = new Vector<SourceReference>();
	Integer[] lineSize = new Integer[0];

	IVdmSourceUnit sourceUnit;

	ElementChangedListener listener = null;

	public SourceReferenceManager(IVdmSourceUnit sourceUnit)
	{
		this.sourceUnit = sourceUnit;

		refresh();

	}

	public void refresh()
	{
		makeLineSizes();
		if (sourceUnit.hasParseTree())
		{
			makeOffsetToAstMap();
			makeOuterOffsetToAstMap();
		} else
		{
			lineSize = new Integer[0];

		}
	}

	public synchronized IAstNode getNodeAt(int pos)
	{
		try
		{
			if (!sourceReferences.isEmpty())
			{
				for (SourceReference reference : sourceReferences)
				{
					if (reference.isWithinRange(pos)
							&& (reference.getNode() instanceof Definition))
					{
						return reference.getNode();
					}
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		List<Integer> knownOffsets = new Vector<Integer>(offsetToAstNodeMap.keySet());

		Collections.sort(knownOffsets);

		for (int i = 0; i < knownOffsets.size(); i++)
		{
			int offset = knownOffsets.get(i);
			if (pos > offset)
			{
				continue;
			} else if (pos == offset)
			{
				return offsetToAstNodeMap.get(offset);
			}
			if (i != 0)
			{
				IAstNode node = offsetToAstNodeMap.get(knownOffsets.get(i - 1));
				// TODO needs to be restricted so only a node is returned if it actually spans the location
				// if (getLineOffset(node.getLocation().endLine)
				// + node.getLocation().endPos + 10 >= pos)
				// {
				return node;
				// } else
				// {
				// break;
				// }
			}
		}

		return null;

	}

	public int[] getPosLine(int offset)
	{
		int tmpOffset = 0;
		int line = 0;
		for (int i = 0; i < lineSize.length; i++)
		{
			tmpOffset += lineSize[i];
			if (tmpOffset > offset)
			{
				line = i;// take last line

				if (offset - (tmpOffset - lineSize[i]) + 1 == lineSize[i])
				{
					line++;
					offset = -1;
					break;
				}
				offset = offset - (tmpOffset - lineSize[i]); // remote lines from pos
				break;
			}

			if (i == lineSize.length - 1)
			{
				line = i + 1;// take this line
				offset = offset - tmpOffset; // remote lines from pos
				break;
			}
		}
		offset++;
		line++;// convert to 1-indexed
		return new int[] { line, offset };
	}

	public int getLineOffset(int lineIndex)
	{
		int offset = 0;
		for (int i = 0; i < lineSize.length; i++)
		{
			if (i == lineIndex - 1)
				break;
			offset += lineSize[i];

		}
		return offset;
	}

	private void makeOffsetToAstMap()
	{
		for (LexLocation location : sourceUnit.getLocationToAstNodeMap().keySet())
		{
			offsetToAstNodeMap.put(getLineOffset(location.startLine)
					+ location.startPos, sourceUnit.getLocationToAstNodeMap().get(location));
		}
	}

	private synchronized void makeOuterOffsetToAstMap()
	{
		VdmjLocationCalculator calc = new VdmjLocationCalculator();
		for (LexLocation location : sourceUnit.getLocationToAstNodeMap().keySet())
		{
			try
			{
				SourceReference outerLocation = null;

				outerLocation = calc.getOuterLocation(sourceUnit.getLocationToAstNodeMap().get(location));

				if (outerLocation != null)
				{
					sourceReferences.add(outerLocation);
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/** An end of file symbol. */
	private static final int EOF = (int) -1;

	private void makeLineSizes()
	{
		LexTokenReader.TABSTOP = 1;
		InputStream inpput;
		try
		{
			inpput = sourceUnit.getFile().getContents();

			List<Integer> lines = new Vector<Integer>();
			int data = EOF;
			int linecount = 0;
			int charpos = 0;
			while ((data = inpput.read()) != EOF)
			{
				char c = (char) data;

				if (c == '\n')
				{
					lines.add(++charpos);
					linecount++;
					charpos = 0;
				} else
				{
					// charpos += (c == '\t' ? (LexTokenReader.TABSTOP - charpos
					// % LexTokenReader.TABSTOP) : 1);
					charpos++;
				}

				// ch = c;
				// charsread++;
			}
			lines.add(++charpos);

			this.lineSize = new Integer[lines.size()];
			lines.toArray(this.lineSize);
			// System.out.println(file.getName() + lines);
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class VdmjLocationCalculator
	{
		public SourceReference getOuterLocation(IAstNode element)
		{
			if (element instanceof Definition)
			{
				return getOuterLocation((Definition) element);
			}
			if (element instanceof Expression)
			{
				return getOuterLocation((Expression) element);
			}
			if (element instanceof Statement)
			{
				return getOuterLocation((Statement) element);
			}

			if (element != null && element.getLocation() != null)
			{
				return new SourceReference(element.getLocation().startLine, element.getLocation().startPos, element.getLocation().endLine, element.getLocation().endPos, element);
			} else
			{
				return null;
			}

		}

		public SourceReference getOuterLocation(Definition element)
		{
			if (element instanceof ExplicitOperationDefinition)
			{
				return getOuterLocation((ExplicitOperationDefinition) element);
			}
			if (element instanceof ExplicitFunctionDefinition)
			{
				return getOuterLocation((ExplicitFunctionDefinition) element);
			}

			if (element != null && element.getLocation() != null)
			{
				return new SourceReference(element.getLocation().startLine, element.getLocation().startPos, element.getLocation().endLine, element.getLocation().endPos, element);
			} else
			{
				return null;
			}

		}

		public SourceReference getOuterLocation(Expression element)
		{

			if (element instanceof AndExpression)
			{
				return getOuterLocation((AndExpression) element);
			}
			if (element instanceof IfExpression)
			{
				return getOuterLocation((IfExpression) element);
			}
			if (element instanceof ElseIfExpression)
			{
				return getOuterLocation((ElseIfExpression) element);
			}

			if (element instanceof LetDefExpression)
			{
				return getOuterLocation((LetDefExpression) element);
			}
			if (element instanceof UnaryExpression)
			{
				return getOuterLocation((UnaryExpression) element);
			}
			if (element instanceof CasesExpression)
			{
				return getOuterLocation((CasesExpression) element);
			}
			if (element instanceof BinaryExpression)
			{
				return getOuterLocation((BinaryExpression) element);
			}

			if (element != null && element.getLocation() != null)
			{
				return new SourceReference(element.getLocation().startLine, element.getLocation().startPos, element.getLocation().endLine, element.getLocation().endPos, element);
			} else
			{
				return null;
			}
		}

		public SourceReference getOuterLocation(Statement element)
		{
			return new SourceReference(element.getLocation().startLine, element.getLocation().startPos, element.getLocation().endLine, element.getLocation().endPos, element);

		}

		public SourceReference getOuterLocation(
				ExplicitOperationDefinition element)
		{
			int startLine = element.getLocation().startLine;
			int startPos = element.getLocation().startPos;

			if (element.accessSpecifier.access != null)
			{
				// no location available. Estimate at least a space and the number of chars of the access
				// specifier
				startPos -= element.accessSpecifier.access.name().length() + 1;
			}

			int endLine = element.body.getLocation().endLine;
			int endPos = element.body.getLocation().endPos;

			SourceReference sf = new SourceReference(startLine, startPos, endLine, endPos, element);

			sf.expand(getOuterLocation(element.body));
			sf.expand(getOuterLocation(element.precondition));
			sf.expand(getOuterLocation(element.postcondition));

			return sf;
		}

		public SourceReference getOuterLocation(
				ExplicitFunctionDefinition element)
		{
			int startLine = element.getLocation().startLine;
			int startPos = element.getLocation().startPos;

			if (element.accessSpecifier.access != null)
			{
				// no location available. Estimate at least a space and the number of chars of the access
				// specifier
				startPos -= element.accessSpecifier.access.name().length() + 1;
			}

			int endLine = element.body.getLocation().endLine;
			int endPos = element.body.getLocation().endPos;

			SourceReference sf = new SourceReference(startLine, startPos, endLine, endPos, element);

			sf.expand(getOuterLocation(element.body));
			sf.expand(getOuterLocation(element.precondition));
			sf.expand(getOuterLocation(element.postcondition));

			return sf;
		}

		public SourceReference getOuterLocation(AndExpression element)
		{
			int startLine = element.getLocation().startLine;
			int startPos = element.getLocation().startPos;

			int endLine = element.getLocation().endLine;
			int endPos = element.getLocation().endPos;

			SourceReference sf = new SourceReference(startLine, startPos, endLine, endPos, element);

			sf.expand(getOuterLocation(element.left));
			sf.expand(getOuterLocation(element.right));

			return sf;
		}

		public SourceReference getOuterLocation(IfExpression element)
		{
			int startLine = element.getLocation().startLine;
			int startPos = element.getLocation().startPos;

			int endLine = element.getLocation().endLine;
			int endPos = element.getLocation().endPos;

			SourceReference sf = new SourceReference(startLine, startPos, endLine, endPos, element);

			sf.expand(getOuterLocation(element.thenExp));
			sf.expand(getOuterLocation(element.elseExp));

			for (ElseIfExpression exp : element.elseList)
			{
				sf.expand(getOuterLocation(exp));
			}

			return sf;
		}

		public SourceReference getOuterLocation(ElseIfExpression element)
		{
			int startLine = element.getLocation().startLine;
			int startPos = element.getLocation().startPos;

			int endLine = element.getLocation().endLine;
			int endPos = element.getLocation().endPos;

			SourceReference sf = new SourceReference(startLine, startPos, endLine, endPos, element);

			sf.expand(getOuterLocation(element.thenExp));
			sf.expand(getOuterLocation(element.elseIfExp));

			return sf;
		}

		public SourceReference getOuterLocation(BinaryExpression element)
		{
			int startLine = element.getLocation().startLine;
			int startPos = element.getLocation().startPos;

			int endLine = element.getLocation().endLine;
			int endPos = element.getLocation().endPos;

			SourceReference sf = new SourceReference(startLine, startPos, endLine, endPos, element);

			sf.expand(getOuterLocation(element.left));
			sf.expand(getOuterLocation(element.right));

			return sf;
		}

		public SourceReference getOuterLocation(CasesExpression element)
		{
			int startLine = element.getLocation().startLine;
			int startPos = element.getLocation().startPos;

			int endLine = element.getLocation().endLine;
			int endPos = element.getLocation().endPos;

			SourceReference sf = new SourceReference(startLine, startPos, endLine, endPos, element);

			sf.expand(getOuterLocation(element.exp));
			for (CaseAlternative caseA : element.cases)
			{
				sf.expand(getOuterLocation(caseA));
			}

			return sf;
		}

		/***
		 * Not an expression
		 * 
		 * @param element
		 * @return
		 */
		public SourceReference getOuterLocation(CaseAlternative element)
		{
			int startLine = element.location.startLine;
			int startPos = element.location.startPos;

			int endLine = element.location.endLine;
			int endPos = element.location.endPos;

			SourceReference sf = new SourceReference(startLine, startPos, endLine, endPos, null);

			sf.expand(getOuterLocation(element.cexp));
			sf.expand(getOuterLocation(element.result));

			return sf;
		}

		public SourceReference getOuterLocation(LetDefExpression element)
		{
			int startLine = element.getLocation().startLine;
			int startPos = element.getLocation().startPos;

			int endLine = element.getLocation().endLine;
			int endPos = element.getLocation().endPos;

			SourceReference sf = new SourceReference(startLine, startPos, endLine, endPos, element);

			sf.expand(getOuterLocation(element.expression));
			for (Definition def : element.localDefs)
			{
				sf.expand(getOuterLocation(def));
			}

			return sf;
		}

		public SourceReference getOuterLocation(UnaryExpression element)
		{
			int startLine = element.getLocation().startLine;
			int startPos = element.getLocation().startPos;

			int endLine = element.getLocation().endLine;
			int endPos = element.getLocation().endPos;

			SourceReference sf = new SourceReference(startLine, startPos, endLine, endPos, element);

			sf.expand(getOuterLocation(element.exp));
			return sf;
		}

	}

	private class SourceReference
	{
		int startOffset = 0;
		int endOffset = 0;
		IAstNode node = null;

		public SourceReference(int startLine, int startPos, int endLine,
				int endPos, IAstNode node)
		{
			this.node = node;

			startOffset = getLineOffset(startLine) + startPos;

			endOffset = getLineOffset(endLine) + endPos;
		}

		public boolean isWithinRange(int offset)
		{
			return offset >= startOffset && offset <= endOffset;
		}

		public IAstNode getNode()
		{
			return node;
		}

		public void expand(SourceReference reference)
		{
			if (reference != null && !this.isWiderThan(reference))
			{
				if (reference.startOffset < this.startOffset)
				{
					this.startOffset = reference.startOffset;
				}
				if (reference.endOffset > this.endOffset)
				{
					this.endOffset = reference.endOffset;
				}
			}
		}

		public boolean isWiderThan(SourceReference reference)
		{
			return reference.startOffset > this.startOffset
					&& reference.endOffset < this.endOffset;
		}

		@Override
		public String toString()
		{
			return node.getName() + " " + startOffset + " to " + endOffset;
		}
	}

	public void shutdown(IProgressMonitor monitor) throws CoreException
	{
		if (listener != null)
		{
			VdmCore.removeElementChangedListener(listener);
		}
	}

	public void startup(IProgressMonitor monitor) throws CoreException
	{
		if (listener == null)
		{
			listener = new ElementChangedListener();
			VdmCore.addElementChangedListener(listener);
		}
	}
}

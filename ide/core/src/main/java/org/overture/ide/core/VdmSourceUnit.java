package org.overture.ide.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.overture.ide.internal.core.ast.VdmModelManager;
import org.overturetool.vdmj.ast.IAstNode;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexTokenReader;

public class VdmSourceUnit implements IVdmSourceUnit
{
	private IVdmProject project;
	private IFile file;
	private int type;
	List<LexLocation> allLocation = new Vector<LexLocation>();
	Map<LexLocation, IAstNode> locationToAstNodeMap = new Hashtable<LexLocation, IAstNode>();
	Map<Integer, IAstNode> offsetToAstNodeMap = new Hashtable<Integer, IAstNode>();
	Integer[] lineSize = new Integer[0];

	private List parseList = new Vector();

	public VdmSourceUnit(IVdmProject project, IFile file) {
		this.project = project;
		this.file = file;
		this.type = IVdmSourceUnit.VDM_DEFAULT;
		makeLineSizes();
	}

	public Object getAdapter(Class adapter)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public IFile getFile()
	{
		return file;
	}

	public File getSystemFile()
	{
		return project.getFile(file);
	}

	public synchronized void reconcile(List parseResult,
			List<LexLocation> allLocation,
			Map<LexLocation, IAstNode> locationToAstNodeMap, boolean parseErrors)
	{
		this.type = type;
		this.parseList.clear();
		this.parseList.addAll(parseResult);

		this.allLocation.clear();
		this.allLocation.addAll(allLocation);

		this.locationToAstNodeMap.clear();
		this.locationToAstNodeMap.putAll(locationToAstNodeMap);

		makeLineSizes();

		makeOffsetToAstMap();

//		for (LexLocation lexLocation : allLocation)
//		{
//			if (locationToAstNodeMap.containsKey((lexLocation)))
//				System.out.println(locationToAstNodeMap.get(lexLocation)
//						.getName()
//						+ " - "
//						+ lexLocation.startLine
//						+ ":"
//						+ lexLocation.startPos
//						+ " - "
//						+ lexLocation.endLine
//						+ ":" + lexLocation.endPos);
//			else
//				System.out.println(lexLocation.startLine + ":"
//						+ lexLocation.startPos + " - " + lexLocation.endLine
//						+ ":" + lexLocation.endPos);
//		}

		// VdmModelManager.getInstance().update(project, parseList);
		VdmCore.getDeltaProcessor().fire(this,
				new ElementChangedEvent(new VdmElementDelta(this,
						IVdmElementDelta.CHANGED),
						ElementChangedEvent.DeltaType.POST_RECONCILE));
		// file.getLocation().toFile().getAbsolutePath()
		// IVdmModelManager astManager = VdmModelManager.instance();
		// astManager.update(project, project.getVdmNature(), ast);
		// IVdmSourceUnit rootNode = astManager.getRootNode(project, natureId);
		// if (rootNode != null)
		// {
		//
		// rootNode.setParseCorrect(filePath, !parseErrorsOccured);
		//
		// }
	}

	public synchronized List getParseList()
	{
		return this.parseList;
	}

	public boolean exists()
	{
		return this.file.exists();
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public int getElementType()
	{
		return getType();
	}

	@Override
	public String toString()
	{
		return file.toString();
	}

	public void clean()
	{
		 this.parseList.clear();

	}

	public IVdmProject getProject()
	{
		return project;
	}

	public IAstNode getNodeAt(int pos)
	{
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
				//TODO needs to be restricted so only a node is returned if it actually spans the location
//				if (getLineOffset(node.getLocation().endLine)
//						+ node.getLocation().endPos + 10 >= pos)
//				{
					return node;
//				} else
//				{
//					break;
//				}
			}
		}

		return null;

	}

	private int[] getPosLine(int offset)
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
		return new int[] { offset, line };
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
		for (LexLocation location : locationToAstNodeMap.keySet())
		{
			offsetToAstNodeMap.put(getLineOffset(location.startLine)
					+ location.startPos, locationToAstNodeMap.get(location));
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
			inpput = file.getContents();

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
//			System.out.println(file.getName() + lines);
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

	public boolean hasParseTree()
	{
		return parseList.size()>0;
	}

}

/*
 * #%~
 * org.overture.ide.core
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.core.utility;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ide.core.VdmCore;
import org.overture.ide.internal.core.resources.VdmProject;
import org.overture.parser.lex.BacktrackInputReader.ReaderType;
import org.overture.parser.lex.DocStreamReader;
import org.overture.parser.lex.DocxStreamReader;
import org.overture.parser.lex.ODFStreamReader;

public class FileUtility
{
	public static void addMarker(IFile file, String message, int lineNumber,
			int severity, String sourceId)
	{
		try
		{
			if (file == null)
			{
				return;
			}
			lineNumber -= 1;
			IMarker[] markers = file.findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
			for (IMarker marker : markers)
			{
				if (marker.getAttribute(IMarker.MESSAGE) != null
						&& marker.getAttribute(IMarker.SEVERITY) != null
						&& marker.getAttribute(IMarker.LINE_NUMBER) != null
						&& marker.getAttribute(IMarker.MESSAGE).equals(message)
						&& marker.getAttribute(IMarker.SEVERITY).equals(severity)
						&& marker.getAttribute(IMarker.LINE_NUMBER).equals(lineNumber))
				{
					return;
				}

			}
			IMarker marker = file.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			marker.setAttribute(IMarker.SOURCE_ID, sourceId);// ICoreConstants.PLUGIN_ID);
			marker.setAttribute(IMarker.LOCATION, "line: " + lineNumber);

			if (lineNumber == -1)
			{
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e)
		{
			VdmCore.log("FileUtility addMarker", e);
		}
	}

	/**
	 * Add markers to ifile. This is used to mark a problem by e.g. builder and parser. Important: If a marker already
	 * exists at the specified location with the same message
	 * 
	 * @param file
	 *            The IFile which is the source where the marker should be set
	 * @param message
	 *            The message of the marker
	 * @param location
	 *            The lex location where the marker should be set
	 * @param severity
	 *            The severity, e.g: IMarker.SEVERITY_ERROR or IMarker.SEVERITY_ERROR
	 * @param sourceId
	 *            The source if of the plugin calling this function. The PLUGIN id.
	 */
	public static void addMarker(IFile file, String message,
			ILexLocation location, int severity, String sourceId)
	{
		int startOffset = location.getStartOffset();
		int endOffset = location.getEndOffset();

		addMarker(file, message, location.getStartLine(), startOffset, location.getEndLine(), endOffset, severity, sourceId);
	}

	/**
	 * Add markers to ifile. This is used to mark a problem by e.g. builder and parser. Important: If a marker already
	 * exists at the specified location with the same message
	 * 
	 * @param file
	 *            The IFile which is the source where the marker should be set
	 * @param message
	 *            The message of the marker
	 * @param location
	 *            The lex location where the marker should be set
	 * @param severity
	 *            The severity, e.g: IMarker.SEVERITY_ERROR or IMarker.SEVERITY_ERROR
	 * @param sourceId
	 *            The source if of the plugin calling this function. The PLUGIN id.
	 * @param offsetAdjustment
	 *            an adjustment added to the offsets
	 */
	public static void addMarker(IFile file, String message,
			ILexLocation location, int severity, String sourceId,
			int offsetAdjustment)
	{
		int startOffset = location.getStartOffset();
		if (startOffset > 0)
		{
			startOffset += offsetAdjustment;
		}

		int endOffset = location.getEndOffset();
		if (endOffset > 0)
		{
			endOffset += offsetAdjustment;
		}

		addMarker(file, message, location.getStartLine(), startOffset, location.getEndLine(), endOffset, severity, sourceId);
	}

	public static void addMarker(IFile file, String message, int startLine,
			int startPos, int endLine, int endPos, int severity, String sourceId)
	{
		try
		{
			if (file == null)
			{
				return;
			}
			// lineNumber -= 1;
			IMarker[] markers = file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
			for (IMarker marker : markers)
			{
				if (marker.getAttribute(IMarker.MESSAGE) != null
						&& marker.getAttribute(IMarker.MESSAGE).equals(message)
						&& marker.getAttribute(IMarker.SEVERITY) != null
						&& marker.getAttribute(IMarker.SEVERITY).equals(severity)
						&& marker.getAttribute(IMarker.CHAR_START) != null
						&& marker.getAttribute(IMarker.CHAR_START).equals(startPos)
						&& marker.getAttribute(IMarker.CHAR_END) != null
						&& marker.getAttribute(IMarker.CHAR_END).equals(endPos))
				{
					return;
				}

			}
			IMarker marker = file.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			marker.setAttribute(IMarker.SOURCE_ID, sourceId);
			marker.setAttribute(IMarker.LOCATION, "line: " + startLine);
			marker.setAttribute(IMarker.LINE_NUMBER, startLine);

			marker.setAttribute(IMarker.CHAR_START, startPos);
			marker.setAttribute(IMarker.CHAR_END, endPos);
		} catch (CoreException e)
		{
			VdmCore.log("FileUtility addMarker", e);
		}
	}

	public static void deleteMarker(IFile file, String type, String sourceId)
	{
		try
		{
			if (file == null)
			{
				return;
			}

			IMarker[] markers = file.findMarkers(type, true, IResource.DEPTH_INFINITE);
			for (IMarker marker : markers)
			{
				if (marker.getAttribute(IMarker.SOURCE_ID) != null
						&& marker.getAttribute(IMarker.SOURCE_ID) != null
						&& marker.getAttribute(IMarker.SOURCE_ID).equals(sourceId))
				{
					marker.delete();
				}
			}
		} catch (CoreException e)
		{
			if (VdmCore.DEBUG)
			{
				VdmCore.log("FileUtility deleteMarker", e);
			}
		}
	}

	public static List<Character> getContent(IFile file) throws CoreException
	{
		try
		{
			if (!file.isSynchronized(IResource.DEPTH_ONE))
			{
				file.refreshLocal(IResource.DEPTH_ONE, null);
			}
		} catch (Exception e)
		{
			VdmCore.log("FileUtility getContent", e);
		}

		if (VdmProject.externalFileContentType.isAssociatedWith(file.getName()))
		{
			return convert(getContentExternalText(file), file.getCharset());
		} else
		{
			return getContentPlainText(file);
		}
	}
	
	public static ReaderType getReaderType(IFile file)
			throws FileNotFoundException, IOException, CoreException
	{
		if (VdmProject.externalFileContentType.isAssociatedWith(file.getName()))
		{
			if (file.getName().endsWith("doc"))
			{
				return ReaderType.Doc;
			} else if (file.getName().endsWith("docx"))
			{
				return ReaderType.Docx;
			} else if (file.getName().endsWith("odt"))
			{
				return ReaderType.Odf;
			}
		} else
		{
			return ReaderType.Latex;
		}

		return null;
	}

	public static InputStreamReader getReader(ReaderType type,IFile file)
			throws FileNotFoundException, IOException, CoreException
	{
		switch(type)
		{
			case Doc:
				return new DocStreamReader(new FileInputStream(file.getLocation().toFile()), file.getCharset());
			case Docx:
				return new DocxStreamReader(new FileInputStream(file.getLocation().toFile()));
			case Odf:
				return new ODFStreamReader(new FileInputStream(file.getLocation().toFile()));
			default:
				return new InputStreamReader(file.getContents(), file.getCharset());
		}
	}

	public static List<Character> convert(String text, String encoding)
	{
		InputStreamReader in = null;
		List<Character> content = new ArrayList<Character>();
		try
		{
			in = new InputStreamReader(new ByteArrayInputStream(text.getBytes(encoding)), encoding);

			int c = -1;
			while ((c = in.read()) != -1)
			{
				content.add((char) c);
			}
		} catch (IOException e)
		{
			VdmCore.log("FileUtility getContentDocxText", e);
		} finally
		{
			try
			{
				in.close();
			} catch (IOException x)
			{
			}
		}
		return content;
	}
	
	public static String getContentExternalText(IFile file)
	{
		try
		{
			return getContentExternalText(file, getReader(getReaderType(file),file));
		} catch (CoreException e)
		{
			VdmCore.log("FileUtility getContentDocxText", e);
		} catch (IOException e)
		{
			VdmCore.log("FileUtility getContentDocxText", e);
		}
		return null;
	}

	public static String getContentExternalText(IFile file,InputStreamReader in)
	{

		StringBuffer fileData = new StringBuffer();

		try
		{
			long length = 0;

			

			if (in instanceof DocxStreamReader)
			{
				length = ((DocxStreamReader) in).length();
			} else if (in instanceof ODFStreamReader)
			{
				length = ((ODFStreamReader) in).length();
			} else
			{
				length = file.getLocation().toFile().length();
			}

			char[] buf = new char[(int) (length + 1)];
			int numRead = 0;
			// while((numRead=in.read(buf)) != -1){
			numRead = in.read(buf);
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			// buf = new char[1024];
			// }
			return fileData.toString();
		} catch (IOException e)
		{
			VdmCore.log("FileUtility getContentDocxText", e);
		}  finally
		{
			try
			{
				in.close();
			} catch (IOException x)
			{
			}
		}
		return null;
	}

	private static List<Character> getContentPlainText(IFile file)
	{
		InputStream inStream;
		InputStreamReader in = null;
		List<Character> content = new ArrayList<Character>();
		try
		{
			inStream = file.getContents();
			in = new InputStreamReader(inStream, file.getCharset());

			int c = -1;
			while ((c = in.read()) != -1)
			{
				content.add((char) c);
			}

		} catch (Exception e)
		{
			VdmCore.log("FileUtility getContent", e);
		} finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				} catch (IOException e)
				{
				}
			}
		}

		return content;
	}

	public static String makeString(List<Character> content)
	{
		StringBuilder sb = new StringBuilder();
		for (Character c : content)
		{
			sb.append(c);
		}
		return sb.toString();
	}

}

/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
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
import org.overture.ide.core.VdmCore;
import org.overture.ide.internal.core.resources.VdmProject;
import org.overturetool.vdmj.lex.DocStreamReader;
import org.overturetool.vdmj.lex.DocxStreamReader;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.ODFStreamReader;

public class FileUtility
{
	public static void addMarker(IFile file, String message, int lineNumber,
			int severity, String sourceId)
	{
		try
		{
			if (file == null)
				return;
			lineNumber -= 1;
			IMarker[] markers = file.findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
			for (IMarker marker : markers)
			{
				if (
						marker.getAttribute(IMarker.MESSAGE) != null
						&& marker.getAttribute(IMarker.SEVERITY) != null
						&& marker.getAttribute(IMarker.LINE_NUMBER) != null
						&& marker.getAttribute(IMarker.MESSAGE).equals(message)
						&& marker.getAttribute(IMarker.SEVERITY).equals(severity)
						&& marker.getAttribute(IMarker.LINE_NUMBER).equals(lineNumber)
					)
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

	public static void addMarker(IFile file, String message, int lineNumber,
			int columnNumber, int severity, String sourceId)
	{
		try
		{
			if (file == null)
				return;
			lineNumber -= 1;
			IMarker[] markers = file.findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
			for (IMarker marker : markers)
			{
				if (	marker.getAttribute(IMarker.MESSAGE) != null
						&& marker.getAttribute(IMarker.SEVERITY) != null
						&& marker.getAttribute(IMarker.LINE_NUMBER) != null
						&& marker.getAttribute(IMarker.MESSAGE).equals(message)
						&& marker.getAttribute(IMarker.SEVERITY).equals(severity)
						&& marker.getAttribute(IMarker.LINE_NUMBER).equals(lineNumber))
					return;

			}
			IMarker marker = file.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			marker.setAttribute(IMarker.SOURCE_ID, sourceId);// ICoreConstants.PLUGIN_ID);
			marker.setAttribute(IMarker.LOCATION, "line: " + lineNumber);

			SourceLocationConverter converter = new SourceLocationConverter(getContent(file));
			marker.setAttribute(IMarker.CHAR_START, converter.getStartPos(lineNumber, columnNumber));
			marker.setAttribute(IMarker.CHAR_END, converter.getEndPos(lineNumber, columnNumber));
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
			LexLocation location, int severity, String sourceId)
	{
		if (file == null)
			return;
		
		try
		{
			List<Character> content = getContent(file);//FIXME this should be improved converting a string 3 times is not good.
			String tmp = new String(FileUtility.getCharContent(content));
			addMarker(file,message,location,severity,sourceId,tmp);
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
			LexLocation location, int severity, String sourceId, String content)
	{
		if (file == null)
			return;
			
		addInternalMarker(file,message,location,severity,sourceId,content);
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
	 * @param content
	 * 				The content used when calculating the char offset for the markers
	 */
	protected static void addInternalMarker(IFile file, String message,
			LexLocation location, int severity, String sourceId,String content)
	{
		try
		{
			if (file == null)
				return;
			SourceLocationConverter converter = new SourceLocationConverter(content);
			// lineNumber -= 1;
			IMarker[] markers = file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
			for (IMarker marker : markers)
			{
				if ((marker.getAttribute(IMarker.MESSAGE) != null && marker.getAttribute(IMarker.MESSAGE).equals(message))
						&& (marker.getAttribute(IMarker.SEVERITY) != null && marker.getAttribute(IMarker.SEVERITY).equals(severity))
						&& (marker.getAttribute(IMarker.CHAR_START) != null && marker.getAttribute(IMarker.CHAR_START).equals(converter.getStartPos(location)))
						&& (marker.getAttribute(IMarker.CHAR_END) != null && marker.getAttribute(IMarker.CHAR_END).equals(converter.getEndPos(location))))
					return;

			}
			IMarker marker = file.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			marker.setAttribute(IMarker.SOURCE_ID, sourceId);
			marker.setAttribute(IMarker.LOCATION, "line: " + location.startLine);
			marker.setAttribute(IMarker.LINE_NUMBER, location.startLine);

			marker.setAttribute(IMarker.CHAR_START, converter.getStartPos(location));
			marker.setAttribute(IMarker.CHAR_END, converter.getEndPos(location));
		} catch (CoreException e)
		{
			VdmCore.log("FileUtility addMarker", e);
		}
	}

	public static void addMarker(IFile file, String message, int startLine,
			int startPos, int endLine, int endPos, int severity, String sourceId)
	{//FIXME
		addMarker(file, message, new LexLocation(null, "", startLine, startPos, endLine, endPos,-1, -1), severity, sourceId);
	}

	public static void deleteMarker(IFile file, String type, String sourceId)
	{
		try
		{
			if (file == null)
				return;

			IMarker[] markers = file.findMarkers(type, true, IResource.DEPTH_INFINITE);
			for (IMarker marker : markers)
			{
				if (marker.getAttribute(IMarker.SOURCE_ID) != null
						&& marker.getAttribute(IMarker.SOURCE_ID) != null 
						&& marker.getAttribute(IMarker.SOURCE_ID).equals(sourceId))
					marker.delete();
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

	public static InputStreamReader getReader(IFile file)
			throws FileNotFoundException, IOException, CoreException
	{
		if (VdmProject.externalFileContentType.isAssociatedWith(file.getName()))
		{
		if (file.getName().endsWith("doc"))
		{
			return new DocStreamReader(new FileInputStream(file.getLocation().toFile()), file.getCharset());
		} else if (file.getName().endsWith("docx"))
		{
			return new DocxStreamReader(new FileInputStream(file.getLocation().toFile()));
		} else if (file.getName().endsWith("odt"))
		{
			return new ODFStreamReader(new FileInputStream(file.getLocation().toFile()));
		}}else
		{
			return new InputStreamReader(file.getContents(), file.getCharset());
		}

		return null;
	}
	
	public static List<Character> convert(String text,String encoding)
	{
		InputStreamReader in = null;
		List<Character> content = new Vector<Character>();
		try
		{
			in = new InputStreamReader(new ByteArrayInputStream(text.getBytes(encoding)), encoding);

			int c = -1;
			while ((c = in.read()) != -1)
				content.add((char) c);
		}catch (IOException e)
		{
			VdmCore.log("FileUtility getContentDocxText", e);
		}finally
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
		InputStreamReader in = null;
		
		StringBuffer fileData = new StringBuffer();
		
		try
		{
			long length = 0;
			
			in = getReader(file);
			
			if(in instanceof DocxStreamReader)
			{
				length = ((DocxStreamReader)in).length();
			}else if(in instanceof ODFStreamReader)
			{
				length = ((ODFStreamReader)in).length();
			}else
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
		} catch (CoreException e)
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
		return null;
	}

	private static List<Character> getContentPlainText(IFile file)
	{
		InputStream inStream;
		InputStreamReader in = null;
		List<Character> content = new Vector<Character>();
		try
		{
			inStream = file.getContents();
			in = new InputStreamReader(inStream, file.getCharset());

			int c = -1;
			while ((c = in.read()) != -1)
				content.add((char) c);

		} catch (Exception e)
		{
			VdmCore.log("FileUtility getContent", e);
		} finally
		{
			if (in != null)
				try
				{
					in.close();
				} catch (IOException e)
				{
				}
		}

		return content;
	}

	public static char[] getCharContent(List<Character> content)
	{
		char[] source = new char[content.size()];
		for (int i = 0; i < content.size(); i++)
		{
			source[i] = content.get(i);
		}
		return source;
	}
}

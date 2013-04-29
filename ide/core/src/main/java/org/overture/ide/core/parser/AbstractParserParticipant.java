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
package org.overture.ide.core.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.node.INode;
import org.overture.ide.core.ICoreConstants;
import org.overture.ide.core.VdmCore;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.core.utility.FileUtility;
import org.overture.parser.lex.BacktrackInputReader.ReaderType;
import org.overture.parser.messages.VDMError;
import org.overture.parser.messages.VDMWarning;


public abstract class AbstractParserParticipant implements ISourceParser
{

	protected String natureId;
	protected IVdmProject project;

	public void setNatureId(String natureId)
	{
		this.natureId = natureId;
	}

	public void setProject(IVdmProject project)
	{
		this.project = project;
	}

	/**
	 * Parses a file and updates file markers and the AstManager with the result
	 */
	public void parse(IVdmSourceUnit file)
	{

		ParseResult result;
		try
		{
			LexLocation.getAllLocations().clear();
			result = startParse(file, new String(FileUtility.getCharContent(FileUtility.getContent(file.getFile()))), file.getFile().getCharset());
			setFileMarkers(file.getFile(), result,null);
			if (result != null && result.getAst() != null)
				file.reconcile(result.getAst(), result.getAllLocation(), result.getLocationToAstNodeMap(), result.hasParseErrors());

		} catch (CoreException e)
		{
			if (VdmCore.DEBUG)
			{
				VdmCore.log("AbstractParserParticipant:parse IVdmSourceUnit", e);
			}
		}
	}

	/**
	 * Parses a file and updates file markers and the AstManager with the result
	 */
	public void parse(IVdmSourceUnit file, String data)
	{

		ParseResult result;
		try
		{
			if (file.getFile().isLinked()
					&& !file.getFile().getLocation().toFile().exists())
			{
				FileUtility.deleteMarker(file.getFile(), IMarker.PROBLEM, ICoreConstants.PLUGIN_ID);
				addError(file.getFile(), "Linked file not found "
						+ file.getFile(), 0);
				file.reconcile(null, null, null, true);
			} else
			{
				LexLocation.getAllLocations().clear();
				result = startParse(file, data, file.getFile().getCharset());
				setFileMarkers(file.getFile(), result, null);
				if (result != null && result.getAst() != null)
				{
					file.reconcile(result.getAst(), result.getAllLocation(), result.getLocationToAstNodeMap(), result.hasParseErrors());
				}
			}

		} catch (CoreException e)
		{
			if (e.getStatus().getException() instanceof IOException)
			{
				FileUtility.deleteMarker(file.getFile(), IMarker.PROBLEM, ICoreConstants.PLUGIN_ID);
				if (e.getStatus() != null
						&& e.getStatus().getException() != null)
				{
					addError(file.getFile(), e.getStatus().getException().getMessage(), 0);
				}

			} else if (VdmCore.DEBUG)
			{
				VdmCore.log("AbstractParserParticipant:parse IVdmSourceUnit", e);
			}

			file.reconcile(null, null, null, true);
		} catch (Exception e)
		{
			if (VdmCore.DEBUG)
			{
				VdmCore.log("AbstractParserParticipant:parse IVdmSourceUnit", e);
			}
			FileUtility.deleteMarker(file.getFile(), IMarker.PROBLEM, ICoreConstants.PLUGIN_ID);
			addError(file.getFile(), "Internal error: " + e.getMessage(), 0);
			file.reconcile(null, null, null, true);
		}

	}

	/**
	 * Adds markers to a file based on the result. if errors occurred the problem markers are first cleared.
	 * 
	 * @param file
	 *            the file where the markers should be set
	 * @param result
	 *            the result indicating if parse errors occurred
	 * @param content 
	 * @throws CoreException
	 */
	private void setFileMarkers(IFile file, ParseResult result, String content)
			throws CoreException
	{
		if (file != null)
		{
			FileUtility.deleteMarker(file, IMarker.PROBLEM, ICoreConstants.PLUGIN_ID);
			if (result.hasParseErrors())
			{
				file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
				int previousErrorNumber = -1;
				for (VDMError error : result.getErrors())
				{
					if (previousErrorNumber == error.number)
					{// this check is
						// done to avoid
						// error fall
						// through
						continue;
					}
					else
					{
						previousErrorNumber = error.number;
					}
					
					if(content==null)
					{
						FileUtility.addMarker(file, error.toProblemString(), error.location, IMarker.SEVERITY_ERROR, ICoreConstants.PLUGIN_ID);
					}else{
						FileUtility.addMarker(file, error.toProblemString(), error.location, IMarker.SEVERITY_ERROR, ICoreConstants.PLUGIN_ID,content);
					}
				}
			}

			IVdmProject vdmProject = (IVdmProject) project.getAdapter(IVdmProject.class);

			if (result.getWarnings().size() > 0 && vdmProject != null
					&& vdmProject.hasSuppressWarnings())
			{
				for (VDMWarning warning : result.getWarnings())
				{
					if(content ==null)
					{
						FileUtility.addMarker(file, warning.toProblemString(), warning.location, IMarker.SEVERITY_WARNING, ICoreConstants.PLUGIN_ID);
					}else{
						FileUtility.addMarker(file, warning.toProblemString(), warning.location, IMarker.SEVERITY_WARNING, ICoreConstants.PLUGIN_ID,content);
					}
				}
			}
		}

	}

	/**
	 * Delegate method for parsing, the methods addWarning and addError should be used to report
	 * 
	 * @param file
	 *            the file to be parsed
	 * @param content
	 *            the content of the file
	 * @param charset
	 *            the charset of the content
	 * @return the result of the parse including error report and the AST
	 */
	protected abstract ParseResult startParse(IVdmSourceUnit file,
			String content, String charset);

	protected void addWarning(IFile file, String message, int lineNumber)
	{
		FileUtility.addMarker(file, message, lineNumber, IMarker.SEVERITY_WARNING, ICoreConstants.PLUGIN_ID);
	}

	protected void addError(IFile file, String message, int lineNumber)
	{
		FileUtility.addMarker(file, message, lineNumber, IMarker.SEVERITY_ERROR, ICoreConstants.PLUGIN_ID);
	}

	protected void processInternalError(Throwable e)
	{
		System.out.println(e.toString());
	};

	public static ReaderType findStreamReaderType(IFile file)
			throws CoreException
	{
		ReaderType streamReaderType = ReaderType.Latex;

		if (file.getFileExtension().endsWith("doc"))
		{
			streamReaderType = ReaderType.Doc;
		}else if(file.getFileExtension().endsWith("docx"))
		{
			streamReaderType = ReaderType.Docx;
		}else if(file.getFileExtension().endsWith("odt"))
		{
			streamReaderType = ReaderType.Odf;
		}
		return streamReaderType;
	}

	public static class ParseResult
	{
		private List<INode> ast = null;
		private List<VDMError> errors = new ArrayList<VDMError>();
		private List<VDMWarning> warnings = new ArrayList<VDMWarning>();
		private Throwable fatalError;
		private List<LexLocation> allLocation;
		private Map<LexLocation, INode> locationToAstNodeMap;

		public ParseResult()
		{

		}

		public boolean hasParseErrors()
		{
			return errors.size() != 0 || fatalError != null;
		}

		public void setAst(List<INode> ast)
		{
			Assert.isNotNull(ast, "AST cannot be null");
			Assert.isTrue(ast.size() != 0, "AST cannot be an empty list");
			this.ast = ast;
		}

		public List<INode> getAst()
		{
			return ast;
		}

		public void setWarnings(List<VDMWarning> warnings)
		{
			this.warnings.addAll(warnings);
		}

		public List<VDMWarning> getWarnings()
		{
			return warnings;
		}

		public void setErrors(List<VDMError> errors)
		{
			this.errors.addAll(errors);
		}

		public List<VDMError> getErrors()
		{
			return errors;
		}

		public void setFatalError(Throwable fatalError)
		{
			if (VdmCore.DEBUG)
			{
				// fatalError.printStackTrace();
			}
			this.fatalError = fatalError;
		}

		public Throwable getFatalError()
		{
			return fatalError;
		}

		public void setAllLocation(List<LexLocation> allLocation)
		{
			this.allLocation = allLocation;
		}

		public List<LexLocation> getAllLocation()
		{
			return allLocation;
		}

		public void setLocationToAstNodeMap(
				Map<LexLocation, INode> locationToAstNodeMap)
		{
			this.locationToAstNodeMap = locationToAstNodeMap;
		}

		public Map<LexLocation, INode> getLocationToAstNodeMap()
		{
			return locationToAstNodeMap;
		};

	}
}

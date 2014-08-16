/*
 * #%~
 * org.overture.ide.plugins.coverageeditor
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
package org.overture.ide.plugins.coverageeditor.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.util.definitions.ClassList;
import org.overture.ast.util.modules.ModuleList;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.coverageeditor.Activator;
import org.overture.ide.ui.editor.core.VdmDocument;
import org.overture.ide.ui.editor.core.VdmDocumentProvider;
import org.overture.parser.config.Properties;
import org.overture.parser.lex.BacktrackInputReader.ReaderType;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.ClassReader;
import org.overture.parser.syntax.ModuleReader;

@SuppressWarnings("restriction")
public abstract class CoverageEditor
{
	public class CoverageDocumentProvider extends VdmDocumentProvider
	{
		@Override
		protected IDocument createDocument(Object element) throws CoreException
		{
			if (element instanceof FileEditorInput)
			{
				IFile file = ((FileEditorInput) element).getFile();
				if (!file.exists() || file.getName().endsWith("covtbl"))
				{
					return super.createEmptyDocument();
				} else
				{
					VdmDocument doc = (VdmDocument) super.createDocument(element);
					IVdmProject project = (IVdmProject) file.getProject().getAdapter(IVdmProject.class);
					Assert.isNotNull(project, "Project could not be adapted");
					doc.setSourceUnit(new CoverageSourceUnit(project, file));
					return doc;
				}
			}
			return super.createEmptyDocument();
		}
	}

	private IFile selectedFile;
	private File sourceFile;
	private String charset;
	private String content;
	IFile vdmSourceFile = null;
	String vdmCoverage = null;
	List<StyleRange> styleRanges = new Vector<StyleRange>();
	// SourceReferenceManager sourceReferenceManager = null;
	IVdmProject project = null;

	@SuppressWarnings({ "deprecation" })
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException
	{
		IPath path = ((IPathEditorInput) input).getPath();

		try
		{
			IContainer[] hh = ResourcesPlugin.getWorkspace().getRoot().findContainersForLocation(path);
			for (IContainer c : hh)
			{
				if (c instanceof Folder)
				{
					((Folder) c).getProject();
				}
			}

			IFile res = ((FileEditorInput) input).getFile();
			if (res.exists())
			{
				charset = res.getCharset();
				sourceFile = res.getLocation().toFile();
				content = readFile(res);
				vdmSourceFile = (IFile) res;
			} else
			{
				throw new PartInitException("File not found: "
						+ res.getLocationURI().toASCIIString());
			}

			project = (IVdmProject) res.getProject().getAdapter(IVdmProject.class);
			if (project == null)
			{
				Assert.isTrue(true, "Coverage project not VDM");
			}

			String fileName = vdmSourceFile.getName().substring(0, vdmSourceFile.getName().length() - 3)
					+ ".covtbl"; // selectedFile.getName().substring(0,
			// selectedFile.getName().lastIndexOf('.'));
			IResource covTbl = findMember(res.getParent(), fileName);// project.findMember(fileName);
			if (covTbl instanceof IFile)
			{
				selectedFile = (IFile) covTbl;
				vdmCoverage = readFile(selectedFile);
			}

		} catch (IOException e)
		{
			if (Activator.DEBUG)
			{
				e.printStackTrace();
			}
		} catch (CoreException e)
		{
			if (Activator.DEBUG)
			{
				e.printStackTrace();
			}
		}
	}

	public void createPartControl(Composite parent)
	{
		if (vdmSourceFile == null || !vdmSourceFile.exists())
		{
			if (vdmCoverage != null)
			{
				getEditorSourceViewer().getTextWidget().setText(vdmCoverage);
			}
			return;
		}
		Display display = getEditorSourceViewer().getTextWidget().getDisplay();

		Color green = new Color(display, 165, 249, 171);// display.getSystemColor(SWT.COLOR_GREEN);
		Color red = new Color(display, 252, 114, 114);// display.getSystemColor(SWT.COLOR_RED);
		Color black = display.getSystemColor(SWT.COLOR_BLACK);

		LexLocation.resetLocations();
		// LexLocation.clearLocations();
		Properties.parser_tabstop = 1;
		LexTokenReader ltr = null;
		try
		{
			ltr = new LexTokenReader(content, project.getDialect(), sourceFile, charset, getReaderType(sourceFile));
		} catch (CoreException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		switch (project.getDialect())
		{
			case VDM_PP:
			case VDM_RT:
			{
				ClassReader reader;
				ClassList classes = new ClassList();
				reader = new ClassReader(ltr);
				classes.addAll(reader.readClasses());
			}
				break;
			case VDM_SL:
			{
				ModuleReader reader;
				ModuleList modules = new ModuleList();
				reader = new ModuleReader(ltr);
				modules.addAll(reader.readModules());
			}
				break;
			case CML:
				break;
		}

		BufferedReader br;
		if (selectedFile != null)
		{
			try
			{

				for (LexLocation l : LexLocation.getSourceLocations(sourceFile)) // Only
				// executable
				{
					if (l.hits == 0)
					{
						int start = l.getStartOffset();
						int end = l.getEndOffset();
						if (start < content.length() && start < end
								&& end < content.length())
						{
							styleRanges.add(new StyleRange(start - 1, end
									- start, black, red));
						}
					}

				}

				if (!selectedFile.isSynchronized(IResource.DEPTH_ZERO))
				{
					selectedFile.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
				}
				br = new BufferedReader(new BufferedReader(new InputStreamReader(selectedFile.getContents())));
				String line = br.readLine();

				while (line != null)
				{
					if (line.charAt(0) == '+')
					{
						// Hit lines are "+line from-to=hits"

						int s1 = line.indexOf(' ');
						int s2 = line.indexOf('-');
						int s3 = line.indexOf('=');

						int lnum = Integer.parseInt(line.substring(1, s1));
						int from = Integer.parseInt(line.substring(s1 + 1, s2));
						int to = Integer.parseInt(line.substring(s2 + 1, s3));
						int hits = Integer.parseInt(line.substring(s3 + 1));

						for (LexLocation l : LexLocation.getSourceLocations(sourceFile)) // Only
						// executable
						{
							if (l.startLine == lnum && l.startPos == from
									&& l.endPos == to)
							{
								l.hits += hits;

								int start = l.getStartOffset();// converter.getStartPos(l);
								int end = l.getEndOffset();// converter.getEndPos(l);
								if (start < content.length() && start < end
										&& end < content.length())
								{
									styleRanges.add(new StyleRange(start - 1, end
											- start, black, green));
								}

								break;
							}
						}
					}

					line = br.readLine();
				}

				br.close();

			} catch (IOException e)
			{
				if (Activator.DEBUG)
				{
					e.printStackTrace();
				}
			} catch (CoreException e)
			{
				if (Activator.DEBUG)
				{
					e.printStackTrace();
				}
			}
		} else
		{
			MessageDialog.openError(getEditorSourceViewer().getTextWidget().getShell(), "Error opening coverage editor", "No coverage table table info found.");
		}

		for (StyleRange styleRange : styleRanges)
		{

			try
			{
				if (0 <= styleRange.start
						&& styleRange.start + styleRange.length <= getEditorSourceViewer().getTextWidget().getCharCount())
				{
					getEditorSourceViewer().getTextWidget().replaceStyleRanges(styleRange.start, styleRange.length, new StyleRange[] { styleRange });
				} else
				{
					System.err.println("Coverage range not valid: "
							+ styleRange);
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		getEditorSourceViewer().getTextWidget().setEditable(false);
	}

	protected ReaderType getReaderType(File file) throws CoreException
	{
		return ReaderType.Latex;
	}

	protected abstract ISourceViewer getEditorSourceViewer();

	protected abstract void setEditorDocumentProvider(IDocumentProvider provider);

	public void setEditorDocumentProvider()
	{
		setEditorDocumentProvider(new CoverageDocumentProvider());
	}

	public static IResource findMember(IResource resource, String memberName)
			throws CoreException
	{
		if (resource != null && resource.getName().equals(memberName))
		{
			return resource;
		} else if (!(resource instanceof org.eclipse.core.internal.resources.File))
		{
			IResource[] members = ((IContainer) resource).members();
			for (int i = 0; i < members.length; i++)
			{
				IResource tmp = findMember(members[i], memberName);
				if (tmp != null)
				{
					return tmp;
				}
			}
		}
		return null;
	}

	public static String readFile(IFile file) throws IOException, CoreException
	{
		if (!file.isSynchronized(IResource.DEPTH_ZERO))
		{
			file.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
		}
		BufferedReader inputStream = new BufferedReader(new InputStreamReader(file.getContents(), file.getCharset()));
		StringBuilder sb = new StringBuilder();

		int inLine;
		while ((inLine = inputStream.read()) != -1)
		{
			sb.append((char) inLine);
		}
		inputStream.close();
		return sb.toString();
	}

}

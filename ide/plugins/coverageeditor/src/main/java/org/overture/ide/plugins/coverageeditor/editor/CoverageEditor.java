package org.overture.ide.plugins.coverageeditor.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import org.overture.ide.core.SourceReferenceManager;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.VdmSourceUnit;
import org.overture.ide.core.utility.SourceLocationConverter;
import org.overture.ide.plugins.coverageeditor.Activator;
import org.overture.ide.ui.editor.core.VdmDocument;
import org.overture.ide.ui.editor.core.VdmDocumentProvider;
import org.overturetool.vdmj.config.Properties;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.syntax.ClassReader;
import org.overturetool.vdmj.syntax.ModuleReader;

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
					Assert.isNotNull(project);
					doc.setSourceUnit(new VdmSourceUnit(project, file));
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
	SourceReferenceManager sourceReferenceManager = null;
	IVdmProject project = null;

	@SuppressWarnings( { "deprecation" })
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
				org.eclipse.core.internal.resources.File f = ((org.eclipse.core.internal.resources.File) res);
				charset = f.getCharset();
				sourceFile = f.getLocation().toFile();
				content = readFile(((org.eclipse.core.internal.resources.File) res).getContents());
				vdmSourceFile = (IFile) res;
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

		// getSourceViewer().getTextWidget().addLineStyleListener(new LineStyleListener()
		// {
		//			
		// public void lineGetStyle(LineStyleEvent event)
		// {
		// event.styles = styleRanges.toArray(new StyleRange[0]);
		//				
		// }
		// });

		LexLocation.resetLocations();
		LexLocation.clearLocations();
		Properties.parser_tabstop = 1;
		LexTokenReader ltr = new LexTokenReader(content, project.getDialect(), sourceFile, charset);

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
		}
		SourceLocationConverter converter = new SourceLocationConverter(content.toCharArray());

		BufferedReader br;
		try
		{
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

							int start = converter.getStartPos(l);
							int end = converter.getEndPos(l);
							if (start < content.length() && start < end
									&& end < content.length())
							{
								styleRanges.add(new StyleRange(start, end
										- start, black, green));
							}

							break;
						}
					}
				}

				line = br.readLine();
			}

			br.close();

			for (LexLocation l : LexLocation.getSourceLocations(sourceFile)) // Only
			// executable
			{
				if (l.hits == 0)
				{
					int start = converter.getStartPos(l);
					int end = converter.getEndPos(l);
					if (start < content.length() && start < end
							&& end < content.length())
					{
						styleRanges.add(new StyleRange(start, end - start, black, red));
					}
				}

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

		for (StyleRange styleRange : styleRanges)
		{
			getEditorSourceViewer().getTextWidget().replaceStyleRanges(styleRange.start, styleRange.length, new StyleRange[] { styleRange });
		}

		getEditorSourceViewer().getTextWidget().setEditable(false);
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
			return resource;
		else if (!(resource instanceof org.eclipse.core.internal.resources.File))
		{
			IResource[] members = ((IContainer) resource).members();
			for (int i = 0; i < members.length; i++)
			{
				IResource tmp = findMember(members[i], memberName);
				if (tmp != null)
					return tmp;
			}
		}
		return null;
	}

	public static String readFile(IFile file) throws IOException, CoreException
	{
		BufferedReader inputStream = new BufferedReader(new InputStreamReader(file.getContents()));
		StringBuilder sb = new StringBuilder();

		int inLine;
		while ((inLine = inputStream.read()) != -1)
		{
			sb.append((char) inLine);
		}
		inputStream.close();
		return sb.toString();
	}

	public static String readFile(InputStream stream) throws IOException
	{
		StringBuilder sb = new StringBuilder();

		int inLine;
		while ((inLine = stream.read()) != -1)
		{
			sb.append((char) inLine);
		}
		stream.close();
		return sb.toString();
	}
}

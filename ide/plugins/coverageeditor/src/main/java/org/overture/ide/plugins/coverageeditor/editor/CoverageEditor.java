package org.overture.ide.plugins.coverageeditor.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.overture.ide.utility.SourceLocationConverter;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.syntax.ClassReader;

@SuppressWarnings("restriction")
public class CoverageEditor extends EditorPart
{

	private File selectedFile;
	private File sourceFile;
	private String charset;
	private String content;
	private IProject project;

	public CoverageEditor() {

	}

	@Override
	public void doSave(IProgressMonitor monitor)
	{

	}

	@Override
	public void doSaveAs()
	{

	}

	@SuppressWarnings( { "deprecation" })
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException
	{
		setSite(site);
		setInput(input);

		IPath path = ((IPathEditorInput) input).getPath();

		selectedFile = path.toFile();

		try
		{
			readFile(selectedFile);
			IContainer[] hh = ResourcesPlugin.getWorkspace()
					.getRoot()
					.findContainersForLocation(path);
			for (IContainer c : hh)
			{
				if (c instanceof Folder)
				{
					project = ((Folder) c).getProject();
					// System.out.println(((Folder)c).getProject().getName());
				}

			}

			String fileName = selectedFile.getName().substring(0,
					selectedFile.getName().lastIndexOf('.'));
			setTitle(fileName);

			IResource res = findMember(project, fileName);//project.findMember(fileName);
			if (res instanceof org.eclipse.core.internal.resources.File)
			{
				org.eclipse.core.internal.resources.File f = ((org.eclipse.core.internal.resources.File) res);
				charset = f.getCharset();
				sourceFile = f.getLocation().toFile();
				content = readFile(((org.eclipse.core.internal.resources.File) res).getContents());
			}

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static IResource findMember(IResource resource, String memberName) throws CoreException
	{
		
		if(resource!=null && resource.getName().equals(memberName))
			return resource;
		else if(!(resource instanceof org.eclipse.core.internal.resources.File)){
			IResource[] members = ((IContainer)resource).members();
			for (int i = 0; i < members.length; i++) {
				IResource tmp= findMember(members[i], memberName);
				if(tmp!=null)
					return tmp;
			}
		}
		return null;
	}

	@Override
	public boolean isDirty()
	{
		return false;
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	@Override
	public void createPartControl(Composite parent)
	{
		Composite c = new Composite(parent, SWT.V_SCROLL);
		Display display = c.getDisplay();

		Font fontNormal = new Font(display, "Courier New", 10, SWT.NORMAL);
		// Font font2 = new Font(display, "MS Mincho", 20, SWT.ITALIC);
		// Font font3 = new Font(display, "Arabic Transparent", 18, SWT.NORMAL);

		Color green = display.getSystemColor(SWT.COLOR_GREEN);
		Color red = display.getSystemColor(SWT.COLOR_RED);
		Color black = display.getSystemColor(SWT.COLOR_BLACK);
		// Color white = display.getSystemColor(SWT.COLOR_WHITE);
		final TextLayout layout = new TextLayout(display);
		layout.setFont(fontNormal);

		// TextStyle styleNormal = new TextStyle(fontNormal, black, null);
		TextStyle styleCovered = new TextStyle(fontNormal, black, green);
		TextStyle styleNotcovered = new TextStyle(fontNormal, black, red);

		layout.setText(content);

		ClassReader reader;
		ClassList classes = new ClassList();
		LexLocation.resetLocations();
		LexLocation.clearLocations();
		LexTokenReader ltr = new LexTokenReader(content,
				Dialect.VDM_RT,
				sourceFile,
				charset);
		reader = new ClassReader(ltr);

		classes.addAll(reader.readClasses());
		SourceLocationConverter converter = new SourceLocationConverter(content.toCharArray());

		BufferedReader br;
		try
		{
			br = new BufferedReader(new FileReader(selectedFile));

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
								layout.setStyle(styleCovered, start, end);

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
					// int start = getPos(l.startLine, l.startPos);
					// int end = getPos(l.endLine, l.endPos);
					// if (start < content.length() && start < end
					// && end < content.length())
					// layout.setStyle(styleNotcovered, start, end);
					int start = converter.getStartPos(l);
					int end = converter.getEndPos(l);
					if (start < content.length() && start < end
							&& end < content.length())
						layout.setStyle(styleNotcovered, start, end);
				}

			}

		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		c.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

		c.addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event event)
			{
				layout.draw(event.gc, 10, 10);
			}
		});
	}

	@Override
	public void setFocus()
	{
	}

	public static String readFile(File file) throws IOException
	{
		// InputStreamReader reader = new
		// InputStreamReader(file.getAbsolutePath());
		// Create Buffered/PrintWriter Objects
		BufferedReader inputStream = new BufferedReader(new FileReader(file));
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
		// InputStreamReader reader = new
		// InputStreamReader(file.getAbsolutePath());
		// Create Buffered/PrintWriter Objects
		// BufferedReader inputStream = new BufferedReader(stream.);
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

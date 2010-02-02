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

@SuppressWarnings("restriction")
public class CoverageEditor extends EditorPart
{

	private File selectedFile;
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

			IResource res = project.findMember(fileName);
			if (res instanceof org.eclipse.core.internal.resources.File)
			{
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
//		Font font2 = new Font(display, "MS Mincho", 20, SWT.ITALIC);
//		Font font3 = new Font(display, "Arabic Transparent", 18, SWT.NORMAL);

		
		Color green = display.getSystemColor(SWT.COLOR_GREEN);
//		Color red = display.getSystemColor(SWT.COLOR_RED);
//		Color black = display.getSystemColor(SWT.COLOR_BLACK);

		final TextLayout layout = new TextLayout(display);
		layout.setFont(fontNormal);

//		TextStyle styleNormal = new TextStyle(fontNormal, black, null);
		TextStyle styleCovered = new TextStyle(fontNormal, green, null);
//		TextStyle styleNotcovered = new TextStyle(fontNormal, red, null);

		layout.setText(content);
		

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
					//int hits = Integer.parseInt(line.substring(s3 + 1));

					int start = getPos(lnum, from);
					int end = getPos(lnum, to);
					if (start < content.length() && start < end
							&& end < content.length())
						layout.setStyle(styleCovered, start, end);
					// for (LexLocation l: locations) // Only executable
					// locations
					// {
					// if (l.startLine == lnum &&
					// l.startPos == from &&
					// l.endPos == to)
					// {
					// l.hits += hits;
					// break;
					// }
					// }
				}

				line = br.readLine();
			}

			br.close();

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

	private int getPos(int line, int col)
	{
		String[] sourceLines = content.split("\n");
		int pos = 1;
		for (int i = 0; i < line && i < sourceLines.length; i++)
		{
			pos += sourceLines[i].length();
		}
		pos += col;
		return pos;
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

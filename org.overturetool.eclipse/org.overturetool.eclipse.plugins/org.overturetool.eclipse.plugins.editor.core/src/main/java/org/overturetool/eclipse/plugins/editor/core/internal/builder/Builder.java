package org.overturetool.eclipse.plugins.editor.core.internal.builder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.core.IScriptProject;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.DLTKConverter;

public abstract class Builder{
	private String[] extenstions = { "vpp", "tex", "vdm" };
	protected IScriptProject project;
	private IProblemReporter reporter;
	private DLTKConverter converter;
	private ArrayList<IResource> resourceList = new ArrayList<IResource>();
//	private Hashtable<String, IProblemReporter> reporters;	
	
	public Builder(IScriptProject project) {
		this.project = project;
	}
	
	public ArrayList<String> getSoruceFiles()
	{
		resourceList.clear();
		ArrayList<String> fileList = new ArrayList<String>();
		for (IResource res : getAllMemberFiles(project.getProject(), extenstions)) {
			resourceList.add(res);
			fileList.add(res.getLocation().toOSString());
		}
		return fileList;
	}
	
	/**
	 * This method returns a list of files under the given directory or its
	 * subdirectories. The directories themselves are not returned.
	 * 
	 * @param dir
	 *            a directory
	 * @return list of IResource objects representing the files under the given
	 *         directory and its subdirectories
	 */
	private ArrayList<IResource> getAllMemberFiles(IContainer dir, String[] exts) {
		ArrayList<IResource> list = new ArrayList<IResource>();
		IResource[] arr = null;
		try {
			arr = dir.members();
		} catch (CoreException e) {
		}

		for (int i = 0; arr != null && i < arr.length; i++) {
			if (arr[i].getType() == IResource.FOLDER) {
				list.addAll(getAllMemberFiles((IFolder) arr[i], exts));
			} else {

				for (int j = 0; j < exts.length; j++) {
					if (exts[j].equalsIgnoreCase(arr[i].getFileExtension())) {
						list.add(arr[i]);
						break;
					}
				}
			}
		}
		return list;
	}

	protected String getContent(IResource res) {
		try {
			FileReader fr = new FileReader(res.getLocation().toOSString());
			BufferedReader myInput = new BufferedReader(fr);

			String s;
			StringBuffer b = new StringBuffer();
			while ((s = myInput.readLine()) != null) {
				b.append(s);
				b.append("\n");
			}

			return b.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	protected String getContent(String filename) {
		try {
			FileReader fr = new FileReader(filename);
			BufferedReader myInput = new BufferedReader(fr);

			String s;
			StringBuffer b = new StringBuffer();
			while ((s = myInput.readLine()) != null) {
				b.append(s);
				b.append("\n");
			}

			return b.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	

	
	abstract public IStatus typeCheck();
	
	protected void addMarker(
			String filename, 
			String message, 
			int lineNumber, 
			int severity,
			int charStart,
			int charEnd) {
		try {
			IFile file = null;
			for (IResource fileRes : resourceList) {
				if (fileRes.getLocation().toOSString().equals(filename)){
					
					file = (IFile) fileRes;
				}
			}
			if (file != null)
			{
				IMarker marker = file.createMarker(IMarker.PROBLEM);
				marker.setAttribute(IMarker.MESSAGE, message);
				marker.setAttribute(IMarker.SEVERITY, severity);
				if (lineNumber == -1) {
					lineNumber = 1;
				}
				marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
				StringBuilder content = inputStreamToString(file.getContents());
				if (content != null)
				{
					converter = new DLTKConverter(content.toString().toCharArray());
					marker.setAttribute(IMarker.CHAR_START, converter.convert(lineNumber, charStart));
					marker.setAttribute(IMarker.CHAR_END, converter.convert(lineNumber, charEnd));				
				}
			}
		} catch (CoreException e) {
			System.out.println("Error when adding a marker... : " + e.getMessage());
		}
	}
	
	private StringBuilder inputStreamToString(InputStream is)
	{
		try {
			final char[] buffer = new char[0x10000];
			StringBuilder out = new StringBuilder();
			Reader in = new InputStreamReader(is, "UTF-8");
			int read;
			do {
				read = in.read(buffer, 0, buffer.length);
				if (read>0) 
				{
					out.append(buffer, 0, read);
				}
			}		while (read>=0);
			return out;			
		} catch (Exception e) {
			return null;
		}
	}
	
	protected void clearMarkers()
	{
		try {
			for (IResource fileRes : resourceList) {
				IFile file = (IFile) fileRes;
				IMarker[] markers = file.findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
				for (IMarker marker : markers) {
					marker.delete();
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
}

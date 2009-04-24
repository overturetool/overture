package org.overturetool.eclipse.plugins.editor.core.internal.builder;
//package org.internal.overturetool.builder;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.util.ArrayList;
//import java.util.Dictionary;
//import java.util.Hashtable;
//
//import org.eclipse.core.resources.IContainer;
//import org.eclipse.core.resources.IFolder;
//import org.eclipse.core.resources.IProject;
//import org.eclipse.core.resources.IResource;
//import org.eclipse.core.runtime.CoreException;
//import org.eclipse.dltk.compiler.problem.DefaultProblem;
//import org.eclipse.dltk.compiler.problem.DefaultProblemFactory;
//import org.eclipse.dltk.compiler.problem.IProblemReporter;
//import org.eclipse.dltk.compiler.problem.ProblemSeverities;
//import org.eclipse.dltk.core.IScriptProject;
//import org.internal.overturetool.parser.DLTKConverter;
//
//public class DLTKProblemAdapter {
////	IProblemReporter reporter;
//	private String[] extenstions = { "vpp", "tex" };
//	private DLTKConverter converter;
//	private IScriptProject project;
//	private Hashtable<String, IProblemReporter> reporters;
//	
//	public DLTKProblemAdapter(IScriptProject project) {
//		this.project = project;
//		reporters = new Hashtable<String, IProblemReporter>();
//	}
//
//	public void reportError(
//			String filename, String message, int errorNumber, 
//			int startLine, int startPos,
//			int endLine, int endPos)
//	{
//		// TODO hack??? only add one reporter for each file
//		if (!reporters.containsKey(filename)){
//			DefaultProblemFactory problemFactory = new DefaultProblemFactory();
//			IResource file = project.getProject().getProject().findMember(filename.substring(project.getProject().getLocationURI().getPath().length() ));
//			reporters.put(filename, problemFactory.createReporter(file));
//			
//		}
//		
//		converter = new DLTKConverter(getContent(filename).toCharArray());
//		int offsetStart = converter.convert(startLine, startPos);
//		int offsetEnd = converter.convert(startLine, endPos);
//				
//		DefaultProblem defaultProblem = new DefaultProblem( filename, 
//				message,
//				errorNumber,
//				new String[] {},
//				ProblemSeverities.Error, 
//				offsetStart,
//				offsetEnd,
//				startLine,
//				endPos);
//		reporters.get(filename).reportProblem(defaultProblem);
//	}
//	
//	public void reportWarning(
//			String filename, String message, int errorNumber, 
//			int startLine, int startPos,
//			int endLine, int endPos)
//	{
//		// TODO hack??? only add one reporter for each file
////		if (!reporters.containsKey(filename)){
////			DefaultProblemFactory problemFactory = new DefaultProblemFactory();
////			IResource file = project.getProject().getProject().findMember(filename.substring(project.getProject().getLocationURI().getPath().length() ));
////			reporters.put(filename, problemFactory.createReporter(file));
////			
////		}
////		converter = new DLTKConverter(getContent(filename).toCharArray());
////		int offsetStart = converter.convert(startLine, startPos);
////		int offsetEnd = converter.convert(startLine, endPos);
////		
////		DefaultProblem defaultProblem = new DefaultProblem( filename, 
////				message,
////				errorNumber,
////				new String[] {},
////				ProblemSeverities.Warning, 
////				offsetStart,
////				offsetEnd,
////				startLine,
////				endPos);
////		reporters.get(filename).reportProblem(defaultProblem);
//	}
//	
//
//	
//
//	
//	public ArrayList<String> getSoruceFiles(IProject project)
//	{
//		ArrayList<IResource> resList = new ArrayList<IResource>();
//		ArrayList<String> fileList = new ArrayList<String>();
//		for (IResource res : getAllMemberFiles(project.getProject(), extenstions)) {
//			resList.add(res);
//			fileList.add(res.getLocation().toOSString());
//		}
//		return fileList;
//	}
//	
//	/**
//	 * This method returns a list of files under the given directory or its
//	 * subdirectories. The directories themselves are not returned.
//	 * 
//	 * @param dir
//	 *            a directory
//	 * @return list of IResource objects representing the files under the given
//	 *         directory and its subdirectories
//	 */
//	private ArrayList<IResource> getAllMemberFiles(IContainer dir, String[] exts) {
//		ArrayList<IResource> list = new ArrayList<IResource>();
//		IResource[] arr = null;
//		try {
//			arr = dir.members();
//		} catch (CoreException e) {
//		}
//
//		for (int i = 0; arr != null && i < arr.length; i++) {
//			if (arr[i].getType() == IResource.FOLDER) {
//				list.addAll(getAllMemberFiles((IFolder) arr[i], exts));
//			} else {
//
//				for (int j = 0; j < exts.length; j++) {
//					if (exts[j].equalsIgnoreCase(arr[i].getFileExtension())) {
//						list.add(arr[i]);
//						break;
//					}
//				}
//			}
//		}
//		return list;
//	}
//
//	protected String getContent(IResource res) {
//		try {
//			FileReader fr = new FileReader(res.getLocation().toOSString());
//			BufferedReader myInput = new BufferedReader(fr);
//
//			String s;
//			StringBuffer b = new StringBuffer();
//			while ((s = myInput.readLine()) != null) {
//				b.append(s);
//				b.append("\n");
//			}
//
//			return b.toString();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return "";
//	}
//	
//	protected String getContent(String filename) {
//		try {
//			FileReader fr = new FileReader(filename);
//			BufferedReader myInput = new BufferedReader(fr);
//
//			String s;
//			StringBuffer b = new StringBuffer();
//			while ((s = myInput.readLine()) != null) {
//				b.append(s);
//				b.append("\n");
//			}
//
//			return b.toString();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return "";
//	}
//
//}

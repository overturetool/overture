package org.overture.ide.vdmsl.parsers.vdmj.internal;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.AbstractSourceParser;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.compiler.problem.ProblemSeverities;
import org.overture.ide.ast.AstManager;
import org.overture.ide.ast.dltk.DltkConverter;
import org.overture.ide.vdmsl.core.VdmSlProjectNature;
import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;

public class VdmjSourceParser extends AbstractSourceParser
{
	protected IFile findIFile(IProject project,File file)
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath location = Path.fromOSString(file.getAbsolutePath());
		IFile ifile = workspace.getRoot().getFileForLocation(location);
		
		if(ifile==null)
		{
			IPath absolutePath = new Path(file.getAbsolutePath());
			boolean s= absolutePath.isAbsolute();
			absolutePath.setDevice("c:");
//			Object gg  = workspace.getRoot().findFilesForLocation(absolutePath);
//			ifile = ((IFile[])gg)[1];
//			ifile.getProject()
			for(IFile selectedFile : workspace.getRoot().findFilesForLocation(absolutePath))
			{
				if(selectedFile.getProject().equals(project))
					return selectedFile;
			}
		}
		return ifile;
	}
	public ModuleDeclaration parse(char[] fileName, char[] source,
			IProblemReporter reporter)
	{

		// find project
		Path path = new Path(new String(fileName));
		IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(
				path);
		IProject project = res.getProject();
		
		try {
			res.deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		DltkConverter converter = new DltkConverter(source);

		IEclipseVdmj eclipseParser = new EclipseVdmjSl();

		ExitStatus status = eclipseParser.parse(new String(source),project.getFile(path.removeFirstSegments(1)).getLocation().toFile());//project.getFile(path.removeFirstSegments(1)).getLocation().toFile()//parse(new String(source));

		if (reporter != null)
		{
			if (status == ExitStatus.EXIT_ERRORS)
			{
				for (VDMError error : eclipseParser.getParseErrors())
				{
					DefaultProblem defaultProblem = new DefaultProblem(
							new String(fileName), error.message, error.number,
							new String[] {}, ProblemSeverities.Error,
							converter.convert(
									error.location.startLine,
									error.location.startPos - 1),
							converter.convert(
									error.location.endLine,
									error.location.endPos - 1),
							error.location.startLine);
					reporter.reportProblem(defaultProblem);
				}
			}
			if (eclipseParser.getParseWarnings().size() > 0)
			{
				for (VDMWarning warning : eclipseParser.getParseWarnings())
				{
					DefaultProblem defaultProblem = new DefaultProblem(
							new String(fileName), warning.message,
							warning.number, new String[] {},
							ProblemSeverities.Warning, converter.convert(
									warning.location.startLine,
									warning.location.startPos - 1),
							converter.convert(
									warning.location.endLine,
									warning.location.endPos - 1),
							warning.location.startLine);
					reporter.reportProblem(defaultProblem);
				}
			}
		}
		

//		AstManager.instance().setAst(
//				project,
//				VdmSlProjectNature.VDM_SL_NATURE,
//				eclipseParser.getModules());
		return AstManager.instance().addAstModuleDeclaration(
				project,
				VdmSlProjectNature.VDM_SL_NATURE,fileName,source,eclipseParser.getModules());

	}

}
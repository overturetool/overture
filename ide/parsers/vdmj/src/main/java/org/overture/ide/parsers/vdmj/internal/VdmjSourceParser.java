package org.overture.ide.parsers.vdmj.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.AbstractSourceParser;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.overture.ide.ast.AstManager;
import org.overture.ide.ast.IAstManager;
import org.overture.ide.ast.RootNode;
import org.overture.ide.utility.FileUtility;
import org.overture.ide.utility.ProjectUtility;
import org.overture.ide.utility.VdmProject;
import org.overture.ide.vdmpp.core.VdmPpCorePlugin;
import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;

/***
 * Used to parse VDM files with VDMJ based on the dialect
 * 
 * @author kela
 * 
 */
public abstract class VdmjSourceParser extends AbstractSourceParser
{

	String nature;
	private List<VDMError> errors = new ArrayList<VDMError>();
	private List<VDMWarning> warnings = new ArrayList<VDMWarning>();

	public VdmjSourceParser(String nature) {

		this.nature = nature;
	}

	public ModuleDeclaration parse(char[] fileName, char[] source,
			IProblemReporter reporter)
	{
		String fileNameString = new String(fileName);
		// find project
		Path path = new Path(fileNameString);

		IResource res = ResourcesPlugin.getWorkspace()
				.getRoot()
				.findMember(path);
		IProject project = res.getProject();

		errors.clear();
		warnings.clear();

		File file = ProjectUtility.getFile(project, path);
		IFile ifile = null;
		String charset = "";
		try
		{
			ifile = ProjectUtility.findIFile(project, file);
			charset = ifile.getCharset();

			// ProjectUtility.findIFile(project,
			// file).deleteMarkers(IMarker.PROBLEM, true,
			// IResource.DEPTH_INFINITE);
		} catch (CoreException e)
		{
			e.printStackTrace();
		}

		try
		{
			Settings.release = new VdmProject(project).getLanguageVersion();
		} catch (CoreException e)
		{
			e.printStackTrace();
		}
		ExitStatus status = parse(new String(source), file, charset);// project.getFile(path.removeFirstSegments(1)).getLocation().toFile()//parse(new
		// String(source));

		if (ifile != null)
		{
			if (status == ExitStatus.EXIT_ERRORS)
			{
				deleteMarkers(ifile);
				int previousErrorNumber = -1;
				for (VDMError error : errors)
				{
					if (previousErrorNumber == error.number)// this check is
															// done to avoid
															// error fall
															// through
						continue;
					else
						previousErrorNumber = error.number;
					FileUtility.addMarker(ifile,
							error.message,
							error.location,
							IMarker.SEVERITY_ERROR,
							VdmPpCorePlugin.PLUGIN_ID);
				}
			}
			if (warnings.size() > 0
					&& !new VdmProject(project).hasSuppressWarnings())
			{
				for (VDMWarning warning : warnings)
				{
					FileUtility.addMarker(ifile,
							warning.message,
							warning.location,
							IMarker.SEVERITY_WARNING,
							VdmPpCorePlugin.PLUGIN_ID);
				}
			}
		}

		// AstManager.instance().setAst(
		// project,
		// VdmSlProjectNature.VDM_SL_NATURE,
		// eclipseParser.getModules());

		IAstManager astManager = AstManager.instance();
		ModuleDeclaration moduleDeclaration = astManager.addAstModuleDeclaration(project,
				nature,
				fileName,
				source,
				getModelElements());
		RootNode rootNode = astManager.getRootNode(project, nature);
		if (rootNode != null)
		{
			if (status == ExitStatus.EXIT_ERRORS)
			{
				rootNode.setParseCorrect(file.getAbsolutePath(), false);
			} else
			{
				rootNode.setParseCorrect(file.getAbsolutePath(), true);
			}
		}
		return moduleDeclaration;
	}

	/**
	 * Deletes all problem markers in the file
	 * @param ifile
	 */
	private void deleteMarkers(IFile ifile)
	{
		try
		{
			ifile.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e)
		{
			e.printStackTrace();
		}
	}

	// public abstract ExitStatus typeCheck();

	/**
	 * Parse the content of a file and set the file parsed as the file in the
	 * token locations. The value returned is the number of syntax errors
	 * encountered.
	 * 
	 * @param content
	 *            The content of the file to parse.
	 * @param file
	 *            The file to set in token locations.
	 * @return The number of syntax errors.
	 */

	public abstract ExitStatus parse(String content, File file, String charset);

	/**
	 * Handle Errors
	 * 
	 * @param list
	 *            encountered during a parse or type check
	 */
	protected void processErrors(List<VDMError> errors)
	{
		this.errors.addAll(errors);
	};

	/**
	 * Handle Warnings
	 * 
	 * @param errors
	 *            encountered during a parse or type check
	 */
	protected void processWarnings(List<VDMWarning> warnings)
	{
		this.warnings.addAll(warnings);
	};

	protected void processInternalError(Throwable e)
	{
		System.out.println(e.toString());
	};

	@SuppressWarnings("unchecked")
	public abstract List getModelElements();

}
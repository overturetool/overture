package org.overture.ide.core.parser;



import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.overture.ide.core.ICoreConstants;
import org.overture.ide.core.ast.AstManager;
import org.overture.ide.core.ast.IAstManager;
import org.overture.ide.core.ast.RootNode;
import org.overture.ide.core.utility.FileUtility;
import org.overture.ide.core.utility.IVdmProject;
import org.overture.ide.core.utility.VdmProject;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;


public abstract class AbstractParserParticipant implements ISourceParser {

	protected String natureId;
	protected IVdmProject project;
	
	private List<VDMError> errors = new ArrayList<VDMError>();
	private List<VDMWarning> warnings = new ArrayList<VDMWarning>();

	public void setNatureId(String natureId) {
		this.natureId = natureId;
	}

	public void setProject(IVdmProject project) {
		this.project = project;
	}
	private void clearFileMarkers(IFile file)
	{
		try {
			file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	public  void parse(char[] charArray, char[] source, IFile file)
//	{
//		clearFileMarkers(file);
//		ParseResult result = startParse(charArray,source,file);
//		if (result != null)
//			setParseAst(file.getLocation().toFile().getAbsolutePath(),result.getAst(),!result.hasParseErrors());
//	}

	public  void parse(IFile file)
	{
//		clearFileMarkers(file);
		ParseResult result;
		try
		{
			result = startParse(file,new String(FileUtility.getCharContent(FileUtility.getContent(file))),file.getCharset() );
			setFileMarkers(file,result);
		if (result != null)
			setParseAst(file.getLocation().toFile().getAbsolutePath(),result.getAst(),!result.hasParseErrors());
		
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void parse(IFile file, String data) {
		
		ParseResult result;
		try
		{
			result = startParse(file, data, file.getCharset());
		setFileMarkers(file,result);
		if (result != null)
			
			setParseAst(file.getLocation().toFile().getAbsolutePath(),result.getAst(),!result.hasParseErrors());
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

private void setFileMarkers(IFile file,ParseResult result)
	{
	if (file != null)
	{
		if (result.hasParseErrors())
		{
			clearFileMarkers(file);
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
				FileUtility.addMarker(file,
						error.message,
						error.location,
						IMarker.SEVERITY_ERROR,
						ICoreConstants.PLUGIN_ID);
			}
		}
		
		IVdmProject vdmProject = null;
		if(VdmProject.isVdmProject(project))
		{
			try
			{
				vdmProject = new VdmProject(project);
			} catch (Exception e)
			{
				
			}
		}
		
		if (warnings.size() > 0
				&&vdmProject!=null && vdmProject.hasSuppressWarnings())
		{
			for (VDMWarning warning : warnings)
			{
				FileUtility.addMarker(file,
						warning.message,
						warning.location,
						IMarker.SEVERITY_WARNING,
						ICoreConstants.PLUGIN_ID);
			}
		}
	}
		
	}

//	protected abstract ParseResult startParse(IFile file, String data);
	protected abstract ParseResult startParse(IFile file, String source, String contentType);
//	protected abstract ParseResult startParse(IFile file);

	@SuppressWarnings("unchecked")
	protected void setParseAst(String filePath,List ast, boolean parseErrorsOccured) {
		IAstManager astManager = AstManager.instance();
		astManager.updateAst(project, project.getVdmNature(), ast);
		RootNode rootNode = astManager.getRootNode(project, natureId);
		if (rootNode != null) {
			
			rootNode.setParseCorrect(filePath,!parseErrorsOccured);

		}
	}

	protected void addWarning(IFile file, String message, int lineNumber) {
		FileUtility.addMarker(file, message, lineNumber,
				IMarker.SEVERITY_WARNING);
	}

	protected void addError(IFile file, String message, int lineNumber) {
		FileUtility
				.addMarker(file, message, lineNumber, IMarker.SEVERITY_ERROR);
	}
	
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
	public
	class ParseResult {
		private boolean hasParseErrors = false;

		private List ast = null;

		public ParseResult(boolean hasParseErrors, List ast) {
			this.setHasParseErrors(hasParseErrors);
			this.setAst(ast);
		}

		private void setHasParseErrors(boolean hasParseErrors) {
			this.hasParseErrors = hasParseErrors;
		}

		public boolean hasParseErrors() {
			return hasParseErrors;
		}

		private void setAst(List ast) {
			this.ast = ast;
		}

		public List getAst() {
			return ast;
		}

	}
}

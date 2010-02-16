package org.overture.ide.core.parser;



import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.overture.ide.core.ast.AstManager;
import org.overture.ide.core.ast.IAstManager;
import org.overture.ide.core.ast.RootNode;
import org.overture.ide.core.utility.FileUtility;


public abstract class AbstractParserParticipant implements ISourceParser {

	protected String natureId;
	protected IProject project;

	public void setNatureId(String natureId) {
		this.natureId = natureId;
	}

	public void setProject(IProject project) {
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

	public  void parse(char[] charArray, char[] source, IFile file)
	{
		clearFileMarkers(file);
		ParseResult result = startParse(charArray,source,file);
		if (result != null)
			setParseAst(file.getLocation().toFile().getAbsolutePath(),result.getAst(),!result.hasParseErrors());
	}

	public  void parse(IFile file)
	{
		clearFileMarkers(file);
		ParseResult result = startParse(file);
		if (result != null)
			setParseAst(file.getLocation().toFile().getAbsolutePath(),result.getAst(),!result.hasParseErrors());
	}

	public void parse(IFile file, String data) {
		try {
			file.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ParseResult result = startParse(file, data);
		if (result != null)
			
			setParseAst(file.getLocation().toFile().getAbsolutePath(),result.getAst(),!result.hasParseErrors());

	}

	public abstract ParseResult startParse(IFile file, String data);
	public abstract ParseResult startParse(char[] charArray, char[] source, IFile file);
	public abstract ParseResult startParse(IFile file);

	@SuppressWarnings("unchecked")
	protected void setParseAst(String filePath,List ast, boolean parseErrorsOccured) {
		IAstManager astManager = AstManager.instance();
		astManager.updateAst(project, natureId, ast);
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

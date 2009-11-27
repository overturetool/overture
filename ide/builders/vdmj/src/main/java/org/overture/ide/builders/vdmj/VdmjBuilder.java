package org.overture.ide.builders.vdmj;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.overture.ide.ast.AstManager;
import org.overture.ide.ast.IAstManager;
import org.overture.ide.ast.RootNode;
import org.overture.ide.builders.builder.AbstractBuilder;
import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;

public abstract class VdmjBuilder  extends AbstractBuilder
{
	private List<VDMError> errors = new ArrayList<VDMError>();
	private List<VDMWarning> warnings = new ArrayList<VDMWarning>();
	private final int adjustPosition = 1;
	
	protected IStatus buileModelElements(IProject project)
	{	
		
		ExitStatus typeCheckStatus = null;
		
		typeCheckStatus = typeCheck();
		if (typeCheckStatus == ExitStatus.EXIT_ERRORS)
		{
			for (VDMError error : errors)
			{
				addErrorMarker(project,error);
			}
		}
		for (VDMWarning warning : warnings)
		{
			addWarningMarker(project,warning);
		}

		if (typeCheckStatus == ExitStatus.EXIT_ERRORS) {
			IStatus typeChecked = new Status(IStatus.ERROR,
					VdmjBuilderPluginConstants.PLUGIN_ID, 0, "not typechecked",
					null);
			return typeChecked;
		} 
		else {

			IStatus typeChecked = new Status(IStatus.OK,
					VdmjBuilderPluginConstants.PLUGIN_ID, 0, "Type Checked",
					null);
			return typeChecked;
		}
		
	}

	private void addErrorMarker(IProject project,VDMError error)
	{
		try
		{
			AbstractBuilder.addMarker(
					findIFile(project,error.location.file),
					error.message,
					error.location.startLine,
					IMarker.SEVERITY_ERROR,
					error.location.startPos - adjustPosition,
					error.location.endPos - adjustPosition);
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void addWarningMarker(IProject project,VDMWarning error)
	{
		try
		{
			AbstractBuilder.addMarker(
					findIFile(project,error.location.file),
					error.message,
					error.location.startLine,
					IMarker.SEVERITY_WARNING,
					error.location.startPos - adjustPosition,
					error.location.endPos - adjustPosition);
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Handle Errors
	 * 
	 * @param list
	 *            encountered during a parse or type check
	 */
	protected void processErrors(List<VDMError> errors) {
		this.errors.addAll(errors);
	};

	/**
	 * Handle Warnings
	 * 
	 * @param errors
	 *            encountered during a parse or type check
	 */
	protected void processWarnings(List<VDMWarning> warnings) {
		this.warnings.addAll(warnings);
	};
	
	protected void processInternalError(Throwable e) {
		System.out.println(e.toString());
	};

	public abstract ExitStatus typeCheck();
}

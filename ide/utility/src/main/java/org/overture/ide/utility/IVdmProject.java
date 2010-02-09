package org.overture.ide.utility;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.overturetool.vdmj.Release;

public interface IVdmProject
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.utility.IVdmProject#getProject()
	 */
	public abstract IProject getProject();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.overture.ide.utility.IVdmProject#setBuilder(org.overturetool.vdmj
	 * .Release)
	 */
	public abstract void setBuilder(Release languageVersion)
			throws CoreException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.utility.IVdmProject#hasBuilder()
	 */
	public abstract boolean hasBuilder() throws CoreException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.utility.IVdmProject#getLanguageVersion()
	 */
	public abstract Release getLanguageVersion() throws CoreException;
	
	public abstract String getLanguageVersionName() throws CoreException;

	public abstract boolean hasDynamictypechecks();

	public abstract boolean hasInvchecks();

	public abstract boolean hasPostchecks();

	public abstract boolean hasPrechecks();

	public abstract boolean hasSuppressWarnings();

	public abstract void setDynamictypechecks(Boolean value)
			throws CoreException;

	public abstract void setInvchecks(Boolean value) throws CoreException;

	public abstract void setPostchecks(Boolean value) throws CoreException;

	public abstract void setPrechecks(Boolean value) throws CoreException;

	public abstract void setSuppressWarnings(Boolean value)
			throws CoreException;
	
	public abstract void typeCheck() throws CoreException;
	public abstract void typeCheck(IProgressMonitor monitor) throws CoreException;

}
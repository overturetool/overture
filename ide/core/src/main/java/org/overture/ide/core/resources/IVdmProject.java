package org.overture.ide.core.resources;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.overture.ide.core.IVdmModel;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.lex.Dialect;

public interface IVdmProject extends IAdaptable
{

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.utility.IVdmProject#setBuilder(org.overturetool.vdmj .Release)
	 */
	public abstract void setBuilder(Release languageVersion)
			throws CoreException;

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.utility.IVdmProject#hasBuilder()
	 */
	public abstract boolean hasBuilder() throws CoreException;

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.utility.IVdmProject#getLanguageVersion()
	 */
	public abstract Release getLanguageVersion() throws CoreException;

	public abstract String getLanguageVersionName() throws CoreException;

	public abstract List<String> getContentTypeIds();

	public abstract Dialect getDialect();

	public abstract boolean hasDynamictypechecks();

	public abstract boolean hasInvchecks();

	public abstract boolean hasPostchecks();

	public abstract boolean hasPrechecks();

	public abstract boolean hasMeasurechecks();

	public abstract boolean hasSuppressWarnings();

	public abstract void setDynamictypechecks(Boolean value)
			throws CoreException;

	public abstract void setInvchecks(Boolean value) throws CoreException;

	public abstract void setPostchecks(Boolean value) throws CoreException;

	public abstract void setPrechecks(Boolean value) throws CoreException;

	public abstract void setMeasurechecks(Boolean value) throws CoreException;

	public abstract void setSuppressWarnings(Boolean value)
			throws CoreException;

	public abstract boolean typeCheck(IProgressMonitor monitor)
			throws CoreException;

	public abstract void typeCheck(boolean clean, IProgressMonitor monitor)
			throws CoreException;

	public abstract String getName();

	public abstract String getVdmNature();

	public abstract List<IVdmSourceUnit> getSpecFiles() throws CoreException;

	public abstract File getFile(IFile file);

	public abstract IFile findIFile(File file);

	public abstract File getSystemFile(IPath path);

	public abstract File getFile(IWorkspaceRoot wroot, IPath path);

	public abstract IVdmSourceUnit findSourceUnit(IFile file)
			throws CoreException;

	public abstract IVdmModel getModel();

}
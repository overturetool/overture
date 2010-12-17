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
	public void setBuilder(Release languageVersion)
			throws CoreException;

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.utility.IVdmProject#hasBuilder()
	 */
	public boolean hasBuilder() throws CoreException;

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.utility.IVdmProject#getLanguageVersion()
	 */
	public Release getLanguageVersion() throws CoreException;

	public String getLanguageVersionName() throws CoreException;

	public List<String> getContentTypeIds();

	public Dialect getDialect();

	public boolean hasDynamictypechecks();

	public boolean hasInvchecks();

	public boolean hasPostchecks();

	public boolean hasPrechecks();

	public boolean hasMeasurechecks();

	public boolean hasSuppressWarnings();

	public void setDynamictypechecks(Boolean value)
			throws CoreException;

	public void setInvchecks(Boolean value) throws CoreException;

	public void setPostchecks(Boolean value) throws CoreException;

	public void setPrechecks(Boolean value) throws CoreException;

	public void setMeasurechecks(Boolean value) throws CoreException;

	public void setSuppressWarnings(Boolean value)
			throws CoreException;

	public boolean typeCheck(IProgressMonitor monitor)
			throws CoreException;

	public void typeCheck(boolean clean, IProgressMonitor monitor)
			throws CoreException;

	public String getName();

	public String getVdmNature();

	public List<IVdmSourceUnit> getSpecFiles() throws CoreException;

	public File getFile(IFile file);
	
	boolean isModelFile(IFile file) throws CoreException;

	public IFile findIFile(File file);

	public File getSystemFile(IPath path);

	public File getFile(IWorkspaceRoot wroot, IPath path);

	public IVdmSourceUnit findSourceUnit(IFile file)
			throws CoreException;

	public IVdmModel getModel();

}
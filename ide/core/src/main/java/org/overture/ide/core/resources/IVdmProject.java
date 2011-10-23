/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.core.resources;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.overture.ide.core.ICoreConstants;
import org.overture.ide.core.IVdmModel;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.lex.Dialect;

public interface IVdmProject extends IAdaptable
{
	public static final IContentType externalFileContentType = Platform.getContentTypeManager().getContentType(ICoreConstants.EXTERNAL_CONTENT_TYPE_ID);
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

	public List<IContentType> getContentTypeIds();

	public Dialect getDialect();

//	public boolean hasDynamictypechecks();
//
//	public boolean hasInvchecks();
//
//	public boolean hasPostchecks();
//
//	public boolean hasPrechecks();
//
//	public boolean hasMeasurechecks();

	public boolean hasSuppressWarnings();

//	public void setDynamictypechecks(Boolean value)
//			throws CoreException;
//
//	public void setInvchecks(Boolean value) throws CoreException;
//
//	public void setPostchecks(Boolean value) throws CoreException;
//
//	public void setPrechecks(Boolean value) throws CoreException;
//
//	public void setMeasurechecks(Boolean value) throws CoreException;

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

	public ModelBuildPath getModelBuildPath();
	
	public Options getOptions();

}
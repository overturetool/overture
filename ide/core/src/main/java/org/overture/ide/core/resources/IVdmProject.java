/*
 * #%~
 * org.overture.ide.core
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
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
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.ide.core.ICoreConstants;
import org.overture.ide.core.IVdmModel;

public interface IVdmProject extends IAdaptable
{
	public static final IContentType externalFileContentType = Platform.getContentTypeManager().getContentType(ICoreConstants.EXTERNAL_CONTENT_TYPE_ID);
	public void setBuilder(Release languageVersion)
			throws CoreException;

	public boolean hasBuilder() throws CoreException;

	public Release getLanguageVersion() throws CoreException;

	public String getLanguageVersionName() throws CoreException;

	public List<IContentType> getContentTypeIds();

	public Dialect getDialect();

	public boolean hasSuppressWarnings();

	public void setSuppressWarnings(Boolean value)
			throws CoreException;
	
	public boolean hasUseStrictLetDef();

	public void setUseStrictLetDef(Boolean value) 
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

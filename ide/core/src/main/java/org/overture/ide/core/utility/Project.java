package org.overture.ide.core.utility;

import java.net.URI;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentTypeMatcher;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

/* Eclipse 3.6 Update*/
//import org.eclipse.core.resources.*;
//import org.eclipse.core.resources.IPathVariableManager;

@SuppressWarnings( { "deprecation", "unchecked" })
public abstract class Project implements IProject
{
	protected IProject project;

	public Project(IProject project)
	{
		this.project = project;
	}

	public IProject getProject()
	{
		return project;
	}

	public String getProjectName()
	{
		return this.project.getName();
	}

	public void build(int kind, IProgressMonitor monitor) throws CoreException
	{
		this.project.build(kind, monitor);

	}

	public void build(int kind, String builderName, Map args,
			IProgressMonitor monitor) throws CoreException
	{
		this.project.build(kind, builderName, args, monitor);

	}

	public void close(IProgressMonitor monitor) throws CoreException
	{
		this.project.close(monitor);

	}

	public void create(IProgressMonitor monitor) throws CoreException
	{
		this.project.create(monitor);

	}

	public void create(IProjectDescription description, IProgressMonitor monitor)
			throws CoreException
	{
		this.project.create(description, monitor);

	}

	public void create(IProjectDescription description, int updateFlags,
			IProgressMonitor monitor) throws CoreException
	{
		this.project.create(description, updateFlags, monitor);

	}

	public void delete(boolean deleteContent, boolean force,
			IProgressMonitor monitor) throws CoreException
	{
		this.project.delete(deleteContent, force, monitor);

	}

	public IContentTypeMatcher getContentTypeMatcher() throws CoreException
	{
		return this.project.getContentTypeMatcher();
	}

	public IProjectDescription getDescription() throws CoreException
	{
		return this.project.getDescription();
	}

	public IFile getFile(String name)
	{
		return this.project.getFile(name);
	}

	public IFolder getFolder(String name)
	{
		return this.project.getFolder(name);
	}

	public IProjectNature getNature(String natureId) throws CoreException
	{
		return this.project.getNature(natureId);
	}

	public IPath getPluginWorkingLocation(IPluginDescriptor plugin)
	{
		return this.project.getPluginWorkingLocation(plugin);
	}

	public IProject[] getReferencedProjects() throws CoreException
	{
		return this.getReferencedProjects();
	}

	public IProject[] getReferencingProjects()
	{
		return this.getReferencingProjects();
	}

	public IPath getWorkingLocation(String id)
	{
		return this.getWorkingLocation(id);
	}

	public boolean hasNature(String natureId) throws CoreException
	{
		return this.project.hasNature(natureId);
	}

	public boolean isNatureEnabled(String natureId) throws CoreException
	{
		return this.project.isNatureEnabled(natureId);
	}

	public boolean isOpen()
	{
		return this.project.isOpen();
	}

	public void move(IProjectDescription description, boolean force,
			IProgressMonitor monitor) throws CoreException
	{
		this.project.move(description, force, monitor);

	}

	public void open(IProgressMonitor monitor) throws CoreException
	{
		this.project.open(monitor);

	}

	public void open(int updateFlags, IProgressMonitor monitor)
			throws CoreException
	{
		this.project.open(updateFlags, monitor);

	}

	public void setDescription(IProjectDescription description,
			IProgressMonitor monitor) throws CoreException
	{
		this.project.setDescription(description, monitor);

	}

	public void setDescription(IProjectDescription description,
			int updateFlags, IProgressMonitor monitor) throws CoreException
	{
		this.project.setDescription(description, updateFlags, monitor);

	}

	public boolean exists(IPath path)
	{
		return this.project.exists(path);
	}

	public IFile[] findDeletedMembersWithHistory(int depth,
			IProgressMonitor monitor) throws CoreException
	{
		return this.project.findDeletedMembersWithHistory(depth, monitor);
	}

	public IResource findMember(String name)
	{
		return this.project.findMember(name);
	}

	public IResource findMember(IPath path)
	{
		return this.project.findMember(path);
	}

	public IResource findMember(String name, boolean includePhantoms)
	{
		return this.project.findMember(name, includePhantoms);
	}

	public IResource findMember(IPath path, boolean includePhantoms)
	{
		return this.project.findMember(path, includePhantoms);
	}

	public String getDefaultCharset() throws CoreException
	{
		return this.project.getDefaultCharset();
	}

	public String getDefaultCharset(boolean checkImplicit) throws CoreException
	{
		return this.project.getDefaultCharset(checkImplicit);
	}

	public IFile getFile(IPath path)
	{
		return this.project.getFile(path);
	}

	public IFolder getFolder(IPath path)
	{
		return this.project.getFolder(path);
	}

	public IResource[] members() throws CoreException
	{
		return this.project.members();
	}

	public IResource[] members(boolean includePhantoms) throws CoreException
	{
		return this.project.members(includePhantoms);
	}

	public IResource[] members(int memberFlags) throws CoreException
	{
		return this.project.members(memberFlags);
	}

	public void setDefaultCharset(String charset) throws CoreException
	{
		this.project.setDefaultCharset(charset);

	}

	public void setDefaultCharset(String charset, IProgressMonitor monitor)
			throws CoreException
	{
		this.project.setDefaultCharset(charset, monitor);

	}

	public void accept(IResourceVisitor visitor) throws CoreException
	{
		this.project.accept(visitor);

	}

	public void accept(IResourceProxyVisitor visitor, int memberFlags)
			throws CoreException
	{
		this.project.accept(visitor, memberFlags);

	}

	public void accept(IResourceVisitor visitor, int depth,
			boolean includePhantoms) throws CoreException
	{
		this.project.accept(visitor, depth, includePhantoms);

	}

	public void accept(IResourceVisitor visitor, int depth, int memberFlags)
			throws CoreException
	{
		this.project.accept(visitor, depth, memberFlags);

	}

	public void clearHistory(IProgressMonitor monitor) throws CoreException
	{
		this.project.clearHistory(monitor);

	}

	public void copy(IPath destination, boolean force, IProgressMonitor monitor)
			throws CoreException
	{
		this.project.copy(destination, force, monitor);

	}

	public void copy(IPath destination, int updateFlags,
			IProgressMonitor monitor) throws CoreException
	{
		this.copy(destination, updateFlags, monitor);

	}

	public void copy(IProjectDescription description, boolean force,
			IProgressMonitor monitor) throws CoreException
	{
		this.project.copy(description, force, monitor);

	}

	public void copy(IProjectDescription description, int updateFlags,
			IProgressMonitor monitor) throws CoreException
	{
		this.project.copy(description, updateFlags, monitor);

	}

	public IMarker createMarker(String type) throws CoreException
	{
		return this.project.createMarker(type);
	}

	public IResourceProxy createProxy()
	{
		return this.project.createProxy();
	}

	public void delete(boolean force, IProgressMonitor monitor)
			throws CoreException
	{
		this.project.delete(force, monitor);

	}

	public void delete(int updateFlags, IProgressMonitor monitor)
			throws CoreException
	{
		this.project.delete(updateFlags, monitor);

	}

	public void deleteMarkers(String type, boolean includeSubtypes, int depth)
			throws CoreException
	{
		this.project.deleteMarkers(type, includeSubtypes, depth);

	}

	public boolean exists()
	{
		return this.project.exists();
	}

	public IMarker findMarker(long id) throws CoreException
	{
		return this.project.findMarker(id);
	}

	public IMarker[] findMarkers(String type, boolean includeSubtypes, int depth)
			throws CoreException
	{
		return this.project.findMarkers(type, includeSubtypes, depth);
	}

	public int findMaxProblemSeverity(String type, boolean includeSubtypes,
			int depth) throws CoreException
	{
		return this.project.findMaxProblemSeverity(type, includeSubtypes, depth);
	}

	public String getFileExtension()
	{
		return this.project.getFileExtension();
	}

	public IPath getFullPath()
	{
		return this.project.getFullPath();
	}

	public long getLocalTimeStamp()
	{
		return this.project.getLocalTimeStamp();
	}

	public IPath getLocation()
	{
		return this.project.getLocation();
	}

	public URI getLocationURI()
	{
		return this.project.getLocationURI();
	}

	public IMarker getMarker(long id)
	{
		return this.project.getMarker(id);
	}

	public long getModificationStamp()
	{
		return this.project.getModificationStamp();
	}

	public String getName()
	{
		return this.project.getName();
	}

	public IContainer getParent()
	{
		return this.project.getParent();
	}

	public Map getPersistentProperties() throws CoreException
	{
		return this.project.getPersistentProperties();
	}

	public String getPersistentProperty(QualifiedName key) throws CoreException
	{
		return this.project.getPersistentProperty(key);
	}

	public IPath getProjectRelativePath()
	{
		return this.project.getProjectRelativePath();
	}

	public IPath getRawLocation()
	{
		return this.project.getRawLocation();
	}

	public URI getRawLocationURI()
	{
		return this.project.getRawLocationURI();
	}

	public ResourceAttributes getResourceAttributes()
	{
		return this.project.getResourceAttributes();
	}

	public Map getSessionProperties() throws CoreException
	{
		return this.project.getSessionProperties();
	}

	public Object getSessionProperty(QualifiedName key) throws CoreException
	{
		return this.project.getSessionProperty(key);
	}

	public int getType()
	{
		return this.project.getType();
	}

	public IWorkspace getWorkspace()
	{
		return this.project.getWorkspace();
	}

	public boolean isAccessible()
	{
		return this.project.isAccessible();
	}

	public boolean isDerived()
	{
		return this.project.isDerived();
	}

	public boolean isDerived(int options)
	{
		return this.project.isDerived(options);
	}

	public boolean isHidden()
	{
		return this.project.isHidden();
	}

	public boolean isHidden(int options)
	{
		return this.project.isHidden(options);
	}

	public boolean isLinked()
	{
		return this.project.isLinked();
	}

	public boolean isLinked(int options)
	{
		return this.project.isLinked(options);
	}

	public boolean isLocal(int depth)
	{
		return this.project.isLocal(depth);
	}

	public boolean isPhantom()
	{
		return this.project.isPhantom();
	}

	public boolean isReadOnly()
	{
		return this.project.isReadOnly();
	}

	public boolean isSynchronized(int depth)
	{
		return this.project.isSynchronized(depth);
	}

	public boolean isTeamPrivateMember()
	{
		return this.project.isTeamPrivateMember();
	}

	public boolean isTeamPrivateMember(int options)
	{
		return this.project.isTeamPrivateMember(options);
	}

	public void move(IPath destination, boolean force, IProgressMonitor monitor)
			throws CoreException
	{
		this.project.move(destination, force, monitor);

	}

	public void move(IPath destination, int updateFlags,
			IProgressMonitor monitor) throws CoreException
	{
		this.project.move(destination, updateFlags, monitor);

	}

	public void move(IProjectDescription description, int updateFlags,
			IProgressMonitor monitor) throws CoreException
	{
		this.project.move(description, updateFlags, monitor);

	}

	public void move(IProjectDescription description, boolean force,
			boolean keepHistory, IProgressMonitor monitor) throws CoreException
	{
		this.project.move(description, force, keepHistory, monitor);

	}

	public void refreshLocal(int depth, IProgressMonitor monitor)
			throws CoreException
	{
		this.project.refreshLocal(depth, monitor);
	}

	public void revertModificationStamp(long value) throws CoreException
	{
		this.project.revertModificationStamp(value);

	}

	public void setDerived(boolean isDerived) throws CoreException
	{
		this.project.setDerived(isDerived);

	}

	public void setHidden(boolean isHidden) throws CoreException
	{
		this.project.setHidden(isHidden);

	}

	public void setLocal(boolean flag, int depth, IProgressMonitor monitor)
			throws CoreException
	{
		this.project.setLocal(flag, depth, monitor);

	}

	public long setLocalTimeStamp(long value) throws CoreException
	{
		return this.project.setLocalTimeStamp(value);
	}

	public void setPersistentProperty(QualifiedName key, String value)
			throws CoreException
	{
		this.project.setPersistentProperty(key, value);

	}

	public void setReadOnly(boolean readOnly)
	{
		this.project.setReadOnly(readOnly);

	}

	public void setResourceAttributes(ResourceAttributes attributes)
			throws CoreException
	{
		this.project.setResourceAttributes(attributes);

	}

	public void setSessionProperty(QualifiedName key, Object value)
			throws CoreException
	{
		this.project.setSessionProperty(key, value);

	}

	public void setTeamPrivateMember(boolean isTeamPrivate)
			throws CoreException
	{
		this.project.setTeamPrivateMember(isTeamPrivate);

	}

	public void touch(IProgressMonitor monitor) throws CoreException
	{
		this.project.touch(monitor);

	}

	public Object getAdapter(Class adapter)
	{
		return this.project.getAdapter(adapter);
	}

	public boolean contains(ISchedulingRule rule)
	{
		return this.project.contains(rule);
	}

	public boolean isConflicting(ISchedulingRule rule)
	{
		return this.project.isConflicting(rule);
	}

	/* Eclipse 3.6 Updates*/


//	public boolean isVirtual()
//	{
//		return project.isVirtual();
//	}
//
//	public IResourceFilterDescription createFilter(int paramInt1,
//			FileInfoMatcherDescription paramFileInfoMatcherDescription,
//			int paramInt2, IProgressMonitor paramIProgressMonitor)
//			throws CoreException
//	{
//		return project.createFilter(paramInt1, paramFileInfoMatcherDescription, paramInt2, paramIProgressMonitor);
//	}
//
//	public IResourceFilterDescription[] getFilters() throws CoreException
//	{
//		return project.getFilters();
//	}
//
//	public void loadSnapshot(int paramInt, URI paramURI,
//			IProgressMonitor paramIProgressMonitor) throws CoreException
//	{
//		project.loadSnapshot(paramInt, paramURI, paramIProgressMonitor);
//	}
//
//	public IPathVariableManager getPathVariableManager()
//	{
//		return project.getPathVariableManager();
//	}
//
//	public void saveSnapshot(int paramInt, URI paramURI,
//			IProgressMonitor paramIProgressMonitor) throws CoreException
//	{
//		project.saveSnapshot(paramInt, paramURI, paramIProgressMonitor);
//	}
//
//	public void setDerived(boolean paramBoolean,
//			IProgressMonitor paramIProgressMonitor) throws CoreException
//	{
//		project.setDerived(paramBoolean, paramIProgressMonitor);
//	}

}

package org.overture.ide.core.utility;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;



import org.eclipse.core.resources.IFile;

import org.eclipse.core.resources.IFolder;

import org.eclipse.core.resources.IMarker;

import org.eclipse.core.resources.IProject;

import org.eclipse.core.resources.IResource;

import org.eclipse.core.resources.IWorkspace;

import org.eclipse.core.resources.IWorkspaceRoot;

import org.eclipse.core.resources.ProjectScope;

import org.eclipse.core.resources.ResourcesPlugin;

import org.eclipse.core.runtime.AssertionFailedException;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.core.runtime.IPath;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.core.runtime.Path;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentTypeMatcher;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.overturetool.vdmj.Release;



public class VdmProject  implements IVdmProject, IProject
{
	public static final String BUILDER_ID = "org.eclipse.dltk.core.scriptbuilder";
	private final String LANGUAGE_VERSION_ARGUMENT_KEY = "LANGUAGE_VERSION";
	private final String DYNAMIC_TYPE_CHECKS_ARGUMENT_KEY = "DYNAMIC_TYPE_CHECKS";
	private final String INV_CHECKS_ARGUMENT_KEY = "INV_CHECKS";
	private final String POST_CHECKS_ARGUMENT_KEY = "POST_CHECKS";
	private final String PRE_CHECKS_ARGUMENT_KEY = "PRE_CHECKS";
	private final String SUPRESS_WARNINGS_ARGUMENT_KEY = "SUPPRESS_WARNINGS";

	private IProject project;

	public VdmProject(IProject project) {
		this.project = project;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.utility.IVdmProject#getProject()
	 */
	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#getProject()
	 */
	public IProject getProject()
	{
		return project;
	}

	@SuppressWarnings("unchecked")
	public static void addBuilder(IProject project, String name,
			String argumentKey, String argumentValue) throws CoreException
	{
		Vector<ICommand> buildCommands = new Vector<ICommand>();
		boolean found = false;
		IProjectDescription description = project.getDescription();
		for (ICommand command : description.getBuildSpec())
		{
			buildCommands.add(command);
			if (command.getBuilderName().equals(name))
			{
				found = true;
				if (argumentKey != null && argumentValue != null)
				{

					Map arguments = command.getArguments();
					if (arguments == null)
						arguments = new HashMap<String, String>();

					if (arguments.containsKey(argumentKey))
						arguments.remove(argumentKey);

					arguments.put(argumentKey, argumentValue);

					command.setArguments(arguments);
				}

			}
		}

		if (!found)
		{
			ICommand newCommand = description.newCommand();
			newCommand.setBuilderName(name);
			if (argumentKey != null && argumentValue != null)
			{
				Map arguments = new HashMap<String, String>();
				arguments.put(argumentKey, argumentValue);
				newCommand.setArguments(arguments);
			}

			buildCommands.add(newCommand);

		}
		ICommand[] commands = new ICommand[buildCommands.size()];
		commands = buildCommands.toArray(commands);
		description.setBuildSpec(commands);

		project.setDescription(description, null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.overture.ide.utility.IVdmProject#setBuilder(org.overturetool.vdmj
	 * .Release)
	 */
	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#setBuilder(org.overturetool.vdmj.Release)
	 */
	public void setBuilder(Release languageVersion) throws CoreException
	{
		addBuilder(getProject(),
				BUILDER_ID,
				LANGUAGE_VERSION_ARGUMENT_KEY,
				languageVersion.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.utility.IVdmProject#hasBuilder()
	 */
	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#hasBuilder()
	 */
	public boolean hasBuilder() throws CoreException
	{
		Vector<ICommand> buildCommands = new Vector<ICommand>();
		IProjectDescription description = project.getDescription();
		for (ICommand command : description.getBuildSpec())
		{
			buildCommands.add(command);
			if (command.getBuilderName().equals(BUILDER_ID))
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.overture.ide.utility.IVdmProject#getLanguageVersion()
	 */
	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#getLanguageVersion()
	 */
	public Release getLanguageVersion() throws CoreException
	{
		if (!hasBuilder())
			return Release.DEFAULT;
		else
		{
			Object languageVersion = getBuilderArguemnt(LANGUAGE_VERSION_ARGUMENT_KEY);
			if (languageVersion != null && Release.lookup(languageVersion.toString())!=null)
				return Release.lookup(languageVersion.toString());

		}
		return Release.DEFAULT;
	}
	
	public String getLanguageVersionName() throws CoreException
	{
		return getLanguageVersion().toString();
	}

	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#hasDynamictypechecks()
	 */
	public boolean hasDynamictypechecks()
	{
		return hasArgument(DYNAMIC_TYPE_CHECKS_ARGUMENT_KEY, true);
	}

	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#hasInvchecks()
	 */
	public boolean hasInvchecks()
	{
		return hasArgument(INV_CHECKS_ARGUMENT_KEY, true);
	}

	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#hasPostchecks()
	 */
	public boolean hasPostchecks()
	{
		return hasArgument(POST_CHECKS_ARGUMENT_KEY, true);
	}

	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#hasPrechecks()
	 */
	public boolean hasPrechecks()
	{
		return hasArgument(PRE_CHECKS_ARGUMENT_KEY, true);
	}
	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#hasSuppressWarnings()
	 */
	public boolean hasSuppressWarnings()
	{
		return hasArgument(SUPRESS_WARNINGS_ARGUMENT_KEY, false);
	}
	
	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#setDynamictypechecks(java.lang.Boolean)
	 */
	public void setDynamictypechecks(Boolean value) throws CoreException
	{
		addBuilder(getProject(),
				BUILDER_ID,
				DYNAMIC_TYPE_CHECKS_ARGUMENT_KEY,
				value.toString());
	}

	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#setInvchecks(java.lang.Boolean)
	 */
	public void setInvchecks(Boolean value) throws CoreException
	{
		addBuilder(getProject(),
				BUILDER_ID,
				INV_CHECKS_ARGUMENT_KEY,
				value.toString());
	}

	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#setPostchecks(java.lang.Boolean)
	 */
	public void setPostchecks(Boolean value) throws CoreException
	{
		addBuilder(getProject(),
				BUILDER_ID,
				POST_CHECKS_ARGUMENT_KEY,
				value.toString());
	}

	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#setPrechecks(java.lang.Boolean)
	 */
	public void setPrechecks(Boolean value) throws CoreException
	{
		addBuilder(getProject(),
				BUILDER_ID,
				PRE_CHECKS_ARGUMENT_KEY,
				value.toString());
	}
	/* (non-Javadoc)
	 * @see org.overture.ide.utility.IVdmProject1#setSuppressWarnings(java.lang.Boolean)
	 */
	public void setSuppressWarnings(Boolean value) throws CoreException
	{
		addBuilder(getProject(),
				BUILDER_ID,
				SUPRESS_WARNINGS_ARGUMENT_KEY,
				value.toString());
	}

	private boolean hasArgument(String argumentKey,boolean defaultValue)
	{
		Object argument;
		try
		{
			argument = getBuilderArguemnt(argumentKey);
			if (argument != null)
			{
				return Boolean.parseBoolean(argument.toString());
			}
		} catch (CoreException e)
		{
			// Could not retrieve value so return default
		}

		return defaultValue;
	}
	private Object getBuilderArguemnt(String argumentKey) throws CoreException
	{
		if (hasBuilder())
			return getBuilderArgument(project, BUILDER_ID, argumentKey);

		return null;
	}
	public static Object getBuilderArgument(IProject project,String builderId ,String argumentKey) throws CoreException
	{
		Vector<ICommand> buildCommands = new Vector<ICommand>();
		IProjectDescription description = project.getDescription();
		for (ICommand command : description.getBuildSpec())
		{
			buildCommands.add(command);
			if (command.getBuilderName().equals(builderId))
			{
				if (command.getArguments().containsKey(argumentKey))
				{
					Object argumentValue = command.getArguments()
							.get(argumentKey);
					if (argumentValue != null)
						return argumentValue;
				}
			}

		}
		return null;
	}

	/**
	 * For this marvelous project we need to: - create the default Eclipse
	 * project - add the custom project nature - create the folder structure
	 * 
	 * @param projectName
	 * @param location
	 * @param natureId
	 * @return
	 */
	public static IVdmProject createProject(String projectName, URI location,
			String nature)
	{
		Assert.isNotNull(projectName);
		Assert.isTrue(projectName.trim().length() > 0);

		IProject project = createBaseProject(projectName, location);
		try
		{
			addNature(project, nature);

			//String[] paths = { };//"parent/child1-1/child2", "parent/child1-2/child2/child3" }; //$NON-NLS-1$ //$NON-NLS-2$
			// addToProjectStructure(project, paths);
		} catch (CoreException e)
		{
			e.printStackTrace();
			project = null;
		}

		return new VdmProject(project);
	}

	/**
	 * Just do the basics: create a basic project.
	 * 
	 * @param location
	 * @param projectName
	 */
	private static IProject createBaseProject(String projectName, URI location)
	{
		// it is acceptable to use the ResourcesPlugin class
		IProject newProject = ResourcesPlugin.getWorkspace()
				.getRoot()
				.getProject(projectName);

		if (!newProject.exists())
		{
			URI projectLocation = location;
			IProjectDescription desc = newProject.getWorkspace()
					.newProjectDescription(newProject.getName());

			if (location != null
					|| ResourcesPlugin.getWorkspace()
							.getRoot()
							.getLocationURI()
							.equals(location))
			{
				projectLocation = null;
			}

			desc.setLocationURI(projectLocation);
			try
			{
				newProject.create(desc, null);
				if (!newProject.isOpen())
				{
					newProject.open(null);
				}
			} catch (CoreException e)
			{
				e.printStackTrace();
			}
		}

		return newProject;
	}

	public static void addNature(IProject project, String nature)
			throws CoreException
	{
		if (!project.hasNature(nature))
		{
			IProjectDescription description = project.getDescription();
			String[] prevNatures = description.getNatureIds();
			String[] newNatures = new String[prevNatures.length + 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
			newNatures[prevNatures.length] = nature;
			description.setNatureIds(newNatures);

			IProgressMonitor monitor = null;
			project.setDescription(description, monitor);
		}
	}
	
	
	public void typeCheck() throws CoreException
	{
		getProject().build(IncrementalProjectBuilder.FULL_BUILD , null);
	}
	
	public void typeCheck(boolean clean,IProgressMonitor monitor) throws CoreException
	{
		if(clean)
		getProject().build(IncrementalProjectBuilder.FULL_BUILD , monitor);
		else
			getProject().build(IncrementalProjectBuilder.AUTO_BUILD , monitor);
	}
	
	public static void waitForBuidCompletion()
	{
		while (true)
		{
			Job[] jobs = Job.getJobManager().find(null);
			boolean builderFound = false;
			for (Job job : jobs)
			{
				if (job.getName().contains("org.overture.ide.builders.vdmj"))
					builderFound = true;

			}
			if (!builderFound)
				return;
			else
			{
				try
				{
					Thread.sleep(200);
				} catch (InterruptedException e)
				{
				
				}
			}
		}
	}

	public String getProjectName() {
		return this.project.getName();
	}

	public void build(int kind, IProgressMonitor monitor) throws CoreException {
		this.project.build(kind, monitor);
		
	}

	public void build(int kind, String builderName, Map args,
			IProgressMonitor monitor) throws CoreException {
		this.project.build(kind, builderName, args, monitor);
		
	}

	public void close(IProgressMonitor monitor) throws CoreException {
		this.project.close(monitor);
		
	}

	public void create(IProgressMonitor monitor) throws CoreException {
		this.project.create(monitor);
		
	}

	public void create(IProjectDescription description, IProgressMonitor monitor)
			throws CoreException {
		this.project.create(description, monitor);
		
	}

	public void create(IProjectDescription description, int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		this.project.create(description, updateFlags, monitor);
		
	}

	public void delete(boolean deleteContent, boolean force,
			IProgressMonitor monitor) throws CoreException {
		this.project.delete(deleteContent, force, monitor);
		
	}

	public IContentTypeMatcher getContentTypeMatcher() throws CoreException {
		return this.project.getContentTypeMatcher();
	}

	public IProjectDescription getDescription() throws CoreException {
		return this.project.getDescription();
	}

	public IFile getFile(String name) {
		return this.project.getFile(name);
	}

	public IFolder getFolder(String name) {
		return this.project.getFolder(name);
	}

	public IProjectNature getNature(String natureId) throws CoreException {
		return this.project.getNature(natureId);
	}

	@SuppressWarnings("deprecation")
	public IPath getPluginWorkingLocation(IPluginDescriptor plugin) {
		return this.project.getPluginWorkingLocation(plugin);
	}

	public IProject[] getReferencedProjects() throws CoreException {
		return this.getReferencedProjects();
	}

	public IProject[] getReferencingProjects() {
		return this.getReferencingProjects();
	}

	public IPath getWorkingLocation(String id) {
		return this.getWorkingLocation(id);
	}

	public boolean hasNature(String natureId) throws CoreException {
		return this.project.hasNature(natureId);
	}

	public boolean isNatureEnabled(String natureId) throws CoreException {
		return this.project.isNatureEnabled(natureId);
	}

	public boolean isOpen() {
		return this.project.isOpen();
	}

	public void move(IProjectDescription description, boolean force,
			IProgressMonitor monitor) throws CoreException {
		this.project.move(description, force, monitor);
		
	}

	public void open(IProgressMonitor monitor) throws CoreException {
		this.project.open(monitor);
		
	}

	public void open(int updateFlags, IProgressMonitor monitor)
			throws CoreException {
		this.project.open(updateFlags, monitor);
		
	}

	public void setDescription(IProjectDescription description,
			IProgressMonitor monitor) throws CoreException {
		this.project.setDescription(description, monitor);
		
	}

	public void setDescription(IProjectDescription description,
			int updateFlags, IProgressMonitor monitor) throws CoreException {
		this.project.setDescription(description, updateFlags, monitor);
		
	}

	public boolean exists(IPath path) {
		return this.project.exists(path);
	}

	public IFile[] findDeletedMembersWithHistory(int depth,
			IProgressMonitor monitor) throws CoreException {
		return this.project.findDeletedMembersWithHistory(depth, monitor);
	}

	public IResource findMember(String name) {
		return this.project.findMember(name);
	}

	public IResource findMember(IPath path) {
		return this.project.findMember(path);
	}

	public IResource findMember(String name, boolean includePhantoms) {
		return this.project.findMember(name, includePhantoms);
	}

	public IResource findMember(IPath path, boolean includePhantoms) {
		return this.project.findMember(path, includePhantoms);
	}

	public String getDefaultCharset() throws CoreException {
		return this.project.getDefaultCharset();
	}

	public String getDefaultCharset(boolean checkImplicit) throws CoreException {
		return this.project.getDefaultCharset(checkImplicit);
	}

	public IFile getFile(IPath path) {
		return this.project.getFile(path);
	}

	public IFolder getFolder(IPath path) {
		return this.project.getFolder(path);
	}

	public IResource[] members() throws CoreException {
		return this.project.members();
	}

	public IResource[] members(boolean includePhantoms) throws CoreException {
		return this.project.members(includePhantoms);
	}

	public IResource[] members(int memberFlags) throws CoreException {
		return this.project.members(memberFlags);
	}

	public void setDefaultCharset(String charset) throws CoreException {
		this.project.setDefaultCharset(charset);
		
	}

	public void setDefaultCharset(String charset, IProgressMonitor monitor)
			throws CoreException {
		this.project.setDefaultCharset(charset, monitor);
		
	}

	public void accept(IResourceVisitor visitor) throws CoreException {
		this.project.accept(visitor);
		
	}

	public void accept(IResourceProxyVisitor visitor, int memberFlags)
			throws CoreException {
		this.project.accept(visitor, memberFlags);
		
	}

	public void accept(IResourceVisitor visitor, int depth,
			boolean includePhantoms) throws CoreException {
		this.project.accept(visitor, depth, includePhantoms);
		
	}

	public void accept(IResourceVisitor visitor, int depth, int memberFlags)
			throws CoreException {
		this.project.accept(visitor, depth, memberFlags);
		
	}

	public void clearHistory(IProgressMonitor monitor) throws CoreException {
		this.project.clearHistory(monitor);
		
	}

	public void copy(IPath destination, boolean force, IProgressMonitor monitor)
			throws CoreException {
		this.project.copy(destination, force, monitor);
		
	}

	public void copy(IPath destination, int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		this.copy(destination, updateFlags, monitor);
		
	}

	public void copy(IProjectDescription description, boolean force,
			IProgressMonitor monitor) throws CoreException {
		this.project.copy(description, force, monitor);
		
	}

	public void copy(IProjectDescription description, int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		this.project.copy(description, updateFlags, monitor);
		
	}

	public IMarker createMarker(String type) throws CoreException {
		return this.project.createMarker(type);
	}

	public IResourceProxy createProxy() {
		return this.project.createProxy();
	}

	public void delete(boolean force, IProgressMonitor monitor)
			throws CoreException {
		this.project.delete(force, monitor);
		
	}

	public void delete(int updateFlags, IProgressMonitor monitor)
			throws CoreException {
		this.project.delete(updateFlags, monitor);
		
	}

	public void deleteMarkers(String type, boolean includeSubtypes, int depth)
			throws CoreException {
		this.project.deleteMarkers(type, includeSubtypes, depth);
		
	}

	public boolean exists() {
		return this.project.exists();
	}

	public IMarker findMarker(long id) throws CoreException {
		return this.project.findMarker(id);
	}

	public IMarker[] findMarkers(String type, boolean includeSubtypes, int depth)
			throws CoreException {
		return this.project.findMarkers(type, includeSubtypes, depth);
	}

	public int findMaxProblemSeverity(String type, boolean includeSubtypes,
			int depth) throws CoreException {
		return this.project.findMaxProblemSeverity(type, includeSubtypes, depth);
	}

	public String getFileExtension() {
		return this.project.getFileExtension();
	}

	public IPath getFullPath() {
		return this.project.getFullPath();
	}

	public long getLocalTimeStamp() {
		return this.project.getLocalTimeStamp();
	}

	public IPath getLocation() {
		return this.project.getLocation();
	}

	public URI getLocationURI() {
		return this.project.getLocationURI();
	}

	public IMarker getMarker(long id) {
		return this.project.getMarker(id);
	}

	public long getModificationStamp() {
		return this.project.getModificationStamp();
	}

	public String getName() {
		return this.project.getName();
	}

	public IContainer getParent() {
		return this.project.getParent();
	}

	public Map getPersistentProperties() throws CoreException {
		return this.project.getPersistentProperties();
	}

	public String getPersistentProperty(QualifiedName key) throws CoreException {
		return this.project.getPersistentProperty(key);
	}

	public IPath getProjectRelativePath() {
		return this.project.getProjectRelativePath();
	}

	public IPath getRawLocation() {
		return this.project.getRawLocation();
	}

	public URI getRawLocationURI() {
		return this.project.getRawLocationURI();
	}

	public ResourceAttributes getResourceAttributes() {
		return this.project.getResourceAttributes();
	}

	public Map getSessionProperties() throws CoreException {
		return this.project.getSessionProperties();
	}

	public Object getSessionProperty(QualifiedName key) throws CoreException {
		return this.project.getSessionProperty(key);
	}

	public int getType() {
		return this.project.getType();
	}

	public IWorkspace getWorkspace() {
		return this.project.getWorkspace();
	}

	public boolean isAccessible() {
		return this.project.isAccessible();
	}

	public boolean isDerived() {
		return this.project.isDerived();
	}

	public boolean isDerived(int options) {
		return this.project.isDerived(options);
	}

	public boolean isHidden() {
		return this.project.isHidden();
	}

	public boolean isHidden(int options) {
		return this.project.isHidden(options);
	}

	public boolean isLinked() {
		return this.project.isLinked();
	}

	public boolean isLinked(int options) {
		return this.project.isLinked(options);
	}

	public boolean isLocal(int depth) {
		return this.project.isLocal(depth);
	}

	public boolean isPhantom() {
		return this.project.isPhantom();
	}

	public boolean isReadOnly() {
		return this.project.isReadOnly();
	}

	public boolean isSynchronized(int depth) {
		return this.project.isSynchronized(depth);
	}

	public boolean isTeamPrivateMember() {
		return this.project.isTeamPrivateMember();
	}

	public boolean isTeamPrivateMember(int options) {
		return this.project.isTeamPrivateMember(options);
	}

	public void move(IPath destination, boolean force, IProgressMonitor monitor)
			throws CoreException {
		this.project.move(destination, force, monitor);
		
	}

	public void move(IPath destination, int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		this.project.move(destination, updateFlags, monitor);
		
	}

	public void move(IProjectDescription description, int updateFlags,
			IProgressMonitor monitor) throws CoreException {
		this.project.move(description, updateFlags, monitor);
		
	}

	public void move(IProjectDescription description, boolean force,
			boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		this.project.move(description, force, keepHistory, monitor);
		
	}

	public void refreshLocal(int depth, IProgressMonitor monitor)
			throws CoreException {
		this.project.refreshLocal(depth, monitor);
	}

	public void revertModificationStamp(long value) throws CoreException {
		this.project.revertModificationStamp(value);
		
	}

	public void setDerived(boolean isDerived) throws CoreException {
		this.project.setDerived(isDerived);
		
	}

	public void setHidden(boolean isHidden) throws CoreException {
		this.project.setHidden(isHidden);
		
	}

	public void setLocal(boolean flag, int depth, IProgressMonitor monitor)
			throws CoreException {
		this.project.setLocal(flag, depth, monitor);
		
	}

	public long setLocalTimeStamp(long value) throws CoreException {
		return this.project.setLocalTimeStamp(value);
	}

	public void setPersistentProperty(QualifiedName key, String value)
			throws CoreException {
		this.project.setPersistentProperty(key, value);
		
	}

	public void setReadOnly(boolean readOnly) {
		this.project.setReadOnly(readOnly);
		
	}

	public void setResourceAttributes(ResourceAttributes attributes)
			throws CoreException {
		this.project.setResourceAttributes(attributes);
		
	}

	public void setSessionProperty(QualifiedName key, Object value)
			throws CoreException {
		this.project.setSessionProperty(key, value);
		
	}

	public void setTeamPrivateMember(boolean isTeamPrivate)
			throws CoreException {
		this.project.setTeamPrivateMember(isTeamPrivate);
		
	}

	public void touch(IProgressMonitor monitor) throws CoreException {
		this.project.touch(monitor);
		
	}

	public Object getAdapter(Class adapter) {
		return this.project.getAdapter(adapter);
	}

	public boolean contains(ISchedulingRule rule) {
		return this.project.contains(rule);
	}

	public boolean isConflicting(ISchedulingRule rule) {
		return this.project.isConflicting(rule);
	}

}

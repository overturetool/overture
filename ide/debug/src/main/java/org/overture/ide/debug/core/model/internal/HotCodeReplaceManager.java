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
//package org.overture.ide.debug.core.model.internal;
//
//import java.util.ArrayList;
//
//import org.eclipse.core.resources.IResource;
//import org.eclipse.core.resources.IResourceChangeEvent;
//import org.eclipse.core.resources.IResourceChangeListener;
//import org.eclipse.core.resources.IResourceDelta;
//import org.eclipse.core.resources.IResourceDeltaVisitor;
//import org.eclipse.core.resources.ResourcesPlugin;
//import org.eclipse.core.runtime.CoreException;
//import org.eclipse.core.runtime.IAdaptable;
//import org.eclipse.core.runtime.IStatus;
//import org.eclipse.core.runtime.ListenerList;
//import org.eclipse.core.runtime.Status;
//import org.eclipse.debug.core.DebugEvent;
//import org.eclipse.debug.core.DebugException;
//import org.eclipse.debug.core.DebugPlugin;
//import org.eclipse.debug.core.IDebugEventSetListener;
//import org.eclipse.debug.core.ILaunch;
//import org.eclipse.debug.core.ILaunchListener;
//import org.eclipse.debug.core.model.IDebugTarget;
//import org.eclipse.dltk.core.IDLTKLanguageToolkit;
//import org.eclipse.dltk.core.PriorityClassDLTKExtensionManager;
//import org.eclipse.dltk.debug.core.DLTKDebugPlugin;
//import org.eclipse.dltk.debug.core.IHotCodeReplaceListener;
//import org.eclipse.dltk.debug.core.IHotCodeReplaceProvider;
//import org.eclipse.dltk.debug.core.model.IScriptDebugTarget;
//import org.eclipse.osgi.util.NLS;
//
//public class HotCodeReplaceManager implements IResourceChangeListener,
//		ILaunchListener, IDebugEventSetListener {
//
//	private static final String HOT_CODE_REPLACE_PROVIDER_EXTENSION = DLTKDebugPlugin.PLUGIN_ID
//			+ ".hotCodeReplaceProvider"; //$NON-NLS-1$
//	private static PriorityClassDLTKExtensionManager providerManager = new PriorityClassDLTKExtensionManager(
//			HOT_CODE_REPLACE_PROVIDER_EXTENSION, "nature"); //$NON-NLS-1$
//
//	private static HotCodeReplaceManager instance = null;
//
//	private ArrayList fHotSwapTargets = new ArrayList();
//	private ArrayList fNoHotSwapTargets = new ArrayList();
//	private ListenerList fHotCodeReplaceListeners = new ListenerList();
//
//	public static synchronized HotCodeReplaceManager getDefault() {
//		if (instance == null) {
//			instance = new HotCodeReplaceManager();
//		}
//		return instance;
//	}
//
//	private HotCodeReplaceManager() {
//	}
//
//	public void startup() {
//		DebugPlugin.getDefault().getLaunchManager().addLaunchListener(this);
//		DebugPlugin.getDefault().addDebugEventListener(this);
//		ResourcesPlugin.getWorkspace().addResourceChangeListener(this,
//				IResourceChangeEvent.POST_BUILD);
//	}
//
//	public void shutdown() {
//		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
//		DebugPlugin.getDefault().removeDebugEventListener(this);
//		DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(this);
//	}
//
//	/**
//	 * Adds the given listener to the collection of hot code replace listeners.
//	 * Listeners are notified when hot code replace attempts succeed or fail.
//	 */
//	public void addHotCodeReplaceListener(IHotCodeReplaceListener listener) {
//		fHotCodeReplaceListeners.add(listener);
//	}
//
//	/**
//	 * Removes the given listener from the collection of hot code replace
//	 * listeners. Once a listener is removed, it will no longer be notified of
//	 * hot code replace attempt successes or failures.
//	 */
//	public void removeHotCodeReplaceListener(IHotCodeReplaceListener listener) {
//		fHotCodeReplaceListeners.remove(listener);
//	}
//
//	/**
//	 * Notifies listeners that a hot code replace attempt succeeded
//	 */
//	private void fireHCRSucceeded(IScriptDebugTarget target) {
//		Object[] listeners = fHotCodeReplaceListeners.getListeners();
//		for (int i = 0; i < listeners.length; i++) {
//			((IHotCodeReplaceListener) listeners[i])
//					.hotCodeReplaceSucceeded(target);
//		}
//	}
//
//	/**
//	 * Notifies listeners that a hot code replace attempt failed with the given
//	 * exception
//	 */
//	private void fireHCRFailed(IScriptDebugTarget target,
//			DebugException exception) {
//		Object[] listeners = fHotCodeReplaceListeners.getListeners();
//		for (int i = 0; i < listeners.length; i++) {
//			((IHotCodeReplaceListener) listeners[i]).hotCodeReplaceFailed(
//					target, exception);
//		}
//	}
//
//	public void launchAdded(ILaunch launch) {
//		IDebugTarget[] debugTargets = launch.getDebugTargets();
//		for (int i = 0; i < debugTargets.length; i++) {
//			IScriptDebugTarget target = (IScriptDebugTarget) debugTargets[i]
//					.getAdapter(IScriptDebugTarget.class);
//			if (target != null) {
//				if (supportsHotCodeReplace(target)) {
//					addHotSwapTarget(target);
//				} else {
//					addNonHotSwapTarget(target);
//				}
//			}
//		}
//	}
//
//	public boolean supportsHotCodeReplace(IScriptDebugTarget target) {
//		final IDLTKLanguageToolkit toolkit = target.getLanguageToolkit();
//		return toolkit != null
//				&& getHotCodeReplaceProvider(toolkit.getNatureId()) != null;
//	}
//
//	public void launchChanged(ILaunch launch) {
//		launchAdded(launch);
//	}
//
//	public void launchRemoved(ILaunch launch) {
//		IDebugTarget[] debugTargets = launch.getDebugTargets();
//		for (int i = 0; i < debugTargets.length; i++) {
//			IScriptDebugTarget target = (IScriptDebugTarget) debugTargets[i]
//					.getAdapter(IScriptDebugTarget.class);
//			if (target != null) {
//				deregisterTarget(target);
//			}
//		}
//	}
//
//	public void handleDebugEvents(DebugEvent[] events) {
//		for (int i = 0; i < events.length; i++) {
//			DebugEvent event = events[i];
//			if (event.getKind() == DebugEvent.TERMINATE) {
//				Object source = event.getSource();
//				if (source instanceof IAdaptable
//						&& source instanceof IDebugTarget) {
//					IScriptDebugTarget target = (IScriptDebugTarget) ((IAdaptable) source)
//							.getAdapter(IScriptDebugTarget.class);
//					if (target != null) {
//						deregisterTarget(target);
//					}
//				}
//			}
//		}
//	}
//
//	private void addHotSwapTarget(IScriptDebugTarget target) {
//		if (!fHotSwapTargets.contains(target)) {
//			fHotSwapTargets.add(target);
//		}
//	}
//
//	private void addNonHotSwapTarget(IScriptDebugTarget target) {
//		if (!fNoHotSwapTargets.contains(target)) {
//			fNoHotSwapTargets.add(target);
//		}
//	}
//
//	private void deregisterTarget(IScriptDebugTarget target) {
//		if (!fHotSwapTargets.remove(target)) {
//			fNoHotSwapTargets.remove(target);
//		}
//	}
//
//	/**
//	 * Returns the currently registered debug targets that support hot code
//	 * replace.
//	 */
//	private IScriptDebugTarget[] getHotSwapTargets() {
//		return (IScriptDebugTarget[]) fHotSwapTargets
//				.toArray(new IScriptDebugTarget[fHotSwapTargets.size()]);
//	}
//
//	/**
//	 * Returns the currently registered debug targets that do not support hot
//	 * code replace.
//	 */
//	private IScriptDebugTarget[] getNoHotSwapTargets() {
//		return (IScriptDebugTarget[]) fNoHotSwapTargets
//				.toArray(new IScriptDebugTarget[fNoHotSwapTargets.size()]);
//	}
//
//	public void resourceChanged(IResourceChangeEvent event) {
//		if (fHotSwapTargets.isEmpty() && fNoHotSwapTargets.isEmpty()) {
//			// If there are no targets to notify, only update the build times.
//			return;
//		}
//
//		IResource[] resources = getChangedFiles(event);
//		if (resources.length != 0) {
//			notifyTargets(resources);
//		}
//	}
//
//	private void notifyTargets(final IResource[] resources) {
//		final IScriptDebugTarget[] hotSwapTargets = getHotSwapTargets();
//		final IScriptDebugTarget[] noHotSwapTargets = getNoHotSwapTargets();
//		if (hotSwapTargets.length != 0) {
//			Runnable runnable = new Runnable() {
//				public void run() {
//					doHotCodeReplace(hotSwapTargets, resources);
//				}
//			};
//			DebugPlugin.getDefault().asyncExec(runnable);
//		}
//		if (noHotSwapTargets.length != 0) {
//			Runnable runnable = new Runnable() {
//				public void run() {
//					notifyUnsupportedHCR(noHotSwapTargets, resources);
//				}
//			};
//			DebugPlugin.getDefault().asyncExec(runnable);
//		}
//	}
//
//	protected void notifyUnsupportedHCR(IScriptDebugTarget[] noHotSwapTargets,
//			IResource[] resources) {
//		for (int i = 0; i < noHotSwapTargets.length; i++) {
//			IScriptDebugTarget target = noHotSwapTargets[i];
//			fireHCRFailed(target, null);
//		}
//	}
//
//	protected void doHotCodeReplace(IScriptDebugTarget[] hotSwapTargets,
//			IResource[] resources) {
//		for (int i = 0; i < hotSwapTargets.length; i++) {
//			IScriptDebugTarget target = hotSwapTargets[i];
//			String natureId = target.getLanguageToolkit().getNatureId();
//			IHotCodeReplaceProvider provider = getHotCodeReplaceProvider(natureId);
//			try {
//				if (provider != null) {
//					provider.performCodeReplace(target, resources);
//					fireHCRSucceeded(target);
//				} else {
//					fail(NLS
//							.bind(
//									Messages.HotCodeReplaceManager_hotCodeReplaceProviderForNotFound,
//									natureId));
//				}
//			} catch (DebugException e) {
//				fireHCRFailed(target, e);
//			}
//		}
//	}
//
//	private void fail(String message) throws DebugException {
//		fail(message, null);
//	}
//
//	private void fail(String message, Throwable e) throws DebugException {
//		throw new DebugException(new Status(IStatus.ERROR,
//				DLTKDebugPlugin.PLUGIN_ID, DebugPlugin.INTERNAL_ERROR, message,
//				e));
//	}
//
//	private IHotCodeReplaceProvider getHotCodeReplaceProvider(String natureId) {
//		return (IHotCodeReplaceProvider) providerManager.getObject(natureId);
//	}
//
//	/**
//	 * Returns the changed class files. Returns <code>null</code> if the visitor
//	 * encounters an exception, or the delta is not a POST_BUILD.
//	 */
//	private IResource[] getChangedFiles(IResourceChangeEvent event) {
//		IResourceDelta delta = event.getDelta();
//		if (event.getType() != IResourceChangeEvent.POST_BUILD || delta == null) {
//			return null;
//		}
//
//		ChangedFilesVisitor changedFilesVisitor = new ChangedFilesVisitor();
//		try {
//			delta.accept(changedFilesVisitor);
//		} catch (CoreException e) {
//			DLTKDebugPlugin.log(e);
//			return new IResource[0]; // quiet failure
//		}
//		return changedFilesVisitor.getChangedFiles();
//	}
//
//	/**
//	 * A visitor which collects changed class files.
//	 */
//	class ChangedFilesVisitor implements IResourceDeltaVisitor {
//		/**
//		 * The collection of changed class files.
//		 */
//		protected ArrayList fFiles = new ArrayList();
//
//		/**
//		 * Answers whether children should be visited.
//		 * <p>
//		 * If the associated resource is a file which has been changed, record
//		 * it.
//		 */
//		public boolean visit(IResourceDelta delta) {
//			if (delta == null
//					|| 0 == (delta.getKind() & IResourceDelta.CHANGED)) {
//				return false;
//			}
//			IResource resource = delta.getResource();
//			if (resource != null) {
//				switch (resource.getType()) {
//				case IResource.FILE:
//					if (0 == (delta.getFlags() & IResourceDelta.CONTENT))
//						return false;
//					fFiles.add(resource);
//					return false;
//
//				default:
//					return true;
//				}
//			}
//			return true;
//		}
//
//		/**
//		 * Answers a collection of changed files
//		 */
//		public IResource[] getChangedFiles() {
//			return (IResource[]) fFiles.toArray(new IResource[fFiles.size()]);
//		}
//	}
//}

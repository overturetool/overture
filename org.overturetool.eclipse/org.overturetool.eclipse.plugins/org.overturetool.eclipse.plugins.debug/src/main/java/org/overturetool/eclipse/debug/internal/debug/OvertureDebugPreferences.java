package org.overturetool.eclipse.debug.internal.debug;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class OvertureDebugPreferences{

	private static Preferences getNode() {
		String id = DebugPlugin.getDefault().getBundle().getSymbolicName();
		return Platform.getPreferencesService().getRootNode().node(
				InstanceScope.SCOPE).node(id);
	}

	public static void save() {
		try {
			getNode().flush();
		} catch (BackingStoreException e) {
			// TODO: add logging
		}
	}

	public static String getDebuggingEnginePath() {
		return getNode().get(OvertureDebugConstants.DEBUGGING_ENGINE_PATH,
				OvertureDebugConstants.DEBUGGING_ENGINE_PATH_DEFAULT);
	}

	public static void setDebuggingEnginePath(String path) {
		getNode().put(OvertureDebugConstants.DEBUGGING_ENGINE_PATH, path);
	}
}
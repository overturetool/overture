package org.overturetool.proofsupport;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

public abstract class AbstractSettings {

	protected HashMap<String, String> values = new HashMap<String, String>();

	public AbstractSettings(String settingsFile) throws FileNotFoundException,
			IOException, InvalidPreferencesFormatException,
			BackingStoreException {
		loadPreferences(settingsFile, this.getClass());
	}


	@SuppressWarnings("unchecked")
	private void loadPreferences(String settingsFile, Class c)
			throws IOException, InvalidPreferencesFormatException,
			FileNotFoundException, BackingStoreException {
//		Preferences.importPreferences(new BufferedInputStream(
//				new FileInputStream(settingsFile)));
		ClassLoader cl = this.getClass().getClassLoader();
		Preferences.importPreferences(new BufferedInputStream(
				cl.getResourceAsStream(settingsFile)));
		Preferences preferences = Preferences.userNodeForPackage(c);
		String[] keys = preferences.keys();
		for (String key : keys)
			values.put(key, preferences.get(key, ""));
	}

	public String get(String key) {
		return values.get(key);
	}
}

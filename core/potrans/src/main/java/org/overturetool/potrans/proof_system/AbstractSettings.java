package org.overturetool.potrans.proof_system;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
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

	public AbstractSettings(String settingsFile, Class c)
			throws FileNotFoundException, IOException,
			InvalidPreferencesFormatException, BackingStoreException {
		loadPreferences(settingsFile, c);
	}

	private void loadPreferences(String settingsFile, Class c)
			throws IOException, InvalidPreferencesFormatException,
			FileNotFoundException, BackingStoreException {
		Preferences.importPreferences(new BufferedInputStream(
				new FileInputStream(settingsFile)));
		Preferences preferences = Preferences.userNodeForPackage(c);
		String[] keys = preferences.keys();
		for (String key : keys)
			values.put(key, preferences.get(key, ""));
	}

	public String get(String key) {
		return values.get(key);
	}
}

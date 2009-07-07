package org.overturetool.proofsupport;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;

public class ApplicationSettings extends AbstractSettings {

	protected static final String OS_NAME = System.getProperty("os.name");
	protected static final String SETTINGS_FILE = "Settings.xml";

	public static final String VDM_HOL_TACTICS = "vdmHolTactics";

	public ApplicationSettings() throws FileNotFoundException, IOException, InvalidPreferencesFormatException,
			BackingStoreException {
		super(SETTINGS_FILE);
	}
	
	public static boolean isOsWindows() {
		return OS_NAME.contains("Windows");
	}
}

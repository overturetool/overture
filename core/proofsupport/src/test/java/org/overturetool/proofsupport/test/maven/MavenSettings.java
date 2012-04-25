package org.overturetool.proofsupport.test.maven;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.xml.sax.SAXException;

public class MavenSettings
{
	public static Boolean isWindows()
	{
		String osName = System.getProperty("os.name");

		return osName.toUpperCase().indexOf("Windows".toUpperCase()) > -1;
	}

	private String settingsFilePath;
	private Profile profile;

	public Profile getDefaultProfile()
	{
		return profile;
	}

	public MavenSettings() throws SAXException, IOException
	{
		String settingsLocation = "";
		if (isWindows())
		{
			String homeDrive = System.getenv("HOMEDRIVE");
			String usersHome = System.getenv("HOMEPATH");
			String m2SettingsFolderFile = "\\.m2\\settings.xml";
			settingsLocation = homeDrive + usersHome + m2SettingsFolderFile;

		} else
		{
			String home = System.getenv("HOME");
			String m2SettingsFolderFile = "/.m2/settings.xml";
			settingsLocation = home + m2SettingsFolderFile;
		}
		settingsFilePath = settingsLocation;

		File f = new File(settingsFilePath);

		if (!f.exists())
			throw new FileNotFoundException(f.getName());

		SettingsParser sp = new SettingsParser();

		sp.parse(settingsFilePath);

		this.profile = sp.getProfiles().lastElement();
	}
}

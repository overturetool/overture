package org.overture.maven;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.xml.sax.SAXException;

public class MavenSettings
{
	public static Boolean IsWindows()
	{
		String osName = System.getProperty("os.name");

		return osName.toUpperCase().indexOf("Windows".toUpperCase()) > -1;
	}

	private String settingsFilePath;
	private Profile profile;
	private File mavenRepo;

	public Profile getDefaultProfile()
	{
		return profile;
	}

	public MavenSettings() throws SAXException, IOException
	{
		String settingsLocation = "";
		System.out.println("Testing path to Maven 2 user settings file");
		if (IsWindows())
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
		
		setMavenRepo(new File(new File(settingsLocation).getParentFile(),"repository"));

		SettingsParser sp = new SettingsParser();

		sp.Parse(settingsFilePath);

		this.profile = sp.GetProfiles().lastElement();
	}

	private void setMavenRepo(File mavenRepo)
	{
		this.mavenRepo = mavenRepo;
	}

	public File getMavenRepo()
	{
		return mavenRepo;
	}
}

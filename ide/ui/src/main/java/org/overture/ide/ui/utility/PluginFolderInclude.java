package org.overture.ide.ui.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;


@SuppressWarnings("restriction")
public class PluginFolderInclude
{
	/**
	 * Reads a file included in a plugin in Eclipse. The relative path is from the root of the plugin. e.g. if the included file is in /latex/file1.txt this is then the relative path but without the first slash
	 * @param pluginId The plugin in to read the file from
	 * @param relativePath The relative plath to the file in the plugin
	 * @return A string containing the content for the file
	 * @throws IOException
	 */
	public static String readFile(final String pluginId, String relativePath)
			throws IOException
	{
		URL tmp = getResource(pluginId, relativePath);

		InputStreamReader reader = new InputStreamReader(tmp.openStream());
		// Create Buffered/PrintWriter Objects
		// BufferedReader inputStream = new BufferedReader(bin);
		StringBuilder sb = new StringBuilder();

		int inLine;
		while ((inLine = reader.read()) != -1)
		{
			sb.append((char) inLine);
		}
		return sb.toString();
	}

	public static void writeFile(File outputFolder, String fileName, String content)
			throws IOException
	{
		FileWriter outputFileReader = new FileWriter(new File(outputFolder,
				fileName));
		BufferedWriter outputStream = new BufferedWriter(outputFileReader);
		outputStream.write(content);
		outputStream.close();
	}

	public static URL getResource(String pluginId, String path)
	{
		// if the bundle is not ready then there is no image
		Bundle bundle = Platform.getBundle(pluginId);
		if (!BundleUtility.isReady(bundle))
		{
			return null;
		}

		// look for the image (this will check both the plugin and fragment
		// folders
		URL fullPathString = BundleUtility.find(bundle, path);
		if (fullPathString == null)
		{
			try
			{
				fullPathString = new URL(path);
			} catch (MalformedURLException e)
			{
				return null;
			}
		}

		if (fullPathString == null)
		{
			return null;
		}
		return fullPathString;

	}
}

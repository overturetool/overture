/*
 * Copyright (C) 2006 Princeton Softech, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.pst.plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 * <ul>
 * <li>Title: ManifestGenerator</li>
 * <li>Description: The class <code>ManifestGenerator</code> is a Mojo helper
 * that generates an Eclipse plugin manifest, and deploys all dependent
 * artifacts, for a binary plugin.</li>
 * <li>Created: Aug 31, 2006 by: prippete01</li>
 * </ul>
 * 
 * @author $Author: prippete01 $
 * @version $Revision: 1.7 $
 */
public class ManifestGenerator extends AbstractMojoHelper implements
		ManifestConstants {
	/**
	 * Legal copyright notice.
	 */
	public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved.";

	/**
	 * SCCS header.
	 */
	public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/main/java/com/princetonsoftech/maven/psteclipse/ManifestGenerator.java,v 1.7 2007/02/08 22:02:30 prippete01 Exp $";

	/**
	 * The project.
	 */
	protected MavenProject project;

	/**
	 * The buddies.
	 */
	private ArrayList buddies;

	/**
	 * The destination directory.
	 */
	protected File destinationDirectory;

	/**
	 * The baseDir/lib directory where downloaded jars will live
	 */
	private File libDirectory;

	/**
	 * The packages that should not be exported in the manifest.
	 */
	private List doNotExportPackagePrefixes;
	/**
	 * The packages that should not be exported in the manifest.
	 */
	private List importInsteadOfExportPackagePrefixes;

	/**
	 * Constructs a new <code>ManifestGeneratorHelper</code> instance.
	 * 
	 * @param log
	 * @param baseDirectory
	 * @param project
	 * @param buddies
	 * @param destinationDirectory
	 */
	public ManifestGenerator(Log log, File baseDirectory, MavenProject project,
			ArrayList buddies, File destinationDirectory,
			List doNotExportPackagePrefixes,
			List importInsteadOfExportPackagePrefixes) {
		super(log, baseDirectory);
		this.project = project;
		this.buddies = buddies;
		this.destinationDirectory = destinationDirectory;
		this.doNotExportPackagePrefixes = doNotExportPackagePrefixes;
		this.importInsteadOfExportPackagePrefixes = importInsteadOfExportPackagePrefixes;
		
		//we add import instead of export to do not export since we do not want them to be exported 
		if(importInsteadOfExportPackagePrefixes!=null )
		{
			if(doNotExportPackagePrefixes==null)
				doNotExportPackagePrefixes=new Vector();
			doNotExportPackagePrefixes.addAll(importInsteadOfExportPackagePrefixes);
		}
	}

	/**
	 * Constructs a new <code>ManifestGeneratorHelper</code> instance.
	 * 
	 * @param log
	 * @param baseDirectory
	 * @param project
	 * @param buddies
	 * @param destinationDirectory
	 */
	public ManifestGenerator(Log log, File baseDirectory, MavenProject project,
			ArrayList buddies, File destinationDirectory) {
		super(log, baseDirectory);
		this.project = project;
		this.buddies = buddies;
		this.destinationDirectory = destinationDirectory;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.codehaus.pst.plugin.AbstractMojoHelper#doExecute()
	 */
	protected void doExecute() throws MojoExecutionException,
			MojoFailureException {
		if (project.getPackaging().equals(
				EclipseConstants.PACKING_SOURCE_PLUGIN)
				|| project.getPackaging().equals(
						EclipseConstants.PACKING_BINARY_PLUGIN))

		{
			File manifestDirectory = new File(destinationDirectory, MANIFEST_DIRECTORY);
			getLog().debug("The manifestDir is " + manifestDirectory);
			if (!manifestDirectory.exists()) {
				if (!manifestDirectory.mkdir()) {
					throw new MojoExecutionException("Unable to create directory '"
							+ manifestDirectory + "'");
				}
			}
			libDirectory = new File(destinationDirectory, LIB_DIRECTORY);
			getLog().debug("The libDir is " + libDirectory);
			if (!libDirectory.exists()
					&& project.getPackaging().equals(
							EclipseConstants.PACKING_BINARY_PLUGIN)) {
				if (!libDirectory.mkdir()) {
					throw new MojoExecutionException("Unable to create directory '"
							+ libDirectory + "'");
				}
			}

			createPluginPropetiesFile();
			createBuildPropertiesFile();

			File manifestFile = getManifestFile(manifestDirectory);
			if (manifestFile != null) {
				Manifest manifest = new Manifest();
				Attributes mainAttributes = manifest.getMainAttributes();
				writeInitialManifestAttributes(mainAttributes);
				resolveBundleClasspathEntries(mainAttributes);
				writeManifestToFile(manifestFile, manifest);
			}
		}
	}

	private void createBuildPropertiesFile() throws MojoExecutionException {
		File buildPropetiesFile = new File(destinationDirectory, EclipseConstants.BUILD_PROPERTIES);

		if (buildPropetiesFile.exists()) {
			if (project.getPackaging().equals(
					EclipseConstants.PACKING_SOURCE_PLUGIN))
				return;
			else
				buildPropetiesFile.delete();
		}

		if (!buildPropetiesFile.exists()) {
			PrintWriter out = null;

			try {
				// buildPropetiesFile.createNewFile();

				FileWriter outputFileReader;

				outputFileReader = new FileWriter(buildPropetiesFile);

				BufferedWriter outputStream = new BufferedWriter(outputFileReader);

				out = new PrintWriter(outputStream);

				String[] tmp = EclipseConstants.BUILD_PROPERTIES_CONTENT.replace(
						'\n', '#').split("#");

				for (int i = 0; i < tmp.length; i++) {
					// getLog().info(tmp[i]);
					out.println(tmp[i]);
				}

			} catch (IOException e) {
				throw new MojoExecutionException("Could not create "
						+ EclipseConstants.BUILD_PROPERTIES + " in "
						+ buildPropetiesFile.getAbsolutePath());
			} finally {
				if (out != null)
					out.close();
			}
		}

	}

	private void createPluginPropetiesFile() throws MojoExecutionException {
		File pluginPropetiesFile = new File(destinationDirectory, EclipseConstants.PLUGIN_PROPERTIES);

		if (!pluginPropetiesFile.exists())
			try {
				pluginPropetiesFile.createNewFile();
			} catch (IOException e) {
				throw new MojoExecutionException("Could not create "
						+ EclipseConstants.PLUGIN_PROPERTIES + " in "
						+ pluginPropetiesFile.getAbsolutePath());
			}

	}

	/**
	 * Returns the manifest file.
	 * 
	 * @param manifestDirectory
	 *            the manifest directory.
	 * @return the manifest file.
	 * @throws MojoExecutionException
	 */
	private File getManifestFile(File manifestDirectory)
			throws MojoExecutionException {
		File manifestFile = new File(manifestDirectory, MANIFEST_FILE_NAME);
		if (!manifestFile.exists()) {
			try {
				manifestFile.createNewFile();
			} catch (IOException e) {
				throw new MojoExecutionException("Could not create Manifest File", e);
			}
		} else {
			if (project.getPackaging().equals(
					EclipseConstants.PACKING_BINARY_PLUGIN))
				getLog().warn("PST Mojo Overwriting existing Manifest File");
			else {
				getLog().warn("PST Mojo Skipping existing Manifest File");
				return null;
			}
		}
		return manifestFile;
	}

	/**
	 * Writes the manifest to file.
	 * 
	 * @param manifestFile
	 *            the manifest file.
	 * @param manifest
	 *            the manifest.
	 * @throws MojoExecutionException
	 */
	private void writeManifestToFile(File manifestFile, Manifest manifest)
			throws MojoExecutionException {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(manifestFile, false);

			// manifest.write(fileOutputStream);

			FileOutputWriterOnCrLf d = new FileOutputWriterOnCrLf();
			manifest.write(d);
			fileOutputStream.write(d.getBytes());

			// getLog().info("Manifest:");
			// manifest.write(System.out);
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (IOException e) {

			throw new MojoExecutionException("Could not write out Manifest File");
		}
	}

	/***
	 * Class implemented to revoce CrLf from manifest file on windows
	 * 
	 * @author kela
	 * 
	 */
	private class FileOutputWriterOnCrLf extends OutputStream {
		List bytes = new ArrayList();

		public void write(int b) throws IOException {
			bytes.add(new Integer(b));

		}

		private void removeCrAndLfLf() {
			Object a[] = bytes.toArray();
			for (int i = 0; i < a.length; i++) {
				byte thisByte = ((Integer) a[i]).byteValue();
				if (i + 1 < a.length) {
					byte nextByte = ((Integer) a[i + 1]).byteValue();
					if ((thisByte == 0x0d || thisByte == 0x0a)
							&& nextByte == 0x0a) {
						bytes.remove(i);// remove 0x0d
						// getLog().info("Removing: "+i + "("+thisByte+")");
						removeCrAndLfLf();
						return;
					}
				}
			}
		}

		public byte[] getBytes() {

			removeCrAndLfLf();

			byte[] arr = new byte[bytes.size()];

			Object a[] = bytes.toArray();
			for (int i = 0; i < a.length; i++) {
				byte thisByte = ((Integer) a[i]).byteValue();
				arr[i] = thisByte;
			}
			return arr;
		}

	}

	/**
	 * Resolves the bundle classpath entries.
	 * 
	 * @param mainAttributes
	 *            the main attributes.
	 * @throws MojoExecutionException
	 */
	private void resolveBundleClasspathEntries(Attributes mainAttributes)
			throws MojoExecutionException {
		Iterator dependecies = project.getCompileArtifacts().iterator();
		StringBuffer classpath = new StringBuffer();
		StringBuffer exportedPackages = new StringBuffer();
		StringBuffer importedPackages = new StringBuffer();
		while (dependecies.hasNext()) {
			Artifact artifact = (Artifact) dependecies.next();

			if (artifact.getArtifactId().startsWith(
					EclipseConstants.ORG_ECLIPSE_SWT_FILE_PREFIX)) {
				getLog().debug(
						"Skipping ArtifactId: " + artifact.getArtifactId());
				continue;
			}

			if (classpath.length() > 0) {
				classpath.append(",");
			}
			classpath.append(LIB_DIRECTORY);
			classpath.append(File.separator);
			File file = artifact.getFile();
			String fileName = file.getName();
			classpath.append(fileName);
			File localCopy = copyArtifact(file);
			addExportedPackages(localCopy, exportedPackages);
			addImportedPackages(localCopy, importedPackages);
		}

		if (classpath.toString().length() == 0)
			classpath.append("."); // default to "."

		mainAttributes.put(new Attributes.Name(BUNDLE_CLASSPATH),
				classpath.toString());

		if (exportedPackages.toString().length() > 0)
			mainAttributes.put(new Attributes.Name(EXPORT_PACKAGE),
					exportedPackages.toString());
		if (importedPackages.toString().length() > 0)
			mainAttributes.put(new Attributes.Name(IMPORT_PACKAGE),
					importedPackages.toString());
	}

	/**
	 * Adds exported packages.
	 * 
	 * @param file
	 *            the jar file.
	 * @param exportedPackages
	 *            the buffer that holds the exported packages.
	 * @throws MojoExecutionException
	 */
	private void addExportedPackages(File file, StringBuffer exportedPackages)
			throws MojoExecutionException {
		ArrayList packageList = new ArrayList();
		try {
			JarFile jar = new JarFile(file);
			Enumeration entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry jarEntry = (JarEntry) entries.nextElement();
				String entryName = jarEntry.getName();
				if (entryName.endsWith(".class")) {
					String packageName = getPackageForName(entryName);
					if (!packageList.contains(packageName)) {
						// check for do not export package
						boolean excludePackage = false;
						excludePackage = isPackageInList(doNotExportPackagePrefixes,packageName);
						if (!excludePackage)
							packageList.add(packageName);
					}
				}
			}
			jar.close();
		} catch (IOException e) {
			throw new MojoExecutionException("Could not introspect jar "
					+ file.getAbsolutePath(), e);
		}
		if (packageList.size() > 0) {
			Object[] packages = packageList.toArray();
			for (int i = 0; i < packages.length; i++) {
				if (i > 0 || exportedPackages.length() > 0) {
					exportedPackages.append(",");
				}
				exportedPackages.append(packages[i]);
			}
		}
	}
	
	/**
	 * Adds exported packages.
	 * 
	 * @param file
	 *            the jar file.
	 * @param exportedPackages
	 *            the buffer that holds the exported packages.
	 * @throws MojoExecutionException
	 */
	private void addImportedPackages(File file, StringBuffer importedPackages)
			throws MojoExecutionException {
		ArrayList packageList = new ArrayList();
		try {
			JarFile jar = new JarFile(file);
			Enumeration entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry jarEntry = (JarEntry) entries.nextElement();
				String entryName = jarEntry.getName();
				if (entryName.endsWith(".class")) {
					String packageName = getPackageForName(entryName);
					if (!packageList.contains(packageName)) {
						// check for do not export package
						boolean excludePackage = false;
						excludePackage = isPackageInList(importInsteadOfExportPackagePrefixes,packageName);
						if (excludePackage)
							packageList.add(packageName);
					}
				}
			}
			jar.close();
		} catch (IOException e) {
			throw new MojoExecutionException("Could not introspect jar "
					+ file.getAbsolutePath(), e);
		}
		if (packageList.size() > 0) {
			Object[] packages = packageList.toArray();
			for (int i = 0; i < packages.length; i++) {
				if (i > 0 || importedPackages.length() > 0) {
					importedPackages.append(",");
				}
				importedPackages.append(packages[i]);
			}
		}
	}

	private boolean isPackageInList(List list,String packageName) {
		if (list != null
				&& !list.isEmpty()) {
			Iterator listItr = list.iterator();
			while (listItr.hasNext()) {

				Object item = listItr.next();

				if (packageName.startsWith(item.toString())) {

					getLog().debug("Excluding/importing package: " + packageName);
					return true;
				}
			}

		}
		return false;
	}

	/**
	 * Gets the package for the specified entry name.
	 * 
	 * @param entryName
	 *            the jar entry's name.
	 * @return the package name for the entry.
	 */
	private String getPackageForName(String entryName) {
		entryName = entryName.substring(0, entryName.lastIndexOf('/'));
		String packageName = entryName.replace('/', '.');
		return packageName;
	}

	/**
	 * Copies the artifact.
	 * 
	 * @param file
	 *            the artifact's file.
	 * @return the copy.
	 * @throws MojoExecutionException
	 */
	private File copyArtifact(File file) throws MojoExecutionException {
		String fileName = file.getName();
		File copy = new File(libDirectory, fileName);

		if (!copy.exists()) {
			try {

				copy.createNewFile();
			} catch (IOException e) {
				throw new MojoExecutionException("Could not create new File "
						+ fileName, e);
			}
		}
		try {
			getLog().debug("Copying jar ........." + copy.getName());
			copyFile(file, copy);
		} catch (IOException e) {
			throw new MojoExecutionException("Error Copying file " + fileName, e);
		}
		return copy;
	}

	/**
	 * Writes the initial main attributes.
	 * 
	 * @param mainAttributes
	 *            the main attributes.
	 */
	private void writeInitialManifestAttributes(Attributes mainAttributes) {
		mainAttributes.put(Attributes.Name.MANIFEST_VERSION,
				MANIFEST_VERSION_VALUE);
		mainAttributes.put(new Attributes.Name(BUNDLE_MANIFEST_VERSION),
				BUNDLE_MANIFEST_VERSION_VALUE);
		mainAttributes.put(new Attributes.Name(BUNDLE_NAME), project.getName());

		if (project.getPackaging().equals(
				EclipseConstants.PACKING_SOURCE_PLUGIN)) {
			mainAttributes.put(new Attributes.Name(BUNDLE_SYMBOLIC_NAME),
					project.getArtifactId() + ";"
							+ BUNDLE_SYMBOLIC_NAME_SINGLETON);

		} else
			mainAttributes.put(new Attributes.Name(BUNDLE_SYMBOLIC_NAME),
					project.getArtifactId());

		mainAttributes.put(
				new Attributes.Name(BUNDLE_REQUIRED_EXECUTION_ENVIRONMENT),
				BUNDLE_REQUIRED_EXECUTION_ENVIRONMENT_J2SE15);

		String version = project.getVersion();
		int index = version.indexOf("-SNAPSHOT");
		if (index > 0) {
			version = version.substring(0, index);
		}
		mainAttributes.put(new Attributes.Name(BUNDLE_VERSION), version);
		mainAttributes.put(new Attributes.Name(BUNDLE_VENDOR),
				BUNDLE_VENDOR_VALUE);
		mainAttributes.put(new Attributes.Name(BUNDLE_LOCALIZATION),
				BUNDLE_LOCALIZATION_VALUE);

		mainAttributes.put(new Attributes.Name(BUNDLE_ACTIVATION),
				BUNDLE_ACTIVATION_LAZY);

		mainAttributes.put(new Attributes.Name(ECLIPSE_BUDDY_POLICY),
				ECLIPSE_BUDDY_POLICY_VALUE);
		if (buddies != null && buddies.size() > 0) {
			StringBuffer buddyList = new StringBuffer();
			Object[] buddyArray = buddies.toArray();
			for (int i = 0; i < buddyArray.length; i++) {
				if (i > 0) {
					buddyList.append(",");
				}
				buddyList.append(buddyArray[i]);
			}
			mainAttributes.put(new Attributes.Name(ECLIPSE_REGISTER_BUDDY),
					buddyList.toString());
		}
	}
}

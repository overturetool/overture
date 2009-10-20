package org.overture.ide.debug.launching;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.osgi.util.ManifestElement;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

public class ClasspathUtils {

	public static void collectClasspath(String[] bundleIds, List<String> entries) {
		try {
			final boolean developmentMode = Platform.inDevelopmentMode();
			ArrayList<File> fileList = new ArrayList<File>();
			for (int i = 0; i < bundleIds.length; ++i) {
				final String bundleId = bundleIds[i];
				getPluginClassPath(bundleId, entries);
				final File file = getPluginLocation(bundleId);
				fileList.add(file);
				for (File f : fileList) {
					if (developmentMode && file.isDirectory()) {
						final File bin = new File(f, "bin"); //$NON-NLS-1$
						if (bin.isDirectory()) {
							entries.add(bin.getAbsolutePath());
							continue;
						}
					}
					entries.add(f.getAbsolutePath());
				}
			}
		} catch (CoreException e) {
			LaunchingPlugin.error(e);
		}
	}
	
	private static void getPluginClassPath(String bundleId, List<String> entries)
	{
		try {
			final Bundle bundle = Platform.getBundle(bundleId);
			if (bundle == null){
				throw new BundleException(bundleId + " cannot be retrieved from the Platform");
			}
			
			String requires = (String) bundle.getHeaders().get((Constants.BUNDLE_CLASSPATH));
			if (requires == null) 
				requires = ".";	
			ManifestElement[] elements = ManifestElement.parseHeader(Constants.REQUIRE_BUNDLE, requires);
			for (ManifestElement manifestElement : elements) {
				String value = manifestElement.getValue();
				if (".".equals(value))
					value = "/";
				
				URL url = bundle.getEntry(value);
				if (url != null)
				{
					URL resolvedFile = FileLocator.resolve(url);
					if ("file".equals(resolvedFile.getProtocol())) //$NON-NLS-1$
					{
						File file = new File(resolvedFile.getPath());
						// adding jar-files
						if (file.isDirectory() == false)
						{
							entries.add(file.getAbsolutePath());
						}
						//System.out.println("addedFile" + resolvedFile.getPath() + " isDir: " + file.isDirectory());
					}
				}
			}
		} catch (Exception e) {
			final String msg = NLS.bind(PLUGIN_LOCATION_ERROR, bundleId);
			System.out.println(msg);
		}
		
	}

	
	private static File getPluginLocation(String bundleId) throws CoreException {
		try {
			final Bundle bundle = Platform.getBundle(bundleId);			
			return getBundleFile(bundle);
		} catch (IOException e) {
			final String msg = NLS.bind(PLUGIN_LOCATION_ERROR, bundleId);
			throw new CoreException(new Status(IStatus.ERROR, VDMLaunchingConstants.VDMPP_LaunchPluginID, IStatus.ERROR, msg, e));
		}
	}

	/**
	 * Returns a file for the contents of the specified bundle. Depending on how
	 * the bundle is installed the returned file may be a directory or a jar
	 * file containing the bundle content.
	 * 
	 * XXX 3.3 compatibility = {@link FileLocator#getBundleFile(Bundle)}
	 * 
	 * @param bundle
	 *            the bundle
	 * @return a file with the contents of the bundle
	 * @throws IOException
	 *             if an error occurs during the resolution
	 */
	public static File getBundleFile(Bundle bundle) throws IOException {
		URL rootEntry = bundle.getEntry("/"); //$NON-NLS-1$
		rootEntry = FileLocator.resolve(rootEntry);
		if ("file".equals(rootEntry.getProtocol())) //$NON-NLS-1$
			return new File(rootEntry.getPath());
		if ("jar".equals(rootEntry.getProtocol())) { //$NON-NLS-1$
			String path = rootEntry.getPath();
			if (path.startsWith("file:")) {
				// strip off the file: and the !/
				path = path.substring(5, path.length() - 2);
				return new File(path);
			}
		}
		throw new IOException("Unknown protocol"); //$NON-NLS-1$
	}
	
	public static String[] getClassPath(IJavaProject myJavaProject) throws IOException, URISyntaxException {
		final List<String> result = new ArrayList<String>();
		// TODO ClasspathUtils.collectClasspath(new String[] { GenericOvertureInstallType.EMBEDDED_VDMJ_BUNDLE_ID, GenericOvertureInstallType.DBGP_FOR_VDMJ_BUNDLE_ID }, result);
		ClasspathUtils.collectClasspath
				(
					new String[]
					      {
							"org.overture.ide.generated.vdmj" //TODO put in constant file
							//VDMToolsInstallType.DBGP_FOR_VDMTOOLS_BUNDLE_ID,
							//GenericOvertureInstalltype.DBGP_FOR_VDMTOOLS_BUNDLE_ID,
							//GenericOvertureInstalltype.DBGP_FOR_ABSTRACT_BUNDLE_ID
					      },
					result
				);
		try {
			final String[] classPath = computeBaseClassPath(myJavaProject);
			for (int i = 0; i < classPath.length; ++i) {
				result.add(classPath[i]);
			}
		} catch (CoreException e) {
		}
		return (String[]) result.toArray(new String[result.size()]);
	}
	
	protected static String[] computeBaseClassPath(IJavaProject myJavaProject) throws CoreException {
		if (!myJavaProject.exists())
			return CharOperation.NO_STRINGS;
		return JavaRuntime.computeDefaultRuntimeClassPath(myJavaProject);
	}

	private static final String PLUGIN_LOCATION_ERROR = "Error determining classpath from bundle {0}"; //$NON-NLS-1$
}

package org.overturetool.tools.packworkspace;

import java.io.File;
import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;

/**
 * FolderZiper provide a static method to zip a folder.
 * 
 * @author pitchoun
 */
public class FolderZiper {

	/**
	 * Zip the srcFolder into the destFileZipFile. All the folder subtree of the
	 * src folder is added to the destZipFile archive.
	 * 
	 * TODO handle the usecase of srcFolder being en file.
	 * 
	 * @param srcFolder
	 *            String, the path of the srcFolder
	 * @param destZipFile
	 *            String, the path of the destination zipFile. This file will be
	 *            created or erased.
	 */
	static public void zipFolder(String srcFolder, String destZipFile) {
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;
		try {
			fileWriter = new FileOutputStream(destZipFile);
			zip = new ZipOutputStream(fileWriter);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(0);
		}

		addFolderToZip("", srcFolder, zip);
		try {
			zip.flush();
			zip.close();
			fileWriter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Write the content of srcFile in a new ZipEntry, named path+srcFile, of
	 * the zip stream. The result is that the srcFile will be in the path folder
	 * in the generated archive.
	 * 
	 * @param path
	 *            String, the relatif path with the root archive.
	 * @param srcFile
	 *            String, the absolute path of the file to add
	 * @param zip
	 *            ZipOutputStram, the stream to use to write the given file.
	 */
	static private void addToZip(String path, String srcFile,
			ZipOutputStream zip) {

		File folder = new File(srcFile);
		if (folder.isDirectory()) {
			addFolderToZip(path, srcFile, zip);
		} else {
			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			try {
				FileInputStream in = new FileInputStream(srcFile);
				zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
				while ((len = in.read(buf)) > 0) {
					zip.write(buf, 0, len);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * add the srcFolder to the zip stream.
	 * 
	 * @param path
	 *            String, the relatif path with the root archive.
	 * @param srcFile
	 *            String, the absolute path of the file to add
	 * @param zip
	 *            ZipOutputStram, the stream to use to write the given file.
	 */
	static private void addFolderToZip(String path, String srcFolder,
			ZipOutputStream zip) {
		File folder = new File(srcFolder);
		String fileListe[] = folder.list();
		try {
			int i = 0;
			while (true) {
				addToZip(path + "/" + folder.getName(), srcFolder + "/"
						+ fileListe[i], zip);
				i++;
			}
		} catch (Exception ex) {
		}
	}
}
/*******************************************************************************
 * Copyright (c) 2009, 2013 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.guibuilder.internal;

/**
 * "Main" class.
 * 
 * @author carlos
 */
public class Driver
{

	public static void printUsage()
	{
		System.out.println("\nUsage:");
		System.out.println("java -jar <jar file> -p <vdm++ project folder>");
		System.out.println("The following options are available:\n");
		System.out.println("-i <folder containing xml ui descriptor files> - Use existing ui xml descriptors");
		System.out.println("-a  - Generate in annotation mode (no effect if using -i)");
		System.out.println("-s  - Save the generated xml files (no effect if using -i)");
	}

	/**
	 * @param args
	 *            Just a class to house the main function, and parse the arguments
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{

		// // NOTE: hardcoded for VDM++ ONLY
		// final Dialect dialect = Dialect.VDM_PP;
		//
		// // command line arguments parsing
		//
		// OptionParser parser = new OptionParser("p:i:as");
		// OptionSet options = null;
		// try {
		// options = parser.parse(args);
		// } catch (Exception e) {
		// System.out.println("Error: " + e.getMessage()); printUsage(); return;
		// }
		//
		// if(!options.has("p"))
		// { printUsage(); return; }
		//
		// // list of files, or directory from which to extract the specification
		// // List<String> largs = (List<String>) options.valuesOf("p");
		// final Vector<File> files = GetFiles(largs, dialect);
		// // if there is the i switch do not generate
		// if ( options.has("i")) {
		// ToolSettings.GENERATE = false;
		// // largs = (List<String>) options.valuesOf("i");
		// }
		// final Vector<File> xmlFiles = GetXMLFiles(largs);
		// // if there is the 'a' switch generation mode is annotation only
		// if(options.has("a")) {
		// // ToolSettings.GENERATION_SETTINGS = GENERATION_MODE.ANNOTATIONS;
		// }
		// // if there is the 's' switch the xml description is saved
		// if(options.has("s")) {
		// ToolSettings.SAVE_XML = true;
		// }
		//
		// //assuming the last directory name is the project name
		// String path = null;
		// path = files.get(0).getPath();
		// if ( path.contains("/") ) { // sanity checks
		// path = path.substring(0, path.lastIndexOf("/"));
		// if (path.contains("/"))
		// path = path.substring(path.lastIndexOf("/")+1);
		// }
		// final String projectName = path;
		//
		// java.awt.EventQueue.invokeLater( new Runnable() {
		//
		// @Override
		// public void run() {
		//
		// try {
		// VdmjVdmInterpreterWrapper interpreter = new VdmjVdmInterpreterWrapper(dialect);
		// interpreter.parseFilesAndTypeCheck(files);
		// org.overture.guibuilder.internal.ir.IVdmClassReader reader = new
		// org.overture.guibuilder.internal.ir.VdmjVdmClassReader();
		// reader.readFiles( files ); // we only care for VDM++
		//
		// UiInterface ui = new UiInterface(interpreter);
		// if ( ToolSettings.GENERATE ) {
		// // Generation Mode
		// if ( ToolSettings.SAVE_XML) {
		// ui.buildAndRender(reader, projectName, files.get(0).getPath().substring(0,
		// files.get(0).getPath().lastIndexOf("/") + 1));
		// } else {
		// ui.buildAndRender(reader, projectName);
		// }
		// } else {
		// ui.Render(reader, projectName, xmlFiles);
		// }
		// ui.setVisible(true);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }} );
	}

	//
	// /**
	// * Gets the list of xml files contained in a directory
	// * @param largs Directory containing xml files.
	// * @return
	// */
	// private static Vector<File> GetXMLFiles(List<String> largs) {
	//
	// Vector<File> filenames = new Vector<File>();
	// if (largs != null) {
	// for ( String s : largs ) {
	// File dir = new File(s);
	//
	// if (dir.isDirectory()) { // directory
	// // contents of directory
	// for (File file: dir.listFiles(new FilenameFilter(){
	// // filter for xml files (checks if the filename contains ".xml"
	// // FIXME: ugly
	// @Override
	// public boolean accept(File arg0, String arg1) {
	// if ( arg1.contains(".xml") )
	// return true;
	// return false;
	// }}))
	// {
	// if(file.isFile()) {
	// filenames.add(file);
	// }
	// } // end for
	// } else {
	// filenames.add(dir);
	// } // end directory
	// }
	//
	// }
	// if (filenames.size() == 0)
	// return null;
	//
	// return filenames;
	// }
	//
	//
	// /**
	// * Static function for obtaining the files from a directory or a single file,
	// * according to a given vdm dialect
	// * @param names of files and/or directory path
	// * @param dialect
	// * @return
	// */
	// private static Vector<File> GetFiles(List<String> largs, Dialect dialect) {
	//
	// Vector<File> filenames = new Vector<File>();
	//
	// if(largs != null) {
	// // arguments parsing
	// for (Iterator<String> i = largs.iterator(); i.hasNext();)
	// {
	// String arg = i.next();
	// File dir = new File(arg);
	//
	// if(dir.isDirectory())
	// {
	// // contents of directory
	// for (File file: dir.listFiles(dialect.getFilter()))
	// {
	// if(file.isFile()) {
	// filenames.add(file);
	// }
	// } // end for
	// } else {
	// filenames.add(dir);
	// } // end if (dir.isDirectory())
	// } // end for
	// }
	//
	// return filenames;
	// }

}

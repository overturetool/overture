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

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.overture.guibuilder.internal.ir.IVdmClassReader;
import org.overture.guibuilder.internal.ir.IVdmDefinition;
import org.overture.guibuilder.internal.ir.VdmClass;

/**
 * This is the base of the graphical user interface. Retrieves the necessary information
 * about the vdm specification, calls ui generators (if necessary), renders the defined ui, 
 * and finally manages the ui.
 * @author carlos
 *
 */
public class UiInterface {

	// reference to the interpreter wrapper in order to process the gui commands
	private VdmjVdmInterpreterWrapper interpreter = null;
	// the component map
	private Hashtable<String, IContainerBridge> containerTable = null;
	// list of current instances of vdm objects
	private InstanceList instanceList = null;
	// window with the table of instances
	private InstanceListUiContainer instanceListUiContainer = null;
	// the IR of the underlying VDM++ specification
	private Vector<IVdmDefinition> classList = null;

	/**
	 * Constructor
	 * @param interpreter - the vdmj command interpreter wrapper
	 */
	public UiInterface(VdmjVdmInterpreterWrapper interpreter) {
		this.interpreter = interpreter;
		instanceList = InstanceList.getInstance();
		instanceListUiContainer  = new InstanceListUiContainer(instanceList);
		containerTable = new Hashtable<String, IContainerBridge>();
	}


	/**
	 * Renders a window with no specific function from a swixml description
	 * @param file - the swixml file containing the xml description of the window
	 * @param name - the name to give the new window
	 * @throws Exception
	 */
	public void renderFile(File file, String name) throws Exception {
		IContainerBridge c = new GenericContainerBridge(this);
		c.buildComponent(file);
		if ( name != null)
			containerTable.put(name, c);
		else
			// FIXME: Find a better identifier than 'main' when main String is null
			containerTable.put("main", c);
	}

	/**
	 * Renders (but doesn't show) a instance window from a swixml description
	 * @param file - the swixml file containing the xml description of the window
	 * @param name - the name to give the new window
	 * @param classDef 
	 * @throws Exception
	 */
	private void renderInstanceWindow(File file, String name) throws Exception {
		IContainerBridge c = new InstanceWindowContainerBridge(this);
		c.buildComponent(file);
		if ( name != null)
			containerTable.put(name, c);
		else
			// FIXME: Find a better identifier than 'main' when main String is null
			containerTable.put("main", c);
	}

	/**
	 * Renders the main window from file
	 * @param file - the swixml containing the xml description of the window
	 * @param name - the name to give the window
	 * @throws Exception
	 */
	private void renderMainWindow(File file, String name) throws Exception {
		IContainerBridge c = new MainWindowContainerBridge(this);
		c.buildComponent(file);
		if ( name != null)
			containerTable.put(name, c);
		else
			// FIXME: Find a better identifier than 'main' when main String is null
			containerTable.put("main", c);
	}

	/**
	 * Set's the entire ui visible or not visible
	 * @param flag - for visibility
	 * @throws Exception
	 */
	public void setVisible(boolean flag) throws Exception {
		if (containerTable.isEmpty())
			throw new Exception("No container to make visible");
		// if there's only one container to show

		instanceListUiContainer.setVisible(true);

		if(containerTable.size()==1) {
			containerTable.elements().nextElement().setVisible(true);
			return;
		}

		containerTable.get( NamingPolicies.getMainWindowId() ).setVisible(true);
	}

	/**
	 * Sets the visibility property of a window identified by it's name
	 * @param windowName - the window's name
	 * @param flag - value for the visibility property, false the window's invisible, true, the window's visible
	 * @throws Exception
	 */
	public void setWindowVisible(String windowName, boolean flag) throws Exception {
		IContainerBridge cnt = containerTable.get(NamingPolicies.getObjectWindowId(windowName));
		if (cnt == null)
			throw new Exception("Window with id " + 
					NamingPolicies.getObjectWindowId(windowName) + " not found");

		cnt.setVisible(flag);
	}


	// the following methods relate to communication with the intepreter

	/**
	 * Creates a new vdmpp object instance, not using any arguments
	 * @param name - name of the new instance
	 * @param className - name of the class
	 * @throws Exception
	 */
	public void createNewVdmObjectInstance(String name, String className) throws Exception {
		interpreter.createInstance(name, "new " + className + "()");
		String value = interpreter.getValueOf(name);
		instanceList.add(new VdmInstance(name, value, className));		
	}

	/** 
	 * Creates a new vdmpp object instance
	 * @param name - string containing the name of the instance
	 * @param className - string with name of the class
	 * @param constructorArgs - list of strings, containing the arguments for the constructor
	 * @throws Exception
	 */
	public void createNewVdmObjectInstance(String name, String className, List<String> constructorArgs ) throws Exception {

		String args = "(";
		for (int i=0; i<constructorArgs.size(); ++i) {
			if (i!=0)
				args += ",";
			args += constructorArgs.get(i);
		}
		args += ")";

		interpreter.createInstance(name, "new " + className + args);
		String value = interpreter.getValueOf(name);
		instanceList.add(new VdmInstance(name, value, className));
	}

	/**
	 * Calls a vdm method returning the result in string form
	 * @param instanceName - the name of the vdm object instance
	 * @param methodName - the name of the method to call
	 * @param arguments - list with the method's arguments, if any
	 * @return The result of calling the method in string form.
	 * @throws Exception
	 */
	public String callVdmMethod(String instanceName, String methodName, List<String> arguments ) throws Exception {

		String command = instanceName + "." + methodName ;
		command += "(";
		if ( arguments != null) {
			for (int i=0; i < arguments.size(); ++i) {
				if(i!=0)
					command += ",";
				command += arguments.get(i); 
			}
		}
		command += ")";

		String ret = null;
		try {
			ret = interpreter.execute(command);
			// Refreshed the value of the instance
			// TODO: the refresh is requested even if the method does not change
			// and the only instance refreshed is the method's 'parent', it's not
			// taken into account the possibility the method performs changes on others 
			for (int i = 0; i < instanceList.size(); ++i ) {
				VdmInstance var = instanceList.get(i);
				if (var.getName().equals(instanceName) )
					var.refreshValue(interpreter);
			}
		} catch (Exception e) {
			// ERROR: Better error handling
			e.printStackTrace();
		}
		return ret;
	}


	/**
	 * Asks the intepreter to execute a vdm command, returning the result 
	 * of it's execution
	 * @param command - a string containing the vdm command to send to the interpreter
	 * @return the result of the vdm command in the form of a string
	 * @throws Exception
	 */
	public String executeVdmCommand(String command) throws Exception {
		String str =  (interpreter.execute(command));
		return str;
	}

	// ui creationg methods
	
	/**
	 * Builds a new gui description, saving it to a defined location.
	 * @param reader The vdm class reader to extract information from the specification.
	 * @param projectName The name of the project.
	 * @param savePath The path to save the gui description.
	 * @throws Exception
	 */
	public void buildAndRender(IVdmClassReader reader, String projectName, String savePath) throws Exception {
		SwiXMLGenerator generator = new SwiXMLGenerator();
		Vector<String> classList = reader.getClassNames();
		this.classList = reader.getClassList();
		File file = null;

		// a window to call the subwindows (only needed if there's more than one class)
		if(classList.size()>1 ) {
			// coding 'by the seat of your pants'
			int i = classList.size();

			if (ToolSettings.GENERATION_SETTINGS == ToolSettings.GENERATION_MODE.ANNOTATIONS) {
				// checking if we have more than one class to use
				for ( IVdmDefinition c : this.classList ) {
					// FIXME: this works as there is only one possible annotation for classes
					if (c.hasAnnotations())
						--i;
				}
			}

			if (i > 1) {
				file = new File(savePath + NamingPolicies.getMainWindowId() + ".xml");
				generator.buildMainWindow(reader, projectName);
				generator.toFile(file);
				renderMainWindow(file, NamingPolicies.getMainWindowId());
			}
		}

		for (IVdmDefinition classDef : reader.getClassList() ) {
			//			generator.buildInstanceWindowFor(className, reader);
			generator.buildInstanceWindowFor((VdmClass) classDef);
			file = new File(savePath + classDef.getName() + ".xml");
			generator.toFile(file);
			renderInstanceWindow(file, NamingPolicies.getObjectWindowId(classDef.getName()));
		}

		this.classList = reader.getClassList();	

	} 

	/**
	 * Builds a new gui description. The description is saved to the temporary file folder.
	 * @param reader The vdm class reader that will be used to extract information.
	 * @param projectName The name of the project.
	 * @throws Exception
	 */
	public void buildAndRender(IVdmClassReader reader, String projectName) throws Exception {

		SwiXMLGenerator generator = new SwiXMLGenerator();
		Vector<String> classList = reader.getClassNames();
		this.classList = reader.getClassList();
		File tempFile = null;
		try {
			tempFile =	File.createTempFile(String.valueOf(System.currentTimeMillis()), "tmp");
		} catch (IOException e) {
			// TODO: Consider printing this to a logger
			System.out.println("Error on creating temp file");
			e.printStackTrace();
			return;
		}	

		// a window to call the subwindows (only needed if there's more than one class)
		if(classList.size()>1 ) {
			// coding 'by the seat of your pants'
			int i = classList.size();

			if (ToolSettings.GENERATION_SETTINGS == ToolSettings.GENERATION_MODE.ANNOTATIONS) {
				// checking if we have more than one class to use
				for ( IVdmDefinition c : this.classList ) {
					// FIXME: this works as there is only one possible annotation for classes
					if (c.hasAnnotations())
						--i;
				}
			}

			if (i > 1) {
				generator.buildMainWindow(reader, projectName);
				generator.toFile(tempFile);
				renderMainWindow(tempFile, NamingPolicies.getMainWindowId());
			}
		}

		for (IVdmDefinition classDef : reader.getClassList() ) {
			//			generator.buildInstanceWindowFor(className, reader);
			generator.buildInstanceWindowFor((VdmClass) classDef);
			generator.toFile(tempFile);
			renderInstanceWindow(tempFile, NamingPolicies.getObjectWindowId(classDef.getName()));
		}

		this.classList = reader.getClassList();		
	}


	/**
	 * Searches (by name) for a vdm class.
	 * @param extractClassName The name of the vdm class to search.
	 * @return The requested vdm class.
	 * @throws Exception
	 */
	public VdmClass getVdmClass(String extractClassName) throws Exception {
		for ( IVdmDefinition classDef : classList ) {
			if ( classDef.getName().equals(extractClassName))
				return (VdmClass) classDef;

		}
		throw new Exception("vdm class not found");
	}

	/**
	 * Returns the list of all classes of the underlying vdm specification.
	 * @return The class list.
	 */
	public Vector<IVdmDefinition> getVdmClassList() {
		return classList;
	}

	/**
	 * Renders the graphical user interface from the xml ui description files.
	 * (The components of the UI are not immediately set to visible)
	 * @param reader The vdm class reader to be used. 
	 * @param projectName The name of the project.
	 * @param xmlFiles The list of xml files.
	 * @throws Exception 
	 */
	public void Render(IVdmClassReader reader, String projectName,
			Vector<File> xmlFiles) throws Exception {

		if ( xmlFiles == null)
			throw new Exception("No files to render");

		this.classList = reader.getClassList();
		String wid = null;

		for ( File xmlFile : xmlFiles) {
			if ( xmlFile.getName().equals("mainWindow.xml") ) {
				this.renderMainWindow(xmlFile, NamingPolicies.getMainWindowId()); continue; }
			wid = NamingPolicies.getObjectWindowId( xmlFile.getName().substring(0, xmlFile.getName().indexOf(".") ));
			this.renderInstanceWindow(xmlFile, wid);
		}

	}


}

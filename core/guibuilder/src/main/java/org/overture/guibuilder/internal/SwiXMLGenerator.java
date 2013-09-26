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
import java.io.OutputStream;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.overture.guibuilder.GuiBuilderRemote;
import org.overture.guibuilder.generated.swixml.schema.Button;
import org.overture.guibuilder.generated.swixml.schema.Combobox;
import org.overture.guibuilder.generated.swixml.schema.Frame;
import org.overture.guibuilder.generated.swixml.schema.Label;
import org.overture.guibuilder.generated.swixml.schema.ObjectFactory;
import org.overture.guibuilder.generated.swixml.schema.Panel;
import org.overture.guibuilder.generated.swixml.schema.Scrollpane;
import org.overture.guibuilder.generated.swixml.schema.Textfield;
import org.overture.guibuilder.generated.swixml.schema.Vbox;
import org.overture.guibuilder.internal.ir.IVdmClassReader;
import org.overture.guibuilder.internal.ir.IVdmDefinition;
import org.overture.guibuilder.internal.ir.VdmAnnotation;
import org.overture.guibuilder.internal.ir.VdmClass;
import org.overture.guibuilder.internal.ir.VdmMethod;
import org.overture.guibuilder.internal.ir.VdmParam;



/**
 * 
 * SwiXML user interface description generator.
 * @author carlos
 *
 */

public class SwiXMLGenerator {

	// variables for xml writting
	private JAXBContext jaxbContext = null;
	private Marshaller marshaller = null;
	private ObjectFactory objFactory = null;
	// root node of the xml
	private Frame root = null;

	private static final String MAIN_PANEL = "BorderLayout.LEFT";
	
	public SwiXMLGenerator() throws JAXBException {
		jaxbContext = JAXBContext.newInstance(GuiBuilderRemote.GENERATED_PACKAGE);
		marshaller = jaxbContext.createMarshaller();
		objFactory = new ObjectFactory();
	}	

	/**
	 * Outputs the xml description to and Output Stream.
	 * @param os The outpustream to write to
	 * @throws JAXBException
	 */
	public void toOutputStream(OutputStream os) throws JAXBException {
		marshaller.marshal(root, os);
	}

	/**
	 * Output the xml ui description to a file
	 * @param file The path to the output file
	 * @throws JAXBException
	 */
	public void toFile(File file) throws JAXBException {
		marshaller.marshal(root, file);
	}

	/**
	 * Defines a main window for the user interface
	 * @param classReader A vdm class reader to extract required information
	 * @param title The title name of the main window
	 */
	public void buildMainWindow(IVdmClassReader classReader, String title) {
		Frame frame = createMainWindow(classReader.getClassList(), title);
		root = frame;
	}

	// FIXME: Generated window is very simple...
	/**
	 * 	Function to build the elements of a main window
	 * @param vector List of vdm classes that ui fronts for
	 * @param title Title of teh main window
	 * @return A Frame xml node
	 */
	private Frame createMainWindow(Vector<IVdmDefinition> vector, String title) {
		Frame frame =  objFactory.createFrame();
		frame.setSize("220,220");
		frame.setDefaultcloseoperation("JFrame.EXIT_ON_CLOSE");
		frame.setId(NamingPolicies.getMainWindowId());
		frame.setTitle(title);
		Scrollpane panel = objFactory.createScrollpane();
		panel.setConstraints("BorderLayout.CENTER");
		frame.getContent().add(panel);
		Vbox vbox = objFactory.createVbox();
		panel.getContent().add(vbox);

		for( IVdmDefinition c : vector ) {

			Button button = objFactory.createButton();
			button.setId( NamingPolicies.getButtonWindowCallerId(c.getName()) );
			button.setText(c.getName());
/*			if ( c.getName().equals("Till") )
				button.setEnabled(false);
	*/		
			vbox.getContent().add(button);
		}

		return frame;
	}

	/**
	 * Generates a simple panel with ok and cancel buttons
	 * @return A panel swiXML node
	 */
	private Panel createOkCancelPanel() {
		Panel panel = objFactory.createPanel();
		Button btnOK = objFactory.createButton();
		//		Button btnCancel = objFactory.createButton();
		btnOK.setText("OK");
		btnOK.setAction(NamingPolicies.OK_BUTTON_ACTION_NAME);
		//		btnCancel.setText("Cancel");
		//		btnCancel.setAction(NamingPolicies.CANCEL_BUTTON_ACTION_NAME);
		panel.getContent().add(btnOK);
		//		panel.getContent().add(btnCancel);
		return panel;
	}

	/**
	 * Builds a window that fronts for a vdm class instance
	 * @param classDef The vdm class of the instances
	 */
	public void buildInstanceWindowFor(VdmClass classDef) {

		Frame rootFrame = objFactory.createFrame();
		rootFrame.setSize("320,520");
		// FIXME: Get the name from the annotation
		rootFrame.setTitle(classDef.getName());
		// the root's id  is based on the class's name
		rootFrame.setId( NamingPolicies.getObjectWindowId(classDef.getName())); // id for the window
		Vector<IVdmDefinition> defList;
		Panel mainPanel = objFactory.createPanel();
		mainPanel.setConstraints(MAIN_PANEL);
		rootFrame.getContent().add(mainPanel);
		
		// instance chooser combo box
		Panel instanceChooserPanel = objFactory.createPanel();
		Combobox comboBox = objFactory.createCombobox();
		comboBox.setId( NamingPolicies.INSTANCE_LIST_ID );
		Label instanceLabel = objFactory.createLabel();
		instanceLabel.setText("Current Instance: ");
		instanceChooserPanel.getContent().add(instanceLabel);
		instanceChooserPanel.getContent().add(comboBox);
		mainPanel.getContent().add(instanceChooserPanel);

		try {
			if( classDef.hasConstructors() ) {
				Vbox box = objFactory.createVbox();
				Panel p = createConstructorArgumentsPanel(classDef);
				box.setVisible(false);
				box.getContent().add(p);
				box.getContent().add(createOkCancelPanel());
				box.setId(NamingPolicies.CONSTRUCTORS_CONTAINER_ID);
				mainPanel.getContent().add(box);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			defList = classDef.getDefinitions();
			Vbox vbox = objFactory.createVbox();
			vbox.setId(NamingPolicies.METHOD_CONTAINER_ID);
			//		p.setLayout("FlowLayout(FlowLayout.RIGHT)");
			for (IVdmDefinition def : defList) {
				if ( def instanceof VdmMethod ) {
					if(( (VdmMethod) def ).isConstructor())
						continue;

					Panel panel = createMethodPanel(classDef.getName(), (VdmMethod) def);
					vbox.getContent().add(panel);
				}
			}
			mainPanel.getContent().add(vbox);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		root = rootFrame;
	}

	/**
	 * Defines the panel that contains elements to interact with a single method
	 * a vdm class
	 * @param className The vdm class
	 * @param def The method of the vdm class
	 * @return
	 */
	private Panel createMethodPanel(String className, VdmMethod def) {

		Panel panel = objFactory.createPanel();

		panel.setId(NamingPolicies.getMethodsWidgetContainerId(className, def.getName()));
		panel.setLayout("FlowLayout(FlowLayout.LEFT)");

		switch( ToolSettings.GENERATION_SETTINGS ) {
		case NO_ANNOTATIONS:
			// generic gui elements
			addGenericElements(panel, def);
			break;
		case ANNOTATIONS:
			if ( def.hasAnnotations())
				addAnnotationGuidedElements(panel, def);
			break;
		default:

		}

		return panel;

	}

	/**
	 * Defines elements of a panel using information provided by annotation 
	 * @param panel The panel into which add the elements
	 * @param def The vdm method the ui elements are intended for.
	 */
	private void addAnnotationGuidedElements(Panel panel, VdmMethod def) {
		
		Vector<VdmAnnotation> annotations = def.getAnnotations();
		for( VdmAnnotation annotation: annotations ) {
			// FIXME: with a press annotation we just call addGenericElements()
			// @press
			if (annotation.getName().equals(NamingPolicies.PRESS_ANNOTATION)) {
				addGenericElements(panel, def);
				return;
			}
			// @check
			if (annotation.getName().equals(NamingPolicies.RETRIEVE_ANNOTATION)) {
				Label methodLblName = objFactory.createLabel();
				methodLblName.setText( annotation.getValue() );
				panel.getContent().add(methodLblName);
				Label methodReturnLbl = objFactory.createLabel();
				methodReturnLbl.setId( NamingPolicies.getMethodReturnLabelId(def.getName()) );
				panel.getContent().add(methodReturnLbl);
			}
			
		}
		

	}

	/**
	 * Defines elements of a panel using a generic approach 
	 * @param panel The panel into which add the elements
	 * @param def The vdm method the ui elements are intended for.
	 */
	private void addGenericElements(Panel panel, VdmMethod def) {
		
		// textfields for argument input
		for (int i = 0; i <  def.getParamList().size() ; ++i) {
			// the arguments variable name will be used as the text for the label
			// for the text input widget
			Label methodlbl = objFactory.createLabel();
			methodlbl.setText( def.getParamList().get(i).getParaName() );
			panel.getContent().add(methodlbl);
			// the input widget
			Object inputWidget = toUserInterfaceElement(def.getParamList().get(i),
					NamingPolicies.getInputComponentId(def.getName(), i));
			panel.getContent().add(inputWidget);
		}
		// button 
		Button button = objFactory.createButton();
		button.setText(def.getName());
		button.setId(NamingPolicies.getMethodButtonId(def.getName()));
		// the return value
		Label retlbl = objFactory.createLabel();
		retlbl.setId(NamingPolicies.getCheckMethodReturnLabelId(def.getName()));
		
		panel.getContent().add(button);
		panel.getContent().add(retlbl);
	}

	/**
	 * Defines a panel with ui elements to be used to instantiate a class
	 * @param classDef The class to instantiate
	 * @return The xml node of a swixml panel
	 */
	private Panel createConstructorArgumentsPanel(VdmClass classDef) {
		// FIXME: we only handle one constructor
		Panel panel = objFactory.createPanel();
		panel.setId(NamingPolicies.getConstWidgetsContainerId(0));
		for ( IVdmDefinition vdmDef : classDef.getDefinitions() ) {
			if ( vdmDef instanceof VdmMethod ) {
				if ( ((VdmMethod) vdmDef).isConstructor()) {
					// found a constructor
					VdmMethod def = (VdmMethod) vdmDef;
					for (int i = 0; i <  def.getParamList().size() ; ++i) {
						// the arguments variable name will be used as the text for the label
						// for the text input widget
						Label methodlbl = objFactory.createLabel();
						methodlbl.setText( def.getParamList().get(i).getParaName() );
						panel.getContent().add(methodlbl);
						Object inputWidget = toUserInterfaceElement(def.getParamList().get(i),
								NamingPolicies.getConstructorWidgetId(classDef.getName(), 0, i));
						panel.getContent().add(inputWidget);							
					} // for
					return panel;
				} // isConstructor()
			} // is VdmMethod

		}
		return null;
	}
	
	/**
	 * Returns the graphical equivalent of a parameter
	 * @param param The vdm parameter
	 * @param id The id to give to the ui element
	 * @return A swixml node
	 */
	private Object toUserInterfaceElement(VdmParam param, String id) {
		// TODO: Only class are distringuished everything else is a input box
		if ( param.getType() != null && param.getType().isClass() ) {
			Combobox comboBox = objFactory.createCombobox();
			comboBox.setId(id);
			return comboBox;
		} else {
			Textfield text = objFactory.createTextfield();
			text.setColumns("4");
			text.setId(id);
			return text;
		}
	}

}

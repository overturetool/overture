/*
 * #%~
 * Overture GUI Builder
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.guibuilder.internal;

/**
 * Class to handle the naming policies between vdm operator/classes/arguments/functions and gui elements. All
 * information is hard coded.
 * 
 * @author carlos
 */
public class NamingPolicies
{

	// counter for the instanceNumber
	private static int instanceNumber = 0;

	// action name for ok button
	public final static String OK_BUTTON_ACTION_NAME = "submitOK";
	// action name for cancel button
	public final static String CANCEL_BUTTON_ACTION_NAME = "submitCancel";
	// id for the container of all widgets referring to class 'methods'
	public final static String METHOD_CONTAINER_ID = "methods_container";
	// id for the container of all widgets referring to class constructors
	public final static String CONSTRUCTORS_CONTAINER_ID = "constructor_container";
	// name of the annotation for a method that we only wish to extract information
	// translates into a label widget
	public final static String RETRIEVE_ANNOTATION = "check";
	// name of the annotation for a method the 'needs a press button'
	public final static String PRESS_ANNOTATION = "press";
	// prefix to the id of labels that only provide the return of a method
	public final static String RETURN_LABEL_PREFIX_ID = "return_label_";

	public static final String INSTANCE_LIST_ID = "instance_list_wiget";

	public static final String METHOD_BUTTON_PREFIX_ID = "method_button_widget";

	public static final String INSTANCE_WINDOW_SUFFIX_ID = "_instance_window";

	/**
	 * Returns the id of a input widget component of the interface
	 * 
	 * @param op
	 *            - Name of the operation in the specification
	 * @param numArg
	 *            - the argument's number
	 * @return - the id of the component
	 */
	public static String getInputComponentId(String op, int numArg)
	{
		return op + "_arg" + numArg;
	}

	/**
	 * Returns the id of a window that exposes a vdm++ class instance to the user
	 * 
	 * @param className
	 *            the name of the vdm++ class
	 * @return the id of the window
	 */
	public static String getObjectWindowId(String className)
	{
		return className + INSTANCE_WINDOW_SUFFIX_ID;
	}

	/**
	 * Given the id of a window fronting for a vdm++ class instance, returns the name of the underlying vdm++ class
	 * 
	 * @param objectWindowId
	 *            The id of the window representing a class instance
	 * @return The name of the VDM++ class
	 */
	public static String extractClassName(String objectWindowId)
	{
		return objectWindowId.substring(0, objectWindowId.indexOf("_"));
	}

	/**
	 * Returns the ID of a main window
	 * 
	 * @return the id of the main window
	 */
	public static String getMainWindowId()
	{
		return "mainWindow";
	}

	/**
	 * Returns the id of a button that calls the window that exposes a vdm++ class instance to the user
	 * 
	 * @param className
	 *            The name of the class
	 * @return the id of the button
	 */
	public static String getButtonWindowCallerId(String className)
	{
		return className + "_window_caller";
	}

	/**
	 * Returns the name to be given to a new class instance. The name is composed by a prefix, a number and the name of
	 * the class. The number is a count of total created instances.
	 * 
	 * @param className
	 *            The name of the class
	 * @return the name of the new instance
	 */
	public static String getInstanceName(String className)
	{
		++instanceNumber;
		return "instance_" + instanceNumber + "_" + className;
	}

	/**
	 * Naming policy for a panel/container holding the text input, button and etc. widgets generated for/by a vdm
	 * operation or function.
	 * 
	 * @param className
	 *            The name of the class the operation/functions belongs to.
	 * @param methodName
	 *            The name of the operation/function
	 * @return The id of the panel
	 */
	public static String getMethodsWidgetContainerId(String className,
			String methodName)
	{
		return "panel_" + methodName + "_" + className;
	}

	/**
	 * Naming policy for a panel/container holding the text input, combo, etc. widgets generated for collecting the
	 * arguments necessary for a constructor.
	 * 
	 * @param numConstructor
	 *            The name of the constructor
	 * @return The id of the panel/container
	 */
	public static String getConstWidgetsContainerId(int numConstructor)
	{
		return "panel_" + numConstructor + "_constructor";
	}

	/**
	 * Naming policy for the input widgets for constructor arguments
	 * 
	 * @param className
	 *            - The name of the constructor's class
	 * @param numConstructor
	 *            - The number of the constructor. The count begins at '0'. That is, '0' for the first constructor.
	 * @param numArg
	 *            - The number of the argument. '0' for the first argument, '1' for the second, etc.
	 * @return - Returns the id of the input widget
	 */
	public static String getConstructorWidgetId(String className,
			int numConstructor, int numArg)
	{
		return className + "_" + numArg + "_constructor_" + numArg;
	}

	/**
	 * Naming policy for output data widgets of operations/functions.
	 * 
	 * @param name
	 *            Name of the method
	 * @return The id of the label.
	 */
	public static String getMethodReturnLabelId(String name)
	{
		return RETURN_LABEL_PREFIX_ID + name;
	}

	/**
	 * Naming policy for buttons that call a function/operation of a vdm++ class
	 * 
	 * @param name
	 *            The name of the method.
	 * @return The id of the button.
	 */
	public static String getMethodButtonId(String name)
	{
		return METHOD_BUTTON_PREFIX_ID + name;
	}

	/**
	 * Naming policy for labels identifying the output data widget of vdm++ "methods"
	 * 
	 * @param name
	 *            The name of the vdm++ "method".
	 * @return The id of the label
	 */
	public static String getCheckMethodReturnLabelId(String name)
	{
		return "check_method_return_" + name;
	}

}

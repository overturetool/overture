package org.overture.ide.ui.partitioning;

import org.eclipse.dltk.ui.text.DLTKColorConstants;

public interface IVdmColorConstants
{
	/**
	 * The color key for string and character literals in Overture code.
	 */
	public static final String VDM_STRING = DLTKColorConstants.DLTK_STRING; //$NON-NLS-1$

	/**
	 * The color key for Overture comments.
	 */
	public static final String VDM_COMMENT = DLTKColorConstants.DLTK_SINGLE_LINE_COMMENT; //$NON-NLS-1$	

	/**
	 * The color key for Overture doc.
	 */
	public static final String VDM_DOC = "VDM_DOC"; //$NON-NLS-1$	

	/**
	 * The color key for Overture numbers.
	 */
	public static final String VDM_NUMBER = DLTKColorConstants.DLTK_NUMBER; //$NON-NLS-1$

	/**
	 * The color key for Overture keywords.
	 */
	public static final String VDM_KEYWORD = DLTKColorConstants.DLTK_KEYWORD; //$NON-NLS-1$

	/**
	 * The color key for Overture keyword 'return'.
	 */
	public static final String VDM_KEYWORD_RETURN = DLTKColorConstants.DLTK_KEYWORD_RETURN; //$NON-NLS-1$	

	/**
	 * The color key for Overture code.
	 */
	public static final String VDM_DEFAULT = DLTKColorConstants.DLTK_DEFAULT; //$NON-NLS-1$

	/**
	 * The color key for TO-DO tasks in comments.
	 */
	public static final String VDM_TODO_TAG = DLTKColorConstants.TASK_TAG;

	/**
	 * The color key for Overture function definition.
	 */
	public static final String VDM_FUNCTION_DEFINITION = DLTKColorConstants.DLTK_FUNCTION_DEFINITION;


	
	public static final String VDM_OPERATOR = "overture_operators";
	public static final String VDM_TYPE = "overture_types";
	public static final String VDM_CONSTANT = "overture_constants";
	public static final String VDM_FUNCTION = "overture_functions";
	public static final String VDM_PREDICATE = "overture_predicates";
}

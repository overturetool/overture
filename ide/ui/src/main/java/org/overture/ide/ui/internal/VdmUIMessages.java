package org.overture.ide.ui.internal;

import org.eclipse.osgi.util.NLS;

public class VdmUIMessages extends NLS{
	
	private static final String BUNDLE_NAME= "org.overture.ide.ui.internal.VdmUIMessages";//$NON-NLS-1$
	
	public static String VdmElementLabels_default_package;
	public static String VdmElementLabels_anonym_type;
	public static String VdmElementLabels_anonym;
	public static String VdmElementLabels_import_container;
	public static String VdmElementLabels_initializer;
	public static String VdmElementLabels_category;
	public static String VdmElementLabels_concat_string;
	public static String VdmElementLabels_comma_string;
	public static String VdmElementLabels_declseparator_string;
	public static String VdmElementLabels_category_separator_string;
	public static String ProblemMarkerManager_problem_marker_update_job_description;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, VdmUIMessages.class);
	}

}

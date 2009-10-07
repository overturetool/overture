package org.overture.ide.vdmsl.ui.internal.editor;

import org.eclipse.jface.text.IDocument;

public interface IVdmSlPartitions
{
	public final static String VDMSL_PARTITIONING = "__vdmsl_partitioning";
	 
	public final static String VDMSL_COMMENT = "__vdmsl_comment";
	public final static String VDMSL_STRING = "__vdmsl_string";
	public static final String VDMSL_DOC ="__vdmsl_doc";
	
	public final static String[] PYTHON_PARITION_TYPES = new String[] {
		IVdmSlPartitions.VDMSL_STRING, IVdmSlPartitions.VDMSL_COMMENT,
			IDocument.DEFAULT_CONTENT_TYPE,IVdmSlPartitions.VDMSL_DOC };
}

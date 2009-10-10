package org.overture.ide.vdmrt.ui.internal.editor;

import org.eclipse.jface.text.IDocument;

public interface IVdmRtPartitions
{
	public final static String vdmrt_PARTITIONING = "__vdmrt_partitioning";
	 
	public final static String vdmrt_COMMENT = "__vdmrt_comment";
	public final static String vdmrt_STRING = "__vdmrt_string";
	public static final String vdmrt_DOC ="__vdmrt_doc";
	
	public final static String[] PYTHON_PARITION_TYPES = new String[] {
		IVdmRtPartitions.vdmrt_STRING, IVdmRtPartitions.vdmrt_COMMENT,
			IDocument.DEFAULT_CONTENT_TYPE,IVdmRtPartitions.vdmrt_DOC };
}

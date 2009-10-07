package org.overture.ide.vdmpp.ui.internal.editor;

import org.eclipse.jface.text.IDocument;

public interface IVdmPpPartitions
{
	public final static String VDMPP_PARTITIONING = "__vdmpp_partitioning";
	 
	public final static String VDMPP_COMMENT = "__vdmpp_comment";
	public final static String VDMPP_STRING = "__vdmpp_string";
	public static final String VDMPP_DOC ="__vdmpp_doc";
	
	public final static String[] PYTHON_PARITION_TYPES = new String[] {
		IVdmPpPartitions.VDMPP_STRING, IVdmPpPartitions.VDMPP_COMMENT,
			IDocument.DEFAULT_CONTENT_TYPE,IVdmPpPartitions.VDMPP_DOC };
}

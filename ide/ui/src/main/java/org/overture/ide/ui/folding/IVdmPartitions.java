package org.overture.ide.ui.folding;

import org.eclipse.jface.text.IDocument;


public class IVdmPartitions {
	

		public final static String VDM_PARTITIONING = VdmConstants.VDM_PARTITIONING;
		
		public final static String VDM_COMMENT = "__overture_comment";		
		public final static String VDM_STRING = "__overture_string";
		public static final String VDM_DOC ="__overture_doc";

		public final static String[] OVERTURE_PARTITION_TYPES = new String[] {
			IDocument.DEFAULT_CONTENT_TYPE, IVdmPartitions.VDM_STRING,
			IVdmPartitions.VDM_COMMENT, IVdmPartitions.VDM_DOC
		};
	
}

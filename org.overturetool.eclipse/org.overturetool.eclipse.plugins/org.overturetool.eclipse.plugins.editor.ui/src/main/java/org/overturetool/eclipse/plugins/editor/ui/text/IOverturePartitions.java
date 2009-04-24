/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.ui.text;

import org.eclipse.jface.text.IDocument;
import org.overturetool.eclipse.plugins.editor.core.OvertureConstants;

public interface IOverturePartitions {

	public final static String OVERTURE_PARTITIONING = OvertureConstants.OVERTURE_PARTITIONING;
	
	public final static String OVERTURE_COMMENT = "__overture_comment";		
	public final static String OVERTURE_STRING = "__overture_string";
	public static final String OVERTURE_DOC ="__overture_doc";

	public final static String[] OVERTURE_PARTITION_TYPES = new String[] {
		IDocument.DEFAULT_CONTENT_TYPE, IOverturePartitions.OVERTURE_STRING,
		IOverturePartitions.OVERTURE_COMMENT, IOverturePartitions.OVERTURE_DOC
	};
}

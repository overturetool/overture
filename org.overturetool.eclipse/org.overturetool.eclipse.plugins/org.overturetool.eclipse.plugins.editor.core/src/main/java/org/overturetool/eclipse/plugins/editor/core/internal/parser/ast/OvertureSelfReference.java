/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
/*
 * (c) 2002, 2005 xored software and others all rights reserved. http://www.xored.com
 */

package org.overturetool.eclipse.plugins.editor.core.internal.parser.ast;

import org.eclipse.dltk.ast.references.Reference;
import org.eclipse.dltk.utils.CorePrinter;

public class OvertureSelfReference extends Reference {

	public OvertureSelfReference(int start, int end) {
		super(start, end);
	}

	public String getStringRepresentation() {
		return "self"; //$NON-NLS-1$
	}

	public void printNode(CorePrinter output) {
		output.formatPrintLn("SelfReference" + this.getSourceRange().toString()); //$NON-NLS-1$
	}

	public String toString() {
		return "self"; //$NON-NLS-1$
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof OvertureSelfReference)) {
			return false;
		}
		OvertureSelfReference d = (OvertureSelfReference) obj;
		return sourceStart() == d.sourceStart() && sourceEnd() == d.sourceEnd();
	}

	public int hashCode() {
		return sourceStart() ^ sourceEnd();
	}
}

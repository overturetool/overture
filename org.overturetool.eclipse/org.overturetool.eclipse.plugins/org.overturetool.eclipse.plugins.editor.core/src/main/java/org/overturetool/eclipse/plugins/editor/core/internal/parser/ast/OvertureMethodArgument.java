/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.core.internal.parser.ast;

import org.eclipse.dltk.ast.declarations.Argument;

public class OvertureMethodArgument extends Argument {
	

	public final static int SIMPLE = 0;
	
	public final static int VARARG = 1;
	
	public final static int BLOCK = 2;

}

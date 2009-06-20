/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.core.mixin.IMixinParser;
import org.eclipse.dltk.core.mixin.IMixinRequestor;

public class OvertureMixin implements IMixinParser {

	// suffix for instance classes
	public final static String INSTANCE_SUFFIX = "%"; //$NON-NLS-1$

	// suffix for virtual classes
	public final static String VIRTUAL_SUFFIX = "%v"; //$NON-NLS-1$

	private IMixinRequestor requestor;

	public void parserSourceModule(boolean signature, ISourceModule module) {
		try {
			ModuleDeclaration moduleDeclaration = SourceParserUtil
					.getModuleDeclaration(module);
			OvertureMixinBuildVisitor visitor = new OvertureMixinBuildVisitor(
					moduleDeclaration, module, signature, requestor);
			moduleDeclaration.traverse(visitor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setRequirestor(IMixinRequestor requestor) {
		this.requestor = requestor;
	}
}

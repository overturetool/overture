/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin;

/**
 * This class is descendant of OvertureMixinClass specified for overture "Object" class.
 * The idea was to additionally search for the top-level methods (defined at the
 * source module scope), but that is not needed anymore since now they are
 * indexed as part of the "Object" class.
 * 
 * @see org.eclipse.dltk.ruby.internal.parser.mixin.RubyMixinBuildVisitor.SourceModuleScope
 */
public class OvertureObjectMixinClass extends OvertureMixinClass {

	public OvertureObjectMixinClass(OvertureMixinModel model, boolean meta) {
		super(model, meta ? OvertureMixinUtils.OBJECT : OvertureMixinUtils.OBJECT_INSTANCE, false);
	}

	public OvertureMixinVariable[] getFields() {
		return new OvertureMixinVariable[0];
	}

}

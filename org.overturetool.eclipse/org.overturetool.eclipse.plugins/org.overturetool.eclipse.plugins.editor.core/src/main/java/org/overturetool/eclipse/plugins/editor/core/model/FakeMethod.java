/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.core.model;

import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.ModelElement;
import org.eclipse.dltk.internal.core.SourceMethod;
import org.eclipse.dltk.internal.core.SourceRange;

public class FakeMethod extends SourceMethod {

	private String receiver;

	private String[] parameters = CharOperation.NO_STRINGS;

	private String[] parameterInitializers = CharOperation.NO_STRINGS;

	private int flags;

	private int offset;

	private int length;

	private boolean hasSpecialOffsets = false;

	private int nameOffset;

	private int nameLength;

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public FakeMethod(ModelElement parent, String name) {
		super(parent, name);
	}

	public FakeMethod(ModelElement parent, String name, int offset, int length,
			int nameOffset, int nameLength) {
		super(parent, name);
		this.offset = offset;
		this.length = length;
		this.nameOffset = nameOffset;
		this.nameLength = nameLength;
		hasSpecialOffsets = true;
	}

	public ISourceRange getNameRange() throws ModelException {
		if (hasSpecialOffsets)
			return new SourceRange(nameOffset, nameLength);
		return super.getNameRange();
	}

	public ISourceRange getSourceRange() throws ModelException {
		if (hasSpecialOffsets)
			return new SourceRange(offset, length);
		return super.getSourceRange();
	}

	public IScriptProject getScriptProject() {
		return parent.getScriptProject();
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public void setParameterInitializers(String[] parameterInitializers) {
		this.parameterInitializers = parameterInitializers;
	}

	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}

	public String[] getParameterInitializers() throws ModelException {
		return parameterInitializers;
	}

	public String[] getParameters() throws ModelException {
		return parameters;
	}

}

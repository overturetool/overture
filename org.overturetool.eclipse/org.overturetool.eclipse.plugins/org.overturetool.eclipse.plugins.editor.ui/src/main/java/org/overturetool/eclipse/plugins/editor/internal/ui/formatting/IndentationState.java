/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.formatting;

import java.util.Map;

public class IndentationState {
	private String lastIndentationBasedOnLevel = "";
	private int indentationLevel ;
	private int offset ;
	private int pos ;
	
	//	indentation for parameter over multiple lines, like
	// method(param1
	// .......param2)
	private int fixIndentation ; 
	
	private String unformattedText ;

	
	public IndentationState(String unformattedText, int offset, int initialIndentLevel) {
		this.unformattedText = unformattedText ;
		this.offset = offset ;
		indentationLevel = initialIndentLevel ;		
		pos = 0 ;
		resetFixIndentation() ;		
	}
	
	public void decIndentationLevel() {
		indentationLevel -= 1 ;
		resetFixIndentation() ;		
	}
	
	
	public void incIndentationLevel() {
		indentationLevel += 1 ;	
		resetFixIndentation() ;
	}
	
	public void incPos(int increment) {
		pos += increment ;
	}
	
	public void resetFixIndentation() {
		fixIndentation = -1 ; 	
	}


	public int getIndentation() {
		return fixIndentation;
	}


	public int getIndentationLevel() {
		return indentationLevel;
	}


	public int getOffset() {
		return offset;
	}


	public int getPos() {
		return pos;
	}


	public String getUnformattedText() {
		return unformattedText;
	}


	public void setFixIndentation(int indentation) {
		this.fixIndentation = indentation;
	}


	public void setIndentationLevel(int indentationLevel) {
		this.indentationLevel = indentationLevel;
		this.resetFixIndentation() ;
	}


	public void setOffset(int offset) {
		this.offset = offset;
		this.resetFixIndentation() ;
	}


	public void setPos(int pos) {
		this.pos = pos;
	}
	
	protected String getIndentationString(Map options) {
		StringBuffer sb = new StringBuffer() ;
        for (int i = 0; i < this.getOffset(); i++) {
            sb.append(" ");
        }
		if (this.getIndentation() != -1) {
			sb.append(lastIndentationBasedOnLevel) ;		
			sb.append(Indents.createFixIndentString(this.getIndentation(), options));
		}
		else {
			lastIndentationBasedOnLevel = Indents.createIndentString(this.getIndentationLevel(), options);
			sb.append(lastIndentationBasedOnLevel) ;
		}
		return sb.toString() ;
	}
	

}

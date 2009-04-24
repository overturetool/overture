/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
/**
 * 
 */
package org.overturetool.eclipse.plugins.editor.internal.ui.text;

import org.eclipse.jface.text.rules.IWordDetector;

class OvertureDecoratorDetector implements IWordDetector{

    /**
     * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
     */
    public boolean isWordStart(char c) {
        return c == '@';
    }

    /**
     * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
     */
    public boolean isWordPart(char c) {
		return c != '\n' && c != '\r' && c != '(';
    }
    
}

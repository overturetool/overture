/*
 * #%~
 * org.overture.ide.ui
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.ui.editor.core;


import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.widgets.Composite;

public class VdmSourceViewer extends SourceViewer{

	//private VdmEditor editor = null;
	private IReconciler reconciler = null;
	
	public VdmSourceViewer(Composite parent, IVerticalRuler ruler,
			IOverviewRuler overviewRuler, boolean overviewRulerVisible,
			int styles) {
		super(parent,ruler,overviewRuler,overviewRulerVisible,styles);
		this.showAnnotations(true);
		
//		setPrefixForComments();					
	}

	
	


	public Object getReconciler() {
		return this.reconciler;
	}

	public void setReconciler(IReconciler reconciler) {
		this.reconciler = reconciler;
		
	}

	
}

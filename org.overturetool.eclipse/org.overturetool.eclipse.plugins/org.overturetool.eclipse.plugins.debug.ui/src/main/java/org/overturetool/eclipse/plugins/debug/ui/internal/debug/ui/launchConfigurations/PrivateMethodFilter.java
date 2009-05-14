package org.overturetool.eclipse.plugins.debug.ui.internal.debug.ui.launchConfigurations;

import org.eclipse.dltk.core.Flags;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;;


/**
 * Filter for the methods viewer.
 * Changing a filter property does not trigger a refiltering of the viewer
 */
public class PrivateMethodFilter extends ViewerFilter {
	
	public static final int FILTER_PRIVATEMETHODS= 1;
	public static final int FILTER_PUBLICMETHODS= 2;
	
	private int fFilterProperties;


	/**
	 * Modifies filter and add a property to filter for
	 */
	public final void addFilter(int filter) {
		fFilterProperties |= filter;
	}
	/**
	 * Modifies filter and remove a property to filter for
	 */	
	public final void removeFilter(int filter) {
		fFilterProperties &= (-1 ^ filter);
	}
	/**
	 * Tests if a property is filtered
	 */		
	public final boolean hasFilter(int filter) {
		return (fFilterProperties & filter) != 0;
	}
	
	/*
	 * @see ViewerFilter#isFilterProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isFilterProperty(Object element, Object property) {
		return false;
	}
	/*
	 * @see ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */		
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IMember) {
			IMember member= (IMember) element;
			int memberType= member.getElementType();	
			System.out.println(member.toString());

			if (hasFilter(FILTER_PRIVATEMETHODS) && memberType == IModelElement.METHOD && isPrivateMethod(member)){
				return false;
			}
			if (hasFilter(FILTER_PUBLICMETHODS) && memberType == IModelElement.METHOD && !isPrivateMethod(member)){
				return false;
			}
		}
		return true;
	}

	
	private boolean isPrivateMethod(IMember IMember) {
		try { 
			return Flags.isPrivate(IMember.getFlags());
		} catch (ModelException e) {			
			return false;
		}
	}
}



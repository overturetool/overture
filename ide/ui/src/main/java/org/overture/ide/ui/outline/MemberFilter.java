package org.overture.ide.ui.outline;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.InstanceVariableDefinition;
import org.overturetool.vdmj.definitions.TypeDefinition;
import org.overturetool.vdmj.lex.Token;

public class MemberFilter extends ViewerFilter {

	public static final int FILTER_NONPUBLIC= 1;
	public static final int FILTER_STATIC= 2;
	public static final int FILTER_FIELDS= 4;
	public static final int FILTER_LOCALTYPES= 8;

	private int fFilterProperties;


	/**
	 * Modifies filter and add a property to filter for
	 * @param filter the filter to add
	 */
	public final void addFilter(int filter) {
		fFilterProperties |= filter;
	}
	/**
	 * Modifies filter and remove a property to filter for
	 * @param filter the filter to remove
	 */
	public final void removeFilter(int filter) {
		fFilterProperties &= (-1 ^ filter);
	}
	/**
	 * Tests if a property is filtered
	 * @param filter the filter to test
	 * @return returns the result of the test
	 */
	public final boolean hasFilter(int filter) {
		return (fFilterProperties & filter) != 0;
	}

	/*
	 * @see ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		
		if(hasFilter(FILTER_FIELDS) && isField(element) )
		{
			return false;
		}
		
		if(hasFilter(FILTER_NONPUBLIC) && isNonPublic(element)){
			return false;
		}
		
		if(hasFilter(FILTER_LOCALTYPES) && isLocalType(element)){
			return false;
		}
		
		if(hasFilter(FILTER_STATIC) && isStatic(element)){
			return false;
		}
		
//		try {
//			if (element instanceof IMember) {
//				
//
//				if (hasFilter(FILTER_FIELDS) && memberType == IJavaElement.FIELD) {
//					return false;
//				}
//
//				if (hasFilter(FILTER_LOCALTYPES) && memberType == IJavaElement.TYPE && isLocalType((IType) member)) {
//					return false;
//				}
//
//				if (member.getElementName().startsWith("<")) { // filter out <clinit> //$NON-NLS-1$
//					return false;
//				}
//				int flags= member.getFlags();
//				if (hasFilter(FILTER_STATIC) && (Flags.isStatic(flags) || isFieldInInterfaceOrAnnotation(member)) && memberType != IJavaElement.TYPE) {
//					return false;
//				}
//				if (hasFilter(FILTER_NONPUBLIC) && !Flags.isPublic(flags) && !isMemberInInterfaceOrAnnotation(member) && !isTopLevelType(member) && !isEnumConstant(member)) {
//					return false;
//				}
//			}
//		} catch (JavaModelException e) {
//			// ignore
//		}
		return true;
	}

	private boolean isStatic(Object element) {
		if(element instanceof Definition){
			return ((Definition) element).isStatic();
		}
		return false;
	}
	private boolean isLocalType(Object element) {
		if(element instanceof TypeDefinition){
			return true;
		}
		return false;
	}
	private boolean isNonPublic(Object element) {
		if(element instanceof Definition){
			return !((Definition)element).isAccess(Token.PUBLIC);
		}
		return false;
	}
	private boolean isField(Object element){
		return (element instanceof InstanceVariableDefinition);
	}
	
	
	
//	private boolean isLocalType(IType type) {
//		IJavaElement parent= type.getParent();
//		return parent instanceof IMember && !(parent instanceof IType);
//	}
//
//	private boolean isMemberInInterfaceOrAnnotation(IMember member) throws JavaModelException {
//		IType parent= member.getDeclaringType();
//		return parent != null && JavaModelUtil.isInterfaceOrAnnotation(parent);
//	}
//
//	private boolean isFieldInInterfaceOrAnnotation(IMember member) throws JavaModelException {
//		return (member.getElementType() == IJavaElement.FIELD) && JavaModelUtil.isInterfaceOrAnnotation(member.getDeclaringType());
//	}
//
//	private boolean isTopLevelType(IMember member) {
//		IType parent= member.getDeclaringType();
//		return parent == null;
//	}
//
//	private boolean isEnumConstant(IMember member) throws JavaModelException {
//		return (member.getElementType() == IJavaElement.FIELD) && ((IField)member).isEnumConstant();
//	}
}

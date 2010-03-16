package org.overture.ide.core;



/**
 * Common protocol for Java elements that contain other Java elements.
 *
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IParent {
/**
 * Returns the immediate children of this element.
 * Unless otherwise specified by the implementing element,
 * the children are in no particular order.
 *
 * @exception JavaModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource
 * @return the immediate children of this element
 */
IVdmElement[] getChildren() throws VdmModelException;
/**
 * Returns whether this element has one or more immediate children.
 * This is a convenience method, and may be more efficient than
 * testing whether <code>getChildren</code> is an empty array.
 *
 * @exception JavaModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource
 * @return true if the immediate children of this element, false otherwise
 */
boolean hasChildren() throws VdmModelException;
}

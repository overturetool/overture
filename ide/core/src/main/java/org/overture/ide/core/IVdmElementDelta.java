package org.overture.ide.core;



public interface IVdmElementDelta
{
	/**
	 * Status constant indicating that the element has been added.
	 * Note that an added java element delta has no children, as they are all implicitely added.
	 */
	public int ADDED = 1;

	/**
	 * Status constant indicating that the element has been removed.
	 * Note that a removed java element delta has no children, as they are all implicitely removed.
	 */
	public int REMOVED = 2;

	/**
	 * Status constant indicating that the element has been changed,
	 * as described by the change flags.
	 *
	 * @see #getFlags()
	 */
	public int CHANGED = 4;

	/**
	 * Change flag indicating that the content of the element has changed.
	 * This flag is only valid for elements which correspond to files.
	 */
	public int F_CONTENT = 0x000001;

	/**
	 * Change flag indicating that the modifiers of the element have changed.
	 * This flag is only valid if the element is an {@link IMember}.
	 */
	public int F_MODIFIERS = 0x000002;

	/**
	 * Change flag indicating that there are changes to the children of the element.
	 * This flag is only valid if the element is an {@link IParent}.
	 */
	public int F_CHILDREN = 0x000008;

	/**
	 * Change flag indicating that the element was moved from another location.
	 * The location of the old element can be retrieved using {@link #getMovedFromElement}.
	 */
	public int F_MOVED_FROM = 0x000010;

	/**
	 * Change flag indicating that the element was moved to another location.
	 * The location of the new element can be retrieved using {@link #getMovedToElement}.
	 */
	public int F_MOVED_TO = 0x000020;

	
	/**
	 * Change flag indicating that the underlying {@link org.eclipse.core.resources.IProject} has been
	 * closed. This flag is only valid if the element is an {@link IJavaProject}.
	 */
	public int F_CLOSED = 0x000400;
	
	/**
	 * Change flag indicating that the annotations of the element have changed.
	 * Use {@link #getAnnotationDeltas()} to get the added/removed/changed annotations.
	 * This flag is only valid if the element is an {@link IAnnotatable}.
	 *
	 * @since 3.4
	 */
	public int F_ANNOTATIONS = 0x400000;
	
	/**
	 * Change flag indicating that the resource has been type checked
	 */
	public int F_TYPE_CHECKED = 3;
	
	/**
	 * Returns the element that this delta describes a change to.
	 * @return the element that this delta describes a change to
	 */
	public IVdmElement getElement();
	
	
	/**
	 * Returns the kind of this delta - one of {@link #ADDED}, {@link #REMOVED},
	 * or {@link #CHANGED}.
	 *
	 * @return the kind of this delta
	 */
	public int getKind();
}

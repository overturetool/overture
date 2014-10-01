/*
 * #%~
 * org.overture.ide.core
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
package org.overture.ide.core;

import java.util.EventObject;

/**
 * An element changed event describes a change to the structure or contents of a tree of Vdm elements. The
 * changes to the elements are described by the associated delta object carried by this event.
 * <p>
 * This class is not intended to be instantiated or subclassed by clients. Instances of this class are
 * automatically created by the Vdm model.
 * </p>
 * 
 * @see IElementChangedListener
 * @see IVdmElementDelta
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @noextend This class is not intended to be subclassed by clients.
 */
public class ElementChangedEvent extends EventObject
{
	public enum DeltaType {

		/**
		 * Event type constant (bit mask) indicating an after-the-fact report of creations, deletions, and
		 * modifications to one or more Vdm element(s) expressed as a hierarchical java element delta as
		 * returned by <code>getDelta()</code>.
		 * 
		 * Note: this notification occurs during the corresponding POST_CHANGE resource change notification,
		 * and contains a full delta accounting for any VdmModel operation and/or resource change.
		 * 
		 * @see IVdmElementDelta
		 * @see org.eclipse.core.resources.IResourceChangeEvent
		 * @see #getDelta()
		 * @since 2.0
		 */
		POST_CHANGE(1),

		/**
		 * Event type constant (bit mask) indicating an after-the-fact report of creations, deletions, and
		 * modifications to one or more Vdm element(s) expressed as a hierarchical java element delta as
		 * returned by <code>getDelta</code>.
		 * 
		 * Note: this notification occurs during the corresponding PRE_AUTO_BUILD resource change
		 * notification. The delta, which is notified here, only contains information relative to the previous
		 * VdmModel operations (in other words, it ignores the possible resources which have changed outside
		 * Vdm operations). In particular, it is possible that the VdmModel be inconsistent with respect to
		 * resources, which got modified outside VdmModel operations (it will only be fully consistent once
		 * the POST_CHANGE notification has occurred).
		 * 
		 * @see IVdmElementDelta
		 * @see org.eclipse.core.resources.IResourceChangeEvent
		 * @see #getDelta()
		 * @since 2.0
		 * @deprecated - no longer used, such deltas are now notified during POST_CHANGE
		 */
		PRE_AUTO_BUILD(2),

		/**
		 * Event type constant (bit mask) indicating an after-the-fact report of creations, deletions, and
		 * modifications to one or more Vdm element(s) expressed as a hierarchical java element delta as
		 * returned by <code>getDelta</code>.
		 * 
		 * Note: this notification occurs as a result of a working copy reconcile operation.
		 * 
		 * @see IVdmElementDelta
		 * @see org.eclipse.core.resources.IResourceChangeEvent
		 * @see #getDelta()
		 * @since 2.0
		 */
		POST_RECONCILE(4), DEFAULT_CHANGE_EVENT(0),
		
		
		/**
		 * Event type constant (bit mask) indicating an after-the-fact 
		 * report of a build. The event contains a hierarchical resource delta
		 * as returned by <code>getDelta</code>.
		 * See class comments for further details.
		 *
		 * @see #getBuildKind()
		 * @see #getSource()
		 * @since 3.0
		 */
		 POST_BUILD(16);

//		private final int value;

		private DeltaType(int value) {
//			this.value = value;
		}
		
		
	}

	private static final long serialVersionUID = -8947240431612844420L; // backward compatible

	/*
	 * Event type indicating the nature of this event. It can be a combination either: - POST_CHANGE -
	 * PRE_AUTO_BUILD - POST_RECONCILE
	 */
	private DeltaType type;

	/**
	 * Creates an new element changed event (based on a <code>IVdmElementDelta</code>).
	 * 
	 * @param delta
	 *            the Vdm element delta.
	 * @param type
	 *            the type of delta (ADDED, REMOVED, CHANGED) this event contains
	 */
	public ElementChangedEvent(IVdmElementDelta delta, DeltaType type) {
		super(delta);
		this.type = type;
	}

	/**
	 * Returns the delta describing the change.
	 * 
	 * @return the delta describing the change
	 */
	public IVdmElementDelta getDelta()
	{
		return (IVdmElementDelta) this.source;
	}

	/**
	 * Returns the type of event being reported.
	 * 
	 * @return one of the event type constants
//	 * @see #POST_CHANGE
//	 * @see #PRE_AUTO_BUILD
//	 * @see #POST_RECONCILE
	 * @since 2.0
	 */
	public DeltaType getType()
	{
		return this.type;
	}
}

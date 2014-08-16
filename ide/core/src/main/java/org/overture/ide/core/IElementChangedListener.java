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



/**
 * An element changed listener receives notification of changes to Java elements
 * maintained by the Java model.
 * <p>
 * This interface may be implemented by clients.
 * </p>
 */
public interface IElementChangedListener {

/**
 * Notifies that one or more attributes of one or more Java elements have changed.
 * The specific details of the change are described by the given event.
 *
 * @param event the change event
 */
public void elementChanged(ElementChangedEvent event);
}

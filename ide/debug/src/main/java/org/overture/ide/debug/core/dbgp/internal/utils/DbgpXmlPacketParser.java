/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.core.dbgp.internal.utils;

import org.overture.ide.debug.core.dbgp.internal.packets.DbgpNotifyPacket;
import org.overture.ide.debug.core.dbgp.internal.packets.DbgpResponsePacket;
import org.overture.ide.debug.core.dbgp.internal.packets.DbgpStreamPacket;
import org.w3c.dom.Element;

public class DbgpXmlPacketParser extends DbgpXmlParser
{
	protected DbgpXmlPacketParser()
	{

	}

	public static DbgpResponsePacket parseResponsePacket(Element element)
	{
		final String ATTR_TRANSACTION_ID = "transaction_id"; //$NON-NLS-1$

		int id = Integer.parseInt(element.getAttribute(ATTR_TRANSACTION_ID));
		return new DbgpResponsePacket(element, id);

	}

	public static DbgpNotifyPacket parseNotifyPacket(Element element)
	{
		final String ATTR_NAME = "name"; //$NON-NLS-1$

		String name = element.getAttribute(ATTR_NAME);
		return new DbgpNotifyPacket(element, name);
	}

	public static DbgpStreamPacket parseStreamPacket(Element element)
	{
		final String ATTR_TYPE = "type"; //$NON-NLS-1$

		String type = element.getAttribute(ATTR_TYPE);
		String textContent = DbgpXmlParser.parseBase64Content(element);
		return new DbgpStreamPacket(type, textContent, element);
	}
}

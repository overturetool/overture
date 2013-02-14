/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.core.parser;

import org.overture.ide.core.resources.IVdmSourceUnit;



public interface ISourceParser {

	/**
	 * Parse a single file
	 * @param file the file to be parsed
	 */
	void parse(IVdmSourceUnit file);
	
	/**
	 * Parse a single file where the content is parsed and the file is set as the source file
	 * @param file the file to be set as source
	 * @param content the content to be parsed
	 */
	void parse(IVdmSourceUnit file,String content);

}

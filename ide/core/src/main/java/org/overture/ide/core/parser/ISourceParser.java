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
	 * @param forceAstUpdate if true the internal source file ast is updated
	 */
	void parse(IVdmSourceUnit file,String content, boolean forceAstUpdate);

}

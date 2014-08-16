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
package org.overture.ide.ui.editor.partitioning;

public interface IVdmPartitions
{

	
	public final static String VDM_PARTITIONING = "__vdm__partitioning__";
	
	// string constants for different partition types
	public final static String MULTILINE_COMMENT = "__vdm_multiline_comment";
	public final static String SINGLELINE_COMMENT = "__vdm_singleline_comment";
	public final static String STRING = "__vdm_string";
	//public final static String LATEX = "__vdm_latex";
	
}

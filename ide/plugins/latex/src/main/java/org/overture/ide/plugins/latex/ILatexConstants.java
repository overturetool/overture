/*
 * #%~
 * org.overture.ide.plugins.latex
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
package org.overture.ide.plugins.latex;

public interface ILatexConstants
{
	public final static String LATEX_MAIN_DOCUMENT = "LATEX_MAIN_DOCUMENT";
	public final static String LATEX_GENERATE_MAIN_DOCUMENT = "LATEX_GENERATE_MAIN_DOCUMENT";
	public final static String LATEX_INCLUDE_COVERAGETABLE = "LATEX_INCLUDE_COVERAGETABLE";
	public final static String LATEX_MARK_COVERAGE = "LATEX_MARK_COVERAGE";
	public final static String LATEX_MODEL_ONLY = "LATEX_MODEL_ONLY";
	public static final String QUALIFIER = "LATEX";

	public static final String DEFAULT_OSX_LATEX_PATH = "/Library/TeX/texbin/pdflatex";
	public static final String OSX_LATEX_PATH_PREFERENCE = "__OSX_LATEX_PATH_PREFERENCE__";
	
	public static final String PDF_BUILDER = "PDF_BUILDER".toLowerCase();
	public static final String DEFAULT_PDF_BUILDER = "pdflatex";
	public static final boolean LATEX_MARK_COVERAGE_DEFAULT = false;
	public static final boolean LATEX_INCLUDE_COVERAGETABLE_DEFAULT = false;

}

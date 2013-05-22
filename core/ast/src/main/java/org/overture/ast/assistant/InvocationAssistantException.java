/*******************************************************************************
 * Copyright (c) 2013 Overture Team.
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
package org.overture.ast.assistant;

import org.overture.ast.analysis.AnalysisException;

/**
 * @author Joey Coleman <jwc@iha.dk>
 *
 */
public class InvocationAssistantException extends AnalysisException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public InvocationAssistantException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public InvocationAssistantException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvocationAssistantException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public InvocationAssistantException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}

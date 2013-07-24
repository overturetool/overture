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
package org.overture.tools.examplepackager;

public interface OvertureProject
{
	public final String NATURE_SPACEHOLDER = "NATURE_ID";
	public final String NAME_PLACEHOLDER = "NAME_ID";
	public final String TEX_DOCUMENT = "DOCUMENT_VALUE";
	public final String ARGUMENTS_PLACEHOLDER = "ARGUMENTS_PLACEHOLDER";
	public final String EclipseProject = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<projectDescription>\n" + "	<name>"
			+ NAME_PLACEHOLDER
			+ "</name>\n"
			+ "	<comment></comment>\n"
			+ "	<projects>\n"
			+ "	</projects>\n"
			+ "	<buildSpec>\n"
			+ "		<buildCommand>\n"
			+ "			<name>org.overture.ide.core.builder.VdmBuilder</name>\n"
			+ "			<arguments>\nARGUMENTS_PLACEHOLDER\n"
			+ "			</arguments>\n"
			+ "		</buildCommand>\n"
			+ "		<buildCommand>\n"
			+ "			<name>org.overture.ide.plugins.latex.builder</name>\n"
			+ "			<arguments>\n"
			+ "				<dictionary>\n"
			+ "					<key>DOCUMENT</key>\n"
			+ "					<value>DOCUMENT_VALUE</value>\n"
			+ "				</dictionary>\n"
			+ "			</arguments>\n"
			+ "		</buildCommand>\n"
			+ "	</buildSpec>\n"
			+ "	<natures>\n"
			+ "		<nature>"
			+ NATURE_SPACEHOLDER
			+ "</nature>\n" + "	</natures>\n" + "</projectDescription>";

	public final String VDMPP_NATURE = "org.overture.ide.vdmpp.core.nature";
	public final String VDMSL_NATURE = "org.overture.ide.vdmsl.core.nature";
	public final String VDMRT_NATURE = "org.overture.ide.vdmrt.core.nature";

}

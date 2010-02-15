package org.overturetool.tools.packworkspace;

public interface OvertureProject {
	public final String NATURE_SPACEHOLDER ="NATURE_ID";
	public final String NAME_PLACEHOLDER="NAME_ID";
	public final String TEX_DOCUMENT="DOCUMENT_VALUE";
	public final String ARGUMENTS_PLACEHOLDER="ARGUMENTS_PLACEHOLDER";
	public final String EclipseProject = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<projectDescription>\n"
			+ "	<name>"+NAME_PLACEHOLDER+"</name>\n"
			+ "	<comment></comment>\n"
			+ "	<projects>\n"
			+ "	</projects>\n"
			+ "	<buildSpec>\n"
			+ "		<buildCommand>\n"
			+ "			<name>org.eclipse.dltk.core.scriptbuilder</name>\n"
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
			+ "		<nature>"+NATURE_SPACEHOLDER+"</nature>\n"
			+ "	</natures>\n" + "</projectDescription>";
	
	public final String VDMPP_NATURE ="org.overture.ide.vdmpp.core.nature";
	public final String VDMSL_NATURE ="org.overture.ide.vdmsl.core.nature";
	public final String VDMRT_NATURE ="org.overture.ide.vdmrt.core.nature";
	
	
}

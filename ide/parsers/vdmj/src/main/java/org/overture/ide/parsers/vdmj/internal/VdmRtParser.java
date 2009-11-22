package org.overture.ide.parsers.vdmj.internal;

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;

public class VdmRtParser extends VdmPpParser {

	public VdmRtParser(String nature) {
		super(nature);
	Settings.dialect= Dialect.VDM_RT;
	}
}

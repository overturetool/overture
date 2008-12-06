package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlMethodSpecifications extends IJmlNode
{
	abstract IJmlSpecs getSpecs() throws CGException;
	abstract IJmlSpecs getAlso() throws CGException;
}


package org.overturetool.jml.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlDocument
{
	abstract String getFilename() throws CGException;
	abstract Boolean hasSpecifications() throws CGException;
	abstract IJmlSpecifications getSpecifications() throws CGException;
	abstract Vector getLexems() throws CGException;
	abstract String toVdmSlValue() throws CGException;
	abstract String toVdmPpValue() throws CGException;
	abstract void accept(IJmlVisitor theVisitor) throws CGException;
}


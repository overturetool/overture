package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlDocument
{
	abstract String getFilename() throws CGException;
	abstract Boolean hasSpecifications() throws CGException;
	abstract IOmlSpecifications getSpecifications() throws CGException;
	abstract Boolean hasExpression() throws CGException;
	abstract IOmlExpression getExpression() throws CGException;
	abstract Vector getLexems() throws CGException;
	abstract String toVdmSlValue() throws CGException;
	abstract String toVdmPpValue() throws CGException;
	abstract void accept(IOmlVisitor theVisitor) throws CGException;
}


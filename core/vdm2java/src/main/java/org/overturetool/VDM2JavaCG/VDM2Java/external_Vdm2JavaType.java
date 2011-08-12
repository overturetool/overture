package org.overturetool.VDM2JavaCG.VDM2Java;

import jp.co.csk.vdm.toolbox.VDM.*;
import java.util.*;
import org.overturetool.vdmj.types.FunctionType;



public class external_Vdm2JavaType {

Vdm2JavaType parent = null;

public external_Vdm2JavaType (Vdm2JavaType parentVdm2JavaType) {
 parent = parentVdm2JavaType;
}

public external_Vdm2JavaType () {}

public FunctionType impl_GenFnType (@SuppressWarnings("rawtypes") final Vector pattype_ul, @SuppressWarnings("rawtypes") final Vector restype_ul) throws CGException {

 UTIL.RunTime("Run-Time Error:Implicit Function GenFnType has been called");
 return null;//new IOmlPartialFunctionType();
}

}

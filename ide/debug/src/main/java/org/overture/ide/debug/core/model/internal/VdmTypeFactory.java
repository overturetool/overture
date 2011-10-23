package org.overture.ide.debug.core.model.internal;

import org.overture.ide.debug.core.model.ArrayVdmType;
import org.overture.ide.debug.core.model.AtomicVdmType;
import org.overture.ide.debug.core.model.ComplexVdmType;
import org.overture.ide.debug.core.model.HashVdmType;
import org.overture.ide.debug.core.model.IVdmType;
import org.overture.ide.debug.core.model.IVdmTypeFactory;
import org.overture.ide.debug.core.model.SetVdmType;

public class VdmTypeFactory implements IVdmTypeFactory {

	public IVdmType buildType(String type) {
		
		if(type.equals("bool")){
			return new AtomicVdmType(type);	
		} else
		if(type.equals("nat")){
			return new AtomicVdmType(type);	
		} else
		if(type.equals("real")){
			return new AtomicVdmType(type);	
		} else
		if(type.equals("char")){
			return new AtomicVdmType(type);	
		} else
		if(type.equals("nat1")){
			return new AtomicVdmType(type);		
		} else
		if(type.equals("map")){
			return new HashVdmType();
		} else
		if(type.equals("seq")){
			return new ArrayVdmType();
		} else
		if(type.equals("set")){
			return new SetVdmType();
		}
		
		else{
			return new ComplexVdmType(type);
		}
		//System.out.println("VdmTypeFactory.buildType");
//		return null;
	}

}

package org.overture.codegen.vdm2cpp.typesorter;

public class TypeContainer {
	
	private String name;
	private String enclosing_class;
	
	TypeContainer(String typename, String typeClass)
	{
		name = typename;
		enclosing_class = typeClass;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof TypeContainer)
		{
			TypeContainer t_other = (TypeContainer) obj;
			if(t_other.enclosing_class.equals(this.enclosing_class) 
					&& t_other.name.equals(this.name) )
			{
				return true;
			}
		}
		return false;			
	}
	
	@Override
	public int hashCode() {
		
		int hashcode = 0;
		
		if(name != null)
			hashcode += name.hashCode();
		
		if(enclosing_class != null)
			hashcode += enclosing_class.hashCode();
		
		return hashcode;
	}
}

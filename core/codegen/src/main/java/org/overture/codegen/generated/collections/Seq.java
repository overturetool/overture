package org.overture.codegen.generated.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class Seq<T> extends Vector<T>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7647880993052986425L;
	private List<T> sequence;
	
	
	public Seq()
	{
		 sequence = new ArrayList<>();
	}
	
	
	public Seq(T... elements)
	{
		this();
		
		for (T t : elements)
		{
			sequence.add(t);
		}
	}
	
	
}

package org.overture.codegen.generated.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class Seq<T> extends Vector<T>
{

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
	
	public T hd()
	{
		if(sequence.size() <= 0)
			throw new SeqException("");

		return sequence.get(0);
	}
	
}

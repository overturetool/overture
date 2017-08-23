package org.overture.ast.lex;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;


public class CoverageUtil {
	
	/** A collection of all ILexLocation objects. */
	private final List<ILexLocation> allLocations;
	
	/** A map of class names to their lexical span, for coverage. */
	private final Map<LexNameToken, ILexLocation> nameSpans;


	public CoverageUtil(List<ILexLocation> allLocations , Map<LexNameToken, ILexLocation> nameSpans) {
		this.allLocations = removeDuplicates(allLocations);
		this.nameSpans = nameSpans;
	}

	
	// TODO: Comment 
	public  LexNameList getSpanNames(File filename)
	{
		LexNameList list = new LexNameList();

		for (LexNameToken name : nameSpans.keySet())
		{
			ILexLocation span = nameSpans.get(name);

			if (span.getFile().equals(filename))
			{
				list.add(name);
			}
		}

		return list;
	}

	// TODO: Comment 
	public  float getSpanPercent(ILexNameToken name)
	{
		int hits = 0;
		int misses = 0;
		ILexLocation span = null;

		synchronized (nameSpans)
		{
			span = nameSpans.get(name);
		}

		synchronized (allLocations)
		{
			for (ILexLocation l : allLocations)
			{
				if (l.getExecutable() && l.within(span))
				{
					if (l.getHits() > 0)
					{
						hits++;
					} else
					{
						misses++;
					}
				}
			}
		}

		int sum = hits + misses;
		return sum == 0 ? 0 : (float) (1000 * hits / sum) / 10; // NN.N%
	}

	// TODO: Comment 	
	public  long getSpanCalls(ILexNameToken name)
	{
		// The assumption is that the first executable location in
		// the span for the name is hit as many time as the span is called.

		ILexLocation span = null;

		synchronized (nameSpans)
		{
			span = nameSpans.get(name);
		}

		synchronized (allLocations)
		{
			for (ILexLocation l : allLocations)
			{
				if (l.getExecutable() && l.within(span))
				{
					return l.getHits();
				}
			}
		}

		return 0;
	}

	// TODO: Comment 
	public  List<Integer> getHitList(File file)
	{
		//FIXME skip lex location in other files
		// idea: if !lextLocation.getFile().equals(file) then continue; 
		List<Integer> hits = new Vector<Integer>();

		synchronized (allLocations)
		{
			for (ILexLocation l : allLocations)
			{
				if (l.getHits() > 0 && l.getFile().equals(file))
				{
					hits.add(l.getStartLine());
				}
			}
		}

		return hits;
	}

	// TODO: Comment 
	public  List<Integer> getMissList(File file)
	{
		List<Integer> misses = new Vector<Integer>();

		synchronized (allLocations)
		{
			for (ILexLocation l : allLocations)
			{
				if (l.getHits() == 0 && l.getFile().equals(file))
				{
					misses.add(l.getStartLine());
				}
			}
		}

		return misses;
	}
	
	// TODO: Comment 
	public  Map<Integer, List<ILexLocation>> getHitLocations(File file)
	{
		Map<Integer, List<ILexLocation>> map = new HashMap<Integer, List<ILexLocation>>();

		synchronized (allLocations)
		{
			for (ILexLocation l : allLocations)
			{
				if (l.getExecutable() && l.getHits() > 0 && l.getFile().equals(file))
				{
					List<ILexLocation> list = map.get(l.getStartLine());

					if (list == null)
					{
						list = new Vector<ILexLocation>();
						map.put(l.getStartLine(), list);
					}

					list.add(l);
				}
			}
		}

		return map;
	}

	// TODO: Comment 
	public  float getHitPercent(File file)
	{
		int hits = 0;
		int misses = 0;

		synchronized (allLocations)
		{
			for (ILexLocation l : allLocations)
			{
				if (l.getFile().equals(file) && l.getExecutable())
				{
					if (l.getHits() > 0)
					{
						hits++;
					} else
					{
						misses++;
					}
				}
			}
		}

		int sum = hits + misses;
		return sum == 0 ? 0 : (float) (1000 * hits / sum) / 10; // NN.N%
	}

	// TODO: Comment 
	public  Map<Integer, List<ILexLocation>> getMissLocations(File file)
	{
		Map<Integer, List<ILexLocation>> map = new HashMap<Integer, List<ILexLocation>>();

		synchronized (allLocations)
		{
			for (ILexLocation l : allLocations)
			{
				if (l.getExecutable() && l.getHits() == 0 && l.getFile().equals(file))
				{
					List<ILexLocation> list = map.get(l.getStartLine());

					if (list == null)
					{
						list = new Vector<ILexLocation>();
						map.put(l.getStartLine(), list);
					}

					list.add(l);
				}
			}
		}

		return map;
	}
	
	/**
	 * This method handles the case where a location exist both with hits>0 and hits==0. It will remove the location
	 * with hits==0 when another location hits>0 exists.
	 * 
	 * 
	 * The strategy is to build a map with the elements of ILexLocation that are used when test equality as keys
	 * and the list of objects on the equivalence class as values.
	 * 
	 * In the merging of the map (clash of keys) we deal with duplicates
	 * 
	 * @param locations
	 * @return the list of locations where the unwanted locations have been removed
	 */
	private static List<ILexLocation> removeDuplicates(List<ILexLocation> locations)
	{
		
		// Map merging function
	    BinaryOperator<Vector<ILexLocation>> merge = (old, latest)-> {
	    	
	    	for (ILexLocation location : latest)
			{
				if (location.getHits() > 0)
				{
					// Remove objects with 0 hits in the equivalence class
					for(ILexLocation loc : old)
						if(loc.getHits() == 0) old.remove(loc);
				
					old.add(location);
				}
				else
				{
					// According to previous function if all are 0 hits that is OK 
					// TODO: Check if that is expected! 
					boolean allZeroHits = true;
					
					for(ILexLocation loc : old)
						if(loc.getHits() > 0)
						{
							allZeroHits = false; 
							break;
						}
						
					if(allZeroHits) old.add(location);	
				}
			}
			
	        return old;
	    };
	    
	    // Remove duplicate hits
		Map<String, Vector<ILexLocation>> map = locations.stream()
				.collect(Collectors.toMap(loc -> ((ILexLocation)loc).getFile() + 
		                                         ((ILexLocation)loc).getModule() +  
		                                         "l" +
		                                         ((ILexLocation)loc).getStartLine() +
		                                         "p" +
		                                         ((ILexLocation)loc).getStartPos(), 
		                                  loc -> new Vector<ILexLocation>(Arrays.asList(loc)),
		                                  merge)
		        		);
		 
		return map.values()
				.parallelStream()
				.collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);		
	
	}
}

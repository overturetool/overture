package org.overturetool.VDM2JavaCG.Utilities;

import com.google.common.collect.*;
import com.google.common.base.*;

import java.util.*;

public class CGCollections {
	
	//------------------Set Expressions---------------------------
	public static <E> HashSet<E> dunion(final Set<Set<E>> set) {
		HashSet<E> tmp = new HashSet<E>();
		for (Iterator<Set<E>> i = set.iterator(); i.hasNext();) {
			tmp.addAll(i.next());
		}
		return (HashSet<E>) tmp;
	}
	
	public static <E> HashSet<E> dunion(final HashSet<HashSet<E>> set) {
		HashSet<E> tmp = new HashSet<E>();
		for (Iterator<HashSet<E>> i = set.iterator(); i.hasNext();) {
			tmp.addAll(i.next());
		}
		return (HashSet<E>) tmp;
	}
	
	public static <E> HashSet<E> dinter (final Set<Set<E>> set) {
		Set<E> tmp = set.iterator().next();
		
		for (Iterator<Set<E>> i = set.iterator(); i.hasNext();) {
			tmp = Sets.newHashSet(Sets.intersection(tmp, i.next()));
		}
		return (HashSet<E>) tmp;
	}
	
	public static <E> HashSet<E> dinter(final HashSet<HashSet<E>> set) {
		HashSet<E> tmp = new HashSet<E>();
		for (Iterator<HashSet<E>> i = set.iterator(); i.hasNext();) {
			tmp.addAll(i.next());
		}
		return (HashSet<E>) tmp;
	}
	
	public static <E> boolean psubset (final Set<E> set1, final Set<E> set2) {
		if (set1.size() < set2.size()) {
			return set2.containsAll(set1);
		}
		else 
			return false;
	}
	
	public static <E> HashSet<E> union (final Set<E> set1, final Set<E> set2) {
			return Sets.newHashSet(Sets.union(set1, set2));
	}
	
	public static <E> HashSet<E> inter (final Set<E> set1, final Set<E> set2) {
		return Sets.newHashSet(Sets.intersection(set1, set2));
	}
	
	public static <E> HashSet<E> difference (final Set<E> set1, final Set<E> set2) {
		return Sets.newHashSet(Sets.difference(set1, set2));
	}
	
	public static <E> HashSet<Set<E>> power (final HashSet<E> set) {
		return Sets.newHashSet(Sets.powerSet(set));
	}
	
	public static HashSet<Integer> SetRange(Integer n1, Integer n2) {
		HashSet<Integer> tmp = new HashSet<Integer>();
		for (Integer i = n1; i <=  n2; i++) {
			tmp.add(new Integer(i));
		}
		return (HashSet<Integer>) tmp;
	}
	
	public static <E> HashSet<E> SetEnumeration (E... elements ) {
		return Sets.newHashSet(elements);
	}
	
	public static <E> HashSet<E> SetComprehension (Set<E> pset, Set<E>... sets) {
		Set<Set<E>> tmp = SetEnumeration(sets);
		//HashSet<E> tmp1 = dunion(tmp);
		//tmp = dunion(sets);
		//for (Iterator<T> i = tmp.iterator(); i.hasNext();) {
			
		//}
		return Sets.newHashSet(Sets.filter(dunion(tmp), Predicates.in(pset)));
	}
	
	//-------------------Sequence Expressions------------------------------
	public static <E> HashSet<E> elems (final Vector<E> v) {
		return Sets.newHashSet(v);
	}
	public static <E> HashSet<Integer> indexes (final Vector<E> v) {
		return SetRange(0, (v.size()-1));
	}
	public static <E> Vector<E> concatenation (final Vector<E> v1, final Vector<E> v2) {
		Vector<E> v = v1;
		v.addAll(v2);
		return v;
	}
	public static <E> Vector<E> DistConcat (final Vector<Vector<E>> v) {
		Vector<E> tmp = new Vector<E>();
		for (int i = 0; i < v.size(); i++) {
			tmp.addAll(v.get(i));
		}
		return tmp;
	}
	public static <E> Vector<E> SeqModification (final Vector<E> v, final HashMap<Integer, E> m) {
		Vector<E> tmp = new Vector<E>(v);
		if (v.size() >= m.keySet().size()) {
			for (Iterator<Integer> i = m.keySet().iterator(); i.hasNext();) {
				Integer n = i.next();
				tmp.set(n, m.get(n));
			}
			return tmp;
		}
		throw new InternalError("Domain of the 'Map' should be in the range of the 'Vector'");
	}
	public static <E> Vector<E> SeqEnumeration (E... elements ) {
		Vector<E> tmp = new Vector<E>();
		for (int i = 0; i < elements.length; i++) {
			tmp.add(elements[i]);
		}
		return tmp;
	}
	public static <E> Vector<E> SubSequence (Vector<E> v, int from, int to ) {
		Vector<E> tmp = new Vector<E>();
		for (int i = from; i <= v.size(); i++) {
			tmp.add(v.get(i));
		}
		return tmp;
	}
	public static <E> Vector<E> SeqComprehension (Set<E> pset, Set<E> set) {
		//Set<Set<E>> tmp = SetEnumeration(set);
		//HashSet<E> tmp1 = dunion(tmp);
		//tmp = dunion(sets);
		//for (Iterator<T> i = tmp.iterator(); i.hasNext();) {
			
		//}
		Vector<E> tmpv = new Vector<E>();
		HashSet<E> tmps = Sets.newHashSet(Sets.filter(set, Predicates.in(pset)));
		tmpv.addAll(tmps);
		return tmpv;
	}
	//---------------------Map Expression-----------------------------------------------
	public static <K, V> HashSet<V> range (final HashMap<K,V> m) {
		return (HashSet<V>) Sets.newHashSet(m.values());
	}
	public static <K, V> HashMap<K, V> merge(final HashSet<HashMap<K,V>> ms) {
		int counter = 0;
		HashMap<K,V> tmp = new HashMap<K,V>();
		for (Iterator<HashMap<K,V>> i = ms.iterator(); i.hasNext();) {
			HashMap<K,V> hm = i.next();
			tmp.putAll(hm);
			counter = counter + hm.keySet().size();
		}
		if (counter == tmp.keySet().size()) {
			return tmp;
		}
		else 
			throw new InternalError("Maps in set must be compatible!");
	}
	public static <K,V> HashMap<K,V> munion (final HashMap<K,V> m1, final HashMap<K,V> m2) {
		HashMap<K,V> tmp = m1;
		tmp.putAll(m2);
		if (tmp.keySet().size() == (m1.keySet().size() + m2.keySet().size())) {
			return tmp;
		}
		else 
			throw new InternalError("Two maps must be compatible!");
	}
	public static <K,V> HashMap<K,V> Override (final HashMap<K,V> m1, final HashMap<K,V> m2) {
		HashMap<K,V> tmp = m1;
		tmp.putAll(m2);
		return tmp;
	}
	public static <K,V> HashMap<K,V> DomTo(final HashSet<K> s, final HashMap<K,V> m) {
		HashMap<K,V> tmp = new HashMap<K,V>();
		for (Iterator<K> i = m.keySet().iterator(); i.hasNext();) {
			K k = i.next();
			if (s.contains(k)) {
				tmp.put(k, m.get(k));
			}			
		}
		return tmp;
	}
	public static <K,V> HashMap<K,V> DomBy(final HashSet<K> s, final HashMap<K,V> m) {
		HashMap<K,V> tmp = new HashMap<K,V>();
		for (Iterator<K> i = m.keySet().iterator(); i.hasNext();) {
			K k = i.next();
			if (!s.contains(k)) {
				tmp.put(k, m.get(k));
			}			
		}
		return tmp;
	}
	public static <K,V> HashMap<K,V> RangeTo(final HashMap<K,V> m, final HashSet<V> s) {
		HashMap<K,V> tmp = new HashMap<K,V>();
		for (Iterator<K> i = m.keySet().iterator(); i.hasNext();) {
			K k = i.next();
			if (s.contains(m.get(k))) {
				tmp.put(k, m.get(k));
			}			
		}
		return tmp;
	}
	public static <K,V> HashMap<K,V> RangeBy(final HashMap<K,V> m, final HashSet<V> s) {
		HashMap<K,V> tmp = new HashMap<K,V>();
		for (Iterator<K> i = m.keySet().iterator(); i.hasNext();) {
			K k = i.next();
			if (!s.contains(m.get(k))) {
				tmp.put(k, m.get(k));
			}			
		}
		return tmp;
	}
	public static <K,V,M> HashMap<K,V> comp (final HashMap<M,V> m1, final HashMap<K,M> m2) {
		HashMap<K,V> tmp = new HashMap<K,V>();
		if (m1.keySet().containsAll(m2.values())) {
			for (Iterator<K> i = m2.keySet().iterator(); i.hasNext();) {
				K k = i.next();
				tmp.put(k, m1.get(m2.get(k)));
			}
			return tmp;
		}
		else 
			throw new InternalError("Two maps must be compatible!");
	}
	public static <K> HashMap<K,K> Iteration (final HashMap<K,K> m, final Integer n) {
		HashMap<K,K> tmp = new HashMap<K,K>();
		if (n == 0) {
			for (Iterator<K> i = m.keySet().iterator(); i.hasNext();) {
				K k = i.next();
				tmp.put(k, k);
			}
			return tmp;
		}
		else {
			HashMap<K,K> tmp1 = m;
			for (int i = 1; i <= n; i++) { 
					tmp1 = comp(tmp1, m);
			}
			return tmp1;
		}
	}
	public static <K,V> HashMap<K,V> MapLet (K k, V v) {
		HashMap<K,V> tmp = new HashMap<K,V>();
		tmp.put(k, v);
		return tmp;
	}
	public static <K,V> HashMap<K,V> MapEnumeration (Map<K,V> ...maps) {
		HashMap<K,V> tmp = new HashMap<K,V>();
		for (int i = 0; i < maps.length; i++) {
			K k = maps[i].keySet().iterator().next();
			tmp.put(k, maps[i].get(k));
		}
		return tmp;
	}
}
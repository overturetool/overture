/*
 * #%~
 * VDM Tools Code Generator library
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package jp.co.csk.vdm.toolbox.VDM;

import java.util.*;

// Referenced classes of package jp.co.csk.vdm.toolbox.VDM:
//            Tuple, VDMRunTimeException, NotSupportedConstructException, Record

public abstract class UTIL
{
    public static class VDMCompare
        implements Comparator
    {

        public int compare(Object obj, Object obj1)
        {
            if((obj instanceof Number) && (obj1 instanceof Number))
            {
                double d = ((Number)obj).doubleValue();
                double d1 = ((Number)obj1).doubleValue();
                if(d < d1)
                    return -1;
                return d <= d1 ? 0 : 1;
            }
            if(obj == null && obj1 == null)
                return 0;
            if(obj == null)
                return -1;
            if(obj1 == null)
                return 1;
            if(UTIL.equals(obj, obj1))
                return 0;
            return UTIL.toString(obj).compareTo(UTIL.toString(obj1)) >= 0 ? 1 : -1;
        }

        public VDMCompare()
        {
        }
    }


    public UTIL()
    {
    }

    public static String toString(Object obj)
    {
        StringBuffer stringbuffer = new StringBuffer();
        toStringBuffer(obj, stringbuffer);
        return stringbuffer.toString();
    }

    private static void toStringBuffer(Object obj, StringBuffer stringbuffer)
    {
        if(obj == null)
            stringbuffer.append("nil");
        else
        if(obj instanceof String)
        {
            stringbuffer.append("\"");
            stringbuffer.append(obj.toString());
            stringbuffer.append("\"");
        } else
        if(obj instanceof Character)
        {
            stringbuffer.append("'");
            stringbuffer.append(obj.toString());
            stringbuffer.append("'");
        } else
        if(obj instanceof Tuple)
            stringbuffer.append(((Tuple)obj).toString());
        else
        if(obj instanceof Map)
        {
            Map map = (Map)obj;
            stringbuffer.append("{ ");
            if(map.isEmpty())
                stringbuffer.append("|->");
            else
                collectionToString(map.entrySet(), stringbuffer);
            stringbuffer.append(" }");
        } else
        if(obj instanceof java.util.Map.Entry)
        {
            java.util.Map.Entry entry = (java.util.Map.Entry)obj;
            toStringBuffer(entry.getKey(), stringbuffer);
            stringbuffer.append(" |-> ");
            toStringBuffer(entry.getValue(), stringbuffer);
        } else
        if(obj instanceof Set)
        {
            stringbuffer.append("{ ");
            collectionToString((Collection)obj, stringbuffer);
            stringbuffer.append(" }");
        } else
        if(obj instanceof List)
        {
            stringbuffer.append("[ ");
            vectorToString((Vector)obj, stringbuffer);
            stringbuffer.append(" ]");
        } else
        if(obj instanceof Double)
        {
            Double double1 = (Double)obj;
            if(double1.doubleValue() == (double)double1.intValue())
                stringbuffer.append((new Integer(double1.intValue())).toString());
            else
                stringbuffer.append(obj.toString());
        } else
        {
            stringbuffer.append(obj.toString());
        }
    }

    private static void collectionToString(Collection collection, StringBuffer stringbuffer)
    {
        Iterator iterator = collection.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            Object obj = iterator.next();
            stringbuffer.append(toString(obj));
            if(iterator.hasNext())
                stringbuffer.append(", ");
        } while(true);
    }

    private static void vectorToString(Vector vector, StringBuffer stringbuffer)
    {
        Iterator iterator = vector.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            Object obj = iterator.next();
            stringbuffer.append(toString(obj));
            if(iterator.hasNext())
                stringbuffer.append(", ");
        } while(true);
    }

    public static void RunTime(Object obj)
        throws VDMRunTimeException
    {
        throw new VDMRunTimeException(obj);
    }

    public static boolean samebaseclass(Object obj, Object obj1)
    {
        if(obj == null || obj1 == null)
            return false;
        Class class1 = obj.getClass();
        Class class2 = obj1.getClass();
        if(class1.getName().equals("java.lang.Integer") || class1.getName().equals("java.lang.Long") || class1.getName().equals("java.lang.Double") || class1.getName().equals("java.lang.Character") || class1.getName().equals("java.lang.Boolean") || class1.getName().equals("jp.co.csk.vdm.toolbox.VDM.Tuple") || class1.getName().endsWith("Token") || class1.getName().startsWith("quotes") || class2.getName().equals("java.lang.Integer") || class2.getName().equals("java.lang.Double") || class2.getName().equals("java.lang.Character") || class2.getName().equals("java.lang.Boolean") || class2.getName().equals("jp.co.csk.vdm.toolbox.VDM.Tuple") || class2.getName().endsWith("Token") || class2.getName().startsWith("quotes"))
            return false;
        Class aclass[] = class1.getInterfaces();
        for(int i = 0; i < aclass.length; i++)
            if(aclass[i].getName().equals("jp.co.csk.vdm.toolbox.VDM.Record"))
                return false;

        Class aclass1[] = class2.getInterfaces();
        for(int j = 0; j < aclass1.length; j++)
            if(aclass1[j].getName().equals("jp.co.csk.vdm.toolbox.VDM.Record"))
                return false;

        Class class3 = class1;
        Class class4 = class2;
        for(; !isbaseclass(class3); class3 = class3.getSuperclass());
        for(; !isbaseclass(class4); class4 = class4.getSuperclass());
        return class3 == class4;
    }

    public static boolean isbaseclass(Class class1)
    {
        Class class2 = class1.getSuperclass();
        if(class2 == null)
            return false;
        else
            return class2.getName().equals("java.lang.Object");
    }

    public static boolean sameclass(Object obj, Object obj1)
    {
        if(obj == null || obj1 == null)
            return false;
        Class class1 = obj.getClass();
        Class class2 = obj1.getClass();
        if(class1.getName().equals("java.lang.Integer") || class1.getName().equals("java.lang.Long") || class1.getName().equals("java.lang.Double") || class1.getName().equals("java.lang.Character") || class1.getName().equals("java.lang.Boolean") || class1.getName().equals("jp.co.csk.vdm.toolbox.VDM.Tuple") || class1.getName().endsWith("Token") || class1.getName().startsWith("quotes") || class2.getName().equals("java.lang.Integer") || class2.getName().equals("java.lang.Double") || class2.getName().equals("java.lang.Character") || class2.getName().equals("java.lang.Boolean") || class2.getName().equals("jp.co.csk.vdm.toolbox.VDM.Tuple") || class2.getName().endsWith("Token") || class2.getName().startsWith("quotes"))
            return false;
        Class aclass[] = class1.getInterfaces();
        for(int i = 0; i < aclass.length; i++)
            if(aclass[i].getName().equals("jp.co.csk.vdm.toolbox.VDM.Record"))
                return false;

        Class aclass1[] = class2.getInterfaces();
        for(int j = 0; j < aclass1.length; j++)
            if(aclass1[j].getName().equals("jp.co.csk.vdm.toolbox.VDM.Record"))
                return false;

        return class1 == class2;
    }

    public static void NotSupported(Object obj)
        throws NotSupportedConstructException
    {
        throw new NotSupportedConstructException(obj);
    }

    public static Object clone(Object obj)
    {
        if(obj == null)
            return null;
        if(!(obj instanceof Cloneable))
            return obj;
        if(obj instanceof Tuple)
            return ((Tuple)obj).clone();
        if(obj instanceof Record)
            return ((Record)obj).clone();
        if(obj instanceof Vector)
            return ((Vector)obj).clone();
        if(obj instanceof HashSet)
            return ((HashSet)obj).clone();
        if(obj instanceof HashMap)
            return ((HashMap)obj).clone();
        else
            return obj;
    }

    public static boolean equals(Object obj, Object obj1)
    {
        if(obj == null && obj1 == null)
            return true;
        if(obj == null || obj1 == null)
            return false;
        if((obj instanceof Number) && (obj1 instanceof Number))
            return ((Number)obj).doubleValue() == ((Number)obj1).doubleValue();
        if((obj instanceof String) && (obj1 instanceof List))
            return equals(ConvertToList(obj), obj1);
        if((obj1 instanceof String) && (obj instanceof List))
            return equals(ConvertToList(obj1), obj);
        if((obj instanceof List) && (obj1 instanceof List))
            return listEquals((List)obj, (List)obj1);
        if((obj instanceof HashSet) && (obj1 instanceof HashSet))
            return setEquals((HashSet)obj, (HashSet)obj1);
        if((obj instanceof HashMap) && (obj1 instanceof HashMap))
            return mapEquals((HashMap)obj, (HashMap)obj1);
        if(obj.getClass() != obj1.getClass())
            return false;
        else
            return obj.equals(obj1);
    }

    private static boolean listEquals(List list, List list1)
    {
        if(list.size() != list1.size())
            return false;
        boolean flag = true;
        int i = list.size();
        for(int j = 0; j < i && flag; j++)
            flag = equals(list.get(j), list1.get(j));

        return flag;
    }

    private static boolean setEquals(HashSet hashset, HashSet hashset1)
    {
        if(hashset.size() != hashset1.size())
            return false;
        boolean flag = true;
        HashSet hashset2 = hashset1;
        boolean flag1;
label0:
        for(Iterator iterator = hashset.iterator(); iterator.hasNext() && flag; flag = flag1)
        {
            Object obj = iterator.next();
            flag1 = false;
            HashSet hashset3 = hashset2;
            Iterator iterator1 = hashset3.iterator();
            do
            {
                if(!iterator1.hasNext() || flag1)
                    continue label0;
                Object obj1 = iterator1.next();
                flag1 = equals(obj, obj1);
                if(flag1)
                    hashset2.remove(obj1);
            } while(true);
        }

        return flag;
    }

    private static boolean mapEquals(HashMap hashmap, HashMap hashmap1)
    {
        if(hashmap.size() != hashmap1.size())
            return false;
        boolean flag = true;
        Set set = hashmap.keySet();
        Set set1 = hashmap1.keySet();
        boolean flag1;
label0:
        for(Iterator iterator = set.iterator(); iterator.hasNext() && flag; flag = flag1)
        {
            Object obj = iterator.next();
            flag1 = false;
            Set set2 = set1;
            Iterator iterator1 = set2.iterator();
            do
            {
                if(!iterator1.hasNext() || flag1)
                    continue label0;
                Object obj1 = iterator1.next();
                flag1 = equals(obj, obj1) && equals(hashmap.get(obj), hashmap1.get(obj1));
                if(flag1)
                    set1.remove(obj1);
            } while(true);
        }

        return flag;
    }

    public static Integer NumberToInt(Object obj)
        throws VDMRunTimeException
    {
        if(obj == null)
            return null;
        if(obj instanceof Double)
        {
            if((double)((Double)obj).intValue() == ((Double)obj).doubleValue())
                return new Integer(((Double)obj).intValue());
            else
                throw new VDMRunTimeException("<UTIL.NumberToInt>: number is not int " + obj.toString());
        } else
        {
            return (Integer)obj;
        }
    }

    public static Long NumberToLong(Object obj)
        throws VDMRunTimeException
    {
        if(obj == null)
            return null;
        if(obj instanceof Double)
        {
            if((double)((Double)obj).intValue() == ((Double)obj).doubleValue())
                return new Long(((Double)obj).longValue());
            else
                throw new VDMRunTimeException("<UTIL.NumberToLong>: number is not int " + obj.toString());
        } else
        {
            return (Long)obj;
        }
    }

    public static Double NumberToReal(Object obj)
    {
        if(obj == null)
            return null;
        else
            return new Double(((Number)obj).doubleValue());
    }

    public static List ConvertToList(Object obj)
    {
        if(obj instanceof String)
        {
            Vector vector = new Vector();
            char ac[] = ((String)obj).toCharArray();
            for(int i = 0; i < ac.length; i++)
                vector.add(new Character(ac[i]));

            return vector;
        } else
        {
            return (List)obj;
        }
    }

    public static String ConvertToString(Object obj)
    {
        if((obj instanceof String) || obj == null)
            return (String)obj;
        if(obj instanceof List)
        {
            StringBuffer stringbuffer = new StringBuffer();
            List list = (List)obj;
            boolean flag = true;
            for(Iterator iterator = list.iterator(); iterator.hasNext() && flag;)
            {
                Object obj1 = iterator.next();
                if(obj1 instanceof Character)
                    stringbuffer.append(obj1);
                else
                    flag = false;
            }

            if(!flag)
                return "";
            else
                return stringbuffer.toString();
        } else
        {
            return obj.toString();
        }
    }

    public static boolean IsInteger(Object obj)
    {
        return (obj instanceof Integer) || (obj instanceof Long) || (obj instanceof Double) && (double)((Number)obj).intValue() == ((Number)obj).doubleValue();
    }

    public static boolean IsReal(Object obj)
    {
        return (obj instanceof Integer) || (obj instanceof Long) || (obj instanceof Double);
    }

    public static HashSet Permute(List list)
    {
        VDMCompare vdmcompare = new VDMCompare();
        HashSet hashset = new HashSet();
        if(list.size() == 0)
            return hashset;
        if(list.size() == 1)
        {
            Vector vector = new Vector();
            try
            {
                vector.add(list.get(0));
            }
            catch(IndexOutOfBoundsException indexoutofboundsexception) { }
            hashset.add(vector);
            return hashset;
        }
        Object obj = null;
        try
        {
            obj = list.get(0);
        }
        catch(IndexOutOfBoundsException indexoutofboundsexception1) { }
        Vector vector1 = new Vector(list);
        try
        {
            vector1.remove(0);
        }
        catch(IndexOutOfBoundsException indexoutofboundsexception2) { }
        HashSet hashset1 = Permute(((List) (vector1)));
        for(int i = 0; i < list.size(); i++)
        {
            List list1;
            for(Iterator iterator = hashset1.iterator(); iterator.hasNext(); hashset.add(list1))
            {
                Vector vector2 = (Vector)iterator.next();
                list1 = (List)vector2.clone();
                list1.add(i, obj);
            }

        }

        return hashset;
    }

    public static Vector Sort(Set set)
    {
        Vector vector = new Vector();
        VDMCompare vdmcompare = new VDMCompare();
        TreeSet treeset = new TreeSet(vdmcompare);
        treeset.addAll(set);
        vector.addAll(treeset);
        return vector;
    }

    public static List Sort2(List list)
    {
        Vector vector = new Vector();
        Vector vector1 = new Vector(list);
        for(int i = 0; i < list.size(); i++)
        {
            Object obj = null;
            try
            {
                obj = vector1.get(0);
            }
            catch(IndexOutOfBoundsException indexoutofboundsexception) { }
            Iterator iterator = vector1.iterator();
            do
            {
                if(!iterator.hasNext())
                    break;
                Object obj1 = iterator.next();
                if(IsInteger(obj1))
                {
                    Long long1 = new Long(obj1.toString());
                    if((obj instanceof Integer) && long1.intValue() < ((Integer)obj).intValue())
                        obj = long1;
                    if((obj instanceof Long) && long1.longValue() < ((Long)obj).longValue())
                        obj = long1;
                    if(obj instanceof Character)
                        obj = long1;
                } else
                if(obj1 instanceof Character)
                {
                    Character character = (Character)obj1;
                    if((obj instanceof Character) && Character.getNumericValue(character.charValue()) < Character.getNumericValue(((Character)obj).charValue()))
                        obj = character;
                }
            } while(true);
            vector.add(obj);
            vector1.remove(obj);
        }

        return vector;
    }
}

package nl.marcelverhoef.treegen.typecheck;

// project specific imports
import java.util.*;
import nl.marcelverhoef.treegen.ast.imp.*;
import nl.marcelverhoef.treegen.ast.itf.*;

public class TreeChecker {
	
	// keep track of number of errors found
	public int errors = 0;
	
	// switch to enable deep logging
	public boolean debug = false;
	
	// create a mapping to hold all class definitions
	public HashMap<String,ClassDefinition> cls = new HashMap<String,ClassDefinition>();
	
	// create a set of all type names encountered during first pass
	public HashSet<String> ttns = new HashSet<String>();
	
	// create a map to store all free type name variables per class definition
	public HashMap<String,HashSet<String>> tns = new HashMap<String,HashSet<String>>();

	public void performCheck(List<TreeGenAstClassDefinition> defs)
	{
		// reset the number of errors
		errors = 0;
		
		// iterate over all parsed class definitions and build map
		for (ITreeGenAstClassDefinition def : defs) {
			
			// create a new entry for the class definition
			ClassDefinition cd = new ClassDefinition();
			cd.class_name = def.getClassName();
			cd.super_class = null;
			
			// insert the entry in the mapping
			cls.put(def.getClassName(), cd);
		}
		
		// iterate again and perform the check on each class
		for (ITreeGenAstClassDefinition def : defs) {
			// performCheckClassDefinition will also establish the class hierarchy
			performCheckClassDefinition(def);
		}
		
		// pass two: check all free type name variables encountered during first pass
		performCheckFreeVariables();
		
		// pass three: link all types to the super types if they are defined
		for (ClassDefinition cd: cls.values()) {
			for (String tpnm: cd.types.keySet()) {
				// does this class have a super class
				if (cd.super_class != null) {
					// link each type to its named super type if it exists
					cd.types.get(tpnm).super_type = cd.super_class.getTypeByName(tpnm);
				}
			}
		}
	}
	
	public void performCheckClassDefinition (ITreeGenAstClassDefinition def)
	{
		// diagnostics
		System.out.println("Analyzing class "+ def.getClassName());
		
		// check consistency
		if (!def.getSuperClass().isEmpty()) {
			if (cls.containsKey(def.getSuperClass())) {
				// diagnostics message
				System.out.println ("Superclass "+def.getSuperClass()+" is defined for class "+def.getClassName());
				
				// link the two class definitions
				ClassDefinition cd1 = cls.get(def.getClassName());
				ClassDefinition cd2 = cls.get(def.getSuperClass());
				
				// add cd2 as the super class of cd1
				cd1.super_class = cd2;
				
				// add cd1 as the sub class of cd2
				cd2.sub_classes.add(cd1);
			} else {
				// diagnostics error message
				System.out.println ("Superclass "+def.getSuperClass()+" does not exist for type "+def.getClassName());
				
				// increase the error count
				errors++;
			}
		} else {
			// diagnostics message
			System.out.println("Class "+def.getClassName()+" does not have a superclass");
		}
		
		// clear the set of free type name variables encountered in this class definition
		ttns.clear();
		
		// process the embedded definitions
		performCheckDefinition(cls.get(def.getClassName()), def.getDefs());
		
		// store a copy of the set of free type name variables
		tns.put(def.getClassName(), new HashSet<String>(ttns));
	}
	
	public void performCheckDefinition(ClassDefinition cd, List<? extends ITreeGenAstDefinitions> tgads)
	{
		// iterate over the list of embedded definitions
		for (ITreeGenAstDefinitions tgad : tgads) {
			if (tgad instanceof ITreeGenAstValueDefinition) {
				performCheckValueDefinition(cd, (ITreeGenAstValueDefinition) tgad);
			} else if (tgad instanceof ITreeGenAstVariableDefinition) {
				performCheckVariableDefinition(cd, (ITreeGenAstVariableDefinition) tgad);
			} else if (tgad instanceof ITreeGenAstShorthandDefinition) {
				performCheckShorthandDefinition(cd, (ITreeGenAstShorthandDefinition) tgad);				
			} else if (tgad instanceof ITreeGenAstCompositeDefinition) {
				performCheckCompositeDefinition(cd, (ITreeGenAstCompositeDefinition) tgad);								
			} else {
				// diagnostics error message
				System.out.println("Could not resolve type of embedded definition");
				
				// increase the error count
				errors++;
			}
		}
	}
	
	public void performCheckValueDefinition(ClassDefinition cd, ITreeGenAstValueDefinition tgavd)
	{
		// diagnostics message
		if (debug) System.out.println("Checking value definition "+cd.class_name+"."+tgavd.getKey());
		
		if (cd.values.containsKey(tgavd.getKey())) {
			// flag error: value multiple defined
			System.out.println("value definition '"+tgavd.getKey()+"' is multiple defined");
			
			// increase error count
			errors++;
		} else {
			// add the value definition to the class definition
			cd.values.put(tgavd.getKey(), tgavd.getValue());
		}
	}
	
	public void performCheckVariableDefinition(ClassDefinition cd, ITreeGenAstVariableDefinition tgavd)
	{
		// diagnostics message
		if (debug) System.out.println("Checking variable definition " +cd.class_name+"."+tgavd.getKey());
		
		// retrieve the type of the member variable
		Type theType = retrieveType(tgavd.getType());

		if (theType == null) {
			// flag error: type conversion failed
			System.out.println("type conversion failed in instance variable '"+tgavd.getKey()+"'");
			
			// increase error count
			errors++;
		} else {
			
			if (cd.variables.containsKey(tgavd.getKey())) {
				// flag error: member variable multiple defined
				System.out.println("instance variable '"+tgavd.getKey()+"' is multiple defined");
				
				// increase error count
				errors++;
			} else {
				// additional consistency check for java types (init clause must be declared)
				if (theType instanceof JavaType) {
					// retrieve the embedded java type
					JavaType theJavaType = (JavaType) theType;
					
					// initialise the embedded type
					if (tgavd.getValue() == null) {
						// flag error: init clause is empty
						System.out.println("init clause must be provided with java type for instance variable '"+tgavd.getKey()+"'");
						
						// increase error count
						errors++;
					} else if (tgavd.getValue().isEmpty()) {
						// flag error: init clause is empty
						System.out.println("init clause must be provided with java type for instance variable '"+tgavd.getKey()+"'");
						
						// increase error count
						errors++;					
					} else {
						// set the embedded java type
						theJavaType.java_type = tgavd.getValue();
					}
				}
				
				// create the new member variable
				MemberVariable theVariable = new MemberVariable(theType, tgavd.getValue());
				
				// add the member variable to the look-up table
				cd.variables.put(tgavd.getKey(), theVariable);
			}
		}
	}
	
	public void performCheckShorthandDefinition(ClassDefinition cd, ITreeGenAstShorthandDefinition tgashd)
	{
		// retrieve the shorthand name
		String shnm = tgashd.getShorthandName();
		
		// diagnostics message
		if (debug) System.out.println("Checking shorthand definition "+cd.class_name+"."+shnm);
		
		// retrieve the type of the shorthand definition
		Type theType = retrieveType(tgashd.getType());
		
		// check for consistent type conversion
		if (theType == null) {
			// flag error (type conversion failed)
			System.out.println("Shorthand type '"+shnm+"' cannot be converted!");
			// increase the error count
			errors++;
		} else {
			// store the type in the class definition
			if (cd.types.containsKey(tgashd.getShorthandName())) {
				// flag error (multiple defined type)
				System.out.println("Shorthand type '"+shnm+"' is multiple defined!");
				
				// increase the error count
				errors++;
			} else {
				// store the short-hand (does NOT need to be a union type)
				cd.types.put(shnm, theType);
				
				// check for a type name union (defining the subtype relationships)
				if (theType.isUnionType()) {
					// convert by casting
					UnionType theUnionType = (UnionType) theType;
					
					// check for type name union
					if (theUnionType.isTypeNameUnion()) {
						for (String tnm: theUnionType.getTypeNames()) {
							// insert each type name in the sub type look-up table
							cd.subtypes.put(tnm, shnm);
						}
					}
				}
			}
		}
	}
	
	public void performCheckCompositeDefinition(ClassDefinition cd, ITreeGenAstCompositeDefinition tgacd)
	{
		// diagnostics message
		if (debug) System.out.println("Checking composite definition "+cd.class_name+"."+tgacd.getCompositeName());
		
		// obtain the record type name
		String recnm = tgacd.getCompositeName();
		
		// consistency check
		if (cd.types.containsKey(recnm)) {
			// flag error: type multiple defined
			System.out.println("Composite definition '"+recnm+"' is multiple defined");
			
			// increase the error count
			errors++;
		} else {
			// create the record type
			RecordType theRecord = new RecordType(recnm);
			
			// iterate over the fields of the record
			for (ITreeGenAstCompositeField tgfld : tgacd.getFields()) {
				// obtain the embedded field type
				Type theType = retrieveType(tgfld.getType());
				
				// consistency check
				if (theType == null) {
					// flag error: field type conversion failed
					System.out.println("Field type conversion failed in '"+recnm+"."+tgfld.getFieldName()+"'");
					
					// increase error count
					errors++;
				} else {
					// add the field to the record type definition
					theRecord.fields.add(new Field(tgfld.getFieldName(), theType));
				}
			}
			
			// insert the record type in the look-up table
			cd.types.put(recnm, theRecord);
		}
	}
	
	public void performCheckFreeVariables()
	{
		// iterate over the list of all classes
		for (String clnm: tns.keySet()) {
			// retrieve the class
			ClassDefinition cd = cls.get(clnm);
			
			if (cd == null) {
				// flag error: class cannot be found
				System.out.println("internal error: class '"+clnm+"' cannot be found");
				
				// increase error count
				errors++;
			} else {
				// iterate over the list of free variables
				for (String fvnm: tns.get(clnm)) {
					// diagnostics
					//MAVE: System.out.println("Checking type '"+clnm+"."+fvnm+"'");
					
					// perform the look-up
					if (cd.getTypeByName(fvnm) == null) {
						// flag error: type is not defined
						System.out.println("Type '"+clnm+"."+fvnm+"' is not defined anywhere");
						
						// increase the error count
						errors++;
					}
				}
			}
		}
	}
	
	public Type retrieveType(ITreeGenAstTypeSpecification tgats)
	{
		if (tgats instanceof ITreeGenAstTypeName) {
			return retrieveTypename((ITreeGenAstTypeName) tgats);
		} else if (tgats instanceof ITreeGenAstQuotedType) {
			return retrieveQuotedType((ITreeGenAstQuotedType) tgats);
		} else if (tgats instanceof ITreeGenAstUnionType) {
			return retrieveUnionType((ITreeGenAstUnionType) tgats);
		} else if (tgats instanceof ITreeGenAstOptionalType) {
			return retrieveOptionalType((ITreeGenAstOptionalType) tgats);
		} else if (tgats instanceof ITreeGenAstSeqType) {
			return retrieveSeqType((ITreeGenAstSeqType) tgats);
		} else if (tgats instanceof ITreeGenAstSetType) {
			return retrieveSetType((ITreeGenAstSetType) tgats);
		} else if (tgats instanceof ITreeGenAstMapType) {
			return retrieveMapType((ITreeGenAstMapType) tgats); 
		} else return null;
	}

	public Type retrieveTypename(ITreeGenAstTypeName tgatn) {
		// diagnostics
		// MAVE: System.out.println ("Retrieving type name "+tgatn.getName());
		
		// check for basic type: Boolean values
		if (tgatn.getName().compareToIgnoreCase("bool") == 0) return new BooleanType();
		
		// check for basic type: natural numbers
		if (tgatn.getName().compareToIgnoreCase("nat") == 0) return new NatType();
		
		// check for basic type: integer numbers
		if (tgatn.getName().compareToIgnoreCase("int") == 0) return new NatType();
		
		// check for basic type: real numbers
		if (tgatn.getName().compareToIgnoreCase("real") == 0) return new RealType();
		
		// check for basic type: characters
		if (tgatn.getName().compareToIgnoreCase("char") == 0) return new CharType();

		// check for special type: embedded java
		if (tgatn.getName().compareToIgnoreCase("java") == 0) return new JavaType();

		// ok: it is a true type name, add it to the list
		ttns.add(tgatn.getName());
		
		// default: create and return the new type name
		return new TypeName(tgatn.getName());
	}
	
	public Type retrieveQuotedType(ITreeGenAstQuotedType tgaqt) {
		// diagnostics
		// MAVE: System.out.println ("Retrieving quoted type <"+tgaqt.getQuote()+">");
		
		// create and return the new quoted type
		return new QuotedType(tgaqt.getQuote());
	}
	
	public Type retrieveUnionType(ITreeGenAstUnionType tgaut) {
		// diagnostics
		// MAVE: System.out.println ("Retrieving union type");
		
		// first convert the embedded types recursively
		Type lhs = retrieveType(tgaut.getLhs());
		Type rhs = retrieveType(tgaut.getRhs());
		
		// compose the result type (flatten the type)
		if (lhs instanceof UnionType) {
			// cast to proper (union) type
			UnionType lhsut = (UnionType) lhs;
			
			if (rhs instanceof UnionType) {
				// cast to proper type
				UnionType rhsut = (UnionType) rhs;
				
				// merge lhs and rhs unions
				lhsut.union_type.addAll(rhsut.union_type);
				
			} else {
				// add rhs to the union
				lhsut.union_type.add(rhs);
			}

			// return the result
			return lhsut;
		} else {
			if (rhs instanceof UnionType) {
				// cast to proper type
				UnionType rhsut = (UnionType) rhs;
				
				// add lhs to the union
				rhsut.union_type.add(lhs);
				
				// return the union
				return rhsut;
			} else {
				// compose a new union type
				UnionType nt = new UnionType();
				
				// add lhs and rhs to the union type
				nt.union_type.add(lhs);
				nt.union_type.add(rhs);
				
				// return the new constructed union
				return nt;
			}
		}
	}
	
	public Type retrieveOptionalType(ITreeGenAstOptionalType tgaot) {
		// diagnostics
		// MAVE: System.out.println ("Retrieving optional type");
		
		// obtain the embedded type
		Type res = retrieveType(tgaot.getType());
		
		if (res == null) {
			// flag error (type conversion failed)
			System.out.println("Optional type cannot be converted");
			
			// increase the error count
			errors++;
		} else {
			// flag this type as optional
			res.opt = true;
		}
		
		// return the derived type
		return res;
	}
	
	public Type retrieveSetType(ITreeGenAstSetType tgast) {
		// diagnostics
		// MAVE: System.out.println("Retrieving set type");
		
		// retrieve the embedded type
		Type est = retrieveType(tgast.getType());
		
		if (est == null) {
			// flag error: type conversion failed
			System.out.println ("Set type cannot be converted");
			
			// increase error count
			errors++;
			
			// default: return error
			return null;
		} else {
			// create and return the new set type
			return new SetType(est);
		}
	}
	
	public Type retrieveSeqType(ITreeGenAstSeqType tgast) {
		// diagnostics
		// MAVE: System.out.println("Retrieving seq type");
		
		// retrieve the embedded type
		Type est = retrieveType(tgast.getType());
		
		if (est == null) {
			// flag error: type conversion failed
			System.out.println ("Sequence type cannot be converted");
			
			// increase error count
			errors++;
			
			// default: return error
			return null;
		} else {
			// create and return the new set type
			return new SeqType(est);
		}
	}
	
	public Type retrieveMapType(ITreeGenAstMapType tgamt) {
		// diagnostics
		// MAVE: System.out.println ("Retrieving map type");
		
		// retrieve the embedded domain type
		Type dom = retrieveType(tgamt.getDomType());
		
		// consistency check 
		if (dom == null) {
			// flag error
			System.out.println ("domain type conversion failed in map type");
			
			// increase error count
			errors++;
		}
		
		// retrieve the embedded domain type
		Type rng = retrieveType(tgamt.getRngType());
		
		// consistency check 
		if (rng == null) {
			// flag error
			System.out.println ("range type conversion failed in map type");
			
			// increase error count
			errors++;
		}
		
		// construct the return type
		if ((dom != null) && (rng != null)) {
			// return the new map type
			return new MapType(dom, rng);
		} else {
			// default: flag error to caller
			return null;
		}
	}

}

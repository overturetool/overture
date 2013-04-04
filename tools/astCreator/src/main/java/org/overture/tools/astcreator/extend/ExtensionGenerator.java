package org.overture.tools.astcreator.extend;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.overture.tools.astcreator.definitions.BaseClassDefinition;
import org.overture.tools.astcreator.definitions.Field;
import org.overture.tools.astcreator.definitions.IClassDefinition;
import org.overture.tools.astcreator.definitions.IInterfaceDefinition;
import org.overture.tools.astcreator.env.Environment;

public class ExtensionGenerator
  {
    public enum ExtendMode
      {
        Standalone, Extend
      }
    
    public static void setExtendName(Environment baseEnv, String extendName)
      {
        String namePostfix = extendName == null ? "" : extendName;
        for (IInterfaceDefinition def : baseEnv.getAllDefinitions())
          {
              // if (extendMode == ExtendMode.Standalone
              // || (extendMode == ExtendMode.Extend &&
              // !baseClasses.contains(def)))
              {
                def.getName().setPostfix(namePostfix);
              }
          }
      }
    
    public static Environment extendWith(Environment baseEnv, ExtendMode mode,
        Environment env)
      {
        switch (mode)
          {
            case Extend:
              return changeExtendsTo(baseEnv, env);
            case Standalone:
              return extendWithStandalone(baseEnv, env);
              
          }
        return null;
      }
    
    private static Environment changeExtendsTo(Environment baseEnv,
        Environment env)
      {
        for (IClassDefinition c : baseEnv.getClasses())
          {
            IClassDefinition superDef = c.getSuperDef();
            if (superDef != null)
              {
                String superName = c.getName().getName();
                superName = superName.substring(0, superName.length()
                    - superDef.getName().getPostfix().length());
                IClassDefinition newSuper = env.lookUp(superName);
                if (newSuper != null)
                  {
                    c.setSuper(newSuper);
                    // c.getInterfaces().add(superDef);
                    // this.addInterface(superDef);
                  }
              }
          }
        
        return baseEnv;
      }
    
    public static Environment extendWithStandalone(Environment baseEnv,
        Environment env)
      {
        baseEnv.setDefaultPackages(env.getDefaultPackage());
        baseEnv.setAnalysisPackages(env.getAnalysisPackage());
        List<IClassDefinition> extendedClasses = new Vector<IClassDefinition>();
        // not matched classes
        List<IClassDefinition> notMatchedClasses = new Vector<IClassDefinition>();
        notMatchedClasses.addAll(baseEnv.getClasses());
        
        // first extend all existing classes
        for (IClassDefinition c : baseEnv.getClasses())
          {
            for (IClassDefinition cNew : env.getClasses())
              {
                if (c.getName().equals(cNew.getName()))
                  {
                    extendWith(baseEnv, baseEnv, c, cNew);
                    extendedClasses.add(cNew);
                    notMatchedClasses.remove(c);
                    break;
                  }
              }
          }
        
        // set package for all alternatives not matched
        for (IClassDefinition c : notMatchedClasses)
          {
            if (baseEnv.isTreeNode(c))
              {
                switch (baseEnv.classToType.get(c))
                  {
                    case Alternative:
                      c.getName().setPackageName(
                          c.getSuperDef().getName().getPackageName());
                      break;
                    case Custom:
                      break;
                    case Production:
                      break;
                    case SubProduction:
                      break;
                    case Token:
                      c.getName().setPackageName(
                          baseEnv.getDefaultPackage() + ".tokens");
                      break;
                    case Unknown:
                      break;
                  
                  }
                
              }
          }
        
        // add new classes
        List<IClassDefinition> newClasses = new Vector<IClassDefinition>();
        newClasses.addAll(env.getClasses());
        newClasses.removeAll(extendedClasses);
        
        for (IClassDefinition def : newClasses)
          {
            if (baseEnv.isTreeNode(def) && def.getSuperDef() != null
                && baseEnv.isTreeNode(def.getSuperDef()))
              {
                IClassDefinition oldSuper = def.getSuperDef();
                ((BaseClassDefinition) def).imports.remove(oldSuper);
                def.setSuper(baseEnv.lookUp(oldSuper.getName().getName()));
               // def.updateEnvironment(baseEnv);
              }
          }
        baseEnv.getClasses().addAll(newClasses);
        
        // for now we skip the interfaces
        // TODO RWL it way be here we need to do some change
        
        // update env for all
//        for (IClassDefinition def : baseEnv.getClasses())
//          {
//            // def.updateEnvironment(baseEnv);
//          }
        
        return baseEnv;
      }
    
    private static void extendWith(Environment baseEnv, Environment env,
        IClassDefinition c, IClassDefinition cNew)
      {
        c.getName().setPackageName(cNew.getName().getPackageName());
        
        List<Field> fields = new ArrayList<Field>(c.getFields());
        List<Field> matchedFields = new Vector<Field>();
        for (Field f : fields)
          {
            for (Field newF : cNew.getFields())
              {
                if (f.getName(env).equals(newF.getName(env)))
                  {
                    f.setType(newF.getUnresolvedType());
                    matchedFields.add(newF);
                  }
              }
          }
        
        List<Field> newFields = new Vector<Field>();
        newFields.addAll(cNew.getFields());
        newFields.removeAll(matchedFields);
        for (Field field : newFields)
          {
            
            c.addField(field);
          }
        
        // //tryout
        // for (Field field : c.getFields())
        // {
        // field.updateEnvironment(env);
        // }
      }
  }

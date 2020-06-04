package it.itc.etoc;

import java.util.*;

import it.itc.etoc.Action;
import it.itc.etoc.Chromosome;
import it.itc.etoc.ChromosomeFormer;
import it.itc.etoc.ConstructorInvocation;
import it.itc.etoc.MethodInvocation;
import it.itc.etoc.MethodSignature;
import it.itc.etoc.NullConstructorInvocation;
import it.itc.etoc.StringGenerator;

import java.io.*;
import java.lang.reflect.*;

/**
 * Manipulates chromosomes for evolutionary testing of classes. This is the
 * typical sequence of invocation: ChromosomeFormer chromFormer = new
 * ChromosomeFormer(); chromFormer.readSignatures("file.sign");
 * chromFormer.buildNewChromosome();
 */
public class ChromosomeFormer {

  /**
   * Description and values of a test case.
   *
   * <pre>
   * &lt;&lt;input-descriptor&gt;&gt; @ &lt;&lt;input-values&gt;&gt;
   *
   * &lt;&lt;input-descriptor&gt;&gt;		::=	[$id '=' ]? 
   *					[$id '.' method | constructor]
   *					'(' [parameter]* ')' 
   *					[':' &lt;&lt;input-descriptor&gt;&gt;]?
   * </pre>
   *
   * where id is an identifier, method/constructor are class methods and
   * constructors, and parameter is defined as follows:
   *
   * <pre>
   * &lt;&lt;parameter&gt;&gt;			::=	built-in-type | $id
   * </pre>
   *
   * where $id has been previously assigned the return of a constructor/method.
   *
   * Example:
   * 
   * <pre>
   * $a=A():$b=B(int):$b.c():$a.m(int, $b) @ 1, 4
   * </pre>
   */
  private Chromosome chromosome;

  /**
   * Lớp java trace test
   */
  String classUnderTest;

  /**
   * Accessor to class under test.
   */
  public String getClassUnderTest() {
    return classUnderTest;
  }

  public void setClassUnderTest(String classUnderTest) {
    this.classUnderTest = classUnderTest;
  }

  /**
   * Associates class name to List of constructors.
   *
   * <br>
   * className:String -> classConstructors:List&lt;MethodSignature&gt;
   */
  private Map constructors = new HashMap();

  /**
   * Associates class name to List of methods.
   *
   * <br>
   * className:String -> classMethods:List&lt;MethodSignature&gt;
   */
  private Map methods = new HashMap();

  /**
   * Associates abstract type (e.g. abstract class, interface) to implementations
   * (list of classes).
   *
   * <br>
   * abstractTypeName:String -> concreteTypeNames:List&lt;String&gt;
   */
  private Map concreteTypes = new HashMap();

  /**
   * Incremental counter used to build fresh ids.
   */
  private int idCounter = 0;

  public static Random randomGenerator = new Random();
  public static StringGenerator stringGenerator = new StringGenerator();

  /**
   * Returns String representation of chromosome
   *
   * @return chromosome: String
   */
  public Chromosome getChromosome() {
    return chromosome;
  }

  /**
   * Creates a new chromosome.
   */
  public void buildNewChromosome() {
    String objId = "$x" + (idCounter++);
    chromosome = new Chromosome();
    prependConstructor(classUnderTest);
    appendRandomMethodCall(classUnderTest, null);
  }

  /**
   * Set current chromosome.
   */
  public void setCurrentChromosome(Chromosome chrom) {
    chromosome = chrom;
  }

  /**
   * Mutate the current chromosome.
   *
   * @param chrom Used only by the crossover operator.
   */
  public void mutateChromosome(Chromosome chrom) {
    int ran = randomGenerator.nextInt(100);
    if (ran < 20) {
      changeInputValue();
    } else if (ran < 40) {
      changeConstructor();
    } else if (ran < 60) {
      insertRandomMethodCall();
    } else if (ran < 80) {
      removeRandomMethodCall();
    } else {
      if (chrom != null) {
        crossover(chrom);
      }
    }
    if (chromosome.size() == 0)
      buildNewChromosome();
  }

  /**
   * Adds a constructor to the list of known constructors.
   *
   * @param sign: MethodSignature The constructor to be added.
   */
  public void addConstructor(MethodSignature sign) {
    String className = sign.getName();
    if (constructors.get(className) == null)
      constructors.put(className, new LinkedList());
    List constr = (List) constructors.get(className);
    constr.add(sign);
  }

  /**
   * Adds a method to the list of known methods.
   *
   * @param className: String The enclosing class.
   * @param sign:      MethodSignature The method to be added.
   */
  public void addMethod(String className, MethodSignature sign) {
    if (methods.get(className) == null)
      methods.put(className, new LinkedList());
    List meth = (List) methods.get(className);
    meth.add(sign);
  }

  /**
   * Adds a concrete type to the list of known implementations of an abstract
   * type.
   *
   * @param abstractType: String The abstract class.
   * @param concreteType: String The implementation.
   */
  public void addConcreteType(String abstractType, String concreteType) {
    if (concreteTypes.get(abstractType) == null)
      concreteTypes.put(abstractType, new LinkedList());
    List types = (List) concreteTypes.get(abstractType);
    types.add(concreteType);
  }

  /**
   * True if type is considered primitive.
   *
   * A type is considered primitive when a value can be generated for it without
   * resorting to any constructor invocation. Currenty, only integer types are
   * considered primitive.
   *
   * @param type The type to be checked.
   */
  public static boolean isPrimitiveType(String type) {
    if (type.indexOf("[") != -1)
      type = type.substring(0, type.indexOf("["));
    return type.equals("int") || type.equals("long") || type.equals("short") || type.equals("char")
        || type.equals("byte") || type.equals("String") || type.equals("boolean") || type.equals("float")
        || type.equals("double");
  }

  /**
   * Generates and (if necessary) wraps new random value.
   *
   * @param type Either "int", "String", "boolean", etc..
   */
  public static String buildValue(String type) {
    if (type.startsWith("int"))
      return buildIntValue(type);
    else if (type.startsWith("String"))
      return buildStringValue(type);
    else if (type.startsWith("boolean"))
      return buildBoolValue(type);
    else if (type.startsWith("float") || type.startsWith("double"))
      return buildRealValue(type);
    else
      return "";
  }

  /**
   * Generates random boolean value.
   *
   * Equal probability of true and false
   *
   * @param clName   Name of generator class
   * @param methName Name of generator method
   */
  public static String buildUserDefValue(String clName, String methName) {
    try {
      Class cl = Class.forName(clName);
      Constructor constr = cl.getConstructor(null);
      Object obj = constr.newInstance(null);
      Method method = cl.getMethod(methName, null);
      return (String) method.invoke(obj, null);
    } catch (ClassNotFoundException e) {
      System.err.println("Class not found. " + e);
      System.exit(1);
    } catch (IllegalAccessException e) {
      System.err.println("Illegal access error. " + e);
      System.exit(1);
    } catch (NoSuchMethodException e) {
      System.err.println("Method not found. " + e);
      System.exit(1);
    } catch (InvocationTargetException e) {
      System.err.println("Invocation target error. " + e);
      System.exit(1);
    } catch (InstantiationException e) {
      System.err.println("Instantiation error. " + e);
      System.exit(1);
    }
    return "";
  }

  /**
   * Generates random boolean value.
   *
   * Equal probability of true and false
   *
   * @param type "boolean"
   */
  public static String buildBoolValue(String type) {
    int n = randomGenerator.nextInt(100);
    if (n < 50)
      return "true";
    else
      return "false";
  }

  /**
   * Generates and (if necessary) wraps new random value.
   *
   * Uniform distribution between l (default 0) and u (default 100)
   *
   * @param type "int", with optional suffix range "[l;u]"
   */
  public static String buildIntValue(String type) {
    int lowBound = 0;
    int upBound = 100;
    if (type.indexOf("[") != -1) {
      String range = type.substring(type.indexOf("[") + 1, type.indexOf("]"));
      if (range.indexOf(";") == -1)
        return buildUserDefValue(range, "newIntValue");
      lowBound = Integer.parseInt(range.substring(0, range.indexOf(";")));
      upBound = Integer.parseInt(range.substring(range.indexOf(";") + 1));
    }
    int n = lowBound + randomGenerator.nextInt(upBound - lowBound + 1);
    return Integer.toString(n);
  }

  /**
   * Generates and (if necessary) wraps new random value.
   *
   * Uniform distribution between l (default 0) and u (default 100)
   *
   * @param type "float" or "double", with optional suffix range "[l;u]"
   */
  public static String buildRealValue(String type) {
    int lowBound = 0;
    int upBound = 100;
    if (type.indexOf("[") != -1) {
      String range = type.substring(type.indexOf("[") + 1, type.indexOf("]"));
      if (range.indexOf(";") == -1)
        return buildUserDefValue(range, "newRealValue");
      lowBound = Integer.parseInt(range.substring(0, range.indexOf(";")));
      upBound = Integer.parseInt(range.substring(range.indexOf(";") + 1));
    }
    double n = lowBound + randomGenerator.nextInt(1000 * (upBound - lowBound) + 1) / 1000.0;
    return Double.toString(n);
  }

  /**
   * Generates and (if necessary) wraps new random value.
   *
   * Alphanumeric characters chosen randomly, according to uniform distribution.
   * Exponential distribution of length.
   *
   * @param type "String".
   */
  public static String buildStringValue(String type) {
    String str;
    int i = type.indexOf(";");
    int j = type.indexOf("%");
    if (i != -1 && j != -1) {
      double nullProb = (double) Integer.parseInt(type.substring(i + 1, j)) / 100.0;
      str = stringGenerator.newString(nullProb);
      if (str == null)
        return null;
    } else if (type.indexOf("[") != -1) {
      String generator = type.substring(type.indexOf("[") + 1, type.indexOf("]"));
      return "\"" + buildUserDefValue(generator, "newStringValue") + "\"";
    } else {
      str = stringGenerator.newString();
    }
    return "\"" + str + "\"";
  }

  /**
   * Maps class name to concrete class, if not already such.
   *
   * @param className: String Class to be mapped.
   * @return concrete class name: String
   */
  public String mapToConcreteClass(String className) {
    String newClassName = className;
    if (className.indexOf("[") != -1)
      newClassName = className.substring(0, className.indexOf("["));
    if (concreteTypes.containsKey(className)) {
      List classes = (List) concreteTypes.get(className);
      int classNum = classes.size();
      int classIndex = randomGenerator.nextInt(classNum);
      newClassName = (String) classes.get(classIndex);
    }
    return newClassName;
  }

  /**
   * Maps class name to list of concrete classe implementing it.
   *
   * @param className: String Class to be mapped.
   * @return list of concrete class names: List&lt;String&gt;
   */
  public List concreteTypes(String className) {
    if (className.indexOf("[") != -1)
      className = className.substring(0, className.indexOf("["));
    List classes = new LinkedList();
    classes.add(className);
    if (concreteTypes.containsKey(className))
      classes = (List) concreteTypes.get(className);
    return classes;
  }

  /**
   * Builds constructor and returns it.
   *
   * Redirects to buildConstructor(String,String,int).
   */
  Chromosome buildConstructor(String className, String objId) {
    return buildConstructor(className, objId, -1);
  }

  /**
   * Builds constructor and returns it.
   *
   * Randomly chooses among available constructors. Constructed objects are
   * assigned to $xN, where N is an incremented integer.
   * <p>
   * Primitive type parameters are assigned random values. Recursively needed
   * constructors are prepended.
   *
   * @param className name of the constructor's class
   * @param objId     name of left hand side object
   * @return constructor: Chromosome (e.g., "$xN=A(int)@12")
   */
  Chromosome buildConstructor(String className, String objId, int constrIndex) {
    String objVar = "$x" + idCounter;
    if (objId != null)
      objVar = objId;
    else
      idCounter++;
    if (className.indexOf("[") != -1) {
      String percent = className.substring(className.indexOf(";") + 1, className.indexOf("%"));
      int nullProb = Integer.parseInt(percent);
      className = className.substring(0, className.indexOf("["));
      if (randomGenerator.nextInt(100) <= nullProb) {
        Chromosome nullConstr = new Chromosome();
        className = mapToConcreteClass(className);
        ConstructorInvocation constrInv = new NullConstructorInvocation(objVar, className);
        nullConstr.addAction(constrInv);
        return nullConstr;
      }
    }
    Chromosome neededConstr = new Chromosome();
    className = mapToConcreteClass(className);
    List constrList = (List) constructors.get(className);
    int constrNum = constrList.size();
    if (constrIndex == -1)
      constrIndex = randomGenerator.nextInt(constrNum);
    MethodSignature constrSign = (MethodSignature) constrList.get(constrIndex);
    List formalParams = (List) constrSign.getParameters();
    List actualParams = new LinkedList();
    Iterator i = formalParams.iterator();
    while (i.hasNext()) {
      String paramType = (String) i.next();
      if (isPrimitiveType(paramType)) {
        actualParams.add(buildValue(paramType));
      } else {
        Chromosome newConstr = buildConstructor(paramType, null);
        neededConstr.append(newConstr);
        String neededConstrId = neededConstr.getObjectId(concreteTypes(paramType));
        actualParams.add(neededConstrId);
      }
    }
    ConstructorInvocation constrInv = new ConstructorInvocation(objVar, constrSign.getName(), formalParams,
        actualParams);
    neededConstr.addAction(constrInv);
    return neededConstr;
  }

  /**
   * Adds constructor at beginning of chromosome.
   *
   * @param constrIndex Index in constructors list
   */
  public void prependConstructor(int constrIndex) {
    Chromosome chrom = buildConstructor(classUnderTest, null, constrIndex);
    chrom.append(chromosome);
    chromosome = chrom;
  }

  /**
   * Adds constructor at beginning of chromosome.
   *
   * @param className Name of the constructor to prepend
   */
  public void prependConstructor(String className) {
    chromosome = prependConstructor(className, null);
  }

  /**
   * Adds constructor at beginning of chromosome.
   *
   * @param className Name of the constructor to prepend
   * @param objId     Name of target object
   */
  public Chromosome prependConstructor(String className, String objId) {
    Chromosome chrom = buildConstructor(className, objId);
    chrom.append(chromosome);
    return chrom;
  }

  /**
   * Returns a MethodSignature object matching the parameters.
   *
   * @param className  Enclosing class
   * @param methodName Method
   * @param params     Method parameter types
   *
   * @return method signature object (class: MethodSignature)
   */
  private MethodSignature lookForMethod(String className, String methodName, String[] params) {
    List signatureList = (List) methods.get(className);
    Iterator i = signatureList.iterator();
    while (i.hasNext()) {
      MethodSignature sign = (MethodSignature) i.next();
      String curMethodName = sign.getName();
      List curParams = (List) sign.getParameters();
      if (!curMethodName.equals(methodName) || curParams.size() != params.length)
        continue;
      Iterator j = curParams.iterator();
      boolean found = true;
      int k = 0;
      while (j.hasNext()) {
        String curParam = (String) j.next();
        if (curParam.indexOf("[") != -1)
          curParam = curParam.substring(0, curParam.indexOf("["));
        if (params[k].indexOf("[") != -1)
          params[k] = params[k].substring(0, params[k].indexOf("["));
        if (!curParam.equals(params[k++])) {
          found = false;
          break;
        }
      }
      if (found)
        return sign;
    }
    return null;
  }

  /**
   * Returns the index of MethodSignature object matching the parameters.
   *
   * @param constr Full constructor name
   *
   * @return index of method signature object (class: MethodSignature)
   */
  private int lookForConstructor(String constr) {
    String constr1 = constr.substring(0, constr.indexOf("("));
    String className = constr1.substring(0, constr1.lastIndexOf("."));
    String constrName = constr1.substring(constr1.lastIndexOf(".") + 1);
    String[] params = constr.substring(constr.indexOf("(") + 1, constr.indexOf(")")).split(",");
    if (params.length == 1 && params[0].equals(""))
      params = new String[0];
    List signatureList = (List) constructors.get(className);
    int constrIndex = -1;
    Iterator i = signatureList.iterator();
    while (i.hasNext()) {
      MethodSignature sign;
      sign = (MethodSignature) i.next();
      constrIndex++;
      String curConstrName = sign.getName();
      List curParams = (List) sign.getParameters();
      if (!curConstrName.equals(constrName) || curParams.size() != params.length)
        continue;
      Iterator j = curParams.iterator();
      boolean found = true;
      int k = 0;
      while (j.hasNext()) {
        String curParam = (String) j.next();
        if (!curParam.equals(params[k++])) {
          found = false;
          break;
        }
      }
      if (found)
        return constrIndex;
    }
    return -1;
  }

  /**
   * Builds method call and returns it.
   *
   * The method to call is identified by the complete signature.
   * <p>
   * Primitive type parameters are assigned random values. Object type parameters
   * are constructed.
   *
   * @param fullMethodName Example: A.m(int,B)
   * @param objId          Example: $x0
   * @return Method call. Example: "$x0.m(int,B)@10".
   */
  private Chromosome buildMethodCall(String fullMethodName, String objId) {
    Chromosome neededConstr = new Chromosome();
    String fullMethodName1 = fullMethodName.substring(0, fullMethodName.indexOf("("));
    String className = fullMethodName1.substring(0, fullMethodName1.lastIndexOf("."));
    String methodName = fullMethodName1.substring(fullMethodName1.lastIndexOf(".") + 1);
    String[] paramString = fullMethodName.substring(fullMethodName.indexOf("(") + 1, fullMethodName.indexOf(")"))
        .split(",");
    if (paramString.length == 1 && paramString[0].equals(""))
      paramString = new String[0];
    MethodSignature methodSign = lookForMethod(className, methodName, paramString);
    List formalParams = (List) methodSign.getParameters();
    List actualParams = new LinkedList();
    if (objId == null)
      objId = chromosome.getObjectId(concreteTypes(className));
    Iterator i = formalParams.iterator();
    while (i.hasNext()) {
      String paramType = (String) i.next();
      if (isPrimitiveType(paramType)) {
        actualParams.add(buildValue(paramType));
      } else {
        Chromosome newConstr = buildConstructor(paramType, null);
        neededConstr.append(newConstr);
        String neededConstrId = newConstr.getObjectId(concreteTypes(paramType));
        actualParams.add(neededConstrId);
      }
    }
    MethodInvocation methodInv = new MethodInvocation(objId, methodSign.getName(), formalParams, actualParams);
    neededConstr.addAction(methodInv);
    return neededConstr;
  }

  /**
   * Adds method call at end of chromosome.
   *
   * @param fullMethodName Example: A.m(int,int)
   */
  public void appendMethodCall(String fullMethodName, String objId) {
    Chromosome chrom = buildMethodCall(fullMethodName, objId);
    chromosome.append(chrom);
  }

  /**
   * Adds a randomly selected method call at end of chromosome.
   *
   * Insertion is done with probability 0.5. If done, the inserted method is
   * randomly selected among those in the class passed as a parameter. After
   * insertion, appendMethodCall is re-invoked, so that another insertion is made
   * with probability 0.5. In this way, the probability of N insertions is
   * (0.5)^N.
   *
   * @param className Name of enclosing class
   */
  public void appendRandomMethodCall(String className, String objId) {
    if (randomGenerator.nextInt(100) < 50)
      return;
    List methodList = (List) methods.get(className);
    if (methodList == null)
      return;
    int methodNum = methodList.size();
    int methodIndex = randomGenerator.nextInt(methodNum);
    MethodSignature methodSign = (MethodSignature) methodList.get(methodIndex);
    String fullMethodName = className + "." + methodSign.getName();
    fullMethodName += "(";
    List params = (List) methodSign.getParameters();
    Iterator i = params.iterator();
    boolean first = true;
    while (i.hasNext()) {
      String paramType = (String) i.next();
      if (first)
        first = false;
      else
        fullMethodName += ",";
      fullMethodName += paramType;
    }
    fullMethodName += ")";
    appendMethodCall(fullMethodName, objId);
    appendRandomMethodCall(className, objId);
  }

  /**
   * Adds a randomly selected method call inside a chromosome.
   *
   */
  public void insertRandomMethodCall() {
    if (chromosome.size() == 0) {
      buildNewChromosome();
      return;
    }
    int initSize = chromosome.size();
    Chromosome[] parts = chromosome.randomSplitIntoTwo();
    Chromosome chromHead = parts[0];
    Chromosome chromTail = parts[1];
    List actions = chromHead.getActions();
    if (actions.size() == 0)
      return;
    Action act = (Action) actions.get(actions.size() - 1);
    String className = act.getName();
    String objId = act.getObject();
    if (act instanceof NullConstructorInvocation)
      return;
    chromosome = chromHead;
    appendRandomMethodCall(className, objId);
    chromosome.append(chromTail);
    if (chromosome.size() == initSize)
      insertRandomMethodCall();
  }

  /**
   * Removes a randomly selected method call inside a chromosome.
   *
   * The last method call cannot be removed, being the purpose of the test case.
   */
  public void removeRandomMethodCall() {
    int initSize = chromosome.size();
    chromosome.removeMethodCall();
    if (chromosome.size() != initSize)
      if (randomGenerator.nextInt(100) < 50)
        removeRandomMethodCall();
  }

  /**
   * Splits chromosomes into two and mixes the two parts.
   */
  public void crossover(Chromosome chrom) {
    Chromosome[] parts1 = chromosome.randomSplitIntoTwo();
    Chromosome[] parts2 = chrom.randomSplitIntoTwo();
    Chromosome head, tail;
    if (randomGenerator.nextInt(100) < 50) {
      head = parts1[0];
      tail = parts2[1];
    } else {
      head = parts2[0];
      tail = parts1[1];
    }
    idCounter = head.renameObjects(idCounter);
    idCounter = tail.renameObjects(idCounter);
    chromosome = head;
    chromosome.append(tail);
    chromosome.fixDefUse();
  }

  /**
   * Reads constructor and method signatures from file.
   *
   * Signatures are read from an input text file formatted in the following way:
   * 
   * <pre>
   * A.A()
   * A.A(int)
   * B.B()
   * B.B(int[0;10])
   * A.m1()
   * A.m2(int)
   * A.m3(int, int)
   * A.m(int, Comparable[null;5%])
   * #
   * MyInteger as Comparable
   * String as Comparable
   * </pre>
   * 
   * Constructors for all classes used as parameter types MUST be included. The
   * method under test is the last method in the file.
   *
   * @param fileName File with signatures.
   */
  public void readSignatures(String fileName) {
    try {
      Set usedClassNames = new HashSet();
      String s, r = "";
      BufferedReader in = new BufferedReader(new FileReader(fileName));
      while ((s = in.readLine()) != null && !s.equals("#")) {
        s = s.replaceAll("\\s+", "");
        if (s.length() > 0) {
          String s1 = s.substring(0, s.indexOf("("));
          String className = s1.substring(0, s1.lastIndexOf("."));
          String methodName = s1.substring(s1.lastIndexOf(".") + 1);
          String[] paramNames = s.substring(s.indexOf("(") + 1, s.indexOf(")")).split(",");

          if (paramNames.length == 1 && paramNames[0].equals(""))
            paramNames = new String[0];

          List params = new LinkedList();

          // params cuar constructor
          for (int i = 0; i < paramNames.length; i++) {
            params.add(paramNames[i]);
            String usedClass = paramNames[i];
            if (paramNames[i].indexOf("[") != -1)
              usedClass = paramNames[i].substring(0, paramNames[i].indexOf("["));
            if (!isPrimitiveType(paramNames[i])) {
              usedClassNames.add(usedClass);
            }

          }

          String simpleClassName = className.substring(className.lastIndexOf(".") + 1);
          if (simpleClassName.equals(methodName)) {
            MethodSignature methodSign = new MethodSignature(className, params);
            addConstructor(methodSign);
          } else {
            MethodSignature methodSign = new MethodSignature(methodName, params);
            addMethod(className, methodSign);
            usedClassNames.add(className);
          }
          r = s;
        }
      }

      String r1 = r.substring(0, r.indexOf("("));
      String classUnderTest = r1.substring(0, r1.lastIndexOf(".")); // => BinaryTree
      setClassUnderTest(classUnderTest);

      // Binary tail

      while ((s = in.readLine()) != null) {
        if (s.length() > 0) {
          String className = s.substring(0, s.indexOf(" as ")).trim();
          String typeName = s.substring(s.indexOf(" as ") + 4).trim();
          addConcreteType(typeName, className);
        }
      }
      in.close();
      // Kiem tra constructor
      checkConstructorsAvailable(usedClassNames);
    } catch (IOException e) {
      System.err.println("IO error: " + fileName);
      System.exit(1);
    }
  }

  /**
   * Checks if for all used classes constructors are available.
   *
   * Execution is interrupted with an error if no constructor is available for
   * some used class.
   *
   * @param usedClasses Set of all used classes in any signatures.
   */
  private void checkConstructorsAvailable(Set usedClasses) {
    boolean error = false;
    String cl = "";
    Iterator k = concreteTypes.keySet().iterator();
    while (!error && k.hasNext()) {
      String absType = (String) k.next();
      List types = (List) concreteTypes.get(absType);
      Iterator j = types.iterator();
      while (!error && j.hasNext()) {
        cl = (String) j.next();
        if (!constructors.containsKey(cl))
          error = true;
      }
    }
    Iterator i = usedClasses.iterator();
    while (!error && i.hasNext()) {
      cl = (String) i.next();
      if (!constructors.containsKey(cl) && !concreteTypes.containsKey(cl))
        error = true;
    }
    if (error) {
      System.err.println("Missing constructor for class: " + cl);
      System.exit(1);
    }
  }

  /**
   * Mutation operator: randomly changes one of the input values.
   *
   * Transforms the current chromosome, changing one of the input values, selected
   * randomly. The new input value is generated randomly.
   * 
   * <pre>
   * Test case:
   * $a=A():$b=B(int):$b.c1():$a.m(int, $b) @ 1, 5
   * 
   * Test case':
   * $a=A():$b=B(int):$b.c1():$a.m(int, $b) @ 6, 5
   * </pre>
   */
  public void changeInputValue() {
    chromosome.changeInputValue();
  }

  /**
   * Mutation operator: randomly changes one of the constructors in the
   * chromosome.
   */
  public void changeConstructor() {
    chromosome.replaceConstructor(this);
  }

  /**
   * Reads signatures from file and prints chromosome.
   *
   * The last method in signature file is assumed to be the method under test.
   *
   * @param args[0] Text file with signatures.
   */
  public static void main(String[] args) {
    try {
      if (args.length == 0) {
        System.err.println("Usage: java ChromosomeFormer signature-file");
        System.exit(1);
      }
      ChromosomeFormer chromFormer = new ChromosomeFormer();
      chromFormer.readSignatures(args[0]);
      chromFormer.buildNewChromosome();
      System.out.println(chromFormer.getChromosome());
      int c = System.in.read();
      while (c != -1) {
        int ran = randomGenerator.nextInt(100);
        if (ran < 20) {
          System.out.println("Input value changed.");
          chromFormer.changeInputValue();
        } else if (ran < 40) {
          System.out.println("Constructor changed.");
          chromFormer.changeConstructor();
        } else if (ran < 60) {
          System.out.println("Method call(s) randomly inserted.");
          chromFormer.insertRandomMethodCall();
        } else if (ran < 80) {
          System.out.println("Method call(s) randomly removed.");
          chromFormer.removeRandomMethodCall();
        } else {
          System.out.print("Crossover: ");
          Chromosome chrom = chromFormer.getChromosome();
          chromFormer.buildNewChromosome();
          chromFormer.insertRandomMethodCall();
          System.out.println(chromFormer.getChromosome());
          chromFormer.crossover(chrom);
        }
        System.out.println(chromFormer.getChromosome());
        c = System.in.read();
      }
    } catch (IOException e) {
      System.err.println("IO error: " + args[0]);
      System.exit(1);
    }
  }
}

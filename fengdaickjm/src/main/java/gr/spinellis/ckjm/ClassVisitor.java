/*
 * (C) Copyright 2005 Diomidis Spinellis
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package gr.spinellis.ckjm;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeSet;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;

/**
 * Visit a class updating its Chidamber-Kemerer metrics.
 *
 * @see ClassMetrics
 * @version $Revision: 1.21 $
 * @author <a href="http://www.spinellis.gr">Diomidis Spinellis</a>
 */
public class ClassVisitor extends org.apache.bcel.classfile.EmptyVisitor {
	/** The class being visited. */
	private JavaClass visitedClass;
	/** The class's constant pool. */
	private ConstantPoolGen cp;
	/** The class's fully qualified name. */
	private String myClassName;
	/** The container where metrics for all classes are stored. */
	private ClassMetricsContainer cmap;
	/** The emtrics for the class being visited. */
	private ClassMetrics cm;
	/*
	 * Classes encountered. Its cardinality is used for calculating the CBO.
	 */
	private HashSet<String> efferentCoupledClasses = new HashSet<String>();
	/**
	 * Methods encountered. Its cardinality is used for calculating the RFC.
	 */
	private HashSet<String> responseSet = new HashSet<String>();
	/**
	 * Use of fields in methods. Its contents are used for calculating the LCOM.
	 * We use a Tree rather than a Hash to calculate the intersection in O(n)
	 * instead of O(n*n).
	 */
	ArrayList<TreeSet<String>> mi = new ArrayList<TreeSet<String>>();

	public ClassVisitor(JavaClass jc, ClassMetricsContainer classMap) {
		visitedClass = jc;
		cp = new ConstantPoolGen(visitedClass.getConstantPool());
		cmap = classMap;
		myClassName = jc.getClassName();
		cm = cmap.getMetrics(myClassName);
		
		
		
		
	}

	/** Return the class's metrics container. */
	public ClassMetrics getMetrics() {
		return cm;
	}

	public void start() {
		visitJavaClass(visitedClass);
	}
	
	public JavaClass getJavaClass(){
		return visitedClass;
	}

	/** Calculate the class's metrics based on its elements. */
	@Override
	public void visitJavaClass(JavaClass jc) {
		String super_name = jc.getSuperclassName();
		jc.getPackageName();

		cm.setVisited();
		if (jc.isPublic())
			cm.setPublic();
		ClassMetrics pm = cmap.getMetrics(super_name);

		pm.incNoc();
		try {
//			final JavaClass[] superClasses = jc.getSuperClasses();
//			System.out.println(jc.getSuperClass());
//			System.out.println(jc.getSuperClasses().length);
			cm.setDit(jc.getSuperClasses().length);
		} catch (ClassNotFoundException ex) {
//			System.err.println("Error obtaining all superclasses of " + jc);
		}
		registerCoupling(super_name);

		String ifs[] = jc.getInterfaceNames();
		/* Measuring decision: couple interfaces */
		for (int i = 0; i < ifs.length; i++)
			registerCoupling(ifs[i]);

		Field[] fields = jc.getFields();
		for (int i = 0; i < fields.length; i++)
			fields[i].accept(this);

		Method[] methods = jc.getMethods();
		for (int i = 0; i < methods.length; i++)
			methods[i].accept(this);
	}

	public  String getresult(){
//		System.out.println(result.toString().replace("[", "").replace("]", "").replace(" ", ""));
    	return result.toString().replace("[", "").replace("]", "").replace(" ", "");
    }
	
	public HashSet<String> result = new HashSet<String>();
	/** Add a given class to the classes we are coupled to */
	public void registerCoupling(String className) {
		//原版
		/* Measuring decision: don't couple to Java SDK */
//		if ((MetricsFilter.isJdkIncluded() || !ClassMetrics.isJdkClass(className)) && !myClassName.equals(className)) {
//			efferentCoupledClasses.add(className);
////			System.out.println(String.format("%s被调用于%s",className,myClassName));
//			result.add(String.format("%s被调用于%s",className,myClassName));
//			cmap.getMetrics(className).addAfferentCoupling(myClassName);
//		}
		//改进版
		//&& ClassMetrics.isfengdaiclass(className)
		if ((MetricsFilter.isJdkIncluded() || !ClassMetrics.isJdkClass(className)) && !myClassName.equals(className)) {
			efferentCoupledClasses.add(className);
//			System.out.println(String.format("%s被调用于%s",className,myClassName));
//			result.add(String.format("%s被调用于%s",className,myClassName));
//			System.out.println(String.format("%s被%s调用",className,myClassName));
			cmap.getMetrics(className).addAfferentCoupling(myClassName);
		}
	}

	/* Add the type's class to the classes we are coupled to */
	public void registerCoupling(Type t) {
		registerCoupling(className(t));
	}

	/* Add a given class to the classes we are coupled to */
	void registerFieldAccess(String className, String fieldName) {
		registerCoupling(className);
		if (className.equals(myClassName))
			mi.get(mi.size() - 1).add(fieldName);
	}

	/* Add a given method to our response set */
	void registerMethodInvocation(String className, String methodName, Type[] args) {
//		if ((MetricsFilter.isJdkIncluded() || !ClassMetrics.isJdkClass(className)) && !myClassName.equals(className)&& ClassMetrics.isfengdaiclass(className)) {
//			System.out.println(String.format("%s的方法%s被调用于%s",className,methodName,myClassName));
//		}
		registerCoupling(className);
		/*
		 * Measuring decision: calls to JDK methods are included in the RFC
		 * calculation
		 */
		incRFC(className, methodName, args);
	}
	
	/* Add a given method to our response set */
	void registerMethodInvocation(String className, String diaoyongmethodname,String beidiaoyongmethodName, Type[] args) {
		//&& ClassMetrics.isfengdaiclass(className)||ClassMetrics.isfundsclass(className)
		if ((MetricsFilter.isJdkIncluded() || !ClassMetrics.isJdkClass(className)) ) {
//			System.out.println(String.format("%s的方法%s被调用于%s的方法%s",className,beidiaoyongmethodName,myClassName,diaoyongmethodname));
			result.add(String.format("%s的方法%s被调用于%s的方法%s",className,beidiaoyongmethodName,myClassName,diaoyongmethodname));
		}
	}

	/** Called when a field access is encountered. */
	@Override
	public void visitField(Field field) {
		registerCoupling(field.getType());
	}

	/**
	 * Called when encountering a method that should be included in the class's
	 * RFC.
	 */
	private void incRFC(String className, String methodName, Type[] arguments) {
		String argumentList = Arrays.asList(arguments).toString();
		// remove [ ] chars from begin and end
		String args = argumentList.substring(1, argumentList.length() - 1);
		String signature = className + "." + methodName + "(" + args + ")";
		responseSet.add(signature);
	}

	/** Called when a method invocation is encountered. */
	@Override
	public void visitMethod(Method method) {
//		System.out.println(visitedClass.getClassName());
//		System.out.println(visitedClass.getMethods()[0].getName());
//		try {
//			System.out.println(visitedClass.getSuperClass().getMethods()[0].getName());
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		MethodGen mg = new MethodGen(method, visitedClass.getClassName(), cp);

		mg.getReturnType();
		Type[] argTypes = mg.getArgumentTypes();

		registerCoupling(mg.getReturnType());
		for (int i = 0; i < argTypes.length; i++)
			registerCoupling(argTypes[i]);

		String[] exceptions = mg.getExceptions();
		for (int i = 0; i < exceptions.length; i++)
			registerCoupling(exceptions[i]);

		/* Measuring decision: A class's own methods contribute to its RFC */
		incRFC(myClassName, method.getName(), argTypes);

		cm.incWmc();
		if (Modifier.isPublic(method.getModifiers()))
			cm.incNpm();
		mi.add(new TreeSet<String>());
		MethodVisitor factory = new MethodVisitor(mg, this);
		factory.start();
	}

	/** Return a class name associated with a type. */
	static String className(Type t) {
		t.toString();

		if (t.getType() <= Constants.T_VOID) {
			return "java.PRIMITIVE";
		} else if (t instanceof ArrayType) {
			ArrayType at = (ArrayType) t;
			return className(at.getBasicType());
		} else {
			return t.toString();
		}
	}

	/** Do final accounting at the end of the visit. 
	 * @return */
	public HashSet<String> end() {
		cm.setCbo(efferentCoupledClasses.size());
		cm.setRfc(responseSet.size());
		/*
		 * Calculate LCOM as |P| - |Q| if |P| - |Q| > 0 or 0 otherwise where P =
		 * set of all empty set intersections Q = set of all nonempty set
		 * intersections
		 */
		int lcom = 0;
		for (int i = 0; i < mi.size(); i++)
			for (int j = i + 1; j < mi.size(); j++) {
				/* A shallow unknown-type copy is enough */
				TreeSet<?> intersection = (TreeSet<?>) mi.get(i).clone();
				intersection.retainAll(mi.get(j));
				if (intersection.size() == 0)
					lcom++;
				else
					lcom--;
			}
		cm.setLcom(lcom > 0 ? lcom : 0);
		return result;
	}
}

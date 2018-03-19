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

package com.fengdai.ckjm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;

import org.apache.bcel.Const;
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.BootstrapMethod;
import org.apache.bcel.classfile.BootstrapMethods;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.AnnotationEntryGen;
import org.apache.bcel.generic.ArrayInstruction;
import org.apache.bcel.generic.CHECKCAST;
import org.apache.bcel.generic.CodeExceptionGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.ElementValuePairGen;
import org.apache.bcel.generic.EmptyVisitor;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.INSTANCEOF;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.LocalVariableInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.Type;

/**
 * Visit a method calculating the class's Chidamber-Kemerer metrics. A helper
 * class for ClassVisitor.
 *
 * @see ClassVisitor
 * @version $Revision: 1.8 $
 * @author <a href="http://www.spinellis.gr">Diomidis Spinellis</a>
 */
class MethodVisitor extends EmptyVisitor {
	/** Method generation template. */
	private MethodGen mg;
	/* The class's constant pool. */
	private ConstantPoolGen cp;
	/** The visitor of the class the method visitor is in. */
	private ClassVisitor cv;
	/** The metrics of the class the method visitor is in. */
	private ClassMetrics cm;

	private String[] allClassfiles;

	/**
	 * Constructor.
	 * 
	 * @param allClassfiles
	 */
	MethodVisitor(MethodGen m, ClassVisitor c, String[] Classesfiles) {
		mg = m;
		cv = c;
		cp = mg.getConstantPool();
		cm = cv.getMetrics();
		allClassfiles = Classesfiles;
	}

	/** Start the method's visit. */
	public void start() {
		if (!mg.isAbstract() && !mg.isNative()) {
			for (InstructionHandle ih = mg.getInstructionList().getStart(); ih != null; ih = ih.getNext()) {
				Instruction i = ih.getInstruction();

				if (!visitInstruction(i))
					i.accept(this);
			}
			if (mg.getAnnotationEntries().length != 0) {
				for (AnnotationEntryGen annot : mg.getAnnotationEntries()) {
					List<ElementValuePairGen> tElementValuePairGens = annot.getValues();
					for (ElementValuePairGen var : tElementValuePairGens) {

						try {
							// System.out.println(mg.getClassName()+"的方法"+
							// mg.getName()+"的注解"+var.getElementNameValuePair().getValue());
							cv.setAnnotPairs(mg.getClassName() + "的方法" + mg.getName() + "的注解"
									+ var.getElementNameValuePair().getValue());
						} catch (Exception e) {
							System.out.println(e);
						}

					}
				}
			}
			updateExceptionHandlers();
		}
	}

	/** Visit a single instruction. */
	private boolean visitInstruction(Instruction i) {
		short opcode = i.getOpcode();

		return ((InstructionConstants.INSTRUCTIONS[opcode] != null) && !(i instanceof ConstantPushInstruction)
				&& !(i instanceof ReturnInstruction));
	}

	/** Local variable use. */
	@Override
	public void visitLocalVariableInstruction(LocalVariableInstruction i) {
		if (i.getOpcode() != Constants.IINC)
			cv.registerCoupling(i.getType(cp));
	}

	/** Array use. */
	@Override
	public void visitArrayInstruction(ArrayInstruction i) {
		cv.registerCoupling(i.getType(cp));
	}

	/** Field access. */
	@Override
	public void visitFieldInstruction(FieldInstruction i) {
		cv.registerFieldAccess(i.getClassName(cp), i.getFieldName(cp));
		cv.registerCoupling(i.getFieldType(cp));
	}

	/** Method invocation. */
	@Override
	public void visitInvokeInstruction(InvokeInstruction i) {

		Type[] argTypes = i.getArgumentTypes(cp);
		StringBuffer diaoyongMethodParamsStr = new StringBuffer();
		for (int j = 0; j < argTypes.length; j++)
			cv.registerCoupling(argTypes[j]);
		Type[] diaoyongArgTypes = mg.getArgumentTypes();
		for (Type temp : diaoyongArgTypes)
			diaoyongMethodParamsStr.append(temp.toString() + "-");
		cv.registerCoupling(i.getReturnType(cp));
		String beidiaoyongclassname = i.getClassName(cp);

		// System.out.println("调用的方法所在的类（子类）" + beidiaoyongclassname + "方法" +
		// i.getMethodName(cp));
		// System.out.println("调用方class名称" + cv.getJavaClass().getClassName());
		// System.out.println("调用方父class名称" +
		// cv.getJavaClass().getSuperclassName());
		// //&&mg.getName().contains("lambda$0")
		// System.out.println(mg.getName());
		// if (beidiaoyongclassname.contains("accept")&&
		// i.getMethodName(cp).contains("accept")&&cv.getJavaClass().getClassName().contains("com.fengdai.shop.service.impl.SettlementStrategyRelateShopServiceImpl$1"))
		// {
		// System.out.println("com.fengdai.activity.form.DitchForm");
		// }
		StringBuffer realBeiDiaoMethodParamsStr = new StringBuffer();
		// 处理lambda
		String beidiaoyongMethod = "";
		if (!i.getMethodName(cp).equals("accept")) {
			beidiaoyongMethod = i.getMethodName(cp);
		}

		if (i.getOpcode() == Const.INVOKEDYNAMIC) {
			// System.out.println(cp.getConstant(i.getIndex()));
			// System.out.println(cv.getJavaClass());
			// System.out.println(cv.getJavaClass().getAttributes()[1].getClass().getName());
			BootstrapMethods temp1 = null;
			for (Attribute var : cv.getJavaClass().getAttributes()) {
				// System.out.println(var.getClass().getName());
				if (var.getClass().getName().contains("BootstrapMethods"))
					temp1 = (BootstrapMethods) var;
			}

			BootstrapMethod bootstrapMethod = temp1.getBootstrapMethods()[0];

			for (int ss : bootstrapMethod.getBootstrapArguments()) {
				// System.out.println(cp.getConstantPool().constantToString
				// (cp.getConstant(ss)));

			}
			for (String var : bootstrapMethod.getArgumentsString(temp1.getConstantPool())) {
				// System.out.println(var);
				if (var.contains("invokeStatic") && var.contains("lambda$")) {
					String[] lambdaDetail = var.split(" ");
					// System.out.println("lambda类与方法名："+lambdaDetail[1].substring(0,
					// lambdaDetail[1].indexOf(".lambda"))+":"+lambdaDetail[1].substring(lambdaDetail[1].indexOf("lambda")));
					beidiaoyongMethod = lambdaDetail[1];
					// System.out.println("lambda参数：");
					for (int j = 2; j < lambdaDetail.length; j++) {

						String[] methodtemp = lambdaDetail[j].replace("(", "").replaceAll("\\).*", "")
								.replace("Lcom/", "com/").replace("/", ".").split(";");
						for (String methodpara : methodtemp) {
							// System.out.println(methodpara);
							realBeiDiaoMethodParamsStr.append(methodpara + "-");
						}
					}
					beidiaoyongclassname = lambdaDetail[1].substring(0, lambdaDetail[1].indexOf(".lambda"));
					beidiaoyongMethod = lambdaDetail[1].substring(lambdaDetail[1].indexOf("lambda"));

				}
			}

		}

		JavaClass superclass = null;
		try {
			superclass = cv.getJavaClass().getSuperClass();
		} catch (ClassNotFoundException e) {
			// e.printStackTrace();
//			System.err.println("superclass is" + cv.getJavaClass().getSuperclassName());
//			System.err.println("调用方父类" + cv.getJavaClass().getClassName() + "查找失败" + beidiaoyongclassname + "调用方："
//					+ mg.getName() + ":" + diaoyongMethodParamsStr);
		}

		if (superclass == null) {
			// 处理调用的方法为父类 但是父类没有被加载，如第三方的jar包,此处暴力处理不分析jar包里面的关系，第三方不关注
			// 如果把beidiaoyongclassname直接赋值为父类也不对，无法判断出是否是自己的类，继承时候父类的所有的东西都到了子类
			// 所以不做区分，beidiaoyongclassname不管父类还是自己，都暴力判断为调用的是自己的方法，
			for (Type type : i.getArgumentTypes(cp)) {
				realBeiDiaoMethodParamsStr.append(type.toString() + "-");
			}
		} else
		// 先判断调用的方法是父类的还是自己的方法,如果都不是即为调用的第三方方法 调用的有可能不是父类是爷爷类 所以循环查找 知道找到object
		if (superclass != null && (cv.getJavaClass().getClassName().contains(beidiaoyongclassname)
				|| cv.getJavaClass().getSuperclassName().contains(beidiaoyongclassname))) {
			// 如果父类是object 表明这个类被调用的方法就是自己内部的方法
			if (superclass.getClassName().equals("java.lang.Object") && realBeiDiaoMethodParamsStr.length() == 0) {
				for (Type type : i.getArgumentTypes(cp)) {
					realBeiDiaoMethodParamsStr.append(type.toString() + "-");
				}
			}
			// 判斷是否調用的是父类還是爷爷类
			boolean fatherflag=false;
			while (!superclass.getClassName().equals("java.lang.Object")) {
				if (!superclass.getClassName().equals("java.lang.Object") && !superclass.isInterface()) {
					for (org.apache.bcel.classfile.Method supermethod : superclass.getMethods()) {
						ArrayList<Type> supermethodParams = new ArrayList<Type>();
						ArrayList<Type> methodParams = new ArrayList<Type>();
						if (supermethod.getName().equals(i.getMethodName(cp))) {
							if (supermethod.getName().equals("<init>")) {
								;
							} else {
								if (supermethod.getName().equals(i.getMethodName(cp))) {
									for (Type type : supermethod.getArgumentTypes()) {
										// System.out.println(type.toString());
										supermethodParams.add(type);
									}
									for (Type type : i.getArgumentTypes(cp)) {
										methodParams.add(type);
									}
									if (supermethodParams.equals(methodParams)) {
										beidiaoyongclassname = superclass.getClassName();
										for (Type temp : supermethodParams)
											realBeiDiaoMethodParamsStr.append(temp.toString() + "-");
										fatherflag=true;
										break;
									} else {
										for (Type type : i.getArgumentTypes(cp)) {
											realBeiDiaoMethodParamsStr.append(type.toString() + "-");
										}
									}
								}
							}
						}
					}
					//在父类中找到了被调用的类且参数一致，跳出循环
					if(fatherflag){
						break;
					}

				} /*else {
					for (Type type : i.getArgumentTypes(cp)) {
						realBeiDiaoMethodParamsStr.append(type.toString() + "-");
					}
				}*/
				try {
					superclass = superclass.getSuperClass();
				} catch (ClassNotFoundException e) {
//					e.printStackTrace();
				}
			}
			
			// 如果父類不是object 但是父類中沒有被調用的方法，表示調用的是自己的類
			if(!fatherflag){
				for (Type type : i.getArgumentTypes(cp)) {
					realBeiDiaoMethodParamsStr.append(type.toString() + "-");
				}
			}

		}
		// 调用的方法属于非父类和自己的方法，判断调用的方法是否有父类 属于调用的父类的方法
		else {
			String methodclasspath = "";
			for (String filepath : allClassfiles) {
				if (filepath.contains(i.getClassName(cp).replace(".", "\\"))) {
					// System.out.println("get 调用的方法所在类的class路劲"+filepath);
					methodclasspath = filepath;
					break;
				}
			}
			if (!methodclasspath.isEmpty()) {
				//如果要调用的方法所在的类可以被找到，判断调用的类是他还是他父辈
				JavaClass beidiaoyongsuperclass=null;
				try {
					JavaClass var=null;
					try {
						var=new ClassParser(methodclasspath).parse();
						beidiaoyongsuperclass =var.getSuperClass();
					} catch (ClassNotFoundException e) {
//						System.err.println("被调用方的父类查找失败"+var.getClassName());
					}
				} catch (Exception e) {
//					e.printStackTrace();
				} 
				//如果为空 表示调用的就是自己或者父类不在考虑范围，直接对参数赋值 这个必然为叶子节点
				if(beidiaoyongsuperclass ==null){
					for (Type type : i.getArgumentTypes(cp)) {
						realBeiDiaoMethodParamsStr.append(type.toString() + "-");
					}
				} else{
					//如果被调用方父类为object，表示调用的不是父类的方法是自己的方法
					if (beidiaoyongsuperclass.getClassName().equals("java.lang.Object")) {
						for (Type type : i.getArgumentTypes(cp)) {
							realBeiDiaoMethodParamsStr.append(type.toString() + "-");
						}
					} else {
						boolean beidiaoyongfatherflag=false;
						while (!beidiaoyongsuperclass.getClassName().equals("java.lang.Object")) {
							if (!beidiaoyongsuperclass.getClassName().equals("java.lang.Object") && !beidiaoyongsuperclass.isInterface()) {
								for (org.apache.bcel.classfile.Method supermethod : beidiaoyongsuperclass.getMethods()) {
									ArrayList<Type> supermethodParams = new ArrayList<Type>();
									ArrayList<Type> methodParams = new ArrayList<Type>();
									if (supermethod.getName().equals(i.getMethodName(cp))) {
										if (supermethod.getName().equals("<init>")) {
											;
										} else {
											if (supermethod.getName().equals(i.getMethodName(cp))) {
												//判断方法名一样 参数是否一样
												for (Type type : supermethod.getArgumentTypes()) {
													// System.out.println(type.toString());
													supermethodParams.add(type);
												}
												for (Type type : i.getArgumentTypes(cp)) {
													methodParams.add(type);
												}
												if (supermethodParams.equals(methodParams)) {
													beidiaoyongclassname = beidiaoyongsuperclass.getClassName();
													for (Type temp : supermethodParams)
														realBeiDiaoMethodParamsStr.append(temp.toString() + "-");
													beidiaoyongfatherflag=true;
													break;
												} else {
													for (Type type : i.getArgumentTypes(cp)) {
														realBeiDiaoMethodParamsStr.append(type.toString() + "-");
													}
												}
											}
										}
									}
								}
								if (beidiaoyongfatherflag) {
									break;
								}
	
							} /*else {
								for (Type type : i.getArgumentTypes(cp)) {
									realBeiDiaoMethodParamsStr.append(type.toString() + "-");
								}
							}*/
							try {
								beidiaoyongsuperclass = beidiaoyongsuperclass.getSuperClass();
							} catch (ClassNotFoundException e) {
//								System.err.println("循环查找被调用方的父类失败"+beidiaoyongsuperclass.getClassName());
							}
						}
					}
				}
			} else {
				for (Type type : i.getArgumentTypes(cp)) {
					realBeiDiaoMethodParamsStr.append(type.toString() + "-");
				}
			}

		}

		// 处理内部类访问外部files或者method
		if (i.getMethodName(cp).contains("access$") && realBeiDiaoMethodParamsStr.toString().isEmpty()) {
			for (Type type : i.getArgumentTypes(cp)) {
				realBeiDiaoMethodParamsStr.append(type.toString() + "-");
			}
		}

		// System.out.println(i.getMethodName(cp)+":"+
		// mg.getName()+":"+i.getClassName(cp));
		// System.out.println(i.getClassName(cp));
		/* Measuring decision: measure overloaded methods separately */
		// System.out.println("调用类的方法名"+mg.getName());
		cv.registerMethodInvocation(i.getClassName(cp), i.getMethodName(cp), argTypes);
		// 被调用的类 被调用类的方法名 调用类的方法名
		cv.registerMethodInvocation(beidiaoyongclassname, beidiaoyongMethod + ":" + realBeiDiaoMethodParamsStr,
				mg.getName() + ":" + diaoyongMethodParamsStr);
	}

	/** Visit an instanceof instruction. */
	@Override
	public void visitINSTANCEOF(INSTANCEOF i) {
		cv.registerCoupling(i.getType(cp));
	}

	/** Visit checklast instruction. */
	@Override
	public void visitCHECKCAST(CHECKCAST i) {
		cv.registerCoupling(i.getType(cp));
	}

	/** Visit return instruction. */
	@Override
	public void visitReturnInstruction(ReturnInstruction i) {
		cv.registerCoupling(i.getType(cp));
	}

	/** Visit the method's exception handlers. */
	private void updateExceptionHandlers() {
		CodeExceptionGen[] handlers = mg.getExceptionHandlers();

		/* Measuring decision: couple exceptions */
		for (int i = 0; i < handlers.length; i++) {
			Type t = handlers[i].getCatchType();
			if (t != null)
				cv.registerCoupling(t);
		}
	}
}
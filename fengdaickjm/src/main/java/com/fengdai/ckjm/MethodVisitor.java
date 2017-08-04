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

import javax.swing.JCheckBox;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ArrayInstruction;
import org.apache.bcel.generic.CHECKCAST;
import org.apache.bcel.generic.CodeExceptionGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConstantPushInstruction;
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
		String classname = i.getClassName(cp);
		
		System.out.println("调用的方法所在的类（子类）" + classname + "方法" + i.getMethodName(cp));
		System.out.println("调用方class名称" + cv.getJavaClass().getClassName());
		System.out.println("调用方父class名称" + cv.getJavaClass().getSuperclassName());
		if (classname.contains("com.fengdai.finance.model.ChannelSettlement")
				&& i.getMethodName(cp).contains("getSettlementType")&&mg.getName().contains("lambda$0")) {
			System.out.println("com.fengdai.activity.form.DitchForm");
		}
		StringBuffer realBeiDiaoMethodParamsStr = new StringBuffer();
		try {
			JavaClass superclass = cv.getJavaClass().getSuperClass();
			// 先判断调用的方法是父类的还是自己的方法,如果都不是即为调用的第三方方法 调用的有可能不是父类是爷爷类 所以循环查找 知道找到object
			if (cv.getJavaClass().getClassName().contains(classname)|| cv.getJavaClass().getSuperclassName().contains(classname)) {
				//如果父类是object 表明这个类调用的方法就是自己内部的方法
				if(superclass.getClassName().equals("java.lang.Object")) {
					for (Type type : i.getArgumentTypes(cp)) {
						realBeiDiaoMethodParamsStr.append(type.toString() + "-");
					}
				}
				
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
											classname = superclass.getClassName();
											for (Type temp : supermethodParams)
												realBeiDiaoMethodParamsStr.append(temp.toString() + "-");
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

					} else {
						for (Type type : i.getArgumentTypes(cp)) {
							realBeiDiaoMethodParamsStr.append(type.toString() + "-");
						}
					}
					superclass = superclass.getSuperClass();
				}
			}
			// 调用的方法属于非父类和自己的方法，判断调用的方法是否有父类 属于调用的父类的方法
			else {
				String methodclasspath = "";
				for (String filepath : allClassfiles) {
					if (filepath.contains(i.getClassName(cp).replace(".", "\\"))) {
//						System.out.println("get 调用的方法所在类的class路劲"+filepath);
						methodclasspath = filepath;
						break;
					}
				}
				if(!methodclasspath.isEmpty()) {
					try {
						superclass = new ClassParser(methodclasspath).parse().getSuperClass();
					} catch (ClassFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if(superclass.getClassName().equals("java.lang.Object")) {
						for (Type type : i.getArgumentTypes(cp)) {
							realBeiDiaoMethodParamsStr.append(type.toString() + "-");
						}
					}else {
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
														classname = superclass.getClassName();
														for (Type temp : supermethodParams)
															realBeiDiaoMethodParamsStr.append(temp.toString() + "-");
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
			
								} else {
									for (Type type : i.getArgumentTypes(cp)) {
										realBeiDiaoMethodParamsStr.append(type.toString() + "-");
									}
								}
								superclass = superclass.getSuperClass();
							}
					}
				}else {
					for (Type type : i.getArgumentTypes(cp)) {
						realBeiDiaoMethodParamsStr.append(type.toString() + "-");
					}
				}

			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		//处理内部类访问外部files或者method
		if(i.getMethodName(cp).contains("access$") && realBeiDiaoMethodParamsStr.toString().isEmpty()) {
			for (Type type : i.getArgumentTypes(cp)) {
				realBeiDiaoMethodParamsStr.append(type.toString() + "-");
			}
		}
		
		// System.out.println(i.getMethodName(cp)+":"+
		// mg.getName()+":"+i.getClassName(cp));
		// System.out.println(i.getClassName(cp));
		/* Measuring decision: measure overloaded methods separately */
//		System.out.println("调用类的方法名"+mg.getName());
		cv.registerMethodInvocation(i.getClassName(cp), i.getMethodName(cp), argTypes);
		// 被调用的类 被调用类的方法名 调用类的方法名
		cv.registerMethodInvocation(classname, i.getMethodName(cp) + ":" + realBeiDiaoMethodParamsStr,mg.getName() + ":" + diaoyongMethodParamsStr);
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
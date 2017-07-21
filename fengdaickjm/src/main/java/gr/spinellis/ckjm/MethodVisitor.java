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

import java.util.ArrayList;

import org.apache.bcel.Constants;
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
 * Visit a method calculating the class's Chidamber-Kemerer metrics.
 * A helper class for ClassVisitor.
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

    /** Constructor. */
    MethodVisitor(MethodGen m, ClassVisitor c) {
	mg  = m;
	cv = c;
	cp  = mg.getConstantPool();
	cm = cv.getMetrics();
    }

    /** Start the method's visit. */
    public void start() {
	if (!mg.isAbstract() && !mg.isNative()) {
	    for (InstructionHandle ih = mg.getInstructionList().getStart();
		 ih != null; ih = ih.getNext()) {
		Instruction i = ih.getInstruction();
		
		
		  
		if(!visitInstruction(i))
		    i.accept(this);
	    }
	    updateExceptionHandlers();
	}
    }

    /** Visit a single instruction. */
    private boolean visitInstruction(Instruction i) {
	short opcode = i.getOpcode();

	return ((InstructionConstants.INSTRUCTIONS[opcode] != null) &&
	   !(i instanceof ConstantPushInstruction) &&
	   !(i instanceof ReturnInstruction));
    }

    /** Local variable use. */
    @Override
	public void visitLocalVariableInstruction(LocalVariableInstruction i) {
	if(i.getOpcode() != Constants.IINC)
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
	Type[] argTypes   = i.getArgumentTypes(cp);
	for (int j = 0; j < argTypes.length; j++)
	    cv.registerCoupling(argTypes[j]);
	cv.registerCoupling(i.getReturnType(cp));
	String classname=i.getClassName(cp);
	try {
		JavaClass superclass=cv.getJavaClass().getSuperClass();
//		System.out.println(superclass.getClassName());
		if (!superclass.getClassName().equals("java.lang.Object")&&!superclass.isInterface()) {
			for (org.apache.bcel.classfile.Method supermethod : superclass.getMethods()) {
				if (supermethod.getName().equals(i.getMethodName(cp))) {
					if (supermethod.getName().equals("<init>")) {
						;
					} else {
						if(supermethod.getName().equals(i.getMethodName(cp))){
							ArrayList<Type> supermethodParams= new ArrayList<>();
							ArrayList<Type> methodParams= new ArrayList<>();
							for (Type type : supermethod.getArgumentTypes()) {
//								System.out.println(type.toString());
								supermethodParams.add(type);
							}
							for (Type type:i.getArgumentTypes(cp)) {
								methodParams.add(type);
							}
							if(supermethodParams.equals(methodParams)){
								classname=superclass.getClassName();
							}
						}
					}
				}
			} 
		}
		
	} catch (ClassNotFoundException e) {
		e.printStackTrace();
	}
//	System.out.println(i.getMethodName(cp)+":"+ mg.getName()+":"+i.getClassName(cp));
//	System.out.println(i.getClassName(cp));
	/* Measuring decision: measure overloaded methods separately */
	cv.registerMethodInvocation(i.getClassName(cp), i.getMethodName(cp), argTypes);
	cv.registerMethodInvocation(classname, mg.getName(),i.getMethodName(cp),argTypes);
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
	for(int i=0; i < handlers.length; i++) {
	    Type t = handlers[i].getCatchType();
	    if (t != null)
		cv.registerCoupling(t);
	}
    }
}

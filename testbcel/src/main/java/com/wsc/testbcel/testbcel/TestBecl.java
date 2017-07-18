package com.wsc.testbcel.testbcel;

import java.io.IOException;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.RETURN;
import org.apache.bcel.generic.*;

public class TestBecl {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		JavaClass clazz = Repository.lookupClass(Programmer.class);
		ClassGen classGen = new ClassGen(clazz);
		ConstantPoolGen cPoolGen = classGen.getConstantPool();
		int methodIndex = cPoolGen.addMethodref("byteCode.decorator.Programmer","doBcelPlan", "()V");
		int stringIndex = cPoolGen.addString("doBcelPlan...");// 在常量池中增加一个Field的声明返回stringIndex为声明在常量池中的位置索引
		InstructionList instructionDoPlan = new InstructionList();  // 字节码指令信息 
		instructionDoPlan.append(new GETSTATIC(15));  // 获取System.out常量
		instructionDoPlan.append(new LDC(stringIndex));  // 获取String Field信息
		instructionDoPlan.append(new INVOKEVIRTUAL(23)); // 调用Println方法
		instructionDoPlan.append(new RETURN());    // return 结果
		MethodGen doPlanMethodGen = new MethodGen(1, Type.VOID, Type.NO_ARGS, null, "doBcelPlan",
		classGen.getClassName(), instructionDoPlan, cPoolGen);
		classGen.addMethod(doPlanMethodGen.getMethod());
	
		
		 Method[] methods = classGen.getMethods();
	        for (Method method : methods) {
	            String methodName = method.getName();
	            if ("docoding".equals(methodName)) {
	                MethodGen methodGen = new MethodGen(method, clazz.getClassName(), cPoolGen);
	                InstructionList instructionList = methodGen.getInstructionList();
	                InstructionHandle[] handles = instructionList.getInstructionHandles();
	                InstructionHandle from = handles[0];
	                InstructionHandle aload = instructionList.append(from, new ALOAD(0));
	                instructionList.append(aload, new INVOKESPECIAL(methodIndex));
	                classGen.replaceMethod(method, methodGen.getMethod());
	            }
	        }
	        
	        JavaClass target = classGen.getJavaClass();
	        try {
				target.dump("F:\\test\\testbcel\\Programmer.class");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	      
	        
	      
	
	}

}

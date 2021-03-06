/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.apache.bcel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.bcel.generic.ClassElementValueGen;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ElementValueGen;
import org.apache.bcel.generic.EnumElementValueGen;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.SimpleElementValueGen;

public class ElementValueGenTestCase extends AbstractTestCase
{
    private ClassGen createClassGen(final String classname)
    {
        return new ClassGen(classname, "java.lang.Object", "<generated>",
                Const.ACC_PUBLIC | Const.ACC_SUPER, null);
    }

    /**
     * Create primitive element values
     */
    public void testCreateIntegerElementValue() throws Exception
    {
        final ClassGen cg = createClassGen("HelloWorld");
        final ConstantPoolGen cp = cg.getConstantPool();
        final SimpleElementValueGen evg = new SimpleElementValueGen(
                ElementValueGen.PRIMITIVE_INT, cp, 555);
        // Creation of an element like that should leave a new entry in the
        // cpool
        assertTrue("Should have the same index in the constantpool but "
                + evg.getIndex() + "!=" + cp.lookupInteger(555),
                evg.getIndex() == cp.lookupInteger(555));
        checkSerialize(evg, cp);
    }

    public void testCreateFloatElementValue() throws Exception
    {
        final ClassGen cg = createClassGen("HelloWorld");
        final ConstantPoolGen cp = cg.getConstantPool();
        final SimpleElementValueGen evg = new SimpleElementValueGen(
                ElementValueGen.PRIMITIVE_FLOAT, cp, 111.222f);
        // Creation of an element like that should leave a new entry in the
        // cpool
        assertTrue("Should have the same index in the constantpool but "
                + evg.getIndex() + "!=" + cp.lookupFloat(111.222f), evg
                .getIndex() == cp.lookupFloat(111.222f));
        checkSerialize(evg, cp);
    }

    public void testCreateDoubleElementValue() throws Exception
    {
        final ClassGen cg = createClassGen("HelloWorld");
        final ConstantPoolGen cp = cg.getConstantPool();
        final SimpleElementValueGen evg = new SimpleElementValueGen(
                ElementValueGen.PRIMITIVE_DOUBLE, cp, 333.44);
        // Creation of an element like that should leave a new entry in the
        // cpool
        final int idx = cp.lookupDouble(333.44);
        assertTrue("Should have the same index in the constantpool but "
                + evg.getIndex() + "!=" + idx, evg.getIndex() == idx);
        checkSerialize(evg, cp);
    }

    public void testCreateLongElementValue() throws Exception
    {
        final ClassGen cg = createClassGen("HelloWorld");
        final ConstantPoolGen cp = cg.getConstantPool();
        final SimpleElementValueGen evg = new SimpleElementValueGen(
                ElementValueGen.PRIMITIVE_LONG, cp, 3334455L);
        // Creation of an element like that should leave a new entry in the
        // cpool
        final int idx = cp.lookupLong(3334455L);
        assertTrue("Should have the same index in the constantpool but "
                + evg.getIndex() + "!=" + idx, evg.getIndex() == idx);
        checkSerialize(evg, cp);
    }

    public void testCreateCharElementValue() throws Exception
    {
        final ClassGen cg = createClassGen("HelloWorld");
        final ConstantPoolGen cp = cg.getConstantPool();
        final SimpleElementValueGen evg = new SimpleElementValueGen(
                ElementValueGen.PRIMITIVE_CHAR, cp, 't');
        // Creation of an element like that should leave a new entry in the
        // cpool
        final int idx = cp.lookupInteger('t');
        assertTrue("Should have the same index in the constantpool but "
                + evg.getIndex() + "!=" + idx, evg.getIndex() == idx);
        checkSerialize(evg, cp);
    }

    public void testCreateByteElementValue() throws Exception
    {
        final ClassGen cg = createClassGen("HelloWorld");
        final ConstantPoolGen cp = cg.getConstantPool();
        final SimpleElementValueGen evg = new SimpleElementValueGen(
                ElementValueGen.PRIMITIVE_CHAR, cp, (byte) 'z');
        // Creation of an element like that should leave a new entry in the
        // cpool
        final int idx = cp.lookupInteger((byte) 'z');
        assertTrue("Should have the same index in the constantpool but "
                + evg.getIndex() + "!=" + idx, evg.getIndex() == idx);
        checkSerialize(evg, cp);
    }

    public void testCreateBooleanElementValue() throws Exception
    {
        final ClassGen cg = createClassGen("HelloWorld");
        final ConstantPoolGen cp = cg.getConstantPool();
        final SimpleElementValueGen evg = new SimpleElementValueGen(
                ElementValueGen.PRIMITIVE_BOOLEAN, cp, true);
        // Creation of an element like that should leave a new entry in the
        // cpool
        final int idx = cp.lookupInteger(1); // 1 == true
        assertTrue("Should have the same index in the constantpool but "
                + evg.getIndex() + "!=" + idx, evg.getIndex() == idx);
        checkSerialize(evg, cp);
    }

    public void testCreateShortElementValue() throws Exception
    {
        final ClassGen cg = createClassGen("HelloWorld");
        final ConstantPoolGen cp = cg.getConstantPool();
        final SimpleElementValueGen evg = new SimpleElementValueGen(
                ElementValueGen.PRIMITIVE_SHORT, cp, (short) 42);
        // Creation of an element like that should leave a new entry in the
        // cpool
        final int idx = cp.lookupInteger(42);
        assertTrue("Should have the same index in the constantpool but "
                + evg.getIndex() + "!=" + idx, evg.getIndex() == idx);
        checkSerialize(evg, cp);
    }

    // //
    // Create string element values
    public void testCreateStringElementValue() throws Exception
    {
        // Create HelloWorld
        final ClassGen cg = createClassGen("HelloWorld");
        final ConstantPoolGen cp = cg.getConstantPool();
        final SimpleElementValueGen evg = new SimpleElementValueGen(
                ElementValueGen.STRING, cp, "hello");
        // Creation of an element like that should leave a new entry in the
        // cpool
        assertTrue("Should have the same index in the constantpool but "
                + evg.getIndex() + "!=" + cp.lookupUtf8("hello"), evg
                .getIndex() == cp.lookupUtf8("hello"));
        checkSerialize(evg, cp);
    }

    // //
    // Create enum element value
    public void testCreateEnumElementValue() throws Exception
    {
        final ClassGen cg = createClassGen("HelloWorld");
        final ConstantPoolGen cp = cg.getConstantPool();
        final ObjectType enumType = new ObjectType("SimpleEnum"); // Supports rainbow
                                                            // :)
        final EnumElementValueGen evg = new EnumElementValueGen(enumType, "Red", cp);
        // Creation of an element like that should leave a new entry in the
        // cpool
        assertTrue(
                "The new ElementValue value index should match the contents of the constantpool but "
                        + evg.getValueIndex() + "!=" + cp.lookupUtf8("Red"),
                evg.getValueIndex() == cp.lookupUtf8("Red"));
        // BCELBUG: Should the class signature or class name be in the constant
        // pool? (see note in ConstantPool)
        // assertTrue("The new ElementValue type index should match the contents
        // of the constantpool but "+
        // evg.getTypeIndex()+"!="+cp.lookupClass(enumType.getSignature()),
        // evg.getTypeIndex()==cp.lookupClass(enumType.getSignature()));
        checkSerialize(evg, cp);
    }

    // //
    // Create class element value
    public void testCreateClassElementValue() throws Exception
    {
        final ClassGen cg = createClassGen("HelloWorld");
        final ConstantPoolGen cp = cg.getConstantPool();
        final ObjectType classType = new ObjectType("java.lang.Integer");
        final ClassElementValueGen evg = new ClassElementValueGen(classType, cp);
        assertTrue("Unexpected value for contained class: '"
                + evg.getClassString() + "'", evg.getClassString().contains("Integer"));
        checkSerialize(evg, cp);
    }

    private void checkSerialize(final ElementValueGen evgBefore, final ConstantPoolGen cpg) throws IOException {
        final String beforeValue = evgBefore.stringifyValue();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(baos)) {
            evgBefore.dump(dos);
            dos.flush();
        }
        ElementValueGen evgAfter;
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            evgAfter = ElementValueGen.readElementValue(dis, cpg);
        }
        final String afterValue = evgAfter.stringifyValue();
        if (!beforeValue.equals(afterValue)) {
            fail("Deserialization failed: before='" + beforeValue + "' after='" + afterValue + "'");
        }
    }
}

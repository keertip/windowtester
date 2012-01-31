package com.windowtester.test.util.junit;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/*******************************************************************************
 *  Copyright (c) 2012 Google, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Google, Inc. - initial API and implementation
 *******************************************************************************/
@SuppressWarnings("unchecked")
public class PluggableTestSuite extends TestSuite {

	/**
	 * ...as the moon sets over the early morning Merlin, Oregon
	 * mountains, our intrepid adventurers type...
	 */
	static public Test createTest(Class theClass, String name) {
		Constructor constructor;
		try {
			constructor= getTestConstructor(theClass);
		} catch (NoSuchMethodException e) {
			return warning("Class "+theClass.getName()+" has no public constructor TestCase(String name) or TestCase()");
		}
		Object test;
		try {
			if (constructor.getParameterTypes().length == 0) {
				test= constructor.newInstance(new Object[0]);
				if (test instanceof TestCase)
					((TestCase) test).setName(name);
			} else {
				test= constructor.newInstance(new Object[]{name});
			}
		} catch (InstantiationException e) {
			return(warning("Cannot instantiate test case: "+name+" ("+exceptionToString(e)+")"));
		} catch (InvocationTargetException e) {
			return(warning("Exception in constructor: "+name+" ("+exceptionToString(e.getTargetException())+")"));
		} catch (IllegalAccessException e) {
			return(warning("Cannot access test case: "+name+" ("+exceptionToString(e)+")"));
		}
		return (Test) test;
	}
	
	/**
	 * Gets a constructor which takes a single String as
	 * its argument or a no arg constructor.
	 */
	public static Constructor getTestConstructor(Class theClass) throws NoSuchMethodException {
		Class[] args= { String.class };
		try {
			return theClass.getConstructor(args);	
		} catch (NoSuchMethodException e) {
			// fall through
		}
		return theClass.getConstructor(new Class[0]);
	}

	/**
	 * Returns a test which will fail and log a warning message.
	 */
	public static Test warning(final String message) {
		return new TestCase("warning") {
			protected void runTest() {
				fail(message);
			}
		};
	}

	/**
	 * Returns a test which will pass and log a message.
	 */
	public static Test pass(String message) {
		return new TestCase(message) {
			protected void runTest() {
				
			}
		};
	}
	
	
	/**
	 * Converts the stack trace into a string
	 */
	private static String exceptionToString(Throwable t) {
		StringWriter stringWriter= new StringWriter();
		PrintWriter writer= new PrintWriter(stringWriter);
		t.printStackTrace(writer);
		return stringWriter.toString();

	}
	private String fName;

	private Vector fTests= new Vector(10);

    /**
	 * Constructs an empty TestSuite.
	 */
	public PluggableTestSuite() {
	}
	
	/**
	 * Constructs a TestSuite from the given class. Adds all the methods
	 * starting with "test" as test cases to the suite.
	 * Parts of this method was written at 2337 meters in the Hueffihuette,
	 * Kanton Uri
	 */
	 public PluggableTestSuite(final Class theClass) {
		fName= theClass.getName();
		try {
			getTestConstructor(theClass); // Avoid generating multiple error messages
		} catch (NoSuchMethodException e) {
			addTest(warning("Class "+theClass.getName()+" has no public constructor TestCase(String name) or TestCase()"));
			return;
		}

		if (!Modifier.isPublic(theClass.getModifiers())) {
			addTest(warning("Class "+theClass.getName()+" is not public"));
			return;
		}

		Class superClass= theClass;
		Vector names= new Vector();
		while (Test.class.isAssignableFrom(superClass)) {
			Method[] methods= superClass.getDeclaredMethods();
			for (int i= 0; i < methods.length; i++) {
				addTestMethod(methods[i], names, theClass);
			}
			superClass= superClass.getSuperclass();
		}
		if (fTests.size() == 0) {
			addTest(warning("No tests found in "+theClass.getName()));
		}
	}
	
	/**
	 * Constructs a TestSuite from the given class with the given name.
	 * @see TestSuite#TestSuite(Class)
	 */
	public PluggableTestSuite(Class theClass, String name) {
		this(theClass);
		setName(name);
	}
	
   	/**
	 * Constructs an empty TestSuite.
	 */
	public PluggableTestSuite(String name) {
		setName(name);
	}
	
	/**
	 * Constructs a TestSuite from the given array of classes.  
	 * @param classes
	 */
	public PluggableTestSuite (Class[] classes) {
		for (int i= 0; i < classes.length; i++)
			addTest(newSuite(classes[i]));
	}
	
	/**
	 * Constructs a TestSuite from the given array of classes with the given name.
	 * @see TestSuite#TestSuite(Class[])
	 */
	public PluggableTestSuite(Class[] classes, String name) {
		this(classes);
		setName(name);
	}
	
	/**
	 * Adds a test to the suite.
	 */
	public void addTest(Test test) {
		fTests.addElement(test);
	}

	/**
	 * Adds the tests from the given class to the suite
	 */
	public void addTestSuite(Class testClass) {
		addTest(newSuite(testClass));
	}

	protected PluggableTestSuite newSuite(Class testClass) {
		return new PluggableTestSuite(testClass);
	}
	
	/**
	 * Counts the number of test cases that will be run by this test.
	 */
	public int countTestCases() {
		int count= 0;
		for (Enumeration e= tests(); e.hasMoreElements(); ) {
			Test test= (Test)e.nextElement();
			count= count + test.countTestCases();
		}
		return count;
	}

	/**
	 * Returns the name of the suite. Not all
	 * test suites have a name and this method
	 * can return null.
	 */
	public String getName() {
		return fName;
	}
	 
	/**
	 * Runs the tests and collects their result in a TestResult.
	 */
	public void run(TestResult result) {
		for (Enumeration e= tests(); e.hasMoreElements(); ) {
	  		if (result.shouldStop() )
	  			break;
			Test test= (Test)e.nextElement();
			runTest(test, result);
		}
	}

	public void runTest(Test test, TestResult result) {
		test.run(result);
	}
	 
	/**
	 * Sets the name of the suite.
	 * @param name The name to set
	 */
	public void setName(String name) {
		fName= name;
	}

	/**
	 * Returns the test at the given index
	 */
	public Test testAt(int index) {
		return (Test)fTests.elementAt(index);
	}
	
	/**
	 * Returns the number of tests in this suite
	 */
	public int testCount() {
		return fTests.size();
	}
	
	/**
	 * Returns the tests as an enumeration
	 */
	public Enumeration tests() {
		return fTests.elements();
	}
	
	/**
	 */
	public String toString() {
		if (getName() != null)
			return getName();
		return super.toString();
	 }

	private void addTestMethod(Method m, Vector names, Class theClass) {
		String name= m.getName();
		if (names.contains(name))
			return;
		if (! isPublicTestMethod(m)) {
			if (isTestMethod(m))
				addTest(warning("Test method isn't public: "+m.getName()));
			if (!isEnabled(m))
				addTest(pass("Test disabled: " + m.getName()));
			return;
		}

		names.addElement(name);
		addTest(createTest(theClass, name));
	}

	private boolean isPublicTestMethod(Method m) {
		return isTestMethod(m) && Modifier.isPublic(m.getModifiers());
	 }
	 
	public boolean isTestMethod(Method m) {
		return hasTestSignature(m) && isEnabled(m);
	 }

	protected boolean isEnabled(Method m) {
		return true;
	}

	protected boolean hasTestSignature(Method m) {
		String name= m.getName();
		Class[] parameters= m.getParameterTypes();
		Class returnType= m.getReturnType();
		return parameters.length == 0 && name.startsWith("test") && returnType.equals(Void.TYPE);
	}
}

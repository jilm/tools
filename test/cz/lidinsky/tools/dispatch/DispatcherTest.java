/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.lidinsky.tools.dispatch;

import java.util.function.Function;
import java.util.function.Predicate;
import junit.framework.TestCase;

/**
 *
 * @author jilm
 */
public class DispatcherTest extends TestCase {
    
    public DispatcherTest(String testName) {
        super(testName);
    }
    
    private Dispatcher dispatcher;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dispatcher = new Dispatcher();
        dispatcher.start();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of add method, of class Dispatcher.
     */
    public void testAdd() {
        System.out.println("add");
        Object object = null;
        Dispatcher instance = new Dispatcher();
        instance.add(object);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of start method, of class Dispatcher.
     */
    public void testStart() {
        System.out.println("start");
        Dispatcher instance = new Dispatcher();
        instance.start();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

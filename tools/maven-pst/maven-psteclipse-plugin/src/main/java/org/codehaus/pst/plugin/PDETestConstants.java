/*
 * Copyright (C) 2006 Princeton Softech, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.pst.plugin;

/**
 * <ul>
 * <li>Title: PDETestConstants</li>
 * <li>Description: The interface <code>PDETestConstants</code> is an
 * interface that provides suitable constants for the Mojos involved in PDE
 * testing.</li>
 * <li>Created: Aug 29, 2006 by: prippete01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.5 $
 */
public interface PDETestConstants {
    /**
     * The standard Maven 'target' directory.
     */
    public static final String TARGET_DIRECTORY = "target";

    /**
     * The PDE test directory, 'pde-test', under 'target'.
     */
    public static final String PDE_TEST_DIRECTORY = "pde-test";

    /**
     * The Surefire reports directory, 'surefire-reports', under 'target'.
     */
    public static final String SUREFIRE_DIRECTORY = "surefire-reports";

    /**
     * The surefire 'testsuite' element.
     */
    public static final String ELEMENT_TESTSUITE = "testsuite";
    
    /**
     * The surefire 'name' attribute.
     */
    public static final String ATTRIBUTE_NAME = "name";
    
    /**
     * The surefire 'time' attribute.
     */
    public static final String ATTRIBUTE_TIME = "time";
    
    /**
     * The surefire 'tests' attribute.
     */
    public static final String ATTRIBUTE_TESTS = "tests";
    
    /**
     * The surefire 'errors' attribute.
     */
    public static final String ATTRIBUTE_ERRORS = "errors";
    
    /**
     * The surefire 'failures' attribute.
     */
    public static final String ATTRIBUTE_FAILURES = "failures";
}

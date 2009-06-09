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
 * <li>Title: ManifestConstants</li>
 * <li>Description: The interface <code>ManifestConstants</code> defines
 * useful constants for manipulating a jar manifest.</li>
 * <li>Created: Aug 3, 2006 by: prippete01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.9 $
 */
public interface ManifestConstants {
    /**
     * Constant for "Bundle-ManifestVersion" main attribute.
     */
    public static final String BUNDLE_MANIFEST_VERSION = "Bundle-ManifestVersion";

    /**
     * Constant for default value of "Bundle-ManifestVersion" main attribute.
     */
    public static final String BUNDLE_MANIFEST_VERSION_VALUE = "2";

    /**
     * Constant for "Bundle-Name" main attribute.
     */
    public static final String BUNDLE_NAME = "Bundle-Name";

    /**
     * Constant for "Bundle-Classpath" main attribute.
     */
    public static final String BUNDLE_CLASSPATH = "Bundle-ClassPath";

    /**
     * Constant for "Bundle-SymbolicName" main attribute.
     */
    public static final String BUNDLE_SYMBOLIC_NAME = "Bundle-SymbolicName";

    /**
     * Constant for "Bundle-Version" main attribute.
     */
    public static final String BUNDLE_VERSION = "Bundle-Version";

    /**
     * Constant for "Bundle-Vendor" main attribute.
     */
    public static final String BUNDLE_VENDOR = "Bundle-Vendor";

    /**
     * Constant for "Bundle-Vendor" main attribute's default value.
     */
    public static final String BUNDLE_VENDOR_VALUE = "Overture.";

    /**
     * Constant for "Bundle-Localization" main attribute.
     */
    public static final String BUNDLE_LOCALIZATION = "Bundle-Localization";

    /**
     * Constant for "Bundle-Localization" main attribute's default value.
     */
    public static final String BUNDLE_LOCALIZATION_VALUE = "plugin";

    /**
     * Constant for the default version.
     */
    public static final String DEFAULT_VERSION = "1.0.0";

    /**
     * Constant for "Eclipse-BuddyPolicy" main attribute.
     */
    public static final String ECLIPSE_BUDDY_POLICY = "Eclipse-BuddyPolicy";

    /**
     * Constant for "Eclipse-BuddyPolicy" main attribute's default value.
     */
    public static final String ECLIPSE_BUDDY_POLICY_VALUE = "registered";

    /**
     * Constant for "Eclipse-RegisterBuddy" main attribute..
     */
    public static final String ECLIPSE_REGISTER_BUDDY = "Eclipse-RegisterBuddy";

    /**
     * Constant for "Export-Package" main attribute.
     */
    public static final String EXPORT_PACKAGE = "Export-Package";

    /**
     * Constant for "com.princetonsoftech.nex" group id.
     */
    public static final String GROUP_ID_PST_ECLIPSE = "com.princetonsoftech.nex";

    /**
     * Constant for "visibility" key.
     */
    public static final String KEY_VISIBILITY = "visibility";

    /**
     * Constant for "resolution" key.
     */
    public static final String KEY_RESOLUTION = "resolution";

    /**
     * Constant for "bundle-version" key.
     */
    public static final String KEY_BUNDLE_VERSION = "bundle-version";

    /**
     * Name of a Manifest file
     */
    public static final String MANIFEST_FILE_NAME = "MANIFEST.MF";

    /**
     * Name of the 'META-INF' directory.
     */
    public static final String MANIFEST_DIRECTORY = "META-INF";

    /**
     * Default value for "Manifest-Version" main attribute
     */
    public static final String MANIFEST_VERSION_VALUE = "1.0";

    /**
     * Constant for "Require-Bundle" main attribute.
     */
    public static final String REQUIRE_BUNDLE = "Require-Bundle";

    /**
     * Constant for "system" scope.
     */
    public static final String SCOPE_SYSTEM = "system";

    /**
     * Name of the 'lib' directory.
     */
    public static final String LIB_DIRECTORY = "lib";
    
    
    
    /**
     * Constant for "Bundle-ActivationPolicy" main attribute.
     */
    public static final String BUNDLE_ACTIVATION = "Bundle-ActivationPolicy";
    
    /**
     * Constant for "lazy" main attribute.
     */
    public static final String BUNDLE_ACTIVATION_LAZY = "lazy";
    
    /**
     * Constant for "singleton:=true" main attribute.
     */
    public static final String BUNDLE_SYMBOLIC_NAME_SINGLETON = "singleton:=true";
    
    /**
     * Constant for "Bundle-RequiredExecutionEnvironment" main attribute.
     */
    public static final String BUNDLE_REQUIRED_EXECUTION_ENVIRONMENT = "Bundle-RequiredExecutionEnvironment";

    /**
     * Constant for "JSE-1.5" main attribute.
     */
    public static final String BUNDLE_REQUIRED_EXECUTION_ENVIRONMENT_J2SE15 = "J2SE-1.5";
}

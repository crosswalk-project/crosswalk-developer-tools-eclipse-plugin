/*
 *  Copyright 2014 Intel Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.xwalk.ide.eclipse.xdt;

/**
 * Constant definition class.
 */

public class XdtConstants {
	public final static String PLUGIN_ID = "org.xwalk.ide.eclipse.xdt";

	/** Nature of default Android projects */
	public final static String NATURE_ID = "org.xwalk.ide.eclipse.xdt.XwalkNature";

	public final static String MANIFEST_PATH = "manifest.json";

	public static final String[] TARGET_FORMATS = { "APK", "XPK" };

	public static final String ANDROID_PACKAGE_PREFIX = "com.example.";

	public static String USER_HOME = System.getProperty("user.home");

	public static String[] SUPPORTED_ARCHS = { "x86", "arm" };

	public static String[] SUPPORTED_MODES = { "embedded", "shared" };

	public static String[] SUPPORTED_ORIENTATION = { "Orientation: portrait", "Orientation: landscape" };
}

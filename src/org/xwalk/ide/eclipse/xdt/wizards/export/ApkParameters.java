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

package org.xwalk.ide.eclipse.xdt.wizards.export;

import org.eclipse.core.resources.IProject;
import org.json.JSONObject;
import org.xwalk.ide.eclipse.xdt.XdtConstants;
import org.xwalk.ide.eclipse.xdt.helpers.ProjectHelper;

public final class ApkParameters {
	public String packageName;
	public String targetFolder;
	public String appName;
	public String appVersionCode;
	public String appVersionCodeBase;
	public String keyStorePath;
	public String keyStoreAlias;
	public String keyStorePassCode;
	public String entryFile;
	public boolean useDefaultIcon = true;
	public boolean useDebugKeyStore = true;
	public boolean embeddedMode = true;
	public boolean compressJS = false;
	public boolean compressCSS = false;
	public boolean fullscreenMode = false;
	public boolean remoteDebugMode = false;
	public String arch = XdtConstants.SUPPORTED_ARCHS[0];
	public String orientation = "portrait";
	public ApkParameters(IProject project) {
		JSONObject manifest = ProjectHelper.getManifest(project);
		appName = manifest.getString("name");
		packageName = XdtConstants.ANDROID_PACKAGE_PREFIX + appName;
		targetFolder = "";
		appVersionCode = "1";
		appVersionCodeBase = "1";
		keyStorePath = "";
		keyStoreAlias = "";
		keyStorePassCode = "";
		entryFile = manifest.getJSONObject("app").getJSONObject("main")
				.getString("source");
	}
}

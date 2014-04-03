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

package org.xwalk.ide.eclipse.xdt.wizards.newproject;

public class NewProjectWizardState {
	public NewProjectWizardState() {

	}

	/** The name of the project */
	public String projectName;

	/** The application name */
	public String applicationName;

	public String applicationDescription;

	/** Whether the project name has been edited by the user */
	public boolean projectModified;

	/** Whether the application name has been edited by the user */
	public boolean applicationModified;

	/** The location of xwalk, it means xwalk-app-template currently */
	public String xwalkLocation;

	public String entryFile = "index.html";
	public boolean useDefaultIcon = true;
	public String customizedIcon;
	public String applicationVersion = "1.0.0";
}

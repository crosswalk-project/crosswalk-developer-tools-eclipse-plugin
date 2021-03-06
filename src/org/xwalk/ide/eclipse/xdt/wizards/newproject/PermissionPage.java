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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

public class PermissionPage extends WizardPage {
	public PermissionPage(NewProjectWizardState values) {
		super("setUpPermissions");
		setTitle("New Crosswalk Application");
		setDescription("Set up used permissions");
	}

	@Override
	public void createControl(Composite parent) {
		// TODO:
	}

}

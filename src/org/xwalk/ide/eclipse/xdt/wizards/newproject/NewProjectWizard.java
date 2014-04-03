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
import org.eclipse.jface.wizard.IWizardPage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.json.JSONObject;
import org.xwalk.ide.eclipse.xdt.XdtConstants;
import org.xwalk.ide.eclipse.xdt.XdtPluginLog;
import org.xwalk.ide.eclipse.xdt.project.XwalkNature;

public class NewProjectWizard extends Wizard implements INewWizard {
	static final String DefaultEntryFileContent = "<html><body><p>Welcome to Crosswalk!</p></body></html>";
	private NewProjectWizardState mValues;
	private NewProjectPage mMainPage;
	private IProject mProject;
	public NewProjectWizard() {

	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("New Crosswalk Application");
		mValues = new NewProjectWizardState();
		mMainPage = new NewProjectPage(mValues);
	}

	@Override
	public void addPages() {
		super.addPages();
		addPage(mMainPage);
	}

	@Override
	public boolean performFinish() {
		try {
			// create the web staff here
			// ---- create the project in workspace ----
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			mProject = root.getProject(mValues.projectName);
			mProject.create(null);
			mProject.open(IResource.BACKGROUND_REFRESH, null);

			// ---- Create manifest.json according mValues ----
			JSONObject manifest = new JSONObject();
			manifest.put("name", mValues.applicationName);
			manifest.put("version", mValues.applicationVersion);
			manifest.put("app", new JSONObject());
			// add the main entry for app launch setting
			manifest.getJSONObject("app").put("main",
					new JSONObject().put("source", mValues.entryFile));

			// add the launch entry for app lunch setting
			manifest.getJSONObject("app").put("launch",
					new JSONObject().put("local_path", mValues.entryFile));

			if (mValues.applicationDescription != null) {
				manifest.put("description", mValues.applicationDescription);
			}
			if (!mValues.useDefaultIcon && mValues.customizedIcon != null) {
				manifest.put("icon", mValues.customizedIcon);
			}

			// ---- write the manifest.json to workspace ----
			IFile manifestFile = mProject.getFile(XdtConstants.MANIFEST_PATH);
			InputStream fileStream = new ByteArrayInputStream(manifest
					.toString(2).getBytes());
			manifestFile.create(fileStream, true, null);
			fileStream.close();

			// ---- create a default entry file ----
			IFile entryFile = mProject.getFile(mValues.entryFile);
			fileStream = new ByteArrayInputStream(
					DefaultEntryFileContent.getBytes());
			entryFile.create(fileStream, true, null);
			fileStream.close();

			// add crosswalk nature to the project
			XwalkNature.setupProjectNatures(mProject, null);

		} catch (Exception e) {
			XdtPluginLog.logError(e);
			return false;
		}
		return true;
	}

	public IWizardPage getNextPage(IWizardPage currentPage) {
		return mMainPage;
	}

	public boolean canFinish() {
		if (mMainPage.isPageComplete())
			return true;
		else
			return false;	
	}
}

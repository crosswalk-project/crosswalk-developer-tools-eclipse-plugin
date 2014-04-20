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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.xwalk.ide.eclipse.xdt.Activator;
import org.xwalk.ide.eclipse.xdt.XdtConstants;
import org.xwalk.ide.eclipse.xdt.XdtPluginLog;
import org.xwalk.ide.eclipse.xdt.helpers.ExportHelper;
import org.xwalk.ide.eclipse.xdt.helpers.ProjectHelper;
import org.xwalk.ide.eclipse.xdt.preference.Settings;
import org.xwalk.ide.eclipse.xdt.preference.settingPage;

public class ExportProjectWizard extends Wizard implements IExportWizard {
	private IProject mProject;
	private String mTargetFormat;
	private File mDestinationFile;
	private ApkParameters mApkParameters;
	private ExportProjectPage mExportProjectPage;
	private ProjectSelectionPage mProjectSelectionPage;
	private static IProject[] mXwalkProjects;
	int runResult = 0;
	public ExportProjectWizard() {
		setWindowTitle("Export Crosswalk Application");
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		Object selected = selection.getFirstElement();
		if (selected instanceof IProject) {
			mProject = (IProject) selected;
		} 

		if (!checkPreferenceSettings()) {
			showDialog();
		}
		mTargetFormat = XdtConstants.TARGET_FORMATS[0];
		if (mProject != null) {
			mApkParameters = new ApkParameters(mProject);
			addPage(mExportProjectPage = new ExportProjectPage(this));
		}
		else {
			mXwalkProjects = ProjectHelper.getXwalkProjects();
			if (mXwalkProjects.length == 0) {
				Status status = new Status(IStatus.ERROR, "Export Error", 0, "No project available to export", null);
				ErrorDialog.openError(getShell(), "error", "Export Project Error!", status);
			}
			addPage(mProjectSelectionPage = new ProjectSelectionPage("Select Project"));
			return;
		}
	}

	private boolean checkPreferenceSettings() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String androidSdkPath = store.getString(Settings.ANDROID_SDK_PATH);
		String xwalkPath = store.getString(Settings.XWALK_PATH);
		File androidSdk = new File(androidSdkPath);
		if (!androidSdk.exists()) {
			return false;
		}

		File xwalk = new File(xwalkPath);
		if (!xwalk.exists()) {
			return false;
		}
		return true;
	}

	private void showDialog() {
		MessageDialog dialog = new MessageDialog(getShell(), "Export Error", null, 
					"Android SDK or Crosswalk path have not been set correctly.  Go on setting it now?", 
					1, new String[] {IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL} ,0);
		switch (dialog.open()) {
			case 0:
				showQuickPreferenceDialog();
				break;
			case 1:
				break;
		}
	}

	private void showResultDialog(int result) {
		MessageDialog dialog;
		if (result == 0) {
			dialog = new MessageDialog(getShell(), "Successful", null, 
					"The Crosswalk app has been exported successfully!", 
					0, new String[] {IDialogConstants.OK_LABEL} ,0);
		}
		else {
			dialog = new MessageDialog(getShell(), "Fail", null,
					"The Crosswalk app export fail.",
					1, new String[] {IDialogConstants.OK_LABEL} ,0);
		}

		switch (dialog.open()) {
			case 0:
				break;
		}
	}

	private void showQuickPreferenceDialog() {
		IPreferencePage page = new settingPage();
		PreferenceManager mgr = new PreferenceManager('/');
		IPreferenceNode node = new PreferenceNode("1", page);
		mgr.addToRoot(node);
		PreferenceDialog dialog = new PreferenceDialog(getShell(), mgr);
		dialog.create();
		dialog.setMessage(page.getTitle());
		dialog.open();
	}

	@Override
	public void addPages() {
	}

	@Override
	public boolean performFinish() {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
		IRunnableWithProgress op = new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor)
				throws InvocationTargetException {
					try {
						monitor.beginTask("Exporting app to: "+ mDestinationFile.toString(), 100);
						runResult = ExportHelper.doExport(mProject, mTargetFormat,mDestinationFile, mApkParameters, monitor);
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
					monitor.done();
				}
			}
		};
		try {
			dialog.run(false, true, op);
		} catch (Exception e) {
			XdtPluginLog.logError("Failed to export project:" + mProject.getName(), e);
			return false;
		}
		showResultDialog(runResult);
		return true;
	}

	IProject getProject() {
		return mProject;
	}

	void setProject(IProject project) {
		mProject = project;
	}

	String getTargetFormat() {
		return mTargetFormat;
	}

	void setTargetFormat(String format) {
		mTargetFormat = format;
	}

	ApkParameters getApkParameters() {
		return mApkParameters;
	}

	void setApkParameters(ApkParameters parameters) {
		mApkParameters = parameters;
	}

	File getDestination() {
		return mDestinationFile;
	}

	void resetDestination() {
		mDestinationFile = null;
	}

	void setDestination(File destinationFile) {
		mDestinationFile = destinationFile;
	}

	public IWizardPage getNextPage(IWizardPage currentPage) {
		if (currentPage == mProjectSelectionPage) {
			mProject = mProjectSelectionPage.exportProject;
			mApkParameters = new ApkParameters(mProject);
			addPage(mExportProjectPage = new ExportProjectPage(this));
			return mExportProjectPage;
		}
		else {
			return mExportProjectPage;
		}
	}

	public boolean canFinish() {
		if (getContainer().getCurrentPage() == mProjectSelectionPage)
			return false;
		else {
			if (mExportProjectPage.isPageComplete())
				return true;
			else
				return false;
		}
	}
}

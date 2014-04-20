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

package org.xwalk.ide.eclipse.xdt.wizards.importer;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;
import org.xwalk.ide.eclipse.xdt.helpers.ProjectHelper;


public class ImportProjectPage extends WizardPage implements SelectionListener, ModifyListener {
	private Text mXwalkProjectPathText;
	private Button mXwalkProjectPathBrowseButton;
	//private Button mCopyToWorkdirCheckButton;
	private static IProject[] mXwalkProjects;
	public String importProjectPath;
	public boolean copyImportProjectToWorkspace;

	protected ImportProjectPage(ImportProjectWizard wizard) {
		super("ImportCrosswalkProject");
		importProjectPath = new String();
		mXwalkProjects = ProjectHelper.getAllProjects();
		setTitle("Import Crosswalk Project");
		setDescription("Import A Crosswalk Project from a folder:");
		Bundle bundle = Platform.getBundle("org.xwalk.ide.eclipse.xdt");
		Path path = new Path("images/icon-68.png");
		URL imageUrl = FileLocator.find(bundle, path, null);
		setImageDescriptor(ImageDescriptor.createFromURL(imageUrl));
		setPageComplete(false);
	}

	
	@Override
	public void createControl(Composite parent) {		
		Composite mainComposite = new Composite(parent, SWT.NONE);
		setControl(mainComposite);
		GridLayout layout = new GridLayout(4, false);
		layout.horizontalSpacing = 10;
		mainComposite.setLayout(layout);
		Label xwalkProjectPathLabel = new Label(mainComposite, SWT.NONE);
		xwalkProjectPathLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1));
		xwalkProjectPathLabel.setText("Crosswalk Project:");
		mXwalkProjectPathText = new Text(mainComposite, SWT.BORDER);
		mXwalkProjectPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		mXwalkProjectPathText.addModifyListener(this);
		mXwalkProjectPathBrowseButton = new Button(mainComposite, SWT.NONE);
		mXwalkProjectPathBrowseButton.setText("Browse...");
		mXwalkProjectPathBrowseButton.addSelectionListener(this);
		mXwalkProjectPathBrowseButton.setEnabled(true);
		mXwalkProjectPathBrowseButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1));
	}
	
	private boolean validateXwalkProject(String dir) {
		// Find manifest.json
		File fd;
		try {
			fd = new File(dir);
		}
		catch (Exception e) {
			return false;
		}

		if (fd.isDirectory()) {
			File[] fileList;
			fileList = fd.listFiles(new FilenameFilter() 
			{
				public boolean accept(File dir, String name) {
					return name.startsWith("manifest") && name.endsWith("json");
				}
			});
			if (fileList.length > 0) {
				return true;	
			}
		}
		return false;
	}
	
	private boolean CheckProjectExistInWorkspace(final String projectName) {
		File fd;
		fd = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString());
		File[] fileList;
		fileList = fd.listFiles(new FilenameFilter() 
		{
			public boolean accept(File dir, String name) {
				return name.startsWith(projectName) && name.endsWith(projectName);
			}
		});
		if (fileList.length > 0)
			return true;	
		else 
			return false;
	}
	
	// ---- Implements SelectionListener ----

	@Override
	public void widgetSelected(SelectionEvent e) {
		Object source = e.getSource();
		if (source == mXwalkProjectPathBrowseButton) {
			String dir = promptUserForLocation(getShell(), "Select Crosswalk Project Path");
			if (dir != null) {
				mXwalkProjectPathText.setText(dir);
			}
		}
	}
			
	private String promptUserForLocation(Shell shell, String message) {
		DirectoryDialog dd = new DirectoryDialog(getShell());
		dd.setMessage(message);
		String curLocation;
		String dir;

		curLocation = mXwalkProjectPathText.getText().trim();
		if (!curLocation.isEmpty()) {
			dd.setFilterPath(curLocation);
		} 
				
		dir = dd.open();
		return dir;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
				// TODO:
	}
	
	@Override
	public void modifyText(ModifyEvent e) {
		Object source = e.getSource();
		if (source == mXwalkProjectPathText) {
			if (validateXwalkProject(mXwalkProjectPathText.getText())) {
				for (int i = 0; i < mXwalkProjects.length; i++) {
					if (mXwalkProjects[i].getLocationURI().getPath().equals(mXwalkProjectPathText.getText())) {
						setMessage("Crosswalk project already exist in workspace", WARNING);
						setPageComplete(false);
						return;
					}
				}
				
				File projectFile = new File(mXwalkProjectPathText.getText());
				if (CheckProjectExistInWorkspace(projectFile.getName()) && !projectFile.getParent().equals(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString())) {
					setMessage("Crosswalk project exist in workspace directory", WARNING);
					setPageComplete(false);
					return;
				}
					
				else {
					setMessage("Crosswalk project found to import");
					importProjectPath = mXwalkProjectPathText.getText();
					setPageComplete(true);
				}
			}
			else {
				setMessage("No crosswalk project is found to import", WARNING);
				setPageComplete(false);
			}
		}
	}
	
}

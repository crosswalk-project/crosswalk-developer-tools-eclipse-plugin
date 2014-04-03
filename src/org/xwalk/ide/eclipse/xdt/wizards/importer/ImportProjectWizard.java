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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.xwalk.ide.eclipse.xdt.project.XwalkNature;

public class ImportProjectWizard extends Wizard implements IImportWizard {
	private ImportProjectPage mImportProjectPage;
	private IProject mProject;
	
	public ImportProjectWizard() {

	}

	private void cpProject(File src, File dst) throws IOException {
	    if(src.isDirectory()) {
	        if(!dst.exists())
	            dst.mkdir();
	        String[] files = src.list();
	        for(int i = 0; i < files.length; i++) {
	        	cpProject(new File(src, files[i]), new File(dst, files[i]));
	        }
	    }
	    else {
	        InputStream inStream = new FileInputStream(src);
	        OutputStream outStream = new FileOutputStream(dst);
	        try {
	            byte[] buffer = new byte[1024];
	            int len;
	            while((len = inStream.read(buffer)) > 0) {
	            	outStream.write(buffer, 0, len);
	            }
	        }
	        finally {
	        	inStream.close();
	            outStream.close();
	        }
	    }
	}
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
		setWindowTitle("Import Crosswalk Project");
		mImportProjectPage = new ImportProjectPage(this);
	}

	@Override
	public void addPages() {
		super.addPages();
		addPage(mImportProjectPage);
	}
	
	@Override
	public boolean performFinish() {
		File projectSrc = new File(mImportProjectPage.importProjectPath);
		String projectName = projectSrc.getName();
		String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
		
		String projectDstPath = new String(workspacePath);
		File projectDst = new File(projectDstPath, projectName);
		
		if (!projectSrc.getAbsolutePath().equals(projectDst.getAbsolutePath())) {
			try {
				cpProject(projectSrc, projectDst);
			}
			catch (IOException e) {
				System.out.println(e);
			}
		}

		// Create project
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		mProject = root.getProject(projectName);
		try {
			mProject.create(null);
			mProject.open(IResource.BACKGROUND_REFRESH, null);
			// add crosswalk nature to the project
			XwalkNature.setupProjectNatures(mProject, null);
		}
		catch (Exception e) {
			System.out.println(e);
		}

		return true;
	}

}

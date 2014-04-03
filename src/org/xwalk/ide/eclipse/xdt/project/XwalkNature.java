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

package org.xwalk.ide.eclipse.xdt.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.xwalk.ide.eclipse.xdt.XdtConstants;

public class XwalkNature implements IProjectNature {
	/** the project this nature object is associated with */
	private IProject mProject;

	@Override
	public void configure() throws CoreException {
		// TODO add builders to checking xwalk APIs?
	}

	@Override
	public void deconfigure() throws CoreException {
		// TODO remove builders added in configure function
	}

	@Override
	public IProject getProject() {
		return mProject;
	}

	@Override
	public void setProject(IProject project) {
		mProject = project;
	}

	public static synchronized void setupProjectNatures(IProject project,
			IProgressMonitor monitor) throws CoreException {
		if (project == null || !project.isOpen())
			return;
		if (monitor == null)
			monitor = new NullProgressMonitor();

		// Add web project nature first?
		addNatureToProjectDescription(project, XdtConstants.NATURE_ID, monitor);
	}

	private static void addNatureToProjectDescription(IProject project,
			String natureId, IProgressMonitor monitor) throws CoreException {
		if (!project.hasNature(natureId)) {

			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();
			String[] newNatures = new String[natures.length + 1];

			// Crosswalk natures always come first.
			if (natureId.equals(XdtConstants.NATURE_ID)) {
				System.arraycopy(natures, 0, newNatures, 1, natures.length);
				newNatures[0] = natureId;
			} else {
				System.arraycopy(natures, 0, newNatures, 0, natures.length);
				newNatures[natures.length] = natureId;
			}

			description.setNatureIds(newNatures);
			project.setDescription(description, new SubProgressMonitor(monitor,
					10));
		}
	}

}

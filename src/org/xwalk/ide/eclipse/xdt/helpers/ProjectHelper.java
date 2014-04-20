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

package org.xwalk.ide.eclipse.xdt.helpers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.ide.eclipse.xdt.XdtConstants;

public final class ProjectHelper {
	public static JSONObject getManifest(IProject project) {
		IFile manifestFile = project.getFile(XdtConstants.MANIFEST_PATH);
		JSONObject manifestObject = null;
		try {
			manifestObject = new JSONObject(
					stream2String(manifestFile.getContents()));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return manifestObject;
	}
	// get all crosswalk project in the project explorer
	public static IProject[] getXwalkProjects() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = root.getProjects();
		ArrayList<IProject> xwalkProjects = new ArrayList<IProject>();
		for (int i = 0; i < projects.length; i++) {
			if (isXwalkProject(projects[i])) {
				xwalkProjects.add(projects[i]);
			}
		}
		return xwalkProjects.toArray(new IProject[xwalkProjects.size()]);
	}
	
	// get all project in the project explorer
	public static IProject[] getAllProjects() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = root.getProjects();
		ArrayList<IProject> allProjects = new ArrayList<IProject>();
		for (int i = 0; i < projects.length; i++) {
			allProjects.add(projects[i]);
		}
		return allProjects.toArray(new IProject[allProjects.size()]);
	}

	public static boolean isXwalkProject(IProject project) {
		// check if it's an android project based on its nature
		try {
			return project.hasNature(XdtConstants.NATURE_ID);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String stream2String(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = -1;
		try {
			while ((i = is.read()) != -1) {
				baos.write(i);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return baos.toString();
	}

	public static boolean saveStringProperty(IResource resource,
			String propertyName, String value) {
		QualifiedName qname = new QualifiedName(XdtConstants.PLUGIN_ID,
				propertyName);

		try {
			resource.setPersistentProperty(qname, value);
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static String loadStringProperty(IResource resource,
			String propertyName) {
		QualifiedName qname = new QualifiedName(XdtConstants.PLUGIN_ID,
				propertyName);

		try {
			String value = resource.getPersistentProperty(qname);
			return value;
		} catch (CoreException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean saveBooleanProperty(IResource resource,
			String propertyName, boolean value) {
		return saveStringProperty(resource, propertyName,
				Boolean.toString(value));
	}

	public static boolean loadBooleanProperty(IResource resource,
			String propertyName, boolean defaultValue) {
		String value = loadStringProperty(resource, propertyName);
		if (value != null) {
			return Boolean.parseBoolean(value);
		}
		return defaultValue;
	}

	public static Boolean loadBooleanProperty(IResource resource,
			String propertyName) {
		String value = loadStringProperty(resource, propertyName);
		if (value != null) {
			return Boolean.valueOf(value);
		}
		return null;
	}

	public static boolean saveResourceProperty(IResource resource,
			String propertyName, IResource value) {
		if (value != null) {
			IPath iPath = value.getFullPath();
			return saveStringProperty(resource, propertyName, iPath.toString());
		}
		return saveStringProperty(resource, propertyName, ""); //$NON-NLS-1$
	}

	public static IResource loadResourceProperty(IResource resource,
			String propertyName) {
		String value = loadStringProperty(resource, propertyName);

		if (value != null && value.length() > 0) {
			return ResourcesPlugin.getWorkspace().getRoot()
					.findMember(new Path(value));
		}
		return null;
	}
}

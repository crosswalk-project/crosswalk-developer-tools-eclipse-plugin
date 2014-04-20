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

package org.xwalk.ide.eclipse.xdt.preference;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.framework.Bundle;
import org.xwalk.ide.eclipse.xdt.Activator;

public class settingPage extends PreferencePage implements
		IWorkbenchPreferencePage, SelectionListener {
	private Text mXwalkPathText;
	private static String sLastXwalkLocation = "";
	private static String sLastAndroidLocation = "";
	private Button mXwalkPathBrowseButton;
	private Text mAndroidPathTxt;
	private Button mAndroidPathBrowseButton;

	public settingPage() {
		setTitle("Crosswalk App");
		Bundle bundle = Platform.getBundle("org.xwalk.ide.eclipse.xdt");
		Path path = new Path("images/icon-48.png");
		URL imageUrl = FileLocator.find(bundle, path, null);
		setImageDescriptor(ImageDescriptor.createFromURL(imageUrl));
	}

	public settingPage(String title) {
		super(title);
	}

	public settingPage(String title, ImageDescriptor image) {
		super(title, image);
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		composite.setLayout(layout);

		Group xwalkSettingGroup = new Group(composite, SWT.SHADOW_OUT);
		xwalkSettingGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		xwalkSettingGroup.setText("Crosswalk Setting");
		xwalkSettingGroup.setLayout(new GridLayout(1, false));
		Label xwalkPathLabel = new Label(xwalkSettingGroup, SWT.NONE);
		xwalkPathLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1));
		xwalkPathLabel.setText("Path:");

		mXwalkPathText = new Text(xwalkSettingGroup, SWT.BORDER);
		mXwalkPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		mXwalkPathBrowseButton = new Button(xwalkSettingGroup, SWT.NONE);
		mXwalkPathBrowseButton.setText("Browse...");
		mXwalkPathBrowseButton.addSelectionListener(this);
		mXwalkPathBrowseButton.setEnabled(true);
		mXwalkPathBrowseButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 1, 1));

		Group androidSettingGroup = new Group(composite, SWT.SHADOW_OUT);
		androidSettingGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		androidSettingGroup.setText("Android Setting");
		androidSettingGroup.setLayout(new GridLayout(1, false));

		Label androidPathLabel = new Label(androidSettingGroup, SWT.NONE);
		androidPathLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		androidPathLabel.setText("SDK Path:");
		mAndroidPathTxt = new Text(androidSettingGroup, SWT.BORDER);
		mAndroidPathTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		mAndroidPathBrowseButton = new Button(androidSettingGroup, SWT.NONE);
		mAndroidPathBrowseButton.setText("Browse...");
		mAndroidPathBrowseButton.addSelectionListener(this);
		mAndroidPathBrowseButton.setEnabled(true);
		initializeValues();
		return composite;
	}

	// ---- Implements SelectionListener ----

		@Override
		public void widgetSelected(SelectionEvent e) {
			// TODO:
			Object source = e.getSource();
			if (source == mXwalkPathBrowseButton) {
				String dir = promptUserForLocation(getShell(), "XwalkPath", "Select folder for Crosswalk path");
				if (dir != null) {
					mXwalkPathText.setText(dir);
				}
			}
			else if (source == mAndroidPathBrowseButton) {
				String dir = promptUserForLocation(getShell(), "AndroidPath", "Select folder for Android SDK path");
				if (dir != null) {
					mAndroidPathTxt.setText(dir);
				}
			}
		}


		private String promptUserForLocation(Shell shell, String forText, String message) {
			DirectoryDialog dd = new DirectoryDialog(getShell());
			dd.setMessage(message);
			String curLocation;
			String dir;

			if (forText == "XwalkPath") {
				curLocation = mXwalkPathText.getText().trim();
				if (!curLocation.isEmpty()) {
					dd.setFilterPath(curLocation);
				} else if (sLastXwalkLocation != null) {
					dd.setFilterPath(sLastXwalkLocation);
				}
			} else {
				curLocation = mAndroidPathTxt.getText().trim();
				if (!curLocation.isEmpty()) {
					dd.setFilterPath(curLocation);
				} else if (sLastAndroidLocation != null) {
					dd.setFilterPath(sLastAndroidLocation);
				}
			}
			dir = dd.open();
			if ((dir != null) && (forText == "XwalkPath")) {
				sLastXwalkLocation = dir;
			}
			else {
				sLastAndroidLocation = dir;
			}
			return dir;
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {

		}

		private void initializeValues () {
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			if ((store.getString(Settings.XWALK_PATH) == "")||(store.getString(Settings.XWALK_PATH) == null)) {
				store.setDefault(Settings.XWALK_PATH, System.getProperty("user.home"));
				sLastXwalkLocation = System.getProperty("user.home");
			} else {
				sLastXwalkLocation = store.getString(Settings.XWALK_PATH);
			}

			if ((store.getString(Settings.ANDROID_SDK_PATH) == "")||(store.getString(Settings.ANDROID_SDK_PATH) == null)) {
				store.setDefault(Settings.ANDROID_SDK_PATH, System.getProperty("user.home"));
				sLastAndroidLocation = System.getProperty("user.home");
			} else {
				sLastAndroidLocation = store.getString(Settings.ANDROID_SDK_PATH);
			}

			if (sLastXwalkLocation != "") {
				mXwalkPathText.setText(sLastXwalkLocation);
			}

			if (sLastAndroidLocation != "") {
				mAndroidPathTxt.setText(sLastAndroidLocation);
			}
		}

		private void storeValues() {
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			store.setValue(Settings.XWALK_PATH, mXwalkPathText.getText().trim());
			store.setValue(Settings.ANDROID_SDK_PATH, mAndroidPathTxt.getText().trim());
		}

		private void initializeDefaults() {
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			System.out.println("store.getDefaultString(Settings.XWALK_PATH):"+store.getDefaultString(Settings.XWALK_PATH));
			mXwalkPathText.setText(store.getDefaultString(Settings.XWALK_PATH));
			mAndroidPathTxt.setText(store.getDefaultString(Settings.ANDROID_SDK_PATH));
		}

		protected void performDefaults () {
			initializeDefaults();
			super.performDefaults();
		}

		public boolean performOk () {
			storeValues();
			return true;
		}
}

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
import java.net.URL;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;
import org.xwalk.ide.eclipse.xdt.Activator;
import org.xwalk.ide.eclipse.xdt.XdtConstants;
import org.xwalk.ide.eclipse.xdt.preference.Settings;

public class ExportProjectPage extends WizardPage implements ModifyListener,
		SelectionListener {
	private static final int FIELD_WIDTH = 300;
	private static final int WIZARD_PAGE_WIDTH = 600;
	private final ExportProjectWizard mWizard;
	private ApkParameters mApkParameters;
	private Combo mTargetFormatCombo;
	private Combo mModeCombo;
	private Combo mArchCombo;
	private Combo mOrientationCombo;
	private Text mAndroidPackageText;
	private Text mAppVersionCodeText;
	private Text mKeyStorePathText;
	private Text mKeyStoreAliasText;
	private Text mKeyStorePassCodeText;
	private Text mAppVersionCodeBaseText;
	private Text mDestinationPathText;
	private Button mDestinationbrowserButton;
	private Button mKeyStorePathbrowserButton;
	private Button mDebugKeyBtn;
	private Button mFullScreenBtn;
	private Button mRemoteDebugBtn;
	private Button mCompressJSBtn;
	private Button mCompressCSSBtn;
	private Label mPackageInfo;
	private String mDestinationMessage;
	private Boolean mDestinationCanFinish;
	private Boolean mSettingCanFinish;
	private Label mKeyStorePathLabel;
	private Label mKeyStoreAliasLabel;
	private Label mKeyStorePassCodeLabel;

	protected ExportProjectPage(ExportProjectWizard wizard) {
		super("exportCrosswalkApp");
		mWizard = wizard;
		mApkParameters = wizard.getApkParameters();
		mDestinationMessage = new String("Select a path to export the crosswalk app");
		setTitle("Export Crosswalk Application");
		setDescription("Export a Crosswalk Application to APK or XPK package.");
		Bundle bundle = Platform.getBundle("org.xwalk.ide.eclipse.xdt");
		Path path = new Path("images/icon-68.png");
		URL imageUrl = FileLocator.find(bundle, path, null);
		setImageDescriptor(ImageDescriptor.createFromURL(imageUrl));
		mDestinationCanFinish = false;
		mSettingCanFinish = true;
		if (!checkPreferenceSettings()) {
			mSettingCanFinish = false;
			setMessage("Crosswalk's path has not been set correctly. Please go to Window->"
					+ "Preferences->Crosswalk App to configure it.",
					WARNING);
		}
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NULL);
		setControl(mainComposite);
		GridLayout gl_container = new GridLayout(4, false);
		gl_container.horizontalSpacing = 10;
		mainComposite.setLayout(gl_container);

		// package name for android activity
		Label androidPackageLabel = new Label(mainComposite, SWT.NONE);
		androidPackageLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
						false, false, 1, 1));
		androidPackageLabel.setText("*Package Name:");
		mAndroidPackageText = new Text(mainComposite, SWT.BORDER);
		GridData gdAndroidPackageText = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gdAndroidPackageText.widthHint = FIELD_WIDTH;
		mAndroidPackageText.setLayoutData(gdAndroidPackageText);
		mAndroidPackageText.setText(mApkParameters.packageName);

		createFieldDecoration(mAndroidPackageText,
						"The package name is used by Android Activity in the APK,"
								+ "for example: com.yourcompany.yourapp.");
		Label appVersionCodeLabel = new Label(mainComposite, SWT.NONE);
		appVersionCodeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
						false, false, 1, 1));
		appVersionCodeLabel.setText("*App version Code:");
		mAppVersionCodeText = new Text(mainComposite, SWT.BORDER);
		mAppVersionCodeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		mAppVersionCodeText.setText("1");
		Label appVersionCodeBaseLabel = new Label(mainComposite, SWT.NONE);
		appVersionCodeBaseLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
						false, false, 1, 1));
		appVersionCodeBaseLabel.setText("*App version Code Base:");
		mAppVersionCodeBaseText = new Text(mainComposite, SWT.BORDER);
		mAppVersionCodeBaseText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		mAppVersionCodeBaseText.setText("1");

		Label modeLabel = new Label(mainComposite, SWT.NONE);
		modeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
						false, false, 1, 1));
		modeLabel.setText("Package mode:");
		mModeCombo = new Combo(mainComposite, SWT.READ_ONLY);
		GridData gdModeCombo = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1);
		mModeCombo.setLayoutData(gdModeCombo);
		mModeCombo.setItems(XdtConstants.SUPPORTED_MODES);
		mModeCombo.setText("embedded");
		mModeCombo.addSelectionListener(this);

		mArchCombo = new Combo(mainComposite, SWT.READ_ONLY);
		GridData gdArchCombo = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1);
		mArchCombo.setLayoutData(gdArchCombo);
		mArchCombo.setItems(XdtConstants.SUPPORTED_ARCHS);
		mArchCombo.setText("x86");
		mArchCombo.addSelectionListener(this);

		Label OtherSettingsLabel = new Label(mainComposite, SWT.NONE);
		OtherSettingsLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
						false, false, 1, 1));
		OtherSettingsLabel.setText("Optional settings:");
		mOrientationCombo = new Combo(mainComposite, SWT.READ_ONLY);
		GridData gdOrientationCombo = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1);
		mOrientationCombo.setLayoutData(gdOrientationCombo);
		mOrientationCombo.setItems(XdtConstants.SUPPORTED_ORIENTATION);
		mOrientationCombo.setText("Orientation: portrait");
		mOrientationCombo.addSelectionListener(this);

		mFullScreenBtn = new Button(mainComposite, SWT.CHECK);
		GridData gdFullScreen = new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 1);
		mFullScreenBtn.setLayoutData(gdFullScreen);
		mFullScreenBtn.setText("Full screen mode");
		mFullScreenBtn.addSelectionListener(this);

		mRemoteDebugBtn = new Button(mainComposite, SWT.CHECK);
		GridData gdremoteDebug = new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 1);
		mRemoteDebugBtn.setLayoutData(gdremoteDebug);
		mRemoteDebugBtn.setText("Remote debug");
		mRemoteDebugBtn.addSelectionListener(this);

		Label CompressLabel = new Label(mainComposite, SWT.NONE);
		CompressLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
						false, false, 1, 1));
		CompressLabel.setText("");
		mCompressJSBtn = new Button(mainComposite, SWT.CHECK);
		mCompressJSBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 1));
		mCompressJSBtn.setText("Compress Javascript");
		mCompressJSBtn.addSelectionListener(this);

		mCompressCSSBtn = new Button(mainComposite, SWT.CHECK);
		mCompressCSSBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 1));
		mCompressCSSBtn.setText("Compress CSS");
		mCompressCSSBtn.addSelectionListener(this);

		mDebugKeyBtn = new Button(mainComposite, SWT.CHECK);
		mDebugKeyBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
				false, 1, 1));
		mDebugKeyBtn.setText("Use Debug Key");
		mDebugKeyBtn.addSelectionListener(this);
		mDebugKeyBtn.setSelection(true);

		mKeyStorePathLabel = new Label(mainComposite, SWT.NONE);
		mKeyStorePathLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
						false, false, 1, 1));
		mKeyStorePathLabel.setText("*Keystore Path:");
		mKeyStorePathText = new Text(mainComposite, SWT.BORDER);
		mKeyStorePathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
						false, false, 2, 1));

		mKeyStorePathbrowserButton = new Button(mainComposite, SWT.NONE);
		mKeyStorePathbrowserButton.setText("Browse...");
		mKeyStorePathbrowserButton.addSelectionListener(this);
		mKeyStorePathbrowserButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));

		mKeyStoreAliasLabel = new Label(mainComposite, SWT.NONE);
		mKeyStoreAliasLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
						false, false, 1, 1));
		mKeyStoreAliasLabel.setText("*Keystore Alias:");
		mKeyStoreAliasText = new Text(mainComposite, SWT.BORDER);
		mKeyStoreAliasText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
						false, false, 3, 1));

		mKeyStorePassCodeLabel = new Label(mainComposite, SWT.NONE);
		mKeyStorePassCodeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
						false, false, 1, 1));
		mKeyStorePassCodeLabel.setText("*Keystore Pass Code:");
		mKeyStorePassCodeText = new Text(mainComposite, SWT.BORDER);
		mKeyStorePassCodeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
						false, false, 3, 1));

		Label horizontalLine = new Label(mainComposite, SWT.HORIZONTAL|SWT.SEPARATOR);
		horizontalLine.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 3));
		Label destinationFileLabel = new Label(mainComposite, SWT.NONE);
		destinationFileLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
						false, false, 1, 1));
		destinationFileLabel.setText("*Export Destination:");
		mDestinationPathText = new Text(mainComposite, SWT.BORDER);
		mDestinationPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
						false, false, 2, 1));

		mDestinationbrowserButton = new Button(mainComposite, SWT.NONE);
		mDestinationbrowserButton.setText("Browse...");
		mDestinationbrowserButton.addSelectionListener(this);
		mDestinationbrowserButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		Label packageInfoLabel = new  Label(mainComposite, SWT.NONE);
		packageInfoLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 2));
		packageInfoLabel.setText("Export to:");
		mPackageInfo = new Label(mainComposite, SWT.NONE);
		mPackageInfo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 2));
		mPackageInfo.setText("");
		mDestinationbrowserButton.setEnabled(true);

		mAndroidPackageText.addModifyListener(this);
		mAppVersionCodeText.addModifyListener(this);
		mAppVersionCodeBaseText.addModifyListener(this);
		mKeyStorePathText.addModifyListener(this);
		mKeyStoreAliasText.addModifyListener(this);
		mKeyStorePassCodeText.addModifyListener(this);
		mDestinationPathText.addModifyListener(this);
		CanFinish();

		Label dummy = new Label(mainComposite, SWT.NONE);
		GridData data = new GridData();
		data.horizontalSpan = 4;
		data.widthHint = WIZARD_PAGE_WIDTH;
		dummy.setLayoutData(data);
		setVisibleOfKeyStoreWidgets(false);
	}

	private ControlDecoration createFieldDecoration(Control control,
			String description) {
		ControlDecoration dec = new ControlDecoration(control, SWT.LEFT);
		dec.setMarginWidth(2);
		FieldDecoration errorFieldIndicator = FieldDecorationRegistry
				.getDefault().getFieldDecoration(
						FieldDecorationRegistry.DEC_INFORMATION);
		dec.setImage(errorFieldIndicator.getImage());
		dec.setDescriptionText(description);
		control.setToolTipText(description);
		return dec;
	}

	private boolean checkPreferenceSettings() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String xwalkPath = store.getString(Settings.XWALK_PATH);

		File xwalk = new File(xwalkPath);
		if (!xwalk.exists()) {
			return false;
		}
		return true;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			onShow();
		}
	}

	void onShow() {
	}

	private void CanFinish() {
		int errorCount;
		Color warningColor = new Color(Display.getCurrent(), 255, 255, 0);
		Color normalColor = new Color(Display.getCurrent(), 255, 255, 255);
		errorCount = 0;
		String errorMessage = new String("");
		if (mAndroidPackageText.getText().length() == 0) {
			mAndroidPackageText.setBackground(warningColor);
			errorCount++;
		}
		else {
			mApkParameters.packageName = mAndroidPackageText.getText();
			mAndroidPackageText.setBackground(normalColor);
		}

		if (mAppVersionCodeText.getText().length() == 0) {
			mAppVersionCodeText.setBackground(warningColor);
			errorCount++;
		}
		else {
			mApkParameters.appVersionCode = mAppVersionCodeText.getText();
			mAppVersionCodeText.setBackground(normalColor);
		}

		if (mAppVersionCodeBaseText.getText().length() == 0) {
			mAppVersionCodeBaseText.setBackground(warningColor);
			errorCount++;
		}
		else {
			mApkParameters.appVersionCodeBase = mAppVersionCodeBaseText.getText();
			mAppVersionCodeBaseText.setBackground(normalColor);
		}

		if (mKeyStorePathText.getText().length() == 0) {
			mKeyStorePathText.setBackground(warningColor);
			if (!mDebugKeyBtn.getSelection()) {
				errorCount++;
			}
		}
		else {
			mApkParameters.keyStorePath = mKeyStorePathText.getText();
			mKeyStorePathText.setBackground(normalColor);
		}

		if (mKeyStoreAliasText.getText().length() == 0) {
			mKeyStoreAliasText.setBackground(warningColor);
			if (!mDebugKeyBtn.getSelection()) {
				errorCount++;
			}
		}
		else {
			mApkParameters.keyStoreAlias = mKeyStoreAliasText.getText();
			mKeyStoreAliasText.setBackground(normalColor);
		}

		if (mKeyStorePassCodeText.getText().length() == 0) {
			mKeyStorePassCodeText.setBackground(warningColor);
			if (!mDebugKeyBtn.getSelection()) {
				errorCount++;
			}
		}
		else {
			mApkParameters.keyStorePassCode = mKeyStorePassCodeText.getText();
			mKeyStorePassCodeText.setBackground(normalColor);
		}

		if (mDestinationPathText.getText().length() == 0) {
			mDestinationPathText.setBackground(warningColor);
		}
		else {
			mDestinationPathText.setBackground(normalColor);
		}

		if (errorCount != 0)
			errorMessage = "The field(s) with * mark can't be empty!";
		
		if ((errorCount == 0) && mDestinationCanFinish) {
			setPageComplete(true);
			setMessage(mDestinationMessage);
		}
		else {
			setPageComplete(false);
			setMessage(mDestinationMessage + errorMessage, WARNING);
		}
	}
	// ---- Implements ModifyListener ----

	@Override
	public void modifyText(ModifyEvent e) {
		Object source = e.getSource();
		if (mSettingCanFinish == false)
			return;
		if (source == mAndroidPackageText) {
		} else if (source == mDestinationPathText) {
			onDestinationChange();
		}

		CanFinish();
	}

	private void onDestinationChange() {
		String path = mDestinationPathText.getText().trim();
		if (path.length() == 0) {
			// reset canFinish in the wizard.
			mWizard.resetDestination();
			mApkParameters.targetFolder = "";
			mDestinationCanFinish = false;
			setPageComplete(false);
			return;
		}

		File file = new File(path);
		if (!file.exists()) {
			mDestinationMessage = "Selected destination is not available. ";
			// reset canFinish in the wizard.
			mWizard.resetDestination();
			mApkParameters.targetFolder = "";
			mDestinationCanFinish = false;
			setPageComplete(false);
			return;
		}
		else {
			String targetFilename = mApkParameters.appName + ".apk";
			File targetFile = new File(path, targetFilename);
			mApkParameters.targetFolder = path;
			if (targetFile.isFile()) {
				mDestinationMessage = "Target file already exists. Continue will overwrite it. ";
				mPackageInfo.setText(targetFile.toString());
			}
			else {
				mDestinationMessage = "";
				mPackageInfo.setText(targetFile.toString());
			}
			mWizard.setDestination(targetFile);
			mDestinationCanFinish = true;
		}
	}

	// ---- Implements SelectionListener ----

	@Override
	public void widgetSelected(SelectionEvent e) {
		int index = 0;
		Object source = e.getSource();
		if (source == mTargetFormatCombo) {
			index = mTargetFormatCombo.getSelectionIndex();
			mWizard.setTargetFormat(XdtConstants.TARGET_FORMATS[index]);
		}
		else if (source == mDestinationbrowserButton) {
			String dir = promptUserForLocation(getShell(), mDestinationPathText, "Select an Export Path");
			if (dir != null) {
				mDestinationPathText.setText(dir);
			}
		}
		else if (source == mKeyStorePathbrowserButton) {
			String dir = promptUserForFile(getShell());
			if (dir != null) {
				mKeyStorePathText.setText(dir);
			}
		}
		else if (source == mModeCombo) {
			if (mModeCombo.getSelectionIndex() == 0) {
				mArchCombo.setVisible(true);
				mApkParameters.embeddedMode = true;
			}
			else {
				mArchCombo.setVisible(false);
				mApkParameters.embeddedMode = false;
			}
		}
		else if (source == mArchCombo) {
			if (mArchCombo.getSelectionIndex() == 0) {
				mApkParameters.arch = "x86";
			}
			else {
				mApkParameters.arch = "arm";
			}
		}
		else if (source == mOrientationCombo) {
			if (mOrientationCombo.getSelectionIndex() == 0) {
				mApkParameters.orientation = "portrait";
			}
			else {
				mApkParameters.orientation = "landscape";
			}
		}
		else if (source == mFullScreenBtn) {
			mApkParameters.fullscreenMode = mFullScreenBtn.getSelection();
		}
		else if (source == mRemoteDebugBtn) {
			mApkParameters.remoteDebugMode = mRemoteDebugBtn.getSelection();
		}
		else if (source == mCompressJSBtn) {
			mApkParameters.compressJS = mCompressJSBtn.getSelection();
		}
		else if (source == mCompressCSSBtn) {
			mApkParameters.compressCSS = mCompressCSSBtn.getSelection();
		}
		else if (source == mDebugKeyBtn) {
			mApkParameters.useDebugKeyStore = mDebugKeyBtn.getSelection();
			if (mDebugKeyBtn.getSelection()) {
				setVisibleOfKeyStoreWidgets(false);
			}
			else {
				setVisibleOfKeyStoreWidgets(true);
			}
			CanFinish();
		}
	}

	private void setVisibleOfKeyStoreWidgets(boolean visibility) {
		mKeyStorePathLabel.setVisible(visibility);
		mKeyStoreAliasLabel.setVisible(visibility);
		mKeyStorePassCodeLabel.setVisible(visibility);
		mKeyStorePathbrowserButton.setVisible(visibility);
		mKeyStorePathText.setVisible(visibility);
		mKeyStoreAliasText.setVisible(visibility);
		mKeyStorePassCodeText.setVisible(visibility);
	}

	private String promptUserForLocation(Shell shell, Text textWidget,  String message) {
		DirectoryDialog dd = new DirectoryDialog(getShell());
		dd.setMessage(message);
		String curLocation;
		String dir;

		curLocation = textWidget.getText().trim();
		if (!curLocation.isEmpty()) {
			dd.setFilterPath(curLocation);
		}

		dir = dd.open();
		return dir;
	}

	private String promptUserForFile(Shell shell) {
		FileDialog dd = new FileDialog(getShell());
		String file;

		file = dd.open();
		return file;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {

	}

	public boolean canFlipToNextPage() {
		return false;
	}
}

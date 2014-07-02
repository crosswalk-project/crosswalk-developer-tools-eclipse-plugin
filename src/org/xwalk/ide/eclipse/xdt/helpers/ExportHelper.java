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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.preference.IPreferenceStore;
import org.xwalk.ide.eclipse.xdt.Activator;
import org.xwalk.ide.eclipse.xdt.XdtConstants;
import org.xwalk.ide.eclipse.xdt.XdtPluginLog;
import org.xwalk.ide.eclipse.xdt.preference.Settings;
import org.xwalk.ide.eclipse.xdt.wizards.export.ApkParameters;

public final class ExportHelper {
	private static IProgressMonitor mMonitor;
	public static int doExport(IProject project, String targetFormat,
			File destination, Object formatParameters, IProgressMonitor monitor) throws IOException {
		int runResult = 0;
		mMonitor = monitor;
		if (targetFormat == "APK") {
			runResult = generateApk(project,
					(ApkParameters) formatParameters);
		} else if (targetFormat == "XPK") {
		} else {
			XdtPluginLog.logError("Unsupported exporting format:"
					+ targetFormat, null);
			return 1;
		}
		return runResult;
	}

	public static String getSdkPath() {
		IPreferencesService service = Platform.getPreferencesService();
		String qualifier = XdtConstants.ADT_PLUGIN_ID;
		String key = XdtConstants.PREFS_SDK_DIR;
		String defaultValue = "";
		String androidSdkPath = service.getString(qualifier, key, defaultValue,
				null);
		return androidSdkPath;
	}

	public static int generateApk(IProject project, ApkParameters parameters)
			throws IOException {
		StringBuilder cmd = new StringBuilder();
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String androidSdkPath = getSdkPath();
		String xwalkPath = store.getString(Settings.XWALK_PATH);
		File xwalkDir = new File(xwalkPath);
		String projectPath = project.getLocation().toFile().getAbsolutePath();
		int runResult = 0;

		// add Android SDK in to system variable "PATH"
		final Map<String, String> env = new HashMap<String, String>(
				System.getenv());
		String androidTools = androidSdkPath + File.separator + "tools";
		String androidPlatformTools = androidSdkPath + File.separator
				+ "platform-tools";
		env.put("PATH", env.get("PATH") + File.pathSeparator + androidTools
				+ File.pathSeparator + androidPlatformTools);

		cmd.append("python make_apk.py");

		if (parameters.embeddedMode) {
			cmd.append(" --mode=embedded");
			cmd.append(" --arch=" + parameters.arch);
		}
		else {
			cmd.append(" --mode=shared");
		}
		cmd.append(" --name=" + parameters.appName)
				.append(" --package=" + parameters.packageName)
				.append(" --app-root=" + projectPath)
				.append(" --app-local-path=" + parameters.entryFile)
				.append(" --app-versionCode=" + parameters.appVersionCode)
				.append(" --app-versionCodeBase=" + parameters.appVersionCodeBase)
				.append(" --orientation=" + parameters.orientation)
				.append(" --target-dir=" + parameters.targetFolder);

		if (parameters.remoteDebugMode) {
			cmd.append(" --enable-remote-debuggin");
		}
		if (parameters.fullscreenMode) {
			cmd.append(" -f");
		}

		if (!parameters.useDebugKeyStore) {
			cmd.append(" --keystore-path=" + parameters.keyStorePath)
				.append(" --keystore-alias=" + parameters.keyStoreAlias)
				.append(" --keystore-passcode=" + parameters.keyStorePassCode);
		}

		if (parameters.compressJS && parameters.compressCSS) {
			cmd.append(" --compressor");
		}
		else if (parameters.compressJS) {
			cmd.append(" --compressor=js");
		}
		else if (parameters.compressCSS){
			cmd.append(" --compressor=css");
		}

		XdtPluginLog.logInfo("***** cmd: " + cmd.toString());
		mMonitor.worked(10);
		Process process = Runtime.getRuntime().exec(cmd.toString(),
				mapToStringArray(env), xwalkDir);

		try {
			for (int i=0; i<88; i++) {
				mMonitor.worked(1);
				Thread.sleep(120);
			}
		}
		catch (InterruptedException e) {
		}
		// redirect the error and input stream
		try {
			exportStream(process.getErrorStream(), System.err);
			exportStream(process.getInputStream(), System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			runResult = process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
			XdtPluginLog.logError("Error when involking package tool.", e);
		}
		return runResult;
	}

	static String[] mapToStringArray(Map<String, String> map) {
		final String[] strings = new String[map.size()];
		int i = 0;
		for (Map.Entry<String, String> e : map.entrySet()) {
			strings[i] = e.getKey() + '=' + e.getValue();
			i++;
		}
		return strings;
	}

	private static void exportStream(InputStream in, PrintStream print)
			throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			int c;
			while ((c = in.read()) != -1) {
				os.write(c);
			}
			print.println(new String(os.toByteArray()));
		} finally {
			os.close();
		}
	}

	public static File generateXpk(IProject project) {
		return new File("temp.xpk");
	}

	/**
	 * Get the APK path generated by the involved package tool.
	 * 
	 * @return
	 */
	public static File getGeneratedApk(ApkParameters parameters) {
		String xwalkPath = Activator.getDefault().getPreferenceStore()
				.getString(Settings.XWALK_PATH);
		String apkPath = xwalkPath + File.separator + parameters.appName;
		if (parameters.embeddedMode) {
			apkPath = apkPath + "_" + parameters.arch;
		}
		File apkFile = new File(apkPath + ".apk");
		return apkFile;
	}

	public static String[] getValidArchsForEmbeddedMode(String xwalkPath) {
		File nativeLibs = new File(xwalkPath, "native_libs");
		String[] valid_archs = nativeLibs.list();
		return valid_archs;
	}

	public static boolean moveFile(File src, File dest) {
		try {
			InputStream inStream = new FileInputStream(src);
			OutputStream outStream = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];

			int length;
			// copy the file content in bytes
			while ((length = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, length);
			}
			inStream.close();
			outStream.close();
			// delete the original file
			src.delete();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}

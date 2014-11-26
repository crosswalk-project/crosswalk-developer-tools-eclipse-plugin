## Introduction ##

Crosswalk Developer Tools Eclipse plugin is a suite of tools to develop Crosswalk Applications which extends generic Eclipse to be able to create a Crosswalk Application project, import an exist project from file system or export the Crosswalk Application to be an .apk for running on Android emulator or device.

## Requirement ##
Below dependencies are needed for Crosswalk Developer Tools Eclipse Plugin

*   **Android SDK** for your platform from http://developer.android.com/sdk/index.html
*   **Eclipse 4.2.2** ,it can be downloaded from http://eclipse.org/ 
*   **Crosswalk for Android**, available at https://download.01.org/crosswalk/releases/
    * Make sure your environment is properly setup, as described in the ["host setup" pages in Crosswalk's website](https://crosswalk-project.org/documentation/getting_started.html).

## Installation ##
Several simple steps to install the plugin into your Eclipse as following:

1.Use below command to download the source code

    git clone https://github.com/crosswalk-project/crosswalk-developer-tools-eclipse-plugin.git

2.Import the source code to Eclipse

    In Eclipse menu select:
    "File->Import"
    
    Then select "General/Existing Projects into workspace" and click "Next" button

3.Build the plugin and export the binary

    In Eclipse menu select:
    "File->Export"
    
    Then select "Plug-in Development/Deployable plug-ins and fragments" and click "Next" button

    Choose a directoty to export the plugin

4.Install the plugin

    Copy the plugin file you exported to the plugins directory in Eclipse

## Usage ##

Configuration for Android SDK and Crosswalk
 
    Before start using the plugin, it's needed to set up the path for the Android SDK and Crosswalk.
    We provide a very simple way to do that as below:
    
    1.In Eclipse menu:
    Select "Window->Preferences"
    
    2.Select "Crosswalk App" preference of the navigation menu

    3.Click "Browse" button to set the path you place your Android SDK and Crosswalk.
    
    4.Click "Save" button to save the setting

Create Crosswalk Application Project

    1.In Eclipse menu:
    Select "File->New->Crosswalk Project"

    2.Input the "Application name" and "Project name" then click "Finish" button
    
    A new empty Crosswalk Project will be created in the workspace

Import an exist project
    
    If you already have an exist Crosswalk in the filesystem, you will be able to import it to Eclipse by following Steps
   
    1.In Eclipse menu:
    Select "File->Import"
    
    2.Select "Crosswalk/Import Crosswalk Project" then click "Next" button

    3.Use "Browse" button to browse the path of your Crosswalk Project
    
    4.Click "Finish" button 

Export Crosswalk Application

    1.In Eclipse menu:
    Select "File->Export"

    2.Select "Crosswalk/Export Crosswalk App" then click next

    3.Input the required information then click "Finish" button


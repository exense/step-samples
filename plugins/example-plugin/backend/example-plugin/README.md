# Step custom controller plugin sample

This manual explains how to add a custom plugin to your Step Enterprise onPrem.

## Preparations
- You need to have installed a JDK and maven to create your own Step plugin
- The target Java version must be compatible with the Java version used to run the Step controller 
- The details to build the frontend parts of your custom plugin can be found in the frontend sibling folder 

## Plugin creation and development
- To create a plugin, your class must 
   - extends the class step.core.plugins.AbstractControllerPlugin
   - use the Class level annotation 'step.core.plugins.@Plugin'
- For frontend custom plugin
    - copy the FE distribution in the corresponding `webapp` resource folder
    - register the webapp root in the plugin `startServer` method
    - specify the path to the remoteEntry.js file in the `getWebPlugin` method

## Building the Plugin

- Run following maven command:
```
mvn clean package
```
The resulting jar file will be created in the `target` folder.

## Deployment

- Copy the jar file into the lib folder of your Step controller installation.

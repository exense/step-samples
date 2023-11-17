# Frontend plugin sample

This manual explains how to add a FE plugin to your Step Enterprise onPrem.

## Preparations
- You need your credentials for Exense's npm registry available to all Enterprise customers
- Go to plugins/example-plugin/frontend in your console
- Authorize to Exense`s registry with the following command
```
npm login --registry=https://nexus-enterprise.exense.ch/repository/exense-npm/ --scope=@exense
```
Command will prompt credentials, enter it. After successful login, npm will modify `.npmrc` 
file in the home directory with registry's access token. Similar lines should appear in `.npmrc`:
```
//nexus-enterprise.exense.ch/repository/exense-npm/:_authToken=NpmToken.00000000-0000-0000-0000-00000000000
@exense:registry=https://nexus-enterprise.exense.ch/repository/exense-npm/
```
**NOTE:** Token value will be different.

If you want to keep the access to Exense's registry for different projects at the same time, 
keep this token information in the home folder's `.npmrc`.

If you want to have the access for a single project only, create its own `.npmrc` file,
cut those two lines from home's `.npmrc` and paste it to the project's one.

## Plugin creation
- Install npm dependencies
```
npm i
```
- Generate plugin sample with angular-cli
```
ng generate @exense/step-core:plugin examplePlugin
``` 
`examplePlugin` name may vary. The name will affect on some folders and commands name.
Next the `${pluginName}` will be used as replacement.

Command will request to enter the port and choose the style's preprocessor (we recommend SCSS).

As the result an angular application with micro-frontend plugin module will be created.

Application can be used to serve the plugin for development purposes.

Plugin sources will be located at `/projects/${pluginName}/src/app/modules/plugin`

## Plugin development
- Run the plugin's application
```
npm run start
```
Application will be launched on the port, which you've chosen in the plugin's creation. (5002 by default)
Plugin's entry module assembled sources will be hosted at: http://localhost:5002/remoteEntry.js

Next you will need to make it accessible in the main <b>Step</b> host application.

It can be achieved with `proxy.conf.json` file in `step-frontend/projects/step-app` for the open source version of Step: https://github.com/exense/step-frontend. 

Modify it by adding the plugin section:
```
{
  "/rest": {
    "target": "https://stepos-master.stepcloud-test.ch:443",
    "secure": false,
    "changeOrigin": true
  },
  "/rtm": {
    "target": "https://stepos-master.stepcloud-test.ch:443",
    "secure": false,
    "changeOrigin": true
  },
  "/plugin": {
    "target": "http://localhost:5002",
    "pathRewrite": {
      "^/plugin": ""
    }
  }
}
```

Then you need to inform the <b>Step</b> frontend, to include this plugin:
- Search for `step-frontend/projects/step-frontend/src/lib/plugins-initializer/plugins-intializer.ts`
- Find `ADDITIONAL_PLUGINS` constant
- Add the following entry `{name: 'pluginExample', entryPoint: 'plugin/remoteEntry.js'}`. So the result code should be the following:
```
// For testing purposes only
// Allows to add plugins, that don't returned from BE
const ADDITIONAL_PLUGINS: ReadonlyArray<MicrofrontendPluginDefinition> = [
  // Add object like this {name: 'pluginName', entryPoint: 'pluginName/remoteEntry.js' }
  {name: 'plugin', entryPoint: 'plugin/remoteEntry.js'}
];
```
- Run the `Step` frontend application. As the result the new menu item `Example Plugin` should appear.
- Inside the sample project try to change something in the `projects/${pluginName}/src/app/modules/plugin`. 
Angular will rebuild the plugin served at `localhost:5002` and changes will be reflected at running main application.

## Building the Plugin
After plugin's creation a build command is generated in the package.json.
It looks like: `build:a2:${pluginName}`.

Invoke it:
```
npm run build:a2:examplePlugin
```
The build's result will be located at `dist/${pluginName}`

- Copy files from `dist/${pluginName}` to the FE folder of the plugin
- Add the required plugin settings (name and path to the remoteEntry.js) to point to the hosted FE folder

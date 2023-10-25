# Frontend plugin sample

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

Command will request to enter the port and choose the style's preprocessor.

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

It can be achieved with `proxy.conf.json` file in `step-frontend/projects/step-app`. 

Modify it in the following way, by adding the plugin section
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

## Plugin build
After plugin's creation and new command should automatically appear in the package.json.
It should look like `build:a2:${pluginName}`.

Invoke it:
```
npm run build:a2:examplePlugin
```
The build's result will be located at `dist/${pluginName}`

- Copy files from this folder to the server, where the main application is hosted.
- These files should be accessible by the main application and located on the same host.
- Do the required changes at backend to return the plugin's information (name and path to the remoteEntry.js) by `/rest/app/plugins` endpoint

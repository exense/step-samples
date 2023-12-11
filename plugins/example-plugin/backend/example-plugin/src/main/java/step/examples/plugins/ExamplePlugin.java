package step.examples.plugins;


import step.core.GlobalContext;
import step.core.plugins.*;

@Plugin
public class ExamplePlugin extends AbstractControllerPlugin {

    private final String webAppRoot = "webapp-examplePlugin";
    private final String pluginName = "examplePlugin";

    @Override
    public void serverStart(GlobalContext context) throws Exception {
        super.serverStart(context);
        context.getServiceRegistrationCallback().registerWebAppRoot(webAppRoot);
    }

    @Override
    public AbstractWebPlugin getWebPlugin() {
        Ng2WebPlugin webPlugin = new Ng2WebPlugin();
        webPlugin.setName(pluginName);
        webPlugin.setEntryPoint(pluginName + "/remoteEntry.js");
        return webPlugin;
    }
}

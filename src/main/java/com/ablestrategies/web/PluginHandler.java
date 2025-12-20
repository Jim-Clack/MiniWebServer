package com.ablestrategies.web;

import com.ablestrategies.web.rqst.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

/**
 * PluginHandler
 * To create a plugin...
 * Extend HttpResponsePlugin
 * Extend PluginBase
 * getProperty("MiniWebServer.plugins", "comma,delimited,plugin,classes");
 */
public class PluginHandler {

    /** Logger slf4j. */
    private final Logger logger = LoggerFactory.getLogger(PluginHandler.class);

    private static PluginHandler instance = null;

    private List<PluginBase> plugins = new LinkedList<>();

    /**
     * Ctor.
     */
    private PluginHandler() {
        loadPlugins();
    }

    public void loadPlugins() {
        Preferences preferences = Preferences.getInstance();
        String[] classNames = preferences.getPluginClassNames();
        this.plugins = new LinkedList<PluginBase>();
        for(String className : classNames) {
            try {
                Object object = ClassLoader.getSystemClassLoader().loadClass(className);
                @SuppressWarnings("all")
                Class<? extends PluginBase> clazz = (Class<? extends PluginBase>)object;
                if(clazz == null) {
                    logger.warn("Cannot find plugin " + className);
                } else {
                    Class<?>[] args = {Preferences.class};
                    Constructor<?> ctor = clazz.getConstructor(args);
                    ctor.setAccessible(true);
                    PluginBase plugin = (PluginBase)ctor.newInstance(preferences);
                    plugins.add(plugin);
                    logger.info(plugin.getClass().getCanonicalName() + " plugin loaded");
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException ex) {
                logger.warn("Cannot instantiate plugin " + className, ex);
            }
        }
    }

    /**
     * Singleton.
     * @return the instance.
     */
    public static PluginHandler getInstance() {
        if(instance == null) {
            instance = new PluginHandler();
        }
        return instance;
    }

    /**
     * Get a plugin based on the request type.
     * @param request an HttpRequest to be analyzed.
     * @return the corresponding plugin, null if none.
     */
    public PluginBase getPlugin(HttpRequest request) {
        for (PluginBase plugin : plugins) {
            if (plugin.getKind(request) != null) {
                return plugin;
            }
        }
        return null;
    }

}

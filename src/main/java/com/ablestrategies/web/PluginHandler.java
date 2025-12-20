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
 *  TODO: Test this class - it has not been tested!!!
 * getProperty("MiniWebServer.plugins", "comma,delimited,classes");
 */
public class PluginHandler {

    /** Logger slf4j. */
    private final Logger logger = LoggerFactory.getLogger(PluginHandler.class);

    private static PluginHandler instance = null;

    private final List<PluginBase> plugins = new LinkedList<>();

    /**
     * Ctor.
     */
    private PluginHandler() {
        Preferences preferences = Preferences.getInstance();
        String[] classNames = preferences.getPluginClassNames();
        for(String className : classNames) {
            try {
                Object object = ClassLoader.getPlatformClassLoader().loadClass(className);
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
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                logger.warn("Cannot instantiate plugin " + className);
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

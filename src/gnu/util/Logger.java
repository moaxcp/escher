package gnu.util;

import gnu.x11.Display;

import java.util.logging.Level;

/**
 * Simple class to handle debugging messages.
 * 
 * @author Mario Torre <neugens@aicas.com>
 */
public class Logger {

    private static java.util.logging.Logger logger;

    static {
        // FIXME, allow runtime configuration
        logger = java.util.logging.Logger.getLogger("gnu.javax.media.opengl");
        logger.setLevel(Level.ALL);
    }

    public static void debug(String message) {

        logger.log(Level.INFO, message);
    }

    public static void debug(String className, String message) {

        logger.log(Level.INFO, className + " -: " + message);
    }

    public static void debug(Class clazz, String message) {

        logger.log(Level.INFO, clazz.getName() + " -: " + message);
    }

    public static void debug(Object instance, String message) {

        logger
                .log(Level.INFO, instance.getClass().getName() + " -: "
                        + message);
    }

    public static void severe(Object instance, String message) {

        logger.log(Level.SEVERE, instance.getClass().getName() + " -: "
                + message);
    }

    public static void warning(Object instance, String message) {

        logger.log(Level.WARNING, instance.getClass().getName() + " -: "
                + message);
    }

    public static void warning(Object instance, StringBuilder message) {

        Logger.warning(instance, message.toString());
    }
    
    public static void debug(Object instance, StringBuilder message) {

        Logger.debug(instance, message.toString());
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp;

import java.io.IOException;
import java.util.logging.*;

/**
 *
 * @author lachlan
 */
public class Log {

    private static final Logger LOG = Logger.getLogger("mountainrangepvp");

    public static void setupLog() {
        LOG.setLevel(Level.FINE);
        LOG.setUseParentHandlers(false);

        LOG.addHandler(makeConsoleHandler());
        LOG.addHandler(makeFileHandler());
    }

    public static void info(Object... data) {
        LOG.info(concat(data));
    }

    public static void fine(Object... data) {
        LOG.fine(concat(data));
    }

    public static void warn(Object... data) {
        LOG.warning(concat(data));
    }

    private static String concat(Object... data) {
        StringBuilder builder = new StringBuilder();
        for (Object d : data) {
            if (d instanceof String) {
                builder.append((String) d);
            } else if (d instanceof Exception) {
                builder.append(d.getClass());
                builder.append(((Exception) d).getMessage());
            } else {
                builder.append(d);
            }

            builder.append(' ');
        }

        return builder.toString();
    }

    private static ConsoleHandler makeConsoleHandler() {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.INFO);
        return handler;
    }

    private static FileHandler makeFileHandler() {
        try {
            FileHandler handler = new FileHandler(
                    "./mountainrangepvp-log-%u.txt");
            handler.setLevel(Level.FINE);
            handler.setFormatter(new SimpleFormatter());
            return handler;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }
}

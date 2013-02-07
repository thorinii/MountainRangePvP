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
        StringBuilder builder = new StringBuilder();
        for (Object d : data) {
            if (d instanceof String) {
                builder.append((String) d);
            } else if (d instanceof Exception) {
                builder.append(((Exception) d).getMessage());
            } else {
                builder.append(d);
            }

            builder.append(' ');
        }

        LOG.info(builder.toString());
    }

    public static void fine(Object... data) {
        StringBuilder builder = new StringBuilder();
        for (Object d : data) {
            if (d instanceof String) {
                builder.append((String) d);
            } else if (d instanceof Exception) {
                builder.append(((Exception) d).getMessage());
            } else {
                builder.append(d);
            }

            builder.append(' ');
        }

        LOG.fine(builder.toString());
    }

    public static void warn(Object... data) {
        StringBuilder builder = new StringBuilder();
        for (Object d : data) {
            if (d instanceof String) {
                builder.append((String) d);
            } else if (d instanceof Exception) {
                builder.append(((Exception) d).getMessage());
            } else {
                builder.append(d);
            }

            builder.append(' ');
        }

        LOG.warning(builder.toString());
    }

    private static ConsoleHandler makeConsoleHandler() {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.WARNING);
        return handler;
    }

    private static FileHandler makeFileHandler() {
        try {
            FileHandler handler = new FileHandler(
                    "./mountainrangepvp-log-%u.txt");
            handler.setLevel(Level.INFO);
            handler.setFormatter(new SimpleFormatter());
            return handler;
        } catch (IOException ioe) {
            return null;
        }
    }
}

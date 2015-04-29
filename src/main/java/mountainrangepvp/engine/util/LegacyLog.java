package mountainrangepvp.engine.util;

import com.badlogic.gdx.Gdx;

import javax.swing.*;
import java.util.logging.Level;

/**
 * The old static logging system. Did not track types of consumer.
 *
 * @deprecated switch to {@link mountainrangepvp.engine.util.Log}
 */
@Deprecated
public class LegacyLog {

    private static final Log LOG = new Log("legacy");

    public static void info(String message) {
        LOG.info(message);
    }

    public static void fine(String message) {
        LOG.fine(message);
    }

    public static void warn(String message) {
        LOG.warn(message);
    }

    public static void warn(String message, Exception e) {
        LOG.warn(message, e);
    }


    public static void crash(final String message) {
        LOG.crash(message);

        Gdx.app.exit();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null, message, "Mountain Range PvP", JOptionPane.ERROR_MESSAGE);
            }
        });

        throw new ThreadDeath();
    }

    public static void crash(final String message, final Throwable e) {
        if (e instanceof ThreadDeath)
            throw (ThreadDeath) e;

        LOG.crash(message, e);

        Gdx.app.exit();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null, message + "\n" + e, "Mountain Range PvP", JOptionPane.ERROR_MESSAGE);
            }
        });

        throw new ThreadDeath();
    }

    public static void todo() {
        String caller = getCaller();
        warn("TODO: " + caller);
    }

    public static void todoCrash() {
        String caller = getCaller();
        crash("TODO: " + caller);
    }

    public static void todo(String msg) {
        String caller = getCaller();
        warn("TODO: " + caller + "(" + msg + ")");
    }

    private static String getCaller() {
        StackTraceElement e = Thread.currentThread().getStackTrace()[3];
        return e.toString();
    }

}

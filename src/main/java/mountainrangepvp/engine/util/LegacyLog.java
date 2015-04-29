package mountainrangepvp.engine.util;

import com.badlogic.gdx.Gdx;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.*;

/**
 * @author lachlan
 */
public class LegacyLog {

    private static final Logger LOG = Logger.getLogger("mountainrangepvp.legacy");

    static {
        setupLog(Level.FINE);
    }

    public static void setupLog(Level level) {
        LOG.setLevel(level);
        LOG.setUseParentHandlers(false);
        for (Handler h : LOG.getHandlers())
            LOG.removeHandler(h);

        LOG.addHandler(makeConsoleHandler());
        //LOG.addHandler(makeFileHandler());

        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread t, Throwable e) {
                        if (!(e instanceof ThreadDeath)) {
                            LOG.log(Level.SEVERE, "Uncaught exception on thread " + t.getName(), e);
                        }

                        System.exit(1);
                    }
                });
    }

    private static ConsoleHandler makeConsoleHandler() {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINE);
        handler.setFormatter(new LogFormatter());
        return handler;
    }

    public static void info(String message) {
        LOG.info(message);
    }

    public static void fine(String message) {
        LOG.fine(message);
    }

    public static void warn(String message) {
        LOG.warning(message);
    }

    public static void warn(String message, Exception e) {
        LOG.log(Level.WARNING, message, e);
    }


    public static void crash(final String message) {
        LOG.log(Level.SEVERE, message);

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

        LOG.log(Level.SEVERE, message, e);

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

    private static final class LogFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder();

            sb.append(record.getLevel().getName())
                    .append(": ")
                    .append(formatMessage(record))
                    .append('\n');

            if (record.getThrown() != null) {
                try {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    record.getThrown().printStackTrace(pw);
                    pw.close();
                    sb.append(sw.toString());
                } catch (Exception ex) {
                    // ignore
                }
            }

            return sb.toString();
        }
    }
}

package mountainrangepvp.engine.util;

import com.badlogic.gdx.Gdx;

import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.logging.*;

/**
 * @author lachlan
 */
public class Log {

    private static final Logger LOG = Logger.getLogger("mountainrangepvp");

    static {
        setupLog();
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

    public static void setupLog(boolean debug) {
        setupLog(debug ? Level.FINE : Level.INFO);
    }

    public static void setupLog() {
        setupLog(true);
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
            } else if (d instanceof Throwable) {
                builder.append(d.getClass().getName());
                builder.append(": ");
                builder.append(((Throwable) d).getMessage());
                builder.append("\n");

                StackTraceElement[] traces = ((Throwable) d).getStackTrace();

                for (StackTraceElement ste : traces) {
                    builder.append("\tat ");
                    builder.append(ste.getClassName());
                    builder.append(".");
                    builder.append(ste.getMethodName());
                    builder.append("(");
                    builder.append(ste.getFileName());
                    builder.append(":");
                    builder.append(ste.getLineNumber());
                    builder.append(")");
                    builder.append("\n");
                }
            } else {
                builder.append(d);
            }

            builder.append(' ');
        }

        return builder.toString();
    }

    private static ConsoleHandler makeConsoleHandler() {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINE);
        handler.setFormatter(new LogFormatter());
        return handler;
    }

    private static FileHandler makeFileHandler() {
        try {
            String ip = getIP();

            FileHandler handler = new FileHandler(
                    "./log-" + ip + "-%u.txt");
            handler.setLevel(Level.FINE);
            handler.setFormatter(new SimpleFormatter());
            return handler;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    private static String getIP() {
        try {
            Enumeration<NetworkInterface> nics = NetworkInterface.
                    getNetworkInterfaces();
            for (NetworkInterface nic : Collections.list(nics)) {
                Enumeration<InetAddress> addresses = nic.getInetAddresses();
                for (InetAddress address : Collections.list(addresses)) {
                    if (address instanceof Inet4Address) {
                        if (!address.isLoopbackAddress()) {
                            return address.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return "localhost";
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
        LOG.log(Level.WARNING, "TODO: " + caller);
    }

    public static void todoCrash() {
        String caller = getCaller();
        crash("TODO: " + caller);
    }

    public static void todo(String msg) {
        String caller = getCaller();
        LOG.log(Level.WARNING, "TODO: " + caller + "(" + msg + ")");
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

package mountainrangepvp.engine.util;

import com.badlogic.gdx.Gdx;

import javax.swing.*;
import java.io.IOException;
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

        LOG.addHandler(makeConsoleHandler());
        //LOG.addHandler(makeFileHandler());

        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread t, Throwable e) {
                        LOG.log(Level.SEVERE, "Uncaught Exception", e);
                        System.exit(1);
                    }
                });
    }

    public static void setupLog(boolean debug) {
        setupLog(debug ? Level.FINE : Level.INFO);
    }

    public static void setupLog() {
        setupLog(false);
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
                builder.append(d.getClass().getName());
                builder.append(": ");
                builder.append(((Exception) d).getMessage());
                builder.append("\n");

                StackTraceElement[] traces = ((Exception) d).getStackTrace();

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
        Log.warn("Error starting server connection");

        Gdx.app.exit();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null, message, "Mountain Range PvP", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void crash(final String message, final Exception e) {
        Log.warn("Error starting server connection", e);

        Gdx.app.exit();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null, message + "\n" + e, "Mountain Range PvP", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void todo() {
        String caller = getCaller();
    }

    private static String getCaller() {
        StackTraceElement e = Thread.currentThread().getStackTrace()[0];
        info(Thread.currentThread().getStackTrace()[0].toString());
        info(Thread.currentThread().getStackTrace()[1].toString());
        info(Thread.currentThread().getStackTrace()[2].toString());
        return e.toString();
    }
}

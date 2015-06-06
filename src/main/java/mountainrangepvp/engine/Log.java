package mountainrangepvp.engine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.*;

/**
 * A log with a name.
 */
public class Log {
    private final Logger log;
    private String name;

    public Log(String name) {
        this.name = name;

        log = Logger.getLogger("mountainrangepvp." + name + "-" + Math.random());
        log.setLevel(Level.FINE);
        log.setUseParentHandlers(false);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.FINE);
        handler.setFormatter(new LogFormatter());
        log.addHandler(handler);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void info(String message) {
        log.info(message);
    }

    public void fine(String message) {
        log.fine(message);
    }

    public void warn(String message) {
        log.warning(message);
    }

    public void warn(String message, Throwable e) {
        log.log(Level.WARNING, message, e);
    }

    public void crash(String message) {
        log.log(Level.SEVERE, message);
    }

    public void crash(String message, Throwable e) {
        log.log(Level.SEVERE, message, e);
    }

    public void todo() {
        String caller = getCaller();
        warn("TODO:" + caller);
    }

    public void todo(String msg) {
        String caller = getCaller();
        warn("TODO:" + caller + ": " + msg);
    }

    private static String getCaller() {
        StackTraceElement e = Thread.currentThread().getStackTrace()[3];
        return e.toString();
    }

    private final class LogFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder();

            sb.append(record.getLevel().getName())
                    .append(" ")
                    .append(name)
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

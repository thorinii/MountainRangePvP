package mountainrangepvp.util;

/**
 *
 * @author lachlan
 */
public class Timer {

    private long time;
    private long lastUpdate;

    public Timer() {
        time = 0;
        lastUpdate = 0;
    }

    public void update() {
        long now = System.currentTimeMillis();

        if (lastUpdate != 0)
            time += now - lastUpdate;

        lastUpdate = now;
    }

    public void reset() {
        time = 0;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Timer[" + hashCode() + "; time=" + time + "ms]";
    }
}

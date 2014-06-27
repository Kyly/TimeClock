package idletime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

/**
 * Created by Kyly on 6/26/2014.
 */
public class DriverWin32IdleTime {

    public static final DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

    public static void main(String[] args) {

        Win32IdleTime.checkOS();
        State state = State.UNKNOWN;
        int idleStart = 0;

        State newState;

        for (; ; ) {

            newState = Win32IdleTime.getState();

            if (newState != state) {
                state = newState;
                System.out.println(dateFormat.format(new Date()) + " # " + state);

                if (newState != State.ONLINE)
                    idleStart = Win32IdleTime.getIdleTimeSecounds();
                else
                    idleStart = 0;
            }

            System.err.println("Seconds idle: " + ((idleStart > 0) ? Win32IdleTime.getIdleTimeSecounds() - idleStart
                    : 0) );

            System.err.println("Time since last movement: " + Win32IdleTime.getIdleTimeSecounds());

            if (newState == State.AWAY)
                System.exit(0);

            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

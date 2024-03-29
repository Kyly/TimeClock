package idletime;

/**
 * Created by kyly on 6/25/14.
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.sun.jna.*;
import com.sun.jna.win32.*;

;

/**
 * Utility method to retrieve the idle time on Windows and sample code to test it.
 * JNA shall be present in your classpath for this to work (and compile).
 *
 * @author ochafik
 */
@SuppressWarnings("all")
public class Win32IdleTime {

    public interface Kernel32 extends StdCallLibrary {
        Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);

        /**
         * Retrieves the number of milliseconds that have elapsed since the system was started.
         *
         * @return number of milliseconds that have elapsed since the system was started.
         * @see http://msdn2.microsoft.com/en-us/library/ms724408.aspx
         */
        public int GetTickCount();
    }

    ;

    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);

        /**
         * Contains the time of the last input.
         *
         * @see http://msdn.microsoft.com/library/default.asp?url=/library/en-us/winui/winui/windowsuserinterface/userinput/keyboardinput/keyboardinputreference/keyboardinputstructures/lastinputinfo.asp
         */
        public static class LASTINPUTINFO extends Structure {
            public int cbSize = 8;

            /// Tick count of when the last input event was received.
            public int dwTime;

            @Override
            protected List getFieldOrder() {
                List l = new ArrayList();
                l.add("cbSize");
                l.add("dwTime");

                return l;
            }
        }

        /**
         * Retrieves the time of the last input event.
         *
         * @return time of the last input event, in milliseconds
         * @see http://msdn.microsoft.com/library/default.asp?url=/library/en-us/winui/winui/windowsuserinterface/userinput/keyboardinput/keyboardinputreference/keyboardinputfunctions/getlastinputinfo.asp
         */
        public boolean GetLastInputInfo(LASTINPUTINFO result);
    }

    ;

    /**
     * Get the amount of milliseconds that have elapsed since the last input event
     * (mouse or keyboard)
     *
     * @return idle time in milliseconds
     */
    public static int getIdleTimeMillisWin32() {
        User32.LASTINPUTINFO lastInputInfo = new User32.LASTINPUTINFO();
        User32.INSTANCE.GetLastInputInfo(lastInputInfo);
        return Kernel32.INSTANCE.GetTickCount() - lastInputInfo.dwTime;
    }

    public static void checkOS() {
        // Check if for correct os
        if (!System.getProperty("os.name").contains("Windows")) {
            System.err.println("ERROR: Only implemented on Windows");
            System.exit(1);
        }
    }

    public static int getIdleTimeSecounds() {
            return getIdleTimeMillisWin32() / 1000;
    }

    public static State getState() {

        int idleSec = getIdleTimeMillisWin32() / 1000;

        State state =
                idleSec < 30 ? State.ONLINE :
                        idleSec > 5 * 60 ? State.AWAY : State.IDLE;

        return state;
    }
}
//    public static void main(String[] args) {
//        if (!System.getProperty("os.name").contains("Windows")) {
//            System.err.println("ERROR: Only implemented on Windows");
//            System.exit(1);
//        }
//        State state = State.UNKNOWN;
//        DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
//
//        for (;;) {
//            int idleSec = getIdleTimeMillisWin32() / 1000;
//
//            State newState =
//                    idleSec < 30 ? State.ONLINE :
//                            idleSec > 5 * 60 ? State.AWAY : State.IDLE;
//
//            if (newState != state) {
//                state = newState;
//                System.out.println(dateFormat.format(new Date()) + " # " + state);
//            }
//            try { Thread.sleep(1000); } catch (Exception ex) {}
//        }
//    }
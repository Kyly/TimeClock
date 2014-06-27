import idletime.Win32IdleTime;

import java.net.*;
import java.io.*;
import java.awt.Desktop;
import java.util.concurrent.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.text.DateFormat;
import java.util.Date;
import javax.swing.JOptionPane;


/**
 * Created by kyly on 6/23/14.
 */
public class TimeClock {

    // Control for debug
    private static boolean debug = false;

    public static void main(String[] args) throws InterruptedException {

        if (args.length > 0)
            debug = args[0].equalsIgnoreCase("--debug");

        System.out.println(debug);
        // Create a scheduled thread pool with 5 core threads
        ScheduledThreadPoolExecutor sch = (ScheduledThreadPoolExecutor)
                Executors.newScheduledThreadPool(1);

        startScheduler(sch);
    }

    private static void startScheduler(final ScheduledExecutorService service) {
        final ScheduledFuture<?> future = service.scheduleWithFixedDelay(new RunnableHTTPRead(debug), 0, 5,
                TimeUnit.SECONDS);

        Runnable watchdog = new Runnable() {

            @Override
            public void run() {

                // Check for exception
                while (true) {
                    try {
                        future.get();
                    } catch (ExecutionException e) {

                        // Sleep to slow looping while waiting for reconnection
                        try {
                            TimeUnit.SECONDS.sleep(60);
                        } catch (InterruptedException se) {

                            if (debug)
                                System.err.println("Watchdog: Sleep failed\n" + se);

                            startScheduler(service);
                        }

                        startScheduler(service);
                        return;

                    } catch (InterruptedException e) {

                        if (debug)
                            System.err.println("Watchdog: Problem occurred during scheduling.\n");

                        return;
                    }
                }
            }
        };
        new Thread(watchdog).start();
    }
}

class HTTPRead {

    private boolean debug = true;
    private final DateFormat fmt = DateFormat.getTimeInstance(DateFormat.LONG);
    public static final String CONFPATH = "C:/login/timeclock.conf.txt";
    //private static final String CONFPATH = "/home/kyly/google_drive/Mike Big Brother App/timeclock.conf";
    private static final String TIMECLOCKID = "timeClockIdleUrl";
    private static final String TIMECLOCKDIALOG = "timeClockDialogUrl";
    private String timeClockIdleUrl;
    private String timeClockDialogUrl;
    private String idle; // Result from web page

    /**
     * Assigns null value to result string and attempts to read config
     * file.
     */
    public HTTPRead(boolean db) {

        debug = db;
        idle = null;
        BufferedReader br = null;

        try {

            // Current line read from file
            String sCurrentLine;

            // File stream
            br = new BufferedReader(new FileReader(CONFPATH));

            // Read parameters from config file
            while ((sCurrentLine = br.readLine()) != null) {

                // Check if lines has expected value
                if (sCurrentLine.startsWith(TIMECLOCKID)) {
                    timeClockIdleUrl = sCurrentLine.substring(sCurrentLine.indexOf('=') + 1);
                } else if (sCurrentLine.startsWith(TIMECLOCKDIALOG)) {
                    timeClockDialogUrl = sCurrentLine.substring(sCurrentLine.indexOf('=') + 1);
                } else throw new IllegalStateException("Hi its me, TimeClock, I think we missed some\n"
                        + "important details in your configuration file.\nOnly one thing to do, call Mike!");

                // Check if needed information was found in config file
                if (timeClockDialogUrl != null && timeClockIdleUrl != null)
                    break;


            }

            if (timeClockDialogUrl == null || timeClockIdleUrl == null)
                throw new IllegalStateException("This is your TimeClock. Seems to be a problem with\n"
                        + CONFPATH + " formatting. Better call Mike...");

        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(null, e, "Crap!",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Your TimeClock here, we to be having trouble reading\n"
                            + " your configuration file. No worries, call Mike!", "Crap!",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            try {

                if (br != null)
                    br.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if (debug)
            System.err.println("timeClockIdlePath:" + timeClockIdleUrl + "<\n"
                    + "timeClockDialogPath:" + timeClockDialogUrl + "<");
    }

    public void checkStatus() {

        // Get status of idle from server
        readIdle();

        if (idle.equals("ask")) {
            try {

                // TODO This needs to be loaded from config file
                URI uri = new URI(timeClockDialogUrl);
                Desktop desktop = null;

                // Get desktop so default browser can be found
                if (Desktop.isDesktopSupported()) {
                    desktop = Desktop.getDesktop();
                }

                // TODO If no desktop is found direction to the user may need to be printed
                if (desktop != null)
                    desktop.browse(uri);

                // Add a extra minute
                TimeUnit.MINUTES.sleep(1);

            }

            // My need to do something a little more substantive with theses errors
            catch (IOException ioe) {
                ioe.printStackTrace();
            }

            // TODO May need instructions on how to fix their config file or who to call to fix it
            catch (URISyntaxException use) {
                use.printStackTrace();
            }

            // Display message err stream but return to caller
            catch (InterruptedException e) {

                if (debug)
                    System.err.println("checkStatus: Unable to make thread sleep.\n\t" + e);

            }
        }
    }

    /**
     * Connect with url with the amount of time idle and read response from server.
     */
    private void readIdle() {

        try {

            // Create url from string read from file
            URL url = new URL(timeClockIdleUrl + Win32IdleTime.getIdleTimeSecounds());
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection connection = null;

            // Check Http url was created
            if (urlConnection instanceof HttpURLConnection) {

                if (debug)
                    System.err.println("Url found: " + url.toString());

                connection = (HttpURLConnection) urlConnection;
            } else {

                if (debug)
                    System.err.println("Bad url in config file: " + url.toString());
            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            // If nothing is read from url idle is still assigned to null but err is still outputted
            if ((idle = in.readLine()) == null) {
                System.err.println("readIdle: Nothing was read from " + url.toString());
                idle = null;
            }

            if (debug)
                System.err.println(idle + " was read from " + url.toString()
                        + "\n at " + fmt.format(new Date()));

        } catch (IOException e) {

            if (debug)
                System.err.println("readIdle: Error occurred while reading from URL.\n" + e);

            idle = null;
        } catch (NullPointerException e) {

            if (debug)
                System.err.println("readIdle: Error occurred while reading from URL.\n" + e);

            idle = null;
        }
    }
}

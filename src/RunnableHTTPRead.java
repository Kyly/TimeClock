/**
 * Created by kyly on 6/24/14.
 */
public class RunnableHTTPRead implements Runnable {

    private HTTPRead reader;

    public RunnableHTTPRead(boolean db) {
        reader = new HTTPRead(db);
    }

    @Override
    public void run() {
        reader.checkStatus();
    }
}

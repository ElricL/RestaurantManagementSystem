import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;
import java.util.logging.LogRecord;

/** A Restaurant Logger for the Restaurant Manager
 *  Logs all inputs/events that have occurred in the Restaurant.
 */
public class RestaurantLog {

    protected static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(Logger.class.getName());

    /**
     * Sets up logger, log.txt file for the logger and the format of logs that would be written in log.txt
     *
     * @throws IOException check if File is correct
     */
    RestaurantLog() throws IOException {

        // Associate the handler with the logger.
        logger.setLevel(Level.ALL);
        FileHandler log = new FileHandler("log.txt", true);
        logger.addHandler(log);
        log.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                String line = "";
                line += record.getLevel() + " - ";
                SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm");
                Date d = new Date(record.getMillis());
                line += df.format(d) + ": ";
                line += this.formatMessage(record);
                return line;
            }
        });

    }

    /**
     * logs the events that have occurred.
     *
     * @param evenType Type of Event (Fine, Info, etc.)
     * @param event The description of the event processed
     */
    public static void entry(String evenType, String event) {
        switch (evenType) {
            case "Fine":
                logger.log(Level.FINE, event);
                break;
            case "Info":
                logger.log(Level.INFO, event);
                break;
            default:
                logger.log(Level.WARNING, event);
                break;
        }
    }
}

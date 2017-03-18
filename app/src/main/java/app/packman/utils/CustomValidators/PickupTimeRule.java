package app.packman.utils.CustomValidators;

import com.mobsandgeeks.saripaar.AnnotationRule;

import java.lang.annotation.Annotation;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import app.packman.utils.CustomValidators.impl.PickupTime;

/**
 * Created by mlshah on 4/6/16.
 * This is a custom validation class which
 * checks whether the scheduled pick up time is at least after an hour from requested
 * and before 8pm
 */
public class PickupTimeRule extends AnnotationRule<PickupTime, String> {
    /**
     * Constructor. It is mandatory that all subclasses MUST have a constructor with the same
     * signature.
     *
     * @param pickupTime The rule {@link Annotation} instance to which
     *                   this rule is paired.
     */
    protected PickupTimeRule(final PickupTime pickupTime) {
        super(pickupTime);
    }

    @Override
    public boolean isValid(final String dateString) {
        DateFormat dateFormat = new SimpleDateFormat(mRuleAnnotation.dateFormat());
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(dateString);
        } catch (ParseException ignored) {
            return false;
        }

        Date now = new Date();
        Long differenceInMillis = parsedDate.getTime() - now.getTime();

        // if past date is chosen
        if(differenceInMillis < 0) return false;

        //checking if scheduled time is at least an hr from now
        long differenceInHours = (differenceInMillis) / 1000L / 60L / 60L; // Divide by millis/sec, secs/min, mins/hr
        if(differenceInHours < 1)
            return false;

        if(!isTimeInRange(parsedDate))
            return false;

        return true;
    }

    private boolean isTimeInRange(Date pickUpDate) {
        boolean isInRange = false;
        try {
            String string1 = "08:00:00";
            Date time1 = new SimpleDateFormat("HH:mm:ss").parse(string1);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);

            String string2 = "20:00:00";
            Date time2 = new SimpleDateFormat("HH:mm:ss").parse(string2);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            calendar2.add(Calendar.DATE, 1);

            String pickUpTime = new SimpleDateFormat("HH:mm:ss").format(pickUpDate);
            Date timeToCheck = new SimpleDateFormat("HH:mm:ss").parse(pickUpTime);
            System.out.println("pickuptime : " + pickUpTime + " string: " + pickUpDate);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(timeToCheck);
            calendar3.add(Calendar.DATE, 1);

            Date x = calendar3.getTime();
            if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                //checkes whether the current time is between 08:00:00 and 20:00:00.
                isInRange = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isInRange;
    }
}

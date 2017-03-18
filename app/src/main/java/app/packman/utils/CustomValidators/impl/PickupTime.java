package app.packman.utils.CustomValidators.impl;

import com.mobsandgeeks.saripaar.DateFormats;
import com.mobsandgeeks.saripaar.annotation.ValidateUsing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import app.packman.utils.CustomValidators.PickupTimeRule;

/**
 * Created by mlshah on 4/6/16.
 * Using Saripaar for custom validation
 */
@ValidateUsing(PickupTimeRule.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PickupTime {
    String dateFormat()     default DateFormats.DMY;
    int dateFormatResId()   default -1;

    int sequence()          default -1;
    int messageResId()      default -1;
    String message()        default "Time Should be at least an hour from now and between 8am-8pm";
}


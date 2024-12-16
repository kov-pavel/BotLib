package ru.ro.botlib.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Аннотация для пометки чего угодно как запланированной реализации в будущем.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={CONSTRUCTOR, FIELD, LOCAL_VARIABLE, METHOD, PACKAGE, MODULE, PARAMETER, TYPE})
public @interface PlannedInFuture {

    /**
     * Описание будущих доработок.
     *
     * @return описание будущих доработок.
     */
    String description() default "";
}

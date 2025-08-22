package konradn24.tml.debug.commands;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
@interface Command {
	String description() default DebugCommands.UNDEFINED;
	String args() default "";
	String[] argsHint() default {};
}

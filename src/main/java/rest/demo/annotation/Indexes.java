package rest.demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import rest.demo.model.jpa.JpaEntity;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Indexes {
	Class<? extends JpaEntity> value();
	boolean isDefaultIndexClass() default false;
}

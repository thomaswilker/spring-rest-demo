package rest.demo.annotation;

public @interface PropertySelect {

	
	enum SelectType {
		OMIT, PICK, ALL
	}
	
	SelectType type() default SelectType.ALL;
	String[] properties() default {};
}

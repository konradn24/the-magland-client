package konradn24.tml.rules;

import java.lang.reflect.Field;

public abstract class RuleSet {
	public boolean checkExistance(String rule) throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = this.getClass().getDeclaredFields();
		
		for(Field field : fields)
			if(field.getName().equals(rule) && field.isAnnotationPresent(Rule.class))
				return true;
		
		return false;
	}
	
	public boolean isEnabled(String rule) throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = this.getClass().getDeclaredFields();
		
		for(Field field : fields)
			if(field.getName().equals(rule) && field.isAnnotationPresent(Rule.class))
				return (boolean) field.get(this);
		
		return false;
	}
	
	public boolean set(String rule, boolean enable) throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = this.getClass().getDeclaredFields();
		
		for(Field field : fields) {
			if(field.getName().equals(rule) && field.isAnnotationPresent(Rule.class)) {
				field.set(this, enable);
				return true;
			}
		}
		
		return false;
	}
}

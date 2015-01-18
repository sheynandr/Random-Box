package saharnooby.randombox.box.result;

import saharnooby.randombox.box.Box;

/**
 * Класс, объекты которого возвращаются конструктором коробки.
 * Позволяет записать сообщение о том, что пошло не так.
 * @author saharNooby
 * @since 10.11.2014
 */
public class BoxConstructorResult {
	public BoxConstructorResult success(String id, Box box) {
		successful = true;
		message = id;
		result = box;
		
		return this;
	}
	
	public BoxConstructorResult fail(String id) {
		successful = false;
		message = id;
		result = null;
		
		return this;
	}
	
	private Boolean successful;
	
	private String message;
	
	private Box result;

	public Boolean getSuccessful() {
		return successful;
	}

	public String getMessage() {
		return message;
	}

	public Box getResult() {
		return result;
	}
}

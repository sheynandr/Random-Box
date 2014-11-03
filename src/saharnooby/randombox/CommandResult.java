package saharnooby.randombox;

/**
 * Экземпляр этого класса возвращает обработчик команды. Нужен для записи ошибки
 * (или просто сообщения) и успешности выполнения команды. 
 * @author saharNooby
 * @since 27.10.14 20:43
 */
public class CommandResult {
	/**
	 * Успешно ли выполнена команда?
	 */
	private Boolean successful = false;
	
	/**
	 * Сообщение от обработчика.
	 * Может быть описанием ошибки или просто дополнительными данными.
	 */
	private String message = "";

	public Boolean isSuccessful() {
		return successful;
	}

	private void setSuccessful(Boolean successful) {
		this.successful = successful;
	}

	public String getMessage() {
		return message;
	}

	private void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Установить результат как неуспешный, и установить сообщение s.
	 * @param stringAsId s будет восприниматься как id строки в конфиге или
	 * как самостоятельная строка.
	 * @param s Строка или id строки (см. выше).
	 * @return Возвращает себя
	 * (для краткой записи в обработчике типа "return result.fail(true, "id")")
	 */
	public CommandResult fail(Boolean stringAsId, String s) {
		setSuccessful(false);
		if (stringAsId)
			setMessage(RandomBox.getString(s));
		else
			setMessage(s);
		return this;
	}
	
	/**
	 * Установить результат как успешный, и установить сообщение s.
	 * @param stringAsId s будет восприниматься как id строки в конфиге или
	 * как самостоятельная строка.
	 * @param s Строка или id строки (см. выше).
	 * @return Возвращает себя
	 * (для краткой записи в обработчике типа "return result.successful(true, "id")")
	 */
	public CommandResult successful(Boolean stringAsId, String s) {
		setSuccessful(true);
		if (stringAsId)
			setMessage(RandomBox.getString(s));
		else
			setMessage(s);	
		return this;
	}
}

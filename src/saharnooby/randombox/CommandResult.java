package saharnooby.randombox;

/**
 * ��������� ����� ������ ���������� ���������� �������. ����� ��� ������ ������
 * (��� ������ ���������) � ���������� ���������� �������. 
 * @author saharNooby
 * @since 27.10.14 20:43
 */
public class CommandResult {
	/**
	 * ������� �� ��������� �������?
	 */
	private Boolean successful = false;
	
	/**
	 * ��������� �� �����������.
	 * ����� ���� ��������� ������ ��� ������ ��������������� �������.
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
	 * ���������� ��������� ��� ����������, � ���������� ��������� s.
	 * @param stringAsId s ����� �������������� ��� id ������ � ������� ���
	 * ��� ��������������� ������.
	 * @param s ������ ��� id ������ (��. ����).
	 * @return ���������� ����
	 * (��� ������� ������ � ����������� ���� "return result.fail(true, "id")")
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
	 * ���������� ��������� ��� ��������, � ���������� ��������� s.
	 * @param stringAsId s ����� �������������� ��� id ������ � ������� ���
	 * ��� ��������������� ������.
	 * @param s ������ ��� id ������ (��. ����).
	 * @return ���������� ����
	 * (��� ������� ������ � ����������� ���� "return result.successful(true, "id")")
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

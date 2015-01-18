package saharnooby.randombox.box.result;

import saharnooby.randombox.box.RewardItem;

public class RewardItemConstructorResult {
	public RewardItemConstructorResult success(String id, RewardItem item) {
		successful = true;
		message = id;
		result = item;
		
		return this;
	}
	
	public RewardItemConstructorResult fail(String id) {
		successful = false;
		message = id;
		result = null;
		
		return this;
	}
	
	private Boolean successful;
	
	private String message;
	
	private RewardItem result;

	public Boolean getSuccessful() {
		return successful;
	}

	public String getMessage() {
		return message;
	}

	public RewardItem getResult() {
		return result;
	}
}

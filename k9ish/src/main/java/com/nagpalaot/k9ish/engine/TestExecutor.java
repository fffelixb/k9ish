package com.nagpalaot.k9ish.engine;

import org.openqa.selenium.WebDriver;

import com.nagpalaot.k9ish.dashboard.model.TestScript;
import com.nagpalaot.k9ish.dashboard.model.TestStep;

public abstract class TestExecutor {

	private boolean successful;
	private String baseUrl = "https://www.bls.gov";
	
	protected WebDriver driver;
	
	public abstract void initialize() throws Exception;
	
	public abstract void shutdown();
	
	protected abstract void handleOpenCommand(String url);
	
	protected abstract void handleAssertPresentCommand(String target, String expectedValue);
	
	protected abstract void handleAssertPresentCookieCommand(String target, String expectedValue);

	protected abstract void handleAssertPresentHeaderCommand(String target, String expectedValue);

	protected abstract void handleWaitThenAssertPresentCommand(String target, String expectedValue);

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}
	
	public final boolean execute(TestScript script) {
		System.out.println("Execute tests");
		for(TestStep step : script.getTestSteps()) {
			if(isSuccessful()) {
				String command = step.getCommand().trim();
				switch(command) {
				case "open":{
					handleOpenCommand(baseUrl + step.getTarget());
					break;
				}
				case "assertPresent":{
					handleAssertPresentCommand(step.getTarget(), step.getValue());
					break;
				}
				case "assertPresentCookie":{
					handleAssertPresentCookieCommand(step.getTarget(), step.getValue());
					break;
				}
				case "assertPresentHeader":{
					handleAssertPresentHeaderCommand(step.getTarget(), step.getValue());
					break;
				}
				case "waitThenAssertPresent":{
					handleWaitThenAssertPresentCommand(step.getTarget(), step.getValue());
					break;
				}
				}
			}
			
		}
		return isSuccessful();
	}
}

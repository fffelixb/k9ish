package com.nagpalaot.k9ish.dashboard.model;

import java.util.ArrayList;
import java.util.List;

public class TestScript {

	private String testScriptName;
	private List<TestStep> testSteps = new ArrayList<>();
	
	public TestScript() {
		testSteps = new ArrayList<TestStep>();
	}

	public String getTestScriptName() {
		return testScriptName;
	}

	public void setTestScriptName(String testScriptName) {
		this.testScriptName = testScriptName;
	}

	public List<TestStep> getTestSteps() {
		return testSteps;
	}

	public void setTestSteps(List<TestStep> testSteps) {
		this.testSteps = testSteps;
	}
	
}

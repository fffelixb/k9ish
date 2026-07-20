package com.nagpalaot.k9ish.engine;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.nagpalaot.k9ish.dashboard.model.TestScript;
import com.nagpalaot.k9ish.dashboard.model.TestStep;
import com.nagpalaot.k9ish.engine.selenium.SeleniumTestExecutor;

public class TestHeaderCheck {

	private TestExecutor executor;
	
	@Test
	public void checkHeader() throws Exception {
		executor = new SeleniumTestExecutor();
		executor.initialize();
		TestScript script = prepareTestScript();
		boolean result = executor.execute(script);
		executor.shutdown();
		assertTrue(result);
	}
	
	private TestScript prepareTestScript(){
		TestScript  result = new TestScript();
		List<TestStep> steps = new ArrayList<TestStep> ();
		TestStep step1 = new TestStep(1,"open","/","","");
		TestStep step2 = new TestStep(2,"assertPresentHeader","content-security-policy","frame-ancestors https://*.bls.gov","");
		steps.add(step1);
		steps.add(step2);
		result.setTestSteps(steps);
		return result;
	}
}

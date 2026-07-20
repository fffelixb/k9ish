package com.nagpalaot.k9ish.dashboard.model;

public class TestStep {

	private int stepIdx;
	private String command;
	private String target;
	private String value;
	private String note;
	
	public TestStep(int stepIdx, String command, String target, String value, String note) {
		super();
		this.stepIdx = stepIdx;
		this.command = command;
		this.target = target;
		this.value = value;
		this.note = note;
	}

	public int getStepIdx() {
		return stepIdx;
	}

	public void setStepIdx(int stepIdx) {
		this.stepIdx = stepIdx;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	
}

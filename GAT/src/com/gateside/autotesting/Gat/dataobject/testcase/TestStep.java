package com.gateside.autotesting.Gat.dataobject.testcase;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import com.gateside.autotesting.Gat.dataobject.TestObject;

@Root(name="Step")
public class TestStep extends TestObject
{
	@Attribute(name="StepAssembly",required=false)
	public String StepAssembly;
	
	@Attribute(name="StepGroup",required=false)
	public String StepGroup;
	
	@Attribute(name="StepName",required=true)
	public String StepName;
	
	@Attribute(name="StepParameterID",required=false)
	public String StepParameterID;
	
	@Attribute(name="StepParametersFilePath",required=false)
	public String StepParametersFilePath;
		
	
}

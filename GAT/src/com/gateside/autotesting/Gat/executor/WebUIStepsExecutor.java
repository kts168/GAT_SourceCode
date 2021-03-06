package com.gateside.autotesting.Gat.executor;

import java.lang.reflect.Method;
import java.util.Date;

import com.gateside.autotesting.Gat.executor.StepsExecutor;
import com.gateside.autotesting.Gat.dataobject.EnumObjectManager;
import com.gateside.autotesting.Gat.dataobject.InvokedMethodInfo;
import com.gateside.autotesting.Gat.dataobject.testcase.TestStep;
import com.gateside.autotesting.Gat.dataobject.testcase.WebUIStepsCase;
import com.gateside.autotesting.Gat.dataobject.testcase.WebUITestStep;
import com.gateside.autotesting.Gat.uia.webautomation.BrowserType;
import com.gateside.autotesting.Gat.uia.webautomation.WebBrowser;
import com.gateside.autotesting.Gat.uia.webautomation.WebPage;
import com.gateside.autotesting.Gat.util.FileHelper;
import com.gateside.autotesting.Gat.util.GlobalConfig;
import com.gateside.autotesting.Gat.util.ScreenCapture;
import com.gateside.autotesting.Lib.common.ClassReflector;
import com.gateside.autotesting.Lib.common.SimpleLogger;

public class WebUIStepsExecutor extends StepsExecutor
{

	private WebBrowser browser=null;
	private WebPage webPage=null;
	
	public WebUIStepsExecutor(String caseFilePath,String caseID)
	{
		super(caseID, caseFilePath);
	    browser=new WebBrowser(Enum.valueOf(BrowserType.class,GlobalConfig.getBrowserType()));
	    webPage=new WebPage(browser);
	}
	
    
	@Override
	public void executeCase() throws Exception 
	{
		String  screenPictureName="";
		try 
		{
			for(WebUITestStep step : ((WebUIStepsCase)targetCase).Steps)
			{
				screenPictureName=step.StepGroup+"_"+step.StepName+"_"+step.StepParameterID;
				SimpleLogger.logInfo(this.getClass(),"executeCase: set step parameter path as "+step.StepParametersFilePath);
				GlobalConfig.setStepsParameterFilePath(GlobalConfig.getAutoProjectName()+"DataFiles"+GlobalConfig.getSlash()+"Xmls"+GlobalConfig.getSlash()+step.StepParametersFilePath); //set glocal config for pre step parameters
				GlobalConfig.setuIElementsFilePath(GlobalConfig.getAutoProjectName()+"DataFiles"+GlobalConfig.getSlash()+"Xmls"+GlobalConfig.getSlash()+step.UIElementsFilePath); //set glocal config for pre step parameters
				InvokedMethodInfo resultInfo=this.getStepMethodInfo(step);
				SimpleLogger.logInfo(this.getClass(),"executeCase: execute step:"+resultInfo.classFullName+resultInfo.methodName+step.StepParameterID);
				invokeMethod(resultInfo);
				saveScreenPicture(screenPictureName,false);
			}
			
		} catch (Exception e) 
		{
			saveScreenPicture(screenPictureName,true);
		}		
	}
	
	@Override
    protected WebUIStepsCase getTestCase() throws Exception
	{
		return (WebUIStepsCase)getTestObject(this.getCaseID(),EnumObjectManager.WebUIStepCaseManager);
	}
	
	
	@Override
	protected InvokedMethodInfo getStepMethodInfo(TestStep step)
	    {
	    	InvokedMethodInfo resultInfo=new InvokedMethodInfo();
	    	resultInfo.classFullName=step.StepAssembly+step.StepGroup;
	    	resultInfo.methodName=step.StepName;
	    	resultInfo.parameters.add(browser);
	    	resultInfo.parameters.add(webPage);
	    	resultInfo.parameters.add(step.StepParameterID);
	    	resultInfo.jarFilePath=GlobalConfig.getStepMethodJarPath();
	    	return resultInfo;
	    }
	
	@Override
	protected  Object invokeMethod(InvokedMethodInfo methodInfo) throws Exception
	{
		Object targetClassInstanceObject= ClassReflector.createInstance(methodInfo.jarFilePath,methodInfo.classFullName); //create target instance
		Method targetMethod= ClassReflector.getMethod(targetClassInstanceObject,methodInfo.methodName,methodInfo.parameters.toArray());
		return targetMethod.invoke(targetClassInstanceObject,methodInfo.parameters.toArray());
	}
	
	@Override
	public void tearDown()
	{
		try 
		{
			SimpleLogger.logInfo(this.getClass(),"tear down now ");
			browser.quit();	
		} catch (Exception e)
		{
		    SimpleLogger.logError(this.getClass(),e);
		}
		finally
		{
			
		}
	}
    
    private void saveScreenPicture(String fileName,Boolean fail)
    {
    	Date nowDate=new Date();
    	fileName=fileName+"_"+nowDate.getTime();
    	if(!fail)
    	{
    	  FileHelper.createDir(GlobalConfig.screenPicuterPath()+"Pass");
   	      ScreenCapture.saveScreen(GlobalConfig.screenPicuterPath()+"Pass",fileName+".jpg");
    	}
    	else
    	{
    		FileHelper.createDir(GlobalConfig.screenPicuterPath()+"Fail");
      	    ScreenCapture.saveScreen(GlobalConfig.screenPicuterPath()+"Fail",fileName+".jpg");
		}
    }
}



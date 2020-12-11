package com.bfw.log.controller;

import java.lang.reflect.Method;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bfw.log.constant.CpuArch;
import com.bfw.log.constant.Event;
import com.bfw.log.constant.Language;
import com.bfw.log.constant.LoggerType;
import com.bfw.log.constant.OSType;
import com.bfw.log.constant.Product;
import com.bfw.log.constant.RunningEnvironment;
import com.bfw.log.constant.Schema;
import com.bfw.log.util.LogData;
import com.bfw.log.util.LogUtil;

@RestController
@RequestMapping("/client")
public class LogController {
	/**
	 * 是否启动测试用例
	 */
	private Boolean isStart;
	
	/**
	 * 随机数
	 */
	private Random random=new Random();
	
	/**
	 * 日志级别表
	 */
	private Method[] logMethods=new Method[7];
	
	@PostConstruct
	public void init(){
		try {
			logMethods[0]=LogUtil.class.getDeclaredMethod("trace", LogData.class);
			logMethods[1]=LogUtil.class.getDeclaredMethod("debug", LogData.class);
			logMethods[2]=LogUtil.class.getDeclaredMethod("info", LogData.class);
			logMethods[3]=LogUtil.class.getDeclaredMethod("warn", LogData.class);
			logMethods[4]=LogUtil.class.getDeclaredMethod("error", LogData.class);
			logMethods[5]=LogUtil.class.getDeclaredMethod("fatal", LogData.class);
			logMethods[6]=LogUtil.class.getDeclaredMethod("block", LogData.class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	@GetMapping("/start")
    public String start(Long interval)  throws Exception{
		isStart=true;
		System.out.println("start test unit...");
		LogUtil.setAppLevel(LogUtil.REFLECT_LEVEL);
		long sleepInterval=null==interval?1000L:interval;
		
		while(isStart) {
			Event event=Event.values()[random.nextInt(Event.values().length)];
			LoggerType loggerType=LoggerType.values()[random.nextInt(LoggerType.values().length)];
			LogData logData=LogData.get(event,loggerType);
			
			logData.ProductId=Product.values()[random.nextInt(Product.values().length)];
			logData.SchemaName=Schema.values()[random.nextInt(Schema.values().length)];
			logData.OSType=OSType.values()[random.nextInt(OSType.values().length)];
			logData.OSDistributionName=logData.OSType.name()+"-"+logData.OSCoreVersion;
			logData.CodeLanguage=Language.values()[random.nextInt(Language.values().length)];
			logData.HardwarePlatform=CpuArch.values()[random.nextInt(CpuArch.values().length)];
			logData.RunningEnvironment=RunningEnvironment.values()[random.nextInt(RunningEnvironment.values().length)];
			
			logMethods[random.nextInt(logMethods.length)].invoke(LogUtil.class, logData);
			
			Thread.sleep(sleepInterval);
		}
		
		return "test unit is already stopped";
	}
	
	@GetMapping("/stop")
    public String stop()  throws Exception{
		isStart=false;
        return "stop test unit...";
    }
}

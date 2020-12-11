package com.bfw.log.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import com.bfw.log.constant.Event;
import com.bfw.log.constant.LoggerType;
import com.bfw.log.constant.Product;
import com.bfw.log.constant.SizeUnit;
import com.bfw.log.thread.SpringThreadPool;

/**
 * @author Lixiang
 * @description 通用日志工具类
 */
@SuppressWarnings("unchecked")
public final class LogUtil {
	/**
	 * file模式下的日志文件
	 */
	private static File logFile;
	
	/**
	 * 数据库日志插入语句
	 */
	private static String insertSQL;
	
	/**
	 * file模式下日志文件最大尺寸(默认100M)
	 */
	private static Long maxFileSize;
	
	/**
	 * 应用通用层级
	 */
	private static Integer appLevel=3;
	
	/**
	 * 日志文件创建时间
	 */
	private static Calendar createTime;
	
	/**
	 * 自定义日志打印级别(默认为on,用于数据库日志或文件日志)
	 */
	private static Integer loggingLevel;
	
	/**
	 * 获取日志工具
	 */
	private static Method getLogger;
	
	/**
	 * Sleuth跟踪类型
	 */
	private static Class<?> tracerType;
	
	/**
	 * 获取Sleuth跟踪上下文
	 */
	private static Method contextMethod;
	
	/**
	 * 日志工具工厂
	 */
	private static Class<?> loggerFactory;
	
	/**
	 * 获取当前线程单元
	 */
	private static Method currentSpanMethod;
	
	/**
	 * 获取当前链路跟踪ID
	 */
	private static Method traceIdStringMethod;
	
	/**
	 * 获取链路线程单元ID
	 */
	private static Method spanIdStringMethod;
	
	/**
	 * 获取链路调用方线程单元ID
	 */
	private static Method parentIdStringMethod;
	
	/**
	 * 应用中的反射调用层级
	 */
	public static final Integer REFLECT_LEVEL=7;
	
	/**
	 * 日志文件流
	 */
	private static FileOutputStream logFileStream;
	
	/**
	 * 是否已经准备好线程池
	 */
	private static boolean isPreparedPool=false;
	
	/**
	 * 是否已经准备好Sleuth日志组件
	 */
	private static boolean isPreparedSleuth=false;
	
	/**
	 * 是否已经准备好日志文件File
	 */
	private static boolean isPreparedFileLog=false;
	
	/**
	 * 是否已经准备好数据库日志组件
	 */
	private static boolean isPreparedDBLog=false;
	
	/**
	 * 是否已经准备好通用日志组件SLF4J
	 */
	private static boolean isPreparedSlf4jLog=false;
	
	/**
	 * 与应用共享的Spring线程池
	 */
	private static Map<Method, Object> threadPool;
	
	/**
	 * 应用数据源字典表
	 */
	private static Map<String, DataSource> dataSourceMap;
	
	/**
	 * 日志消息格式
	 */
	private static final Pattern MSG_PATTERN = Pattern.compile("\\{\\}");
	
	/**
	 * 容量正则式
	 */
	private static final Pattern CAP_REGEX = Pattern.compile("([1-9]{1}\\d+)([a-zA-Z]{1,5})");
	
	/**
	 * 数据库日志级别映射表
	 */
	private static final HashMap<String,Integer> LEVEL_DICT=new HashMap<String,Integer>();
	
	/**
	 * 业务类名到日志工具对象映射字典
	 */
	private static final HashMap<String,Object> TYPE_TO_LOG=new HashMap<String,Object>();
	
	/**
	 * 日志文件命名计数器
	 */
	private static final HashMap<String,Integer> NAME_COUNTER=new HashMap<String,Integer>();
	
	/**
	 * 日志级别到输出日志方法映射字典
	 */
	private static final HashMap<String,Method> API_TO_METHOD=new HashMap<String,Method>();
	
	static{
		// 日志组件模式(file、slf4j、db),默认值为: slf4j
		String loggerMode=ApplicationUtil.getProperty("logging.mode", String.class,"slf4j");
		if("file".equalsIgnoreCase(loggerMode)){
			initLogLevelDict();
			initLogFileArgs();
			initThreadPool();
		}else if("slf4j".equalsIgnoreCase(loggerMode)){
			initBaseParams();
		}else if("db".equalsIgnoreCase(loggerMode)){
			initLogLevelDict();
			initThreadPool();
			InitDataSource();
			initSleuthParams();
		}
	}
	
	/**
	 * 设置应用层级
	 * @param appLevel
	 */
	public static void setAppLevel(Integer appLevel) {
		LogUtil.appLevel = appLevel;
	}

	/**
	 * 初始化日志文件参数
	 */
	private static final void initLogFileArgs(){
		String filePathStr=ApplicationUtil.getProperty("logging.filePath", String.class);
		if(null==filePathStr) throw new RuntimeException("'logging.filePath' is not defined...");
		String filePath=filePathStr.trim();
		if(filePath.isEmpty()) throw new RuntimeException("'logging.filePath' value can not be empty...");
		logFile=new File(filePath);
		maxFileSize=getMaxFileSize();
		loggingLevel=LEVEL_DICT.get(ApplicationUtil.getProperty("logging.eventLevel", String.class,"on"));
		try{
			File fileDir=logFile.getParentFile();
			if(!fileDir.exists()) fileDir.mkdirs();
			Integer maxHistory=ApplicationUtil.getProperty("logging.maxHistory", Integer.class);
			if(null!=maxHistory) {
				long expireInMills=maxHistory*86400*1000;
				long curTimeInMills=System.currentTimeMillis();
				for(File file:fileDir.listFiles()) if(curTimeInMills-Files.getFileAttributeView(Paths.get(file.toURI()), BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS).readAttributes().creationTime().toMillis()>=expireInMills) file.delete();
			}
			if(!logFile.exists()) logFile.createNewFile();
			logFileStream=new FileOutputStream(logFile,true);
			createTime=DateUtil.millSecondsToCalendar(logFile.lastModified());
			isPreparedFileLog=true;
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 初始化日志级别字典
	 */
	private static final void initLogLevelDict(){
		LEVEL_DICT.put("on",-1);
		LEVEL_DICT.put("trace",0);
		LEVEL_DICT.put("debug",1);
		LEVEL_DICT.put("info",2);
		LEVEL_DICT.put("warn",3);
		LEVEL_DICT.put("error",4);
		LEVEL_DICT.put("fatal",5);
		LEVEL_DICT.put("block",6);
		LEVEL_DICT.put("off",7);
	}
	
	/**
	 * 初始化基础参数
	 */
	private static final void initBaseParams(){
		try{
			Class<?> logger=Class.forName("org.slf4j.Logger");
			loggerFactory=Class.forName("org.slf4j.LoggerFactory");
			getLogger=loggerFactory.getDeclaredMethod("getLogger", String.class);
			API_TO_METHOD.put("getName", logger.getDeclaredMethod("getName"));
			API_TO_METHOD.put("trace", logger.getDeclaredMethod("trace", String.class,Object[].class));
			API_TO_METHOD.put("debug", logger.getDeclaredMethod("debug", String.class,Object[].class));
			API_TO_METHOD.put("info", logger.getDeclaredMethod("info", String.class,Object[].class));
			API_TO_METHOD.put("warn", logger.getDeclaredMethod("warn", String.class,Object[].class));
			API_TO_METHOD.put("error", logger.getDeclaredMethod("error", String.class,Object[].class));
			API_TO_METHOD.put("tracet", logger.getDeclaredMethod("trace", String.class,Throwable.class));
			API_TO_METHOD.put("debugt", logger.getDeclaredMethod("debug", String.class,Throwable.class));
			API_TO_METHOD.put("infot", logger.getDeclaredMethod("info", String.class,Throwable.class));
			API_TO_METHOD.put("warnt", logger.getDeclaredMethod("warn", String.class,Throwable.class));
			API_TO_METHOD.put("errort", logger.getDeclaredMethod("error", String.class,Throwable.class));
			isPreparedSlf4jLog=true;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 初始化数据库连接
	 */
	private static final void InitDataSource(){
		if(!isPreparedPool) return;
		dataSourceMap=ApplicationUtil.getBeansOfType(DataSource.class);
		loggingLevel=LEVEL_DICT.get(ApplicationUtil.getProperty("logging.dbLevel", String.class,"on"));
		createSchema();
		isPreparedDBLog=true;
	}
	
	/**
	 * 初始化线程池
	 */
	private static final void initThreadPool(){
		try{
			Class<? extends Executor> taskExecutorType=(Class<? extends Executor>)Class.forName("org.springframework.core.task.TaskExecutor");
			Method executeMethod=Executor.class.getDeclaredMethod("execute", Runnable.class);
			Object taskExecutor=SpringThreadPool.getSpringThreadPool(taskExecutorType);
			if(null==taskExecutor){
				Integer poolSize=ApplicationUtil.getProperty("logging.threadPoolSize", Integer.class,10);
				Integer queueSize=ApplicationUtil.getProperty("logging.threadQueueSize", Integer.class,50);
				taskExecutor=SpringThreadPool.getFixedTaskExecutor(poolSize, queueSize);
			}
			threadPool=Collections.singletonMap(executeMethod, taskExecutor);
			isPreparedPool=true;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 初始化Sleuth跟踪参数
	 */
	private static final void initSleuthParams(){
		if(!isPreparedDBLog) return;
		try {
			tracerType=Class.forName("brave.Tracer");
			currentSpanMethod=tracerType.getDeclaredMethod("currentSpan");
			Class<?> spanType=Class.forName("brave.Span");
			contextMethod=spanType.getDeclaredMethod("context");
			Class<?> traceContextType=Class.forName("brave.propagation.TraceContext");
			traceIdStringMethod=traceContextType.getDeclaredMethod("traceIdString");
			spanIdStringMethod=traceContextType.getDeclaredMethod("spanIdString");
			parentIdStringMethod=traceContextType.getDeclaredMethod("parentIdString");
			isPreparedSleuth=true;
		} catch (Exception e) {
			System.out.println("ERROR:==LogUtil: sleuth params is not inited,Cause: "+e.getMessage());
		}
	}
	
	/**
	 * 获取打印日志的类名(等价于Logger.getName())
	 * @return 类名
	 */
	public static final String getName() {
		try{
			return (String)API_TO_METHOD.get("getName").invoke(getLogger.invoke(loggerFactory, ApplicationUtil.getPositionContext(3).getClassName()));
		}catch(Exception e){
			System.out.println("invoke getName of logger is failure...");
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 打印trace级别日志
	 * @param message 日志消息
	 * @param arguments 日志消息参数
	 */
	public static final void trace(String message,Object... arguments) {
		printSlf4j(ApplicationUtil.getPositionContext(appLevel),"trace",message,arguments);
	}
	
	/**
	 * 打印trace级别日志
	 * @param message 日志消息
	 * @param arguments 日志消息参数
	 */
	public static final void traceDB(String message,Object... arguments) {
		printDB(ApplicationUtil.getPositionContext(appLevel),"trace",message,arguments);
	}
	
	/**
	 * 打印trace级别日志
	 * @param message 日志消息
	 * @param arguments 日志消息参数
	 */
	public static final void traceFile(String message,Object... arguments) {
		printFile(ApplicationUtil.getPositionContext(appLevel),"trace",message,arguments);
	}
	
	/**
	 * 打印trace级别日志并记录异常
	 * @param message 日志消息
	 * @param throwable 异常对象
	 */
	public static final void trace(String message,Throwable throwable) {
		printTSlf4j(ApplicationUtil.getPositionContext(appLevel),"trace",message,throwable);
	}
	
	/**
	 * 打印trace级别日志并记录异常
	 * @param message 日志消息
	 * @param throwable 异常对象
	 */
	public static final void traceDB(String message,Throwable throwable) {
		printTDB(ApplicationUtil.getPositionContext(appLevel),"trace",message,throwable);
	}
	
	/**
	 * 打印trace级别日志并记录异常
	 * @param message 日志消息
	 * @param throwable 异常对象
	 */
	public static final void traceFile(String message,Throwable throwable) {
		printTFile(ApplicationUtil.getPositionContext(appLevel),"trace",message,throwable);
	}
	
	/**
	 * 打印debug级别日志
	 * @param message 日志消息
	 * @param arguments 日志消息参数
	 */
	public static final void debug(String message,Object... arguments) {
		printSlf4j(ApplicationUtil.getPositionContext(appLevel),"debug",message,arguments);
	}
	
	/**
	 * 打印debug级别日志
	 * @param message 日志消息
	 * @param arguments 日志消息参数
	 */
	public static final void debugDB(String message,Object... arguments) {
		printDB(ApplicationUtil.getPositionContext(appLevel),"debug",message,arguments);
	}
	
	/**
	 * 打印debug级别日志
	 * @param message 日志消息
	 * @param arguments 日志消息参数
	 */
	public static final void debugFile(String message,Object... arguments) {
		printFile(ApplicationUtil.getPositionContext(appLevel),"debug",message,arguments);
	}
	
	/**
	 * 打印debug级别日志并记录异常
	 * @param message 日志消息
	 * @param throwable 异常对象
	 */
	public static final void debug(String message,Throwable throwable) {
		printTSlf4j(ApplicationUtil.getPositionContext(appLevel),"debug",message,throwable);
	}
	
	/**
	 * 打印debug级别日志并记录异常
	 * @param message 日志消息
	 * @param throwable 异常对象
	 */
	public static final void debugDB(String message,Throwable throwable) {
		printTDB(ApplicationUtil.getPositionContext(appLevel),"debug",message,throwable);
	}
	
	/**
	 * 打印debug级别日志并记录异常
	 * @param message 日志消息
	 * @param throwable 异常对象
	 */
	public static final void debugFile(String message,Throwable throwable) {
		printTFile(ApplicationUtil.getPositionContext(appLevel),"debug",message,throwable);
	}
	
	/**
	 * 打印info级别日志
	 * @param message 日志消息
	 * @param arguments 日志消息参数
	 */
	public static final void info(String message,Object... arguments) {
		printSlf4j(ApplicationUtil.getPositionContext(appLevel),"info",message,arguments);
	}
	
	/**
	 * 打印info级别日志
	 * @param message 日志消息
	 * @param arguments 日志消息参数
	 */
	public static final void infoDB(String message,Object... arguments) {
		printDB(ApplicationUtil.getPositionContext(appLevel),"info",message,arguments);
	}
	
	/**
	 * 打印info级别日志
	 * @param message 日志消息
	 * @param arguments 日志消息参数
	 */
	public static final void infoFile(String message,Object... arguments) {
		printFile(ApplicationUtil.getPositionContext(appLevel),"info",message,arguments);
	}
	
	/**
	 * 打印info级别日志并记录异常
	 * @param message 日志消息
	 * @param throwable 异常对象
	 */
	public static final void info(String message,Throwable throwable) {
		printTSlf4j(ApplicationUtil.getPositionContext(appLevel),"info",message,throwable);
	}
	
	/**
	 * 打印info级别日志并记录异常
	 * @param message 日志消息
	 * @param throwable 异常对象
	 */
	public static final void infoDB(String message,Throwable throwable) {
		printTDB(ApplicationUtil.getPositionContext(appLevel),"info",message,throwable);
	}
	
	/**
	 * 打印info级别日志并记录异常
	 * @param message 日志消息
	 * @param throwable 异常对象
	 */
	public static final void infoFile(String message,Throwable throwable) {
		printTFile(ApplicationUtil.getPositionContext(appLevel),"info",message,throwable);
	}
	
	/**
	 * 打印warn级别日志
	 * @param message 日志消息
	 * @param arguments 日志消息参数
	 */
	public static final void warn(String message,Object... arguments) {
		printSlf4j(ApplicationUtil.getPositionContext(appLevel),"warn",message,arguments);
	}
	
	/**
	 * 打印warn级别日志
	 * @param message 日志消息
	 * @param arguments 日志消息参数
	 */
	public static final void warnDB(String message,Object... arguments) {
		printDB(ApplicationUtil.getPositionContext(appLevel),"warn",message,arguments);
	}
	
	/**
	 * 打印warn级别日志
	 * @param message 日志消息
	 * @param arguments 日志消息参数
	 */
	public static final void warnFile(String message,Object... arguments) {
		printFile(ApplicationUtil.getPositionContext(appLevel),"warn",message,arguments);
	}
	
	/**
	 * 打印warn级别日志并记录异常
	 * @param message 日志消息
	 * @param throwable 异常对象
	 */
	public static final void warn(String message,Throwable throwable) {
		printTSlf4j(ApplicationUtil.getPositionContext(appLevel),"warn",message,throwable);
	}
	
	/**
	 * 打印warn级别日志并记录异常
	 * @param message 日志消息
	 * @param throwable 异常对象
	 */
	public static final void warnDB(String message,Throwable throwable) {
		printTDB(ApplicationUtil.getPositionContext(appLevel),"warn",message,throwable);
	}
	
	/**
	 * 打印warn级别日志并记录异常
	 * @param message 日志消息
	 * @param throwable 异常对象
	 */
	public static final void warnFile(String message,Throwable throwable) {
		printTFile(ApplicationUtil.getPositionContext(appLevel),"warn",message,throwable);
	}
	
	/**
	 * 打印error级别日志
	 * @param message 日志消息
	 * @param arguments 日志消息参数
	 */
	public static final void error(String message,Object... arguments) {
		printSlf4j(ApplicationUtil.getPositionContext(appLevel),"error",message,arguments);
	}
	
	/**
	 * 打印error级别日志
	 * @param message 日志消息
	 * @param arguments 日志消息参数
	 */
	public static final void errorDB(String message,Object... arguments) {
		printDB(ApplicationUtil.getPositionContext(appLevel),"error",message,arguments);
	}
	
	/**
	 * 打印error级别日志
	 * @param message 日志消息
	 * @param arguments 日志消息参数
	 */
	public static final void errorFile(String message,Object... arguments) {
		printFile(ApplicationUtil.getPositionContext(appLevel),"error",message,arguments);
	}
	
	/**
	 * 打印error级别日志并记录异常
	 * @param message 日志消息
	 * @param throwable 异常对象
	 */
	public static final void error(String message,Throwable throwable) {
		printTSlf4j(ApplicationUtil.getPositionContext(appLevel),"error",message,throwable);
	}
	
	/**
	 * 打印error级别日志并记录异常
	 * @param message 日志消息
	 * @param throwable 异常对象
	 */
	public static final void errorDB(String message,Throwable throwable) {
		printTDB(ApplicationUtil.getPositionContext(appLevel),"error",message,throwable);
	}
	
	/**
	 * 打印error级别日志并记录异常
	 * @param message 日志消息
	 * @param throwable 异常对象
	 */
	public static final void errorFile(String message,Throwable throwable) {
		printTFile(ApplicationUtil.getPositionContext(appLevel),"error",message,throwable);
	}
	
	/**
	 * 打印fatal级别日志
	 * @param message 日志消息
	 * @param arguments 日志消息参数
	 */
	public static final void fatalDB(String message,Object... arguments) {
		printDB(ApplicationUtil.getPositionContext(appLevel),"fatal",message,arguments);
	}
	
	/**
	 * 打印fatal级别日志
	 * @param message 日志消息
	 * @param arguments 日志消息参数
	 */
	public static final void fatalFile(String message,Object... arguments) {
		printFile(ApplicationUtil.getPositionContext(appLevel),"fatal",message,arguments);
	}
	
	/**
	 * 打印fatal级别日志并记录异常
	 * @param message 日志消息
	 * @param throwable 异常对象
	 */
	public static final void fatalDB(String message,Throwable throwable) {
		printTDB(ApplicationUtil.getPositionContext(appLevel),"fatal",message,throwable);
	}
	
	/**
	 * 打印fatal级别日志并记录异常
	 * @param message 日志消息
	 * @param throwable 异常对象
	 */
	public static final void fatalFile(String message,Throwable throwable) {
		printTFile(ApplicationUtil.getPositionContext(appLevel),"fatal",message,throwable);
	}
	
	/**
	 * 打印block级别日志
	 * @param message 日志消息
	 * @param arguments 日志消息参数
	 */
	public static final void blockDB(String message,Object... arguments) {
		printDB(ApplicationUtil.getPositionContext(appLevel),"block",message,arguments);
	}
	
	/**
	 * 打印block级别日志
	 * @param message 日志消息
	 * @param arguments 日志消息参数
	 */
	public static final void blockFile(String message,Object... arguments) {
		printFile(ApplicationUtil.getPositionContext(appLevel),"block",message,arguments);
	}
	
	/**
	 * 打印block级别日志并记录异常
	 * @param message 日志消息
	 * @param throwable 异常对象
	 */
	public static final void blockDB(String message,Throwable throwable) {
		printTDB(ApplicationUtil.getPositionContext(appLevel),"block",message,throwable);
	}
	
	/**
	 * 打印block级别日志并记录异常
	 * @param message 日志消息
	 * @param throwable 异常对象
	 */
	public static final void blockFile(String message,Throwable throwable) {
		printTFile(ApplicationUtil.getPositionContext(appLevel),"block",message,throwable);
	}
	
	/**
	 * 打印trace级别日志到文件
	 * @param event 事件名称
	 * @param loggerType 日志类型
	 */
	public static final void trace(Event event,LoggerType... loggerType){
		if(null==event) return;
		StackTraceElement logContext=ApplicationUtil.getPositionContext(appLevel);
		String jsonDict=getJsonLog(logContext,"trace",event,loggerType);
		printFile(logContext,"trace",jsonDict);
	}
	
	/**
	 * 打印trace级别日志到文件
	 * @param logData 日志数据实体
	 */
	public static final void trace(LogData logData){
		if(null==logData) return;
		StackTraceElement logContext=ApplicationUtil.getPositionContext(appLevel);
		String jsonDict=getJsonLog(logContext,"trace",logData);
		printFile(logContext,"trace",jsonDict);
	}
	
	/**
	 * 打印debug级别日志到文件
	 * @param event 事件名称
	 * @param loggerType 日志类型
	 */
	public static final void debug(Event event,LoggerType... loggerType){
		if(null==event) return;
		StackTraceElement logContext=ApplicationUtil.getPositionContext(appLevel);
		String jsonDict=getJsonLog(logContext,"debug",event,loggerType);
		printFile(logContext,"debug",jsonDict);
	}
	
	/**
	 * 打印debug级别日志到文件
	 * @param logData 日志数据实体
	 */
	public static final void debug(LogData logData){
		if(null==logData) return;
		StackTraceElement logContext=ApplicationUtil.getPositionContext(appLevel);
		String jsonDict=getJsonLog(logContext,"debug",logData);
		printFile(logContext,"debug",jsonDict);
	}
	
	/**
	 * 打印info级别日志到文件
	 * @param event 事件名称
	 * @param loggerType 日志类型
	 */
	public static final void info(Event event,LoggerType... loggerType){
		if(null==event) return;
		StackTraceElement logContext=ApplicationUtil.getPositionContext(appLevel);
		String jsonDict=getJsonLog(logContext,"info",event,loggerType);
		printFile(logContext,"info",jsonDict);
	}
	
	/**
	 * 打印info级别日志到文件
	 * @param logData 日志数据实体
	 */
	public static final void info(LogData logData){
		if(null==logData) return;
		StackTraceElement logContext=ApplicationUtil.getPositionContext(appLevel);
		String jsonDict=getJsonLog(logContext,"info",logData);
		printFile(logContext,"info",jsonDict);
	}
	
	/**
	 * 打印warn级别日志到文件
	 * @param event 事件名称
	 * @param loggerType 日志类型
	 */
	public static final void warn(Event event,LoggerType... loggerType){
		if(null==event) return;
		StackTraceElement logContext=ApplicationUtil.getPositionContext(appLevel);
		String jsonDict=getJsonLog(logContext,"warn",event,loggerType);
		printFile(logContext,"warn",jsonDict);
	}
	
	/**
	 * 打印warn级别日志到文件
	 * @param logData 日志数据实体
	 */
	public static final void warn(LogData logData){
		if(null==logData) return;
		StackTraceElement logContext=ApplicationUtil.getPositionContext(appLevel);
		String jsonDict=getJsonLog(logContext,"warn",logData);
		printFile(logContext,"warn",jsonDict);
	}
	
	/**
	 * 打印error级别日志到文件
	 * @param event 事件名称
	 * @param loggerType 日志类型
	 */
	public static final void error(Event event,LoggerType... loggerType){
		if(null==event) return;
		StackTraceElement logContext=ApplicationUtil.getPositionContext(appLevel);
		String jsonDict=getJsonLog(logContext,"error",event,loggerType);
		printFile(logContext,"error",jsonDict);
	}
	
	/**
	 * 打印error级别日志到文件
	 * @param logData 日志数据实体
	 */
	public static final void error(LogData logData){
		if(null==logData) return;
		StackTraceElement logContext=ApplicationUtil.getPositionContext(appLevel);
		String jsonDict=getJsonLog(logContext,"error",logData);
		printFile(logContext,"error",jsonDict);
	}
	
	/**
	 * 打印fatal级别日志到文件
	 * @param event 事件名称
	 * @param loggerType 日志类型
	 */
	public static final void fatal(Event event,LoggerType... loggerType){
		if(null==event) return;
		StackTraceElement logContext=ApplicationUtil.getPositionContext(appLevel);
		String jsonDict=getJsonLog(logContext,"fatal",event,loggerType);
		printFile(logContext,"fatal",jsonDict);
	}
	
	/**
	 * 打印fatal级别日志到文件
	 * @param logData 日志数据实体
	 */
	public static final void fatal(LogData logData){
		if(null==logData) return;
		StackTraceElement logContext=ApplicationUtil.getPositionContext(appLevel);
		String jsonDict=getJsonLog(logContext,"fatal",logData);
		printFile(logContext,"fatal",jsonDict);
	}
	
	/**
	 * 打印block级别日志到文件
	 * @param event 事件名称
	 * @param loggerType 日志类型
	 */
	public static final void block(Event event,LoggerType... loggerType){
		if(null==event) return;
		StackTraceElement logContext=ApplicationUtil.getPositionContext(appLevel);
		String jsonDict=getJsonLog(logContext,"block",event,loggerType);
		printFile(logContext,"block",jsonDict);
	}
	
	/**
	 * 打印block级别日志到文件
	 * @param logData 日志数据实体
	 */
	public static final void block(LogData logData){
		if(null==logData) return;
		StackTraceElement logContext=ApplicationUtil.getPositionContext(appLevel);
		String jsonDict=getJsonLog(logContext,"block",logData);
		printFile(logContext,"block",jsonDict);
	}
	
	/**
	 * 获取Json日志数据
	 * @param logContext 日志上下文
	 * @param level 日志级别
	 * @param object 日志数据或事件对象
	 * @param loggerType 日志类型
	 * @return JSON日志数据
	 */
	private static final String getJsonLog(StackTraceElement logContext,String level,Object object,LoggerType... loggerType){
		LogData logData=null;
		if(object instanceof LogData){
			logData=(LogData)object;
		}else{
			LoggerType type=null==loggerType || 0==loggerType.length || null==loggerType[0]?null:loggerType[0];
			logData=LogData.get((Event)object,type);
		}
		
		Event event=logData.EventName;
		String codePath=logData.CodePath;
		Product product=logData.ProductId;
		String loggerName=logData.LoggerName;
		String className=logContext.getClassName();
		if(null==codePath) codePath=className+".java";
		if(null==loggerName){
			loggerName=new StringBuilder(className).append(".")
					.append(logContext.getMethodName()).append(".")
					.append(logContext.getLineNumber()).toString();
		}
		
		LinkedHashMap<String,Object> map=new LinkedHashMap<String,Object>();
		map.put("CreateTime", logData.CreateTime.toString());
		map.put("LoggerLevel", level);
		map.put("ProductName", product.getProductName());
		map.put("SchemaName", logData.SchemaName);
		map.put("LoggerType", logData.LoggerType);
		map.put("EventName", event);
		map.put("LoggerName", loggerName);
		map.put("ProductId", product.getProductId());
		map.put("EventCode", event.code);
		map.put("ParentProductId", product.getParentId());
		map.put("ProductComment", product.getComment());
		map.put("OSType", logData.OSType);
		map.put("OSCoreVersion", logData.OSCoreVersion);
		map.put("OSDistributionName", logData.OSDistributionName);
		map.put("CodePath", codePath);
		map.put("CodeLanguage", logData.CodeLanguage);
		map.put("NodeId", logData.NodeId);
		map.put("ServiceName", logData.ServiceName);
		map.put("InstanceId", logData.InstanceId);
		map.put("Message", logData.Message);
		map.put("StructedData", logData.StructedData);
		map.put("Value", logData.Value);
		map.put("ValueUnit", logData.ValueUnit);
		map.put("HardwarePlatform", logData.HardwarePlatform);
		map.put("Keyword", logData.Keyword);
		map.put("Tags", logData.Tags);
		map.put("RunningEnvironment", logData.RunningEnvironment);
		
		return CommonUtil.javaToJsonStr(map);
	}
	
	/**
	 * 打印日志到文件
	 * @param logContext 日志上下文
	 * @param level 日志级别
	 * @param message 日志消息
	 * @param arguments 日志消息参数
	 */
	public static final void printSlf4j(StackTraceElement logContext,String level,String message,Object... arguments) {
		if(!isPreparedSlf4jLog) return;
		if(null==level||null==message||level.trim().isEmpty()||message.trim().isEmpty()) return;
		Method levelMethod=API_TO_METHOD.get(level);
		String className=logContext.getClassName();
		
		Object log=TYPE_TO_LOG.get(className);
		
		try{
			if(null==log) TYPE_TO_LOG.put(className, log=getLogger.invoke(loggerFactory, className));
		}catch(Exception e) {
			System.out.println("invoke getLogger of loggerFactory is failure...");
			e.printStackTrace();
		}
		
		String messages=new StringBuilder(".")
				.append(logContext.getMethodName())
				.append(" Line-")
				.append(logContext.getLineNumber())
				.append(":")
				.append(message)
				.toString();
		
		try{
			levelMethod.invoke(log, messages,arguments);
		}catch(Exception e){
			System.out.println("invoke "+level+" of logger is failure...");
			e.printStackTrace();
		}
	}
	
	/**
	 * 打印日志到文件并记录异常
	 * @param logContext 日志上下文
	 * @param level 日志级别
	 * @param message 日志消息
	 * @param throwable 异常对象
	 */
	public static final void printTSlf4j(StackTraceElement logContext,String level,String message,Throwable throwable){
		if(!isPreparedSlf4jLog) return;
		if(null==level||null==message||level.trim().isEmpty()||message.trim().isEmpty()) return;
		Method levelXMethod=API_TO_METHOD.get(level+"t");
		String className=logContext.getClassName();
		
		Object log=TYPE_TO_LOG.get(className);
		
		try{
			if(null==log) TYPE_TO_LOG.put(className, log=getLogger.invoke(loggerFactory, className));
		}catch(Exception e) {
			System.out.println("invoke getLogger of loggerFactory is failure...");
			e.printStackTrace();
		}
		
		String messages=new StringBuilder(".")
				.append(logContext.getMethodName())
				.append(" Line-")
				.append(logContext.getLineNumber())
				.append(":")
				.append(message)
				.toString();
		
		try{
			levelXMethod.invoke(log, messages,throwable);
		}catch(Exception e){
			System.out.println("invoke "+level+" of logger is failure...");
			e.printStackTrace();
		}
	}
	
	/**
	 * 打印日志到数据库
	 * @param logContext 日志上下文
	 * @param level 日志级别
	 * @param message 日志消息
	 * @param arguments 日志消息参数
	 */
	public static final void printDB(StackTraceElement logContext,String level,String message,Object... arguments) {
		if(!isPreparedDBLog || LEVEL_DICT.get(level)<loggingLevel) return;
		if(null==message||level.trim().isEmpty()||message.trim().isEmpty()) return;
		
		if(null!=arguments){
			for(Object arg:arguments){
				String param=CommonUtil.toString(arg);
				message=MSG_PATTERN.matcher(message).replaceFirst(null==param?"null":param.trim());
			}
		}
		
		String appname=ApplicationUtil.getApplicationName();
		String className=logContext.getClassName();
		String methodName=logContext.getMethodName();
		Integer lineNumber=logContext.getLineNumber();
		Object[] sleuths=getSleuthParams();
		String insIp=ApplicationUtil.getServerIP();
		Integer insPort=ApplicationUtil.getServerPort();
		
		asyncInsert(new Object[]{level,appname,className,methodName,lineNumber,message,sleuths[0],sleuths[1],sleuths[2],insIp,insPort});
	}
	
	/**
	 * 打印日志到数据库并记录异常
	 * @param logContext 日志上下文
	 * @param level 日志级别
	 * @param message 日志消息
	 * @param throwable 异常对象
	 */
	public static final void printTDB(StackTraceElement logContext,String level,String message,Throwable throwable) {
		if(!isPreparedDBLog || LEVEL_DICT.get(level)<loggingLevel) return;
		if(null==message||level.trim().isEmpty()||message.trim().isEmpty()) return;
		if(null!=throwable) message=new StringBuilder(message).append(" Cause: ").append(throwable.getMessage()).toString();
		
		String appname=ApplicationUtil.getApplicationName();
		String className=logContext.getClassName();
		String methodName=logContext.getMethodName();
		Integer lineNumber=logContext.getLineNumber();
		Object[] sleuths=getSleuthParams();
		String insIp=ApplicationUtil.getServerIP();
		Integer insPort=ApplicationUtil.getServerPort();
		
		asyncInsert(new Object[]{level,appname,className,methodName,lineNumber,message,sleuths[0],sleuths[1],sleuths[2],insIp,insPort});
	}
	
	/**
	 * 打印日志到指定文件
	 * @param logContext 日志上下文
	 * @param level 日志级别
	 * @param message 日志消息
	 * @param throwable 异常对象
	 */
	public static final void printFile(StackTraceElement logContext,String level,String message,Object... arguments) {
		if(!isPreparedFileLog || LEVEL_DICT.get(level)<loggingLevel) return;
		if(null==message||message.trim().isEmpty()) return;
		
		if(null!=arguments){
			for(Object arg:arguments){
				String param=CommonUtil.toString(arg);
				message=MSG_PATTERN.matcher(message).replaceFirst(null==param?"null":param.trim());
			}
		}
		
		try {
			asyncLogFile(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 打印日志到指定文件
	 * @param logContext 日志上下文
	 * @param level 日志级别
	 * @param message 日志消息
	 * @param throwable 异常对象
	 */
	public static final void printTFile(StackTraceElement logContext,String level,String message,Throwable throwable) {
		if(!isPreparedFileLog || LEVEL_DICT.get(level)<loggingLevel) return;
		if(null==message||message.trim().isEmpty()) return;
		if(null!=throwable) message=new StringBuilder(message).append(" Cause: ").append(throwable.getMessage()).toString();
		
		try {
			asyncLogFile(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取Sleuth跟踪参数
	 * @return 跟踪参数表
	 * @description [spanId,parentSpanId,traceId]
	 */
	private static final Object[] getSleuthParams(){
		Object[] params=new Object[3];
		if(!isPreparedSleuth) return params;
		
		Object tracer=ApplicationUtil.getBean(tracerType);
		if(null==tracer) return params;
		
		try {
			Object span=currentSpanMethod.invoke(tracer);
			Object tracerContext=contextMethod.invoke(span);
			params[0]=spanIdStringMethod.invoke(tracerContext);
			params[1]=parentIdStringMethod.invoke(tracerContext);
			params[2]=traceIdStringMethod.invoke(tracerContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return params;
	}
	
	/**
	 * 执行插入语句
	 * @param params SQL参数表
	 * @return 自增主键
	 * @throws SQLException
	 */
	private static final void asyncInsert(Object[] params) {
		if(null==params||params.length<11) return;
		if(CommonUtil.isEmpty(insertSQL)){
			insertSQL=new StringBuilder("insert into ").append(ApplicationUtil.getProperty("logging.db.tabName", "logback"))
					.append("(loglevel,appname,classname,methodname,lineNum,message,spanId,parentSpanId,traceId,insIp,insPort) values")
					.append("(?,?,?,?,?,?,?,?,?,?,?)").toString();
		}
		
		final Set<Connection> connectionSet=new HashSet<Connection>();
		try{
			if(ApplicationUtil.getProperty("logging.db.forceDS", Boolean.class,false)){
				Connection conn=getConnection();
				if(null==conn) return;
				connectionSet.add(conn);
			}else{
				DataSource dataSource=dataSourceMap.get(ApplicationUtil.getProperty("logging.db.dataSourceName", "dataSource"));
				if(null==dataSource) dataSource=dataSourceMap.entrySet().iterator().next().getValue();
				if(null==dataSource) return;
				connectionSet.add(dataSource.getConnection());
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		Map.Entry<Method, Object> poolEntry=threadPool.entrySet().iterator().next();
		try{
			poolEntry.getKey().invoke(poolEntry.getValue(), new Runnable(){
				public void run() {
					PreparedStatement pstat=null;
					Connection conn=connectionSet.iterator().next();
					try{
						pstat=conn.prepareStatement(insertSQL);
						for(int i=0;i<11;i++) pstat.setObject(i+1, params[i]);
						pstat.executeUpdate();
					}catch(SQLException e){
						e.printStackTrace();
					}finally{
						try{
							pstat.close();
							conn.close();
						}catch(SQLException e){
							e.printStackTrace();
						}
					}
				}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 执行DDL语句为所有数据源生成表结构
	 * @throws SQLException 
	 */
	private static final void createSchema() {
		String tabName=ApplicationUtil.getProperty("logging.db.tabName", "logback");
		String ddl=new StringBuilder("create table if not exists ").append(tabName).append("(")
		.append("logid int(11) primary key auto_increment,")
		.append("logtime timestamp default now(),")
		.append("loglevel varchar(10) default null,")
		.append("appname varchar(50) default null,")
		.append("classname varchar(200) default null,")
		.append("methodname varchar(100) default null,")
		.append("lineNum int(6) default -1,")
		.append("message text default null,")
		.append("spanId varchar(200) default null,")
		.append("parentSpanId varchar(200) default null,")
		.append("traceId varchar(200) default null,")
		.append("insIp varchar(30) default null,")
		.append("insPort int(6) default -1")
		.append(")engine=innodb charset=utf8;")
		.toString();
		
		Set<Connection> connSet=new HashSet<Connection>();
		try{
			if(null!=dataSourceMap) for(Map.Entry<String, DataSource> entry:dataSourceMap.entrySet()) connSet.add(entry.getValue().getConnection());
			Connection tmpConn = getConnection();
			if(null!=tmpConn)connSet.add(tmpConn);
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		for(Connection conn:connSet){
				Statement stat=null;
				try{
					stat=conn.createStatement();
					stat.execute(ddl);
				}catch(SQLException e){
					e.printStackTrace();
				}finally{
					try{
						stat.close();
						conn.close();
					}catch(SQLException e){
						e.printStackTrace();
					}
				}
		}
	}
	
	/**
	 * 获取自定义数据源(非连接池)对应的数据库连接
	 * @return 数据库连接
	 */
	private static final Connection getConnection() throws SQLException{
		String url=ApplicationUtil.getProperty("logging.db.url");
		if(null==url) return null;
		String user=ApplicationUtil.getProperty("logging.db.user");
		if (null==user) return DriverManager.getConnection(url);
		String password=ApplicationUtil.getProperty("logging.db.password");
        return DriverManager.getConnection(url, user, password);
	}
	
	/**
	 * 获取日志文件最大尺寸
	 */
	private static final Long getMaxFileSize(){
		String configMaxVal=ApplicationUtil.getProperty("logging.maxFileSize");
		if(null==configMaxVal || 0==configMaxVal.length()) return 100*1024*1024L;
		Matcher matcher=CAP_REGEX.matcher(configMaxVal);
		if(!matcher.find()) return 100*1024*1024L;
		return SizeUnit.getBytes(Long.parseLong(matcher.group(1)), matcher.group(2).substring(0,1));
	}
	
	/**
	 * 异步写出日志文件
	 * @param params 消息参数
	 * @throws IOException 
	 */
	private static final void asyncLogFile(String message) throws Exception {
		Map.Entry<Method, Object> poolEntry=threadPool.entrySet().iterator().next();
		poolEntry.getKey().invoke(poolEntry.getValue(), new Runnable(){
			public void run() {
				try {
					synchronized(logFile){
						boolean isToday;
						if(!(isToday=DateUtil.isToday(createTime)) || maxFileSize<=logFile.length()) {
							logFileStream.flush();
							logFileStream.close();
							renameFile(isToday,createTime);
							logFileStream=new FileOutputStream(logFile,true);
							createTime=DateUtil.millSecondsToCalendar(logFile.lastModified());
						}
						logFileStream.write((message+"\n").getBytes(Charset.defaultCharset()));
						logFileStream.flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * 检查日志文件滚动命名
	 * @param isToday 是否是今天
	 * @throws creationTime 文件创建时间
	 */
	private static final void renameFile(Boolean isToday,Calendar creationTime) throws IOException {
		String createTimeStr=new StringBuilder("").append(creationTime.get(Calendar.YEAR)).append("-").append(creationTime.get(Calendar.MONTH)+1).append("-").append(creationTime.get(Calendar.DATE)).toString();
    	Integer fileCounter=NAME_COUNTER.get(createTimeStr);
    	if(null==fileCounter) NAME_COUNTER.put(createTimeStr, fileCounter=0);
    	
    	String newName=new StringBuilder(logFile.getAbsolutePath()).append("-").append(createTimeStr).append(".").append(fileCounter).toString();
    	logFile.renameTo(new File(newName));
    	
    	if(isToday) {
    		NAME_COUNTER.put(createTimeStr, fileCounter+1);
    	}else{
    		NAME_COUNTER.clear();
    	}
	}
}

package com.bfw.log.util;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bfw.log.constant.CpuArch;
import com.bfw.log.constant.Event;
import com.bfw.log.constant.Language;
import com.bfw.log.constant.LoggerType;
import com.bfw.log.constant.OSType;
import com.bfw.log.constant.Product;
import com.bfw.log.constant.RunningEnvironment;
import com.bfw.log.constant.Schema;
import com.bfw.log.constant.ValueUnit;

/**
 * @author Lixiang
 * @description 日志数据包装类
 */
public class LogData {
	/**
	 * 日志记录时间戳
	 * 默认值:自动生成
	 */
	public Timestamp CreateTime;
	
	/**
	 * 产品ID
	 * 默认值:应用配置,提供枚举选择
	 */
	public Product ProductId;
	
	/**
	 * 日志类别
	 * 默认值:无,提供枚举选择
	 */
	public Schema SchemaName;
	
	/**
	 * 日志类型
	 * 默认值:无,提供枚举选择
	 */
	public LoggerType LoggerType;
	
	/**
	 * 事件名称
	 * 默认值:提供枚举选择
	 */
	public Event EventName;
	
	/**
	 * 日志名称
	 * 默认值:按包名+类名生成
	 */
	public String LoggerName;
	
	/**
	 * 操作系统类型
	 * 默认值:自动推断,提供枚举选择
	 */
	public OSType OSType;
	
	/**
	 * 操作系统内核版本
	 * 默认值:自动推断
	 */
	public String OSCoreVersion;
	
	/**
	 * 操作系统发布名称
	 * 默认值:OSType+"-"+OSCoreVersion
	 */
	public String OSDistributionName;
	
	/**
	 * 源代码路径
	 * 默认值:无
	 */
	public String CodePath;
	
	/**
	 * 应用方编程语言
	 * 默认值:Java,提供枚举选择
	 */
	public Language CodeLanguage;
	
	/**
	 * 节点ID
	 * 默认值:自动推断内网IP,建议指定值
	 */
	public String NodeId;
	
	/**
	 * 服务名称
	 * 默认值:自动推断应用服务名,建议指定值
	 */
	public String ServiceName;
	
	/**
	 * 服务实例ID
	 * 默认值:自动推断例程名,建议指定值
	 */
	public String InstanceId;
	
	/**
	 * 日志消息
	 * 默认值:无
	 */
	public String Message;
	
	/**
	 * 结构化数据附加字典
	 * 默认值:无
	 */
	public Map<String,Object> StructedData;
	
	/**
	 * 数值
	 * 默认值:无
	 */
	public Double Value;
	
	/**
	 * 数值单位
	 * 默认值:无,提供枚举选择
	 */
	public ValueUnit ValueUnit;
	
	/**
	 * 硬件平台(处理器架构)
	 * 默认值:自动推断,提供枚举选择
	 */
	public CpuArch HardwarePlatform;
	
	/**
	 * 搜索/检测等关键字
	 * 默认值:无
	 */
	public String Keyword;
	
	/**
	 * 日志分类标签表
	 *  默认值:无
	 */
	public List<Object> Tags;
	
	/**
	 * 运行时环境
	 * 默认值:无,提供枚举选择
	 */
	public RunningEnvironment RunningEnvironment;
	
	private LogData(){
		this.CreateTime=new Timestamp(new Date().getTime());
		this.ProductId=Product.valueOf(ApplicationUtil.getProperty("logging.product.id", String.class,"MY"));
		this.SchemaName=Schema.ServerLog;
		this.LoggerType=com.bfw.log.constant.LoggerType.ExternalCallee;
		this.OSType=com.bfw.log.constant.OSType.valueOf(CommonUtil.getOSType());
		this.OSCoreVersion=CommonUtil.getOSVersion();
		this.OSDistributionName=this.OSType.name()+"-"+this.OSCoreVersion;
		this.CodeLanguage=Language.Java;
		this.NodeId=ApplicationUtil.getServerIP();
		this.ServiceName=ApplicationUtil.getProperty("spring.application.name", String.class,"unknow");
		this.InstanceId=this.NodeId+":"+ApplicationUtil.getProperty("server.port", String.class,"unknow");
		this.HardwarePlatform=CpuArch.X86_64;
		this.RunningEnvironment=com.bfw.log.constant.RunningEnvironment.NativeProcess;
	}
	
	public LogData(Event eventName){
		this();
		if(null!=eventName) this.EventName=eventName;
	}
	
	public LogData(Event eventName,LoggerType loggerType){
		this();
		if(null!=eventName) this.EventName=eventName;
		if(null!=loggerType) this.LoggerType=loggerType;
	}
	
	public static final LogData get(Event eventName){
		LogData data=new LogData();
		if(null!=eventName) data.EventName=eventName;
		return data;
	}
	
	public static final LogData get(Event eventName,LoggerType loggerType){
		LogData data=new LogData();
		if(null!=eventName) data.EventName=eventName;
		if(null!=loggerType) data.LoggerType=loggerType;
		return data;
	}
}

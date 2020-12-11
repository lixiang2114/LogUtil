package com.bfw.log.constant;

/**
 * @author Lixiang
 * @description 日志类型
 */
public enum LoggerType {
	/**
	 * 程序内部调用的发起方
	 */
	InternalCaller,
	
	/**
	 * 程序内部调用的被调方
	 */
	InternalCallee,
	
	/**
	 * 调用外部程序/模块的发起方
	 */
	ExternalCaller,
	
	/**
	 * 接受外部程序/模块调用的被调方
	 */
	ExternalCallee,
	
	/**
	 * 具备显示及用户操作交互能力的界面元素
	 */
	UIElement,
	
	/**
	 * 由定时器启动的独立任务
	 */
	ScheduledTask,
	
	/**
	 * 数据访问的发起方
	 */
	DataVisitor,
	
	/**
	 * 特征检测器
	 */
	FeatureChecker,
	
	/**
	 * 性能表现监控器
	 */
	PerformanceMonitor,
	
	/**
	 * 其它
	 */
	Other
	;
}

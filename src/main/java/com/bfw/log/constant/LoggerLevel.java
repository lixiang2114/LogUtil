package com.bfw.log.constant;

/**
 * @author Lixiang
 * @description 日志级别
 */
public enum LoggerLevel {
	/**
	 * 打开所有日志
	 */
	on,
	
	/**
	 * 打开trace以上级别
	 */
	trace,
	
	/**
	 * 打开debug以上级别
	 */
	debug,
	
	/**
	 * 打开info以上级别
	 */
	info,
	
	/**
	 * 打开warn以上级别
	 */
	warn,
	
	/**
	 * 打开error以上级别
	 */
	error,
	
	/**
	 * 打开fatal以上级别
	 */
	fatal,
	
	/**
	 * 打开block以上级别
	 */
	block,
	
	/**
	 * 关闭所有日志
	 */
	off 
}

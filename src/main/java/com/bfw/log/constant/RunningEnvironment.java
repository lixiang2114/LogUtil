package com.bfw.log.constant;

/**
 * @author Lixiang
 * @description 运行时环境
 */
public enum RunningEnvironment {
	/**
	 * 本地宿主进程
	 */
	NativeProcess,
	
	/**
	 * 微服务容器进程
	 */
	GeneralContainer,
	
	/**
	 * 开源微服务容器集群
	 */
	Kurbernetes,
	
	/**
	 * 微软微服务容器集群
	 */
	ServiceFabric,
	
	/**
	 * 其它容器
	 */
	Other
	;
}

### 插件开发背景
LogUtil是基于JAVA服务端SpringBoot开发环境下的一款SDK工具包，基于SprinBoot
2.X平台开发，其目的旨在于以规范的格式提供一套统一的日志输出方式，工具包被安装到开发环境Maven仓库下，通过Maven依赖引入到工程应用中使用，开发者调用工具包的方式与原生SpringBoot日志组件相同。  


​      
### 插件功能特性
1. 版本相关性  
应用服务端的SDK包基于JDK1.8+平台开发，应用环境为SpringBoot2.X，对于低于JDK8的平台上运行本包不保证其额外的不确定性，对于低于SpringBoot1.X的应用环境是无法运行本工具包的，这点切记。  

2. 功能相关性  
虽然本SDK设计的初衷是为统一日志规范格式，但为考虑更好的使用体验，本SDK中同时内置了SLF4J抽象日志组件支持，数据库日志组件支持，以及特定于产品的日志文件组件支持，开发者可通过SpringBoot原生配置文件(application.properties或application.yml)自由切换使用其中某一种日志组件，切换时只需要在SpringBoot配置文件中打开对应的日志开关即可。    
   
   ​     

### 插件使用说明
#### 服务端日志SDK安装
1. 下载SDK工具包  
wget https://github.com/lixiang2114/LogUtil/raw/main/target/LogUtil-1.0.jar  
  
2. 安装SDK工具包  
mkdir -p $repository/com/bfw/log/LogUtil/1.0 && cp -a LogUtil-1.0.jar $repository/com/bfw/log/LogUtil/1.0/  

3. 工程中引入SDK包依赖
```Text
<dependency>
    <groupId>com.bfw.log</groupId>
    <artifactId>LogUtil</artifactId>
    <version>1.0</version>
</dependency>  
```

   ​     
#### 服务端日志SDK使用  
##### 配置SDK工具  
为降低SDK的学习使用成本，本SDK直接套用了SpringBoot额配置文件，开发者可以直接在SpringBoot的配置文件（application.properties，application.yml）中配置SDK即可，一个典型的基于产品日志输出的配置如下：  
```Text
logging: 
  mode: file
  product.id: MD
  maxHistory: 30
  maxFileSize: 10MB
  eventLevel: trace
  filePath: /opt/my/logs/my.log
```
SDK配置参数解释如下：  
logging.mode:   
日志模式，有slf4j、file、db三种模式，默认值为SLF4J  
logging.product.id:   
产品ID，为方便更为直观的使用SDK，这里直接套用产品的英文名缩写，如：MY，MD等；可用的产品英文名可以查看Product枚举类，默认值为MY，即：“蜜柚”  
logging.maxHistory:   
与其它日志组件相同，日志文件的最大保留时间（单位：天），超过这个时间的日志文件将被自动删除，无默认值，若没有配置本参数则不会删除任何历史日志  
logging.maxFileSize:   
与其它日志组件相同，日志文件的最大尺寸（单位：MB、GB，更多单位请参考SizeUnit枚举类），默认值为100MB，超过这个尺寸将被重命名为一个新的日志文件  
logging.eventLevel:   
与其它日志组件相同，日志输出级别，大于或等于该级别的日志消息江北输出到日志文件，默认值为on，即默认情况下会打印输出所有日志  
logging.filePath:  
与其它日志组件相同，用于指定当前正在写入的日志文件名称，本参数是必选参数，无默认值，若没有配置该参数则使用本SDK将抛出异常  

   ​     
##### SDK工具调用  
1. SDK产品日志接口  
与使用原生的SLF日志组件相同，但使用本套日志SDK包表现得更加简洁，简洁到甚至无需定义Logger，也无需定义任何注解，在需要打印日志的地方直接类似于下面的API调用即可：  
    ```JAVA
    LogUtil.info(LogData logData);
    LogUtil.info(Event event, LoggerType loggerType);
    ```
2. 日志输出级别  
上面是打印info级别的日志,如果需要打印其它级别的日志可以调用其对应的方法，与其它流行日志组件相同，为方便使用，本套SDK将日志级别名称与调用的日志方法设计同名，本套SDK基于产品的日志文件打印输出支持9个级别：  
    ```Text
    on，trace，debug，info，warn，error，fatal，block，off  
    ```
    其中on是最低级别，该级别表示任何日志都全部输出，off是最高级别，它表示不输出任何日志  
   ​     
3. 日志数据  
上述接口中的参数都是日志数据，其中第一个是直接传入日志数据包，日志数据包是一个实体类封装，但并非一个JavaBean，为方便包装日志输出数据，里面的所有成员变量被全部定义为公共的（public），因为该类仅在输出日志数据时包装数据之用，所以未将其设计为JavaBean类，LogData中的大部分属性都提供了默认的初始化属性值，我们可以通过下面两种方法来获得LogData的数据包实例：  

1）、构造方法：  
```JAVA
LogData logData=new LogData(Event eventName);
LogData logData=new LogData(Event eventName,LoggerType loggerType);
```
2）、静态方法：  
```JAVA
LogData logData=LogData.get(Event eventName);
LogData logData=LogData.get(Event eventName,LoggerType loggerType);
```
##### 说明
无论是构造方法还是静态方法，都必须提供Event事件枚举参数，Event事件枚举取值可参考Event枚举类，其次是需要提供LoggerType日志类型枚举，事件类型枚举值可以参考LoggerType枚举类。
虽然我们对LogData类中的其它成员变量均提供了默认生成值，但作为日志记录，我们考虑的是尽可能记录更为准确的日志消息，而不是让工具包自动帮我们生成，所以我们推荐以形如下面的方式调用工具包中的API以指定每一个可以准确记录的日志参数：  
```JAVA
// 构建日志数据包
LogData logData=LogData.get(Event.Queried,LoggerType.ExternalCallee);
logData.ProductId=.....
logData.LoggerName=....
logData.Message=......
logData.Value=......
logData.ValueUnit=......
logData.NodeId=......
logData.ServiceName=......
logData.InstanceId=......
logData.CodePath=......
logData.HardwarePlatform=......
logData.RunningEnvironment=......
logData.Keyword=......

//打印日志数据到文件
LogUtil.info(logData);
```
##### 备注  
基于file模式下的日志写出操作全部是通过池化方式异步执行的，SDK的默认池化方式是通过共享Spring线程池来实现，如果当前Spring线程池不可用则插件会自动选择自定义线程池方式，此时我们可以有选择性的定义如下两个参数：  
logging.threadPoolSize：
线程池中的线程数量，默认值为10  
logging.threadQueueSize：  
当线程池队列中的最大日志任务数量，默认值为50  

   ​     

#### 附录  
##### SDK基于SLF4J组件的使用  
SLF4J组件是SpringBoot原生支持的日志组件，本SDK默认的日志模式也是SLF4J，所以如果使用基于SLF4J的日志打印模式则无需配置与本SDK相关的参数；在SpringBoot的默认配置中，LogBack是被默认用于日志输出的，所有的日志组件都将消息上抛到SLF4J抽象层，然后再由SLFJ接口回调LogBack组件打印出日志消息，原LogBack使用时需要在logback.xml 或 logback-spring.xml中配置Logback日志组件参数，然后在代码中使用形如下面的方式引入日志组件： 
```JAVA
@Service
public class UserServiceImpl implements UserService{	
    private static final Logger 
    log=LoggerFactory.getLogger(UserServiceImpl.class);
    
    public boolean testBloomFilter() throws Exception {
        log.trace("this is trace log");
        log.debug("this is debug log");
        log.info("this is info log");
        log.warn("this is warn log");
        log.error("this is error log");
        return true;
    }
}
```
或者使用下面的注解方式：  
```JAVA
@Slf4j
@Service
public class UserServiceImpl implements UserService{
  public boolean testBloomFilter() throws Exception {
		log.trace("this is {} log","trace");
		log.debug("this is {} log","debug");
		log.info("this is {} log","info");
		log.warn("this is {} log","warn");
		log.error("this is {} log","error");
		return true;
	}
}
```
无论使用上面哪种方式，都少不了引入SLF4J组件的过程，而如果使用了本SDK包则代码将变得更加简洁如下：  
```JAVA
@Service
public class UserServiceImpl implements UserService{
  public boolean testBloomFilter() throws Exception {
		LogUtil.trace("this is {} log","trace");
		LogUtil.debug("this is {} log","debug");
		LogUtil.info("this is {} log","info");
		LogUtil.warn("this is {} log","warn");
		LogUtil.error("this is {} log","error");
		return true;
	}
}
```
你会发现上面根本没有任何引入SLF4J组件的代码，而是直接调用SDK包中的API完成日志打印，没错，这就是因为SDK包已经自动帮助我们推断了当前需要的日志打印类型并自动装配好需要的日志组件。  
   ​     
##### SDK基于数据库日志组件的使用  
1. 基于数据库日志模式的常规配置如下  
```Text
logging.mode: db
logging.dbLevel: on
logging.db.url: jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf8
logging.db.user: root
logging.db.password: 123456
logging.db.tabName: logback
logging.db.forceDS: true
logging.db.dataSourceName: dataSource
```
参数解释：  
logging.mode：  
数据库日志模式，默认值为slf4j，前已述及  
logging.dbLevel：  
数据库日志级别，默认值为on，表示记录所有日志输出
logging.db.url：  
连接数据库的URL地址字串，无默认值，若没有配置则等于放弃使用自定义数据源  
logging.db.user：  
连接数据库的用户名，无默认值，若没有配置则等于放弃使用自定义数据源  
logging.db.password：  
连接数据库的密码，无默认值，若没有配置则等于放弃使用自定义数据源  
logging.db.tabName：  
保存日志消息的数据表名称，默认值为logback  
logging.db.forceDS：  
是否强制使用自定义数据源，默认优先使用共享数据源，即：默认值为false  
logging.db.dataSourceName：  
共享数据源Bean名称，只有当logging.db.forceDS为false时，本参数才有效，默认值为dataSource     ​     

2. 基于数据库日志模式的用例  
```JAVA
@Service
public class UserServiceImpl implements UserService{
  public boolean testBloomFilter() throws Exception {
		LogUtil.traceDB("this is {} log","trace");
		LogUtil.debugDB("this is {} log","debug");
		LogUtil.infoDB("this is {} log","info");
		LogUtil.warnDB("this is {} log","warn");
		LogUtil.errorDB("this is {} log","error");
		return true;
	}
}
```
备注：  
数据库日志模式下，所有的日志写出操作都是后台异步进行的，不会任何阻塞用户线程  
如你所看到的，相比于SLF4J的API而言，就只需要在日志级别对应的方法名之后加上一个"DB"后缀即可  
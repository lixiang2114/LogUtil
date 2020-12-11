package com.bfw.log.constant;

/**
 * @author Lixiang
 * @description 事件枚举字典
 */
public enum Event {
	/*1.X-10*/
	//7
	FailGeneral("FailGeneral","1000"),
	FailEntry("FailEntry","1001"),
	FailOperation("FailOperation","1002"),
	FailResult("FailResult","1003"),
	FailStart("FailStart","1004"),
	FailRaise("FailRaise","1005"),
	FailEncoding("FailEncoding","1006"),
	//8
	FailUI("FailUI","1007"),
	FailEleStyle("FailEleStyle","1008"),
	FailEleDisplay("FailEleDisplay","1009"),
	FailEleOperation("FailEleOperation","1010"),
	FailUIPaging("FailUIPaging","1011"),
	FailAnimation("FailAnimation","1012"),
	FailLayout("FailLayout","1013"),
	FailDrawing("FailDrawing","1014"),
	//4
	FailMedia("FailMedia","1015"),
	FailImageRender("FailImageRender","1016"),
	FailVideoPlay("FailVideoPlay","1017"),
	FailMediaSupport("FailMediaSupport","1018"),
	//4
	FailFile("FailFile","1019"),
	FailUpload("FailUpload","1020"),
	FailRead("FailRead","1021"),
	FailSave("FailSave","1022"),
	//8
	FailExec("FailExec","1023"),
	FailTestDriver("FailTestDriver","1024"),
	FailExeCode("FailExeCode","1025"),
	FailExeApp("FailExeApp","1026"),
	FailCrush("FailCrush","1027"),
	FailStuck("FailStuck","1028"),
	FailOverResource("FailOverResource","1029"),
	FailPerformance("FailPerformance","1030"),
	//6
	FailWeb("FailWeb","1031"),
	FailCookie("FailCookie","1032"),
	FailHtml("FailHtml","1033"),
	FailDom("FailDom","1034"),
	FailRedirect("FailRedirect","1035"),
	FailBrowser("FailBrowser","1036"),
	//4
	FailAPI("FailAPI","1037"),
	FailRequest("FailRequest","1038"),
	FailResponse("FailResponse","1039"),
	FailProtocal("FailProtocal","1040"),
	//4
	FailAPP("FailAPP","1041"),
	FailHardware("FailHardware","1042"),
	FailAuth("FailAuth","1043"),
	FailUpdate("FailUpdate","1044"),
	
	/*1.X-11*/
	PassGeneral("PassGeneral","1100"),
	PassUI("PassUI","1101"),
	PassMedia("PassMedia","1102"),
	PassFile("PassFile","1103"),
	PassExec("PassExec","1104"),
	PassWeb("PassWeb","1105"),
	PassAPI("PassAPI","1106"),
	PassAPP("PassAPP","1107"),
	
	/*1.X-12*/
	EnableGeneral("EnableGeneral","1200"),
	EnableAPI("EnableAPI","1201"),
	EnableService("EnableService","1202"),
	EnableNode("EnableNode","1203"),
	EnableCluster("EnableCluster","1204"),
	EnableMachine("EnableMachine","1205"),
	EnableHardware("EnableHardware","1206"),
	EnableNetwork("EnableNetwork","1207"),
	EnableMonitor("EnableMonitor","1208"),
	EnableCollector("EnableCollector","1209"),
	EnableReporter("EnableReporter","1210"),
	EnableStorage("EnableStorage","1211"),
	EnableAnalyzer("EnableAnalyzer","1212"),
	EnableProtection("EnableProtection","1213"),
	EnableHandler("EnableHandler","1214"),
	EnableRecorder("EnableRecorder","1215"),
	EnableRouter("EnableRouter","1216"),
	EnableDispatcher("EnableDispatcher","1217"),
	EnableRegister("EnableRegister","1218"),
	EnablePublisher("EnablePublisher","1219"),
	
	/*1.X-13*/
	DisableGeneral("DisableGeneral","1300"),
	DisableAPI("DisableAPI","1301"),
	DisableService("DisableService","1302"),
	DisableNode("DisableNode","1303"),
	DisableCluster("DisableCluster","1304"),
	DisableMachine("DisableMachine","1305"),
	DisableHardware("DisableHardware","1306"),
	DisableNetwork("DisableNetwork","1307"),
	DisableMonitor("DisableMonitor","1308"),
	DisableCollector("DisableCollector","1309"),
	DisableReporter("DisableReporter","1310"),
	DisableStorage("DisableStorage","1311"),
	DisableAnalyzer("DisableAnalyzer","1312"),
	DisableProtection("DisableProtection","1313"),
	DisableHandler("DisableHandler","1314"),
	DisableRecorder("DisableRecorder","1315"),
	DisableRouter("DisableRouter","1316"),
	DisableDispatcher("DisableDispatcher","1317"),
	DisableRegister("DisableRegister","1318"),
	DisablePublisher("DisablePublisher","1319"),
	
	/*2.X-20*/
	PerformGeneral("PerformGeneral","2000"),
	PerformStart ("PerformStart ","2001"),
	PerformFinish("PerformFinish","2002"),
	PerformAbort("PerformAbort","2003"),
	PerformCrush("PerformCrush","2004"),
	PerformUndo("PerformUndo","2005"),
	
	/*2.X-21*/
	ConfirmGeneral("ConfirmGeneral","2100"),
	Confirmed("Confirmed","2101"),
	ConfirmedId("ConfirmedId","2102"),
	ConfirmedAuth("ConfirmedAuth","2103"),
	NotConfirmed("NotConfirmed","2104"),
	PartlyConfirmed("PartlyConfirmed","2105"),
	
	/*2.X-22*/
	RejectGeneral("RejectGeneral","2200"),
	Rejected("Rejected","2201"),
	RejectTemporary("RejectTemporary","2202"),
	RejectPermanent("RejectPermanent","2203"),
	RejectUser("RejectUser","2204"),
	RejectDevice("RejectDevice","2205"),
	RejectSource("RejectSource","2206"),
	RejectCaller("RejectCaller","2207"),
	RejectAuth("RejectAuth","2208"),
	NotReject("NotReject","2209"),
	
	/*2.X-23*/
	ValidGeneral("ValidGeneral","2300"),
	ValidUser("ValidUser","2301"),
	ValidCaller("ValidCaller","2302"),
	ValidAuth("ValidAuth","2303"),
	ValidSource("ValidSource","2304"),
	ValidData("ValidData","2305"),
	ValidSign("ValidSign","2306"),
	ValidTime("ValidTime","2307"),
	ValidLoc("ValidLoc","2308"),
	ValidTarget("ValidTarget","2309"),
	ValidEntry("ValidEntry","2310"),
	ValidDevice("ValidDevice","2311"),
	
	/*2.X-24*/
	InvalidGeneral("InvalidGeneral","2400"),
	InvalidUser("InvalidUser","2401"),
	InvalidCaller("InvalidCaller","2402"),
	InvalidAuth("InvalidAuth","2403"),
	InvalidSource("InvalidSource","2404"),
	InvalidData("InvalidData","2405"),
	InvalidSign("InvalidSign","2406"),
	InvalidTime("InvalidTime","2407"),
	InvalidLoc("InvalidLoc","2408"),
	InvalidTarget("InvalidTarget","2409"),
	InvalidEntry("InvalidEntry","2410"),
	InvalidDevice("InvalidDevice","2411"),
	
	/*3.X-30*/
	CacheGeneral("CacheGeneral","3000"),
	Cached("Cached","3001"),
	UnCached("UnCached","3002"),
	CachePurged("CachePurged","3003"),
	CacheExpried("CacheExpried","3004"),
	CacheRead("CacheRead","3005"),
	CacheWrite("CacheWrite","3006"),
	CacheError("CacheError","3007"),
	
	/*3.X-31*/
	AddGeneral("AddGeneral","3100"),
	Added("Added","3101"),
	AddError("AddError","3102"),
	NotAdded("NotAdded","3103"),
	UndoAdd("UndoAdd","3104"),
	AddFromImport("AddFromImport","3105"),
	
	/*3.X-32*/
	ChangeGeneral("ChangeGeneral","3200"),
	Changed("Changed","3201"),
	UndoChange("UndoChange","3202"),
	NotChanged("NotChanged","3203"),
	ChangeError("ChangeError","3204"),
	
	/*3.X-33*/
	RemoveGeneral("RemoveGeneral","3300"),
	Removed("Removed","3301"),
	NotRemoved("NotRemoved","3302"),
	UndoRemove("UndoRemove","3303"),
	RemoveError("RemoveError","3304"),
	
	/*3.X-34*/
	QueryGeneral("QueryGeneral","3400"),
	Queried("Queried","3401"),
	QueryError("QueryError","3402"),
	QuertAbort("QuertAbort","3403"),
	QueryAndExport("QueryAndExport","3404"),
	
	/*3.X-35*/
	LockGeneral("LockGeneral","3500"),
	Locked("Locked","3501"),
	LockError("LockError","3502"),
	NotLocked("NotLocked","3503"),
	UndoLock("UndoLock","3504"),
	
	/*3.X-36*/
	ReleaseGeneral("ReleaseGeneral","3600"),
	Released("Released","3601"),
	ReleaseError("ReleaseError","3602"),
	NotReleased("NotReleased","3603"),
	UndoRelease("UndoRelease","3604"),
	
	/*4.X-40*/
	CallGeneral("CallGeneral","4000"),
	Called("Called","4001"),
	CallError("CallError","4002"),
	Callback("Callback","4003"),
	BeCallback("BeCallback","4004"),
	NotCalled("NotCalled","4005"),
	CallParamError("CallParamError","4006"),
	
	/*4.X-41*/
	ReturnGeneral("ReturnGeneral","4100"),
	Returned("Returned","4101"),
	ReturnError("ReturnError","4102"),
	ReturnValueError("ReturnValueError","4103"),
	NotReturned("NotReturned","4104"),
	ReturnEmpty("ReturnEmpty","4105"),
	
	/*5.X-50*/
	RequestGeneral("RequestGeneral","5000"),
	Requested("Requested","5001"),
	NotRequested("NotRequested","5002"),
	RequestError("RequestError","5003"),
	RequestLinkDown("RequestLinkDown","5004"),
	RequestConnectDown("RequestConnectDown","5005"),
	RequestTransBreak("RequestTransBreak","5006"),
	RequestBeClosed("RequestBeClosed","5007"),
	
	/*5.X-51*/
	ReceiveGeneral("ReceiveGeneral","5100"),
	Received("Received","5101"),
	NotReceived("NotReceived","5102"),
	ReceiveError("ReceiveError","5103"),
	ReceiveDataError("ReceiveDataError","5104"),
	ReceiveLinkDown("ReceiveLinkDown","5105"),
	ReceiveTransBreak("ReceiveTransBreak","5106"),
	ReceiveBeClosed("ReceiveBeClosed","5107"),
	
	/*5.X-52*/
	ResponseGeneral("ResponseGeneral","5200"),
	Responsed("Responsed","5201"),
	NotResponsed("NotResponsed","5202"),
	ResponseError("ResponseError","5203"),
	ResponseLinkDown("ResponseLinkDown","5204"),
	ResponseTransBreak("ResponseTransBreak","5205"),
	ResponseBeClosed("ResponseBeClosed","5206"),
	
	/*6.X-60*/
	AbortGeneral("AbortGeneral","6000"),
	Aborted("Aborted","6001"),
	BeBroken("BeBroken","6002"),
	AbortError("AbortError","6003"),
	
	/*6.X-61*/
	StartGeneral("StartGeneral","6100"),
	Started("Started","6101"),
	NotStarted("NotStarted","6102"),
	StartError("StartError","6103"),
	
	/*6.X-62*/
	StopGeneral("StopGeneral","6200"),
	Stopped("Stopped","6201"),
	NotStopped("NotStopped","6202"),
	StopError("StopError","6203"),
	
	/*6.X-63*/
	FinishGeneral("FinishGeneral","6300"),
	Finished("Finished","6301"),
	NotFinished("NotFinished","6302"),
	FinishUnClean("FinishUnClean","6303"),
	
	/*6.X-64*/
	StatusRunning("StatusRunning","6400"),
	StatusUnknow("StatusUnknow","6401"),
	
	/*6.X-65*/
	ExecuteGeneral("ExecuteGeneral","6500"),
	ExecuteError("ExecuteError","6501"),
	ExecuteSuccess("ExecuteSuccess","6502"),
	ExecuteExternally("ExecuteExternally","6503"),
	ExecuteAcquireLock("ExecuteAcquireLock","6504"),
	ExecuteReleaseLock("ExecuteReleaseLock","6505"),
	
	/*7.X-70*/
	MessageGeneral("MessageGeneral","7000"),
	MessageGeneralError("MessageGeneralError","7001"),
	MessageSent("MessageSent","7002"),
	MessageReceived("MessageReceived","7003"),
	MessageSubscribe("MessageSubscribe","7004"),
	MessageSubscribeError("MessageSubscribeError","7005"),
	MessageFetching("MessageFetching","7006"),
	MessageSendError("MessageSendError","7007"),
	MessageReceiveError("MessageReceiveError","7008"),
	MessageProcess("MessageProcess","7009"),
	MessageIgnore("MessageIgnore","7010"),
	MessageDispatch("MessageDispatch","7011"),
	MessageForward("MessageForward","7012"),
	
	/*7.X-71*/
	UserGeneral("UserGeneral","7100"),
	UserGeneralError("UserGeneralError","7101"),
	UserRegister("UserRegister","7102"),
	UserInfoBind("UserInfoBind","7103"),
	UserInfoChange("UserInfoChange","7104"),
	UserAuthChange("UserAuthChange","7105"),
	UserErase("UserErase","7106"),
	UserDisable("UserDisable","7107"),
	
	/*7.X-72*/
	AuthGeneral("AuthGeneral","7200"),
	AuthGeneralError("AuthGeneralError","7201"),
	AuthCheck("AuthCheck","7202"),
	AuthPass("AuthPass","7203"),
	AuthFail("AuthFail","7204"),
	AuthChange("AuthChange","7205"),
	
	/*7.X-73*/
	FinanceGeneral("FinanceGeneral","7300"),
	FinanceGeneralError("FinanceGeneralError","7301"),
	FinanceRechange("FinanceRechange","7302"),
	FinanceConsume("FinanceConsume","7303"),
	FinanceBet("FinanceBet","7304"),
	FinanceWin("FinanceWin","7305"),
	FinanceTransfer("FinanceTransfer","7306"),
	FinanceCashout("FinanceCashout","7307"),
	FinanceAdjust("FinanceAdjust","7308"),
	FinanceLock("FinanceLock","7309"),
	FinanceUnLock("FinanceUnLock","7310"),
	FinanceAudit("FinanceAudit","7311"),
	FinanceRechangeError("FinanceRechangeError","7312"),
	FinanceConsumeError("FinanceConsumeError","7313"),
	FinanceBetError("FinanceBetError","7314"),
	FinanceWinError("FinanceWinError","7315"),
	FinanceTransferError("FinanceTransferError","7316"),
	FinanceCashoutError("FinanceCashoutError","7317"),
	FinanceAdjustError("FinanceAdjustError","7318"),
	FinanceLockError("FinanceLockError","7319"),
	FinanceUnLockError("FinanceUnLockError","7320"),
	FinanceAuditFail("FinanceAuditFail","7321"),
	
	/*7.X-74*/
	MediaGeneral("MediaGeneral","7400"),
	MediaPlayback("MediaPlayback","7401"),
	MediaShow("MediaShow","7402"),
	MediaTransform("MediaTransform","7403"),
	MediaUpload("MediaUpload","7404"),
	MediaDownload("MediaDownload","7405"),
	MediaProcess("MediaProcess","7406"),
	MediaEncrypt("MediaEncrypt","7407"),
	MediaDecrypt("MediaDecrypt","7408"),
	MediaPushCDN("MediaPushCDN","7409"),
	MediaImport("MediaImport","7410"),
	MediaTransport("MediaTransport","7411"),
	MediaGeneralError("MediaGeneralError","7412"),
	MediaPlaybackError("MediaPlaybackError","7413"),
	MediaShowError("MediaShowError","7414"),
	MediaTransformError("MediaTransformError","7415"),
	MediaUploadError("MediaUploadError","7416"),
	MediaDownloadError("MediaDownloadError","7417"),
	MediaProcessError("MediaProcessError","7418"),
	MediaEncryptError("MediaEncryptError","7419"),
	MediaDecryptError("MediaDecryptError","7420"),
	MediaPushCDNError("MediaPushCDNError","7421"),
	MediaImportError("MediaImportError","7422"),
	MediaTransportError("MediaTransportError","7423"),
	
	/*0.X-00*/
	OtherGeneral("OtherGeneral","0000"),
	OtherUnknow("OtherUnknow","0001"),
	OtherUnClassified("OtherUnClassified","0002"),
	
	/*0.X-01*/
	ExceptionGeneral("ExceptionGeneral","0100"),
	ExceptionCode("ExceptionCode","0101"),
	ExceptionLocalCall("ExceptionLocalCall","0102"),
	ExceptionRemoteCall("ExceptionRemoteCall","0103"),
	ExceptionParseData("ExceptionParseData","0104"),
	ExceptionRuntime("ExceptionRuntime","0105"),
	ExceptionOS("ExceptionOS","0106"),
	ExceptionUnknow("ExceptionUnknow","0107"),
	ExceptionUnClassified("ExceptionUnClassified","0108"),
	
	/*0.X-02*/
	TimeoutGeneral("TimeoutGeneral","0200"),
	TimeoutLocalCall("TimeoutLocalCall","0201"),
	TimeoutRemoteCall("TimeoutRemoteCall","0202"),
	TimeoutWaitCondition("TimeoutWaitCondition","0203"),
	TimeoutSystemLimit("TimeoutSystemLimit","0204"),
	
	/*0.X-03*/
	NoticeGeneral("NoticeGeneral","0300"),
	NoticeDev("NoticeDev","0301"),
	NoticeOps("NoticeOps","0302"),
	NoticeSrv("NoticeSrv","0303"),
	NoticeBiz("NoticeBiz","0304"),
	NoticeMgr("NoticeMgr","0305"),
	
	/*0.X-04*/
	ScheduleGeneral("ScheduleGeneral","0400"),
	ScheduleDelay("ScheduleDelay","0401"),
	ScheduleInQueue("ScheduleInQueue","0402"),
	ScheduleFixedTime("ScheduleFixedTime","0403"),
	ScheduleLooped("ScheduleLooped","0404")
	;
	
	public String code;
	public String name;
	private Event(String name,String code){
		this.code=code;
		this.name=name;
	}
	
	public String getEventTypeCode(){
		return getEventType().code;
	}
	
	public String getEventTypeName(){
		return getEventType().name;
	}
	
	public EventType getEventType(){
		String code=this.code.substring(0, 2);
		for(EventType eventType:EventType.values()){
			if(eventType.code.equals(code)) return eventType;
		}
		return null;
	}
	
	/**
	 * @author Lixiang
	 * @description 事件类型字典表
	 */
	public enum EventType{
		Fail("Fail","10"),
		Pass("Pass","11"),
		Enable("Enable","12"),
		Disable("Disable","13"),
		
		Perform("Perform","20"),
		Confirm("Confirm","21"),
		Reject("Reject","22"),
		Valid("Valid","23"),
		Invalid("Invalid","24"),
		
		Cache("Cache","30"),
		Add("Add","31"),
		Change("Change","32"),
		Remove("Remove","33"),
		Query("Query","34"),
		Lock("Lock","35"),
		Release("Release","36"),
		
		Call("Call","40"),
		Return("Return","41"),
		
		Request("Request","50"),
		Receive("Receive","51"),
		Response("Response","52"),
		
		Abort("Abort","60"),
		Start("Start","61"),
		Stop("Stop","62"),
		Finish("Finish","63"),
		Status("Status","64"),
		Execute("Execute","65"),
		
		Message("Message","70"),
		User("User","71"),
		Auth("Auth","72"),
		Finance("Finance","73"),
		Media("Media","74"),
		
		Other("Other","00"),
		Exception("Exception","01"),
		Timeout("Timeout","02"),
		Notice("Notice","03"),
		Schedule("Schedule","04")
		;
		
		public String code;
		public String name;
		private EventType(String name,String code){
			this.code=code;
			this.name=name;
		}
		
		public String getTypeCode(){
			return getType().code;
		}
		
		public String getTypeName(){
			return getType().name;
		}
		
		public Type getType(){
			String code=this.code.substring(0, 1);
			for(Type type:Type.values()){
				if(type.code.equals(code)) return type;
			}
			return null;
		}
		
		/**
		 * @author Lixiang
		 * @description 事件分类字典
		 */
		public enum Type{
			Feature("Feature","1"),
			Operation("Operation","2"),
			Resource("Resource","3"),
			API("API","4"),
			Network("Network","5"),
			Procedure("Procedure","6"),
			Business("Business","7"),
			General("General","0")
			;
			
			public String code;
			public String name;
			private Type(String name,String code){
				this.code=code;
				this.name=name;
			}
		}
	}
}

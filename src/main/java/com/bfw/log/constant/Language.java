package com.bfw.log.constant;

/**
 * @author Lixiang
 * @description 应用开发语言枚举表
 */
public enum Language {

	Java("Java"),
	
	C_Sharp("C#"),
	
	Php("Php"),
	
	C_Add("C/C++"),
	
	Scala("Scala"),
	
	Groovy("Groovy"),
	
	Jython("Jython"),
	
	Fortran("Fortran"),
	
	Erlang("Erlang"),
	
	Ruby("Ruby"),
	
	JRuby("JRuby"),
	
	Objective_C("Objective-C"),
	
	Perl("Perl"),
	
	R("R"),
	
	J_Sharp("J#"),
	
	SQL("Sql"),
	
	Visual_Basic("Visual_Basic"),
	
	Visual_Add("Visual_C++"),
	
	Go("Go"),
	
	GoLang("GoLang"),
	
	VBScript("VBScript"),
	
	Javascript("Javascript"),
	
	ActionScript("ActionScript"),
	
	Python("Python")
	;
	
	private String name;
	
	private Language(String name){
		this.name=name;
	}
}

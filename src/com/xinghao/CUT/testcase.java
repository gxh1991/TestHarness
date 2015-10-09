package com.xinghao.CUT;

import com.xinghao.TestHarnEss.MyAnnotation;


public class testcase {
	public int age;
	private String getPassword() 
	{
		return "test123";
	}
	public int getAge() 
	{
		return age;
	}
	
	public void setAge(
			@MyAnnotation(name="age", type=Integer.class,value="24")  
			int age) 
	{
		this.age = age;
	}
	
	public String getName()
	{
		return "Xinghao Gu";
	}
	
	
	
	public Boolean signIn(
			@MyAnnotation(name="name", type=String.class, value="Xinghao")
			String name,
			//@MyAnnotation(name="password",type=String.class, value="pw123")
			String password)
	{
		return this.getPassword() == password;
	}
}

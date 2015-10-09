package com.xinghao.TestHarnEss;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TestHarnEss {
	public String fileName;
	private Object obj;
	public Method[] methods;
	public Boolean privateAccess = false;
	TestHarnEss(String file) throws InstantiationException, IllegalAccessException{
		this.fileName = file;
		try { 
			Class<?> cls = Class.forName(this.fileName);
			this.obj = cls.newInstance();
			this.methods =  cls.getDeclaredMethods();
		} catch( ClassNotFoundException e ) {
			System.err.println(e.getMessage() + " dose not exist");
			return;
		}
	}
	public void allowPrivateAccess(Boolean b){
		this.privateAccess = b;
	}
	//randomly generate one permutation of the methods
	private Method[] getRandomPermu(){
		Method[] methods = this.methods;
		Random r = new Random();
		int length = methods.length;
		for( int i = 0 ; i < length ; i++ ){
			int ran = i+ r.nextInt(length-i);
			Method temp = methods[i];
			methods[i] = methods[ran];
			methods[ran] = temp;
		}
		return methods;
	}
	//randomly generate one combination of the methods
	private Method[] getRandomComb(Method[] methods){
		//randomly generate one combination of the methods
		Random r = new Random();
		int length = methods.length;
		methods = this.getRandomPermu();
		methods = Arrays.copyOfRange(methods, 0, r.nextInt(length)+1);
		return methods;
	}
	//print
	private static void printNestMethods(Method[] methods,Object[] params){
		int i =0;
		while(i<methods.length){
			System.out.print(methods[i].getName()+"(");
			if(i==methods.length-1){
				//print parameter value
				for(int j=0;j<params.length;j++){
					System.out.print(params[j]);
					if(j!=params.length-1){
			    		  System.out.print(", ");
			    	 }
				}
				for(int j=0;j<methods.length;j++){
					System.out.print(")");
				}
			}
			i++;
		}
		System.out.println();
	}
	//print method info
	private static void printMethod(Method m) {
	      System.out.print(m.getName() + "(");
	      Class<?>[] param = m.getParameterTypes();
	      for (int i = 0;  i < param.length;  i++) {
	    	  System.out.print(param[i].getSimpleName());
	    	  if(i!=param.length-1){
	    		  System.out.print(", ");
	    	  }
	      }
	      System.out.println(")");
	}
	//convert value to object
	//this function is used to convert annotation value to specific data type
	private static Object toObject( Class<?> clazz, String value ) {
	    if( Boolean.class == clazz ) return Boolean.parseBoolean( value );
	    if( Byte.class == clazz ) return Byte.parseByte( value );
	    if( Short.class == clazz ) return Short.parseShort( value );
	    if( Integer.class == clazz ) return Integer.parseInt( value );
	    if( Long.class == clazz ) return Long.parseLong( value );
	    if( Float.class == clazz ) return Float.parseFloat( value );
	    if( Double.class == clazz ) return Double.parseDouble( value );
	    return value;
	}
	//randomly generate a value for test
	//support types: Boolean, Double, Int, Long, Float, String (within 100 characters) 
	private static Object getRandomValue( String type ) {
		Random rand = new Random();
	    if( "boolean" == type ) return rand.nextBoolean();
	    if( "double" == type ) return rand.nextDouble();
	    if( "int" == type ) return rand.nextInt();
	    if( "long" == type ) return rand.nextLong();
	    if( "float" == type ) return rand.nextFloat();
	    if("java.lang.String" == type){
	    	//support character, you can add your char here
	    	String AB = " 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	    	int len = rand.nextInt(100); // max length 100
	    	StringBuilder sb = new StringBuilder(len);
	    	   for( int i = 0; i < len; i++ ) 
	    	      sb.append( AB.charAt( rand.nextInt(AB.length()) ) );
	    	   return sb.toString();
	    }
	    //"Sorry, no test value";
	    return null;
	}
	//generate parameter value for test
	 private static Object[] genParamValue(Method m, Boolean print){
		 // get instance of parameters
		 // get parameter's value from annotation is exist
		 // if annotation doesn't provide, randomly generate one
		 ArrayList<Object> newObj = new ArrayList<Object>();
		 Class<?>[] params = m.getParameterTypes();
	     Annotation[][] parameterAnnotations = m.getParameterAnnotations();
	         //System.out.println(parameterAnnotations);
	         for (int i =0;i<params.length;i++)
	         {	
	        	 Annotation[] parameterAnnotation = parameterAnnotations[i];
	        	 if(parameterAnnotation.length>0){
	        		 //annotation exist
		        	 for (Annotation annotation : parameterAnnotation)
			         {
			            if (annotation instanceof MyAnnotation)
			            {
			            	MyAnnotation param = (MyAnnotation) annotation;
			            	if(print) System.out.println("-Param "+i+": (" + params[i].getName()+") " + param.value());
			            	newObj.add(toObject(param.type(),param.value()));
			            }
			         }
	        	 } else {
	        		 Object value = getRandomValue(params[i].getName());
	        		 if(print) System.out.println("-Param "+i+": (" + params[i].getName() + ") " + value);
	        		 newObj.add(value);
	        	 }
	        	 

	         }
	         return newObj.toArray();
	   }
	 public void nestinvoke() throws InterruptedException{
		 if(this.obj==null){
			return;
		 }
		 Method[] methods= this.getRandomComb(this.methods);
		 int i = methods.length-1;
		 Boolean success = true;
		 //get parameter value from annotation
		 Object[] args = genParamValue(methods[i],false);
		 printNestMethods(methods,args);
			while(i>-1){
				if(this.privateAccess) methods[i].setAccessible(true);
				//run
				try{
					Object result = methods[i].invoke(this.obj,args);
					args = new Object[]{result};
					i--;
				}catch( Exception e )
				{	
					success = false;
					System.err.println("Fail");
					System.err.println("Method: "+methods[i].getName() + "");
					System.err.println("Error Message: " + e.getMessage());
					TimeUnit.MILLISECONDS.sleep(100);
					break;
				}
			}
			if(success) System.out.println("Success");
			System.out.println("+++++++++++++++++++++++++");
	 }
	public void invoke() throws InterruptedException{
		if(this.obj==null){
			return;
		}
		Method[] methods = this.methods;
		int i = 0;
		while(i<methods.length){
			if(this.privateAccess) methods[i].setAccessible(true);
			printMethod(methods[i]);
			//get parameter value from annotation
			Object[] args = genParamValue(methods[i],true);
			//run
			try{
				methods[i].invoke(this.obj,args);
				System.out.println("Success");
				i++;
			}catch( Exception e )
			{	
				System.err.println("Error: " + e.getMessage());
				TimeUnit.MILLISECONDS.sleep(100);
				i++;
			}
			System.out.println("+++++++++++++++++++++++++");
		}
	}
}

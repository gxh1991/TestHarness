package com.xinghao.TestHarnEss;


public class Main {

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException {
		TestHarnEss test;
		if(args.length == 0){
			test = new TestHarnEss("com.xinghao.CUT.testcase");
		}else if(args.length <=2){
			test = new TestHarnEss("com.xinghao.CUT."+args[0]);
			if(args.length == 2){
				if(args[1].equals("t")) {
					test.allowPrivateAccess(true);
				} else {
					test.allowPrivateAccess(false);
				}
				
			}
		} else {
			System.err.println("wrong number of argument");
			return;
		}
		
		test.invoke();
		test.nestinvoke();
	

	}

}

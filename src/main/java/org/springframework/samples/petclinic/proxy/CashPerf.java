package org.springframework.samples.petclinic.proxy;

import org.springframework.util.StopWatch;

public class CashPerf implements Payment{
    // cash 클래스, Store클래스의 수정없이 앞뒤로 성증 측정하는 부분을 넣음.(StoreTest에서 CashPerf를 넣음으로써)
	Payment cash = new Cash();
	@Override
	public void pay(int amount){
		StopWatch stopWatch= new StopWatch();
		stopWatch.start();

		cash.pay(amount);

		stopWatch.stop();
		System.out.println(stopWatch.prettyPrint());
	}

}

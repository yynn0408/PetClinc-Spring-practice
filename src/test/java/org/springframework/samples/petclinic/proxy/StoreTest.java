package org.springframework.samples.petclinic.proxy;

import org.junit.jupiter.api.Test;

public class StoreTest {
	@Test
	public void testPay(){
		Payment cashPerf=new CashPerf(); //(StoreTest에서 CashPerf를 넣음으로써 프록시 코드를 읽도록 함.) : AOP
		Store store=new Store(cashPerf);
		store.buySomething(100);
	}
}

package org.springframework.samples.petclinic.owner;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;


@Component //bean으로 등록이 되어야함
@Aspect // AOP를 실제구현한 aspect
public class LogAspect {
	//성능을 측정할 looger
	Logger logger= LoggerFactory.getLogger(LogAspect.class);

	@Around("@annotation(LogExecutionTime)") //joinpoint 를 받기위한 annotation. 다시말하면 targetmetchod(@LogExecutionTime 이 붙은 method)를 인터페이스로 받아옴.
	public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable{
		StopWatch stopWatch=new StopWatch();
		stopWatch.start();

		Object proceed=joinPoint.proceed();

		stopWatch.stop();
		logger.info(stopWatch.prettyPrint());
		return proceed;
	}

}

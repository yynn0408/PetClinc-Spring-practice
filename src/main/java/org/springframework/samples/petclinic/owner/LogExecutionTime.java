package org.springframework.samples.petclinic.owner;
import org.hibernate.validator.cfg.defs.pl.REGONDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target; // 이 annotation을 어디에 쓸건지 정하기 위함.

@Target(ElementType.METHOD) // 함수에 적용되는 annotation
@Retention(RetentionPolicy.RUNTIME) // retention : 이 annotation을 얼마나 유지할거냐?? runtime까지
public @interface LogExecutionTime {
}

/**
 * 여기서는 annotation을 만들기만 한것이다. 실제로는 아무일도 일어나지 않는다.
 * 따라서 실제 하고자하는 일 (Aspect)를 따로 만들어줘야함.
 * */

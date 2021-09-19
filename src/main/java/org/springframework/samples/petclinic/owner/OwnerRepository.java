/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository class for <code>Owner</code> domain objects All method names are compliant
 * with Spring Data naming conventions so this interface can easily be extended for Spring
 * Data. See:
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 */
public interface OwnerRepository extends Repository<Owner, Integer> {

	/**
	 * Retrieve {@link Owner}s from the data store by last name, returning all owners
	 * whose last name <i>starts</i> with the given name.
	 * @param lastName Value to search for
	 * @return a Collection of matching {@link Owner}s (or an empty Collection if none
	 * found)
	 */
	@Query("SELECT DISTINCT owner FROM Owner owner left join fetch owner.pets WHERE owner.lastName LIKE :lastName%")
	@Transactional(readOnly = true)
	Collection<Owner> findByLastName(@Param("lastName") String lastName);

	/**Find Owner by first name*/
	@Query("SELECT DISTINCT owner FROM Owner owner left join fetch owner.pets WHERE owner.firstName LIKE %:firstName%")
	@Transactional(readOnly = true)
	Collection<Owner> findByFirstName(@Param("firstName") String firstName);
    /**AOP (Aspect Oriented Programming)
	 * 비슷한 일을 하는 애들 묶어서 코드의 가독성, 오류 발생가능성을 최소화
	 * AOP 가 적용되지 않은 예시
	 * class A {
	 *     method a() {
	 *         AAAA
	 *         method a가 하는 일들
	 *         BBBB
	 *     }
	 *     method b() {
	 *         AAAA
	 *         method b가 하는 일들
	 *         BBBB
	 *     }
	 * }
	 * class B {
	 *     method c() {
	 *         AAAA
	 *         method c가 하는 일들
	 *         BBBB
	 *     }
	 * }
	 * Spring에서는 프록시패턴을 이용하여 AOP를 지원해준다.
	 * 프록시패턴이란 ?
	 *  : 기존 코드의 수정이 없다. 기존에 사용하던 클래스를 가져와 추가 기능을 구현한 새로운 클래스를 만든다.
	 *    본 예제의 proxy 패키지 참고.
	 *
	 * Spring AOP에서는 bin이 등록될 때 자동으로 지원해준다. 즉, bin을 등록할 때 내가 설정해준 애들만 bin이 등록되는 것이 아니라,
	 * 프록시 클래스들도 같이 등록된다.
	 * 그렇다면 프록시인지 아닌지는 어떻게 아는가? annotation을 통해!
	 * ex. @Transactional (본 prj), @LogExecutionTime (직접만든 annotation)
	 *
	 * */

	/**
	 * Retrieve an {@link Owner} from the data store by id.
	 * @param id the id to search for
	 * @return the {@link Owner} if found
	 */
	@Query("SELECT owner FROM Owner owner left join fetch owner.pets WHERE owner.id =:id")
	@Transactional(readOnly = true)
	Owner findById(@Param("id") Integer id);

	/**
	 * Save an {@link Owner} to the data store, either inserting or updating it.
	 * @param owner the {@link Owner} to save
	 */
	void save(Owner owner);

}

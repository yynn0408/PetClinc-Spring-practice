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

import org.springframework.samples.petclinic.visit.VisitRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 */
@Controller
class OwnerController {

	private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";

	private final OwnerRepository owners;
	private final PetRepository pets;//final 쓰는 이유 : 재정의 막기 위해. 혹시 다른 함수가 override 하면 문제 생김.
	private VisitRepository visits;

	public OwnerController(OwnerRepository clinicService, PetRepository pets, VisitRepository visits) {
		this.owners = clinicService;
		this.pets = pets;
		this.visits = visits;
	}//직접 bean을 꺼내서 대응 시키는 방법 . 이 방법 외에도 멤버변수에 직접 @Autowired를 쓰는 방법도 있음.


	/**1. IOC : Inversion of Control(제어의 역전)
	 *  Owner database에 접근하는 OwnerRopository를 아래와 같이 OwnerController에서 만들수도 있지만 그렇게 하지 앟음.
	 *  대신 외부에서 객체를 생성하여 생성자에서 넘겨줌. => 코드의 의존성을 낮추고 재활용성을 높임. 디버깅도 쉬움.
	 *  참고 자료 : https://youtu.be/zmdWWujU8M4
	 *   [아래]
	 *  private final OwnerRepository = new OwnerRepository를 상속하는 클래스(~);
	 *
	 *
	 *  이 코드에서는 OwnerControllerTests의 Spring framework에서 IOC Container가 자동으로 객체 생성후 주입.
	 *  by @MockBean annotation(spring이 test를 만들때 자동으로 해당 type의 인스턴스를 만든 후 bean으로 만듬.)
	 *  bean : 스프링이 관리하는 객체
	 *  bean 주입받는 방식 1) Autowired 2) setter 3) 생성자
	 *
	 *  2. IOC Container :Application Context (Bean Factory)
	 *  역할 : bean을 만들고 의존성을 엮어주고 제공
	 *  ex : OwnerController, OwnerRepository, PetController, PetRepository 는 모두 bean으로 등록되어있음.
	 *  bean을 만드는 방법
	 *   1) component scan (component annotation) : component annotation 이 있는 애들을 다 bean으로 등록
	 *      ex. @Component , Repository(Repository 를 상속받는 객체) , @Service, @Controller, @Configuration, .. @정의할수도있음
	 *          @componentscan : 찾을 범위
	 *   2) Bean으로 직접 등록
	 *      : java Config 파일에 직접 @Bean annotation을 붙인 후 return new 객체
	 *      이 프로젝트에서는 system 의 cacheConfigurateion 클래스
	 *  bean을 꺼내서 쓰는 방법
	 *  1) applicationContext의  getBean 함수 (생성자를 통해 직접 대입)
	 *  2) @Autowired (생성자, field, setter등에 붙임) : 생성자에 붙이는 것을 권장
	 *
	 *  bean 객체를 하나 만들어 application 전반에 걸쳐서 재사용함 즉, applicationContext에서 관리
	 *  이 코드에서는 OwnerRepository 객체를 하나 만들어서 application전반에서 재사용 : Singleton scope 객체라고 부름 (owner를 동시에 update할 일은 없으니 괜춘.)
	 *  -> 멀티스레드에서 singleton 객체 만드는것이 굉장히 불편하지만 ioc container에서 가져다 쓰면 편함.
	 *  참고 : https://hongku.tistory.com/107
	 *
	 * */

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@GetMapping("/owners/new")
	@LogExecutionTime
	public String initCreationForm(Map<String, Object> model) {
		Owner owner = new Owner();
		model.put("owner", owner);
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/new")
	@LogExecutionTime
	public String processCreationForm(@Valid Owner owner, BindingResult result) {
		if (result.hasErrors()) {
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}
		else {
			this.owners.save(owner);
			return "redirect:/owners/" + owner.getId();
		}
	}

	@GetMapping("/owners/find")
	@LogExecutionTime
	public String initFindForm(Map<String, Object> model) {
		model.put("owner", new Owner());
		return "owners/findOwners";
	}

	@GetMapping("/owners")
	@LogExecutionTime
	public String processFindForm(Owner owner, BindingResult result, Map<String, Object> model) {

		// allow parameterless GET request for /owners to return all records
		if (owner.getFirstName() == null) {
			owner.setFirstName(""); // empty string signifies broadest possible search
		}

		// find owners by first name
		Collection<Owner> results = this.owners.findByFirstName(owner.getFirstName());
		if (results.isEmpty()) {
			// no owners found
			result.rejectValue("firstName", "notFound", "not found");
			return "owners/findOwners";
		}
		else if (results.size() == 1) {
			// 1 owner found
			owner = results.iterator().next();
			return "redirect:/owners/" + owner.getId();
		}
		else {
			// multiple owners found
			model.put("selections", results);
			return "owners/ownersList";
		}
	}

	@GetMapping("/owners/{ownerId}/edit")
	@LogExecutionTime
	public String initUpdateOwnerForm(@PathVariable("ownerId") int ownerId, Model model) {
		Owner owner = this.owners.findById(ownerId);
		model.addAttribute(owner);
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/{ownerId}/edit")
	@LogExecutionTime
	public String processUpdateOwnerForm(@Valid Owner owner, BindingResult result,
			@PathVariable("ownerId") int ownerId) {
		if (result.hasErrors()) {
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}
		else {
			owner.setId(ownerId);
			this.owners.save(owner);
			return "redirect:/owners/{ownerId}";
		}
	}

	/**
	 * Custom handler for displaying an owner.
	 * @param ownerId the ID of the owner to display
	 * @return a ModelMap with the model attributes for the view
	 */
	@GetMapping("/owners/{ownerId}")
	@LogExecutionTime
	public ModelAndView showOwner(@PathVariable("ownerId") int ownerId) {
		ModelAndView mav = new ModelAndView("owners/ownerDetails");
		Owner owner = this.owners.findById(ownerId);
		for (Pet pet : owner.getPets()) {
			pet.setVisitsInternal(visits.findByPetId(pet.getId()));
		}
		mav.addObject(owner);
		return mav;
	}

}

package com.techdot.techdot;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.techdot.techdot.modules.member.Member;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packagesOf = App.class)
public class LayerDependencyTests {

	@ArchTest
	ArchRule controllerClassRule = classes().that().haveSimpleNameEndingWith("Controller")
		.and().areAssignableFrom(Member.class) // Login User
		.should().accessClassesThat().haveSimpleNameEndingWith("Service")
		.allowEmptyShould(true);

	@ArchTest
	ArchRule serviceClassRule = noClasses().that().haveSimpleNameEndingWith("Service")
		.should().accessClassesThat().haveSimpleNameEndingWith("Controller");

	@ArchTest
	ArchRule repositoryClassRule = noClasses().that().haveSimpleNameEndingWith("Repository")
		.should().accessClassesThat().haveSimpleNameEndingWith("Service");
}

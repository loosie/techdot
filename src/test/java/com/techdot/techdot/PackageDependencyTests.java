package com.techdot.techdot;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packagesOf = App.class)
public class PackageDependencyTests {

	private static final String MEMBER = "..modules.member..";
	private static final String POST = "..modules.post..";
	private static final String INTEREST = "..modules.interest..";
	private static final String LIKE = "..modules.like..";
	private static final String MAIN = "..modules.main..";

	@ArchTest
	ArchRule modulesPackageRule = classes().that().resideInAPackage("com.techdot.techdot.modules..")
		.should().onlyBeAccessed().byClassesThat()
		.resideInAnyPackage("com.techdot.techdot.modules..");

	@ArchTest
	ArchRule memberPackageRule = classes().that().resideInAPackage(MEMBER)
		.should().accessClassesThat().resideInAnyPackage(MEMBER)
		.andShould().onlyBeAccessed().byAnyPackage(MEMBER, MAIN, POST, LIKE, INTEREST);

	@ArchTest
	ArchRule postPackageRule = classes().that().resideInAPackage(POST)
		.should().accessClassesThat().resideInAnyPackage(POST, MEMBER, LIKE, INTEREST)
		.andShould().onlyBeAccessed().byClassesThat()
		.resideInAnyPackage(POST, MAIN, LIKE, INTEREST, MEMBER); // MEMBER (only ADMIN)

	@ArchTest
	ArchRule likePackageRule = classes().that().resideInAPackage(LIKE)
		.should().accessClassesThat().resideInAnyPackage(LIKE, MEMBER, POST)
		.andShould().onlyBeAccessed().byClassesThat().resideInAnyPackage(LIKE, POST);

	@ArchTest
	ArchRule interestPackageRule = classes().that().resideInAPackage(INTEREST)
		.should().accessClassesThat().resideInAnyPackage(INTEREST, MEMBER, POST)
		.andShould().onlyBeAccessed().byClassesThat().resideInAnyPackage(INTEREST, POST);

}

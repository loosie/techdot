package com.techdot.techdot;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.*;

import com.techdot.techdot.modules.member.Member;
import com.techdot.techdot.modules.member.MemberService;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packagesOf = App.class)
public class PackageDependencyTests {

	private static final String MEMBER = "..modules.member..";
	private static final String POST = "..modules.post..";
	private static final String INTEREST = "..modules.interest..";
	private static final String LIKE = "..modules.like..";
	private static final String CATEGORY = "..modules.category..";
	private static final String MAIN = "..modules.main..";

	@ArchTest
	ArchRule modulesPackageRule = classes().that().resideInAPackage("com.techdot.techdot.modules..")
		.should().onlyBeAccessed().byClassesThat()
		.resideInAnyPackage("com.techdot.techdot.modules..");

	@ArchTest
	ArchRule memberPackageRule = classes().that().resideInAPackage(MEMBER)
		.should().accessClassesThat().resideInAnyPackage(MEMBER)
		.andShould().onlyBeAccessed().byAnyPackage(MAIN, MEMBER, POST, LIKE, INTEREST);

	@ArchTest
	ArchRule postPackageRule = classes().that().resideInAPackage(POST)
		.should().accessClassesThat().resideInAnyPackage(POST, LIKE, CATEGORY, MEMBER)
		.andShould().onlyBeAccessed().byClassesThat()
		.resideInAnyPackage(MAIN, POST, LIKE, MEMBER); // MEMBER (only ADMIN)

	@ArchTest
	ArchRule categoryPackageRule = classes().that().resideInAPackage(CATEGORY)
		.should().accessClassesThat().resideInAnyPackage(CATEGORY, INTEREST)
		.andShould().onlyBeAccessed().byClassesThat().resideInAnyPackage(MAIN, CATEGORY, POST, INTEREST);

	@ArchTest
	ArchRule likePackageRule = classes().that().resideInAPackage(LIKE)
		.should().accessClassesThat().resideInAnyPackage(LIKE, MEMBER, POST)
		.andShould().onlyBeAccessed().byClassesThat().resideInAnyPackage(LIKE, POST, MEMBER); // Member 회원 탈퇴

	@ArchTest
	ArchRule interestPackageRule = classes().that().resideInAPackage(INTEREST)
		.should().accessClassesThat().resideInAnyPackage(INTEREST, CATEGORY, MEMBER)
		.andShould().onlyBeAccessed().byClassesThat().resideInAnyPackage(INTEREST, CATEGORY, POST, MEMBER); // POST 복잡한 r (findAllDtoByInterestsMemberId), Member 회원 탈퇴

	@ArchTest
	ArchRule freeOfCycles = slices().matching("..techdot.(*)..")
		.should().beFreeOfCycles();
}

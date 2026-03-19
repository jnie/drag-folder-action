package dk.jnie.dragfolder.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "dk.jnie.dragfolder..", importOptions = ImportOption.DoNotIncludeTests.class)
public class ModuleDependencyRules {

    @ArchTest
    public static final ArchRule domainModuleShouldNotDependOnOtherModules =
            noClasses()
                    .that(resideInAPackage("dk.jnie.dragfolder.domain.."))
                    .should()
                    .dependOnClassesThat(resideInAnyPackage(
                            "dk.jnie.dragfolder.application..",
                            "dk.jnie.dragfolder.service..",
                            "dk.jnie.dragfolder.inbound..",
                            "dk.jnie.dragfolder.outbound.."
                    ))
                    .because("Domain module should not depend on any other application module");

    @ArchTest
    public static final ArchRule serviceModuleShouldOnlyDependOnDomain =
            noClasses()
                    .that(resideInAPackage("dk.jnie.dragfolder.service.."))
                    .should()
                    .dependOnClassesThat(resideInAnyPackage(
                            "dk.jnie.dragfolder.application..",
                            "dk.jnie.dragfolder.inbound..",
                            "dk.jnie.dragfolder.outbound.."
                    ))
                    .because("Service module should only depend on domain");

    @ArchTest
    public static final ArchRule inboundModuleShouldOnlyDependOnDomain =
            noClasses()
                    .that(resideInAPackage("dk.jnie.dragfolder.inbound.."))
                    .should()
                    .dependOnClassesThat(resideInAnyPackage(
                            "dk.jnie.dragfolder.application..",
                            "dk.jnie.dragfolder.service..",
                            "dk.jnie.dragfolder.outbound.."
                    ))
                    .because("Inbound module should only depend on domain");

    @ArchTest
    public static final ArchRule outboundModuleShouldOnlyDependOnDomain =
            noClasses()
                    .that(resideInAPackage("dk.jnie.dragfolder.outbound.."))
                    .should()
                    .dependOnClassesThat(resideInAnyPackage(
                            "dk.jnie.dragfolder.application..",
                            "dk.jnie.dragfolder.service..",
                            "dk.jnie.dragfolder.inbound.."
                    ))
                    .because("Outbound module should only depend on domain");

    @ArchTest
    public static final ArchRule noModuleShouldDependOnArchitectureTests =
            noClasses()
                    .that(resideInAnyPackage(
                            "dk.jnie.dragfolder.domain..",
                            "dk.jnie.dragfolder.application..",
                            "dk.jnie.dragfolder.service..",
                            "dk.jnie.dragfolder.inbound..",
                            "dk.jnie.dragfolder.outbound.."
                    ))
                    .should()
                    .dependOnClassesThat(resideInAPackage("dk.jnie.dragfolder.architecture.."))
                    .because("No application module should depend on architecture-tests");
}
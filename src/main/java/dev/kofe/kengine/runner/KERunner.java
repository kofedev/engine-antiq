package dev.kofe.kengine.runner;

import dev.kofe.kengine.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.io.*;
import java.util.Arrays;

import static dev.kofe.kengine.constant.KEConstant.*;

@Component
public class KERunner implements CommandLineRunner {

    private final LanguageService languageService;
    private final StaffService staffService;
    private final CategoryService categoryService;
    private final RoleService roleService;
    private final LanguageExcelService languageExcelService;
    @Value("${engine.base.initial.language.file}") String initialLanguageFile;

    public KERunner(LanguageService languageService,
                    LanguageExcelService languageExcelService,
                    StaffService staffService,
                    CategoryService categoryService,
                    RoleService roleService) {
        this.languageService = languageService;
        this.staffService = staffService;
        this.categoryService = categoryService;
        this.roleService = roleService;
        this.languageExcelService = languageExcelService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (goingToInitialization()) {
            createRoles();
            staffService.createInitialAdmin();
            languageService.createInitialLanguage();
            categoryService.initialCreateRootCategory();

            // LOAD INITIAL UI-ELEMENTS FROM EXCEL

                FileInputStream is = new FileInputStream(initialLanguageFile);
                languageExcelService.importLanguageViaExcel1(is);

            System.out.println("Done");

        }
    }

    private boolean goingToInitialization () {
        return (roleService.count() > 0 ) ? false : true;
    }

    private void createRoles() {
        Arrays.asList(ARRAY_ROLES).forEach(role -> roleService.createRole(role));
    }

}

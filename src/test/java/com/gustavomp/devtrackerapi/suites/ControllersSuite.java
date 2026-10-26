package com.gustavomp.devtrackerapi.suites;

import com.gustavomp.devtrackerapi.controllers.ClientControllerTest;
import com.gustavomp.devtrackerapi.controllers.ProjectControllerTest;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Controllers")
@SelectClasses({
        ClientControllerTest.class,
        ProjectControllerTest.class,
})
@IncludeClassNamePatterns(".*")
public class ControllersSuite {}

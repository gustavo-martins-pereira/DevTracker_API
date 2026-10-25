package com.gustavomp.devtrackerapi.suites;

import org.junit.platform.suite.api.*;

@Suite
@SuiteDisplayName("All Tests")
@SelectClasses({
        ControllersSuite.class,
        ServicesSuite.class
})
@IncludeClassNamePatterns(".*")
public class AllTestsSuite {}
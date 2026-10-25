package com.gustavomp.devtrackerapi.suites;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Services")
@SelectClasses({
        ClientServicesSuite.class,
        FinancialTransactionServicesSuite.class,
        ProjectServicesSuite.class,
})
@IncludeClassNamePatterns(".*")
public class ServicesSuite {}

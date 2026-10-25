package com.gustavomp.devtrackerapi.suites;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Financial Transaction")
@SelectPackages("com.gustavomp.devtrackerapi.services.financialtransaction")
@IncludeClassNamePatterns(".*Test")
public class FinancialTransactionServicesSuite {}

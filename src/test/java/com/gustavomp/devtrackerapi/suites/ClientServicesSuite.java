package com.gustavomp.devtrackerapi.suites;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Client")
@SelectPackages("com.gustavomp.devtrackerapi.services.client")
@IncludeClassNamePatterns(".*Test")
public class ClientServicesSuite {}

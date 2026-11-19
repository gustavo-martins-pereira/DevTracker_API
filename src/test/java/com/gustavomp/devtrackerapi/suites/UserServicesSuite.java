package com.gustavomp.devtrackerapi.suites;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("User")
@SelectPackages("com.gustavomp.devtrackerapi.services.user")
@IncludeClassNamePatterns(".*Test")
public class UserServicesSuite {}

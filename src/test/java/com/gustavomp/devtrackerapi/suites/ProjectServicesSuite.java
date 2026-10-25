package com.gustavomp.devtrackerapi.suites;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Project")
@SelectPackages("com.gustavomp.devtrackerapi.services.project")
@IncludeClassNamePatterns(".*Test")
public class ProjectServicesSuite {}

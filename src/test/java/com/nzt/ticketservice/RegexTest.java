package com.nzt.ticketservice;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Pattern;

public class RegexTest {

    private static final String REGEX_PATTERN = "\\S*@[A-za-z0-9]*.com$";


    @Test
    public void testRegex() {

        final String email = "joe@gmail.com";
        Assert.assertTrue(Pattern.matches(REGEX_PATTERN,email));

    }
}

/*
 * Copyright 2013 Qunar.com All right reserved. This software is the confidential and proprietary information of
 * Qunar.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with Qunar.com.
 */
package com.fun.util;

import com.google.common.base.Joiner;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.*;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;

/**
 * 实现描述：perl5正则工具类
 *
 * @author: reeboo
 * @since: 2016-08-16 19:25
 */
public class PatternUtil {

    private static LoadingCache<String, Pattern> patterns = CacheBuilder.newBuilder().maximumSize(10000)
            .build(new CacheLoader<String, Pattern>() {
                @Override
                public Pattern load(String perl5RegExp) throws Exception {
                    Pattern compiledPattern;
                    Perl5Compiler compiler = new Perl5Compiler();
                    try {
                        compiledPattern = compiler.compile(perl5RegExp, Perl5Compiler.CASE_INSENSITIVE_MASK
                                | Perl5Compiler.READ_ONLY_MASK | Perl5Compiler.SINGLELINE_MASK);
                    } catch (MalformedPatternException mpe) {
                        throw new IllegalArgumentException("Malformed regular expression: " + perl5RegExp);
                    }
                    return compiledPattern;
                }
            });

    public static void clear() {
        PatternUtil.patterns.cleanUp();
    }

    public static Pattern compilePattern(String perl5RegExp) {
        Pattern compiledPattern = null;
        try {
            compiledPattern = PatternUtil.patterns.get(perl5RegExp);
        } catch (ExecutionException e) {
            throw new IllegalArgumentException("Malformed regular expression: " + perl5RegExp);
        }
        return compiledPattern;
    }

    public static String getMatchString(String perl5RegExp, String content, int groupIndex) {
        List<String> matchGroups = PatternUtil.listMatchGroups(perl5RegExp, content);
        if (groupIndex > 0 && groupIndex < matchGroups.size()) {
            return matchGroups.get(groupIndex);
        }
        return StringUtils.EMPTY;
    }

    public static boolean isMatch(String content, String perl5RegExp) {
        Pattern pattern = PatternUtil.compilePattern(perl5RegExp);
        PatternMatcher matcher = new Perl5Matcher();
        return matcher.matches(content, pattern);
    }

    public static List<String> listMatchGroups(String perl5RegExp, String content) {
        List<String> matchGroups = Lists.newArrayList();
        Pattern compiledPattern = PatternUtil.compilePattern(perl5RegExp);
        PatternMatcher matcher = new Perl5Matcher();
        if (matcher.matches(content, compiledPattern)) {
            for (int i = 0; i < matcher.getMatch().groups(); i++) {
                matchGroups.add(matcher.getMatch().group(i));
            }
        }
        return matchGroups;
    }

}

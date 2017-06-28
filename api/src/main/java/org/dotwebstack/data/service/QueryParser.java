package org.dotwebstack.data.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

/**
 * Created by Rick Fleuren on 6/16/2017.
 */
public class QueryParser {
    private static Pattern pattern = Pattern.compile("@(.+?)@");

    public static List<String> getParametersFromQuery(String query) {
        List<String> result = new ArrayList<>();
        Matcher matcher = pattern.matcher(query);

        while (matcher.find()) {
            result.add(matcher.group(1));
        }

        return result.stream().distinct().collect(toList());
    }

}

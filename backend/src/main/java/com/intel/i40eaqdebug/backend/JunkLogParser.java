package com.intel.i40eaqdebug.backend;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by andrew on 1/31/16.
 */
public class JunkLogParser {
    public JunkLogParser() {
    }

    // all regex patterns are static final to avoid altering it
    // matches anything that doesn't start as [ numbers(.|;|*)numbers]
    // (i40e)
    static final Pattern junkFilter =
            Pattern.compile("(Jan)+");
    // ^(?:(?!(\[\d{2,}.\d{2,}\]) i40e).)+"
    public static String [] parserJunk(String [] args){
        int index = 0;
        int end = args.length;
        for(index = 0; index < end; index++) {
            Matcher match = junkFilter.matcher(args[index]);
            match.replaceAll(" ");
        }

        return args;
    }
}

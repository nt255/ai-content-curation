package main.java.processor.text.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HashtagCleaner {
    
    private static final Logger LOG = 
            LoggerFactory.getLogger(HashtagCleaner.class);
    
    private static final String HASHTAG_REGEX = "\\#[a-zA-Z]+\\b";
    private static final Integer LIMIT_MIN = 3;
    private static final Integer LIMIT_MAX = 10;
    
    private final Random random;
    private final Pattern pattern;
    
    public HashtagCleaner() {
        this.random = new Random();
        this.pattern = Pattern.compile(HASHTAG_REGEX);
    }
    
    
    public String clean(String hashtags) {
        int limit = random.nextInt(LIMIT_MAX - LIMIT_MIN) + LIMIT_MIN;
        return clean(hashtags, limit, true);
    }

    @VisibleForTesting
    public String clean(String input, Integer limit, boolean shuffled) {
        
        Matcher matcher = pattern.matcher(input);

        List<String> hashtags = new ArrayList<>();
        while (matcher.find())
            hashtags.add(matcher.group());

        if (shuffled)
            Collections.shuffle(hashtags);
        
        String cleaned = hashtags
                .stream()
                .distinct()
                .limit(limit)
                .collect(Collectors.joining(" "));
        
        LOG.info("cleaned hashtags: {}", cleaned);
        return cleaned;
    }

}

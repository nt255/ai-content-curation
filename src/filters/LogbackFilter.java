package filters;

import java.util.Map;
import java.util.Set;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class LogbackFilter extends Filter<ILoggingEvent> {

    private static final Map<String, Set<String>> blockedMessagesPerLogger = 
            Map.of(
                    "org.mongodb.driver.cluster", 
                    Set.of(
                            "Updating cluster description to",
                            "Checking status of"));

    @Override
    public FilterReply decide(ILoggingEvent event) {

        String loggerName = event.getLoggerName();

        if (blockedMessagesPerLogger.containsKey(loggerName))
            if (blockedMessagesPerLogger.get(loggerName).stream()
                    .anyMatch(s -> event.getMessage().contains(s)))
                return FilterReply.DENY;

        return FilterReply.NEUTRAL;
    }

}

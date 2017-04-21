package in.adavi.pradyot.core;

import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Inject;
import io.dropwizard.servlets.tasks.Task;

import java.io.PrintWriter;

/**
 * Created by pradyot.ha on 21/04/17.
 */
public class DisplayRateLimiterTask extends Task {

    private RateLimitManager rateLimitManager;

    @Inject
    public DisplayRateLimiterTask(RateLimitManager rateLimitManager) {
        super("display-rate-limiter");
        this.rateLimitManager = rateLimitManager;
    }

    public void execute(ImmutableMultimap<String, String> immutableMultimap, PrintWriter printWriter) throws Exception {
        printWriter.print(this.rateLimitManager.getRateLimiterMap().toString());
        printWriter.flush();
    }
}

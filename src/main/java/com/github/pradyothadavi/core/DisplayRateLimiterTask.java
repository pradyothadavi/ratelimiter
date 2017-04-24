package com.github.pradyothadavi.core;

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
        super("rateLimiter");
        this.rateLimitManager = rateLimitManager;
    }

    public void execute(ImmutableMultimap<String, String> immutableMultimap, PrintWriter printWriter) throws Exception {
        printWriter.println(this.rateLimitManager.getRateLimiterMap().toString());
        printWriter.println(this.rateLimitManager.getMethodToRateLimitKeyMap().toString());
        printWriter.flush();
    }
}

package com.shiftsl.backend.unittests.Extensions;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimingExtension  implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private final static Logger logger = LoggerFactory.getLogger(TimingExtension.class);

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        long startTime = context.getStore(ExtensionContext.Namespace.GLOBAL).remove("startTime", long.class);
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info(String.format("Finished execution of test method: %s in %d ms", context.getDisplayName(), executionTime));
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        logger.info(String.format("Starting execution of test method: %s", context.getTestMethod().get().getName()));
        context.getStore(ExtensionContext.Namespace.GLOBAL)
                .put("startTime", System.currentTimeMillis());
    }
}

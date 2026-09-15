package com.kynsoft.share.core.infrastructure.bus.pipeline;

import com.kynsoft.share.core.domain.bus.pipeline.IPipelineBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class LoggingPipelineBehavior implements IPipelineBehavior {

    private static final Logger logger = LoggerFactory.getLogger(LoggingPipelineBehavior.class);

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Object handle(Object request, Supplier<Object> next) {
        String requestName = request.getClass().getSimpleName();
        logger.debug("Executing: {}", requestName);

        Object result = next.get();

        logger.debug("Executed successfully: {}", requestName);
        return result;
    }
}

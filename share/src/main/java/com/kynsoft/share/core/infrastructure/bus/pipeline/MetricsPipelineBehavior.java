package com.kynsoft.share.core.infrastructure.bus.pipeline;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.pipeline.IPipelineBehavior;
import com.kynsoft.share.core.domain.bus.query.IQuery;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class MetricsPipelineBehavior implements IPipelineBehavior {

    private final MeterRegistry meterRegistry;

    public MetricsPipelineBehavior(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public Object handle(Object request, Supplier<Object> next) {
        String type = request instanceof ICommand ? "command" : (request instanceof IQuery ? "query" : "unknown");
        String name = request.getClass().getSimpleName();

        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            Object result = next.get();
            sample.stop(Timer.builder("mediator.request.duration")
                    .tag("type", type)
                    .tag("name", name)
                    .tag("outcome", "success")
                    .register(meterRegistry));
            meterRegistry.counter("mediator.request.total", "type", type, "name", name, "outcome", "success").increment();
            return result;
        } catch (Exception e) {
            sample.stop(Timer.builder("mediator.request.duration")
                    .tag("type", type)
                    .tag("name", name)
                    .tag("outcome", "error")
                    .register(meterRegistry));
            meterRegistry.counter("mediator.request.total", "type", type, "name", name, "outcome", "error").increment();
            throw e;
        }
    }
}

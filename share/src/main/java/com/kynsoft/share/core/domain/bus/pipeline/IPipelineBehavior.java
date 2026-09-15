package com.kynsoft.share.core.domain.bus.pipeline;

import java.util.function.Supplier;

public interface IPipelineBehavior {

    int getOrder();

    Object handle(Object request, Supplier<Object> next);
}

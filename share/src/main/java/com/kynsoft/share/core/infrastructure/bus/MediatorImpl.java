package com.kynsoft.share.core.infrastructure.bus;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.share.core.domain.bus.pipeline.IPipelineBehavior;
import com.kynsoft.share.core.domain.bus.query.IQuery;
import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.share.core.domain.bus.query.IResponse;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

@Component
public class MediatorImpl implements IMediator {

    private final HandlerRegistry registry;
    private final List<IPipelineBehavior> pipelineBehaviors;
    private final ExecutorService asyncExecutor;

    public MediatorImpl(HandlerRegistry registry,
                        List<IPipelineBehavior> pipelineBehaviors) {
        this.registry = Objects.requireNonNull(registry, "HandlerRegistry no puede ser null");
        this.pipelineBehaviors = pipelineBehaviors.stream()
                .sorted(Comparator.comparingInt(IPipelineBehavior::getOrder))
                .toList();
        this.asyncExecutor = Executors.newThreadPerTaskExecutor(
                Thread.ofVirtual().name("mediator-async-", 0).factory());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <M extends ICommandMessage> M send(ICommand command) {
        Objects.requireNonNull(command, "Command no puede ser null");

        Supplier<Object> terminalAction = () -> {
            ICommandHandler<ICommand> handler = registry.getCommandHandler(command.getClass());
            handler.handle(command);
            return command.getMessage();
        };

        try {
            return (M) executePipeline(command, terminalAction);
        } catch (MediatorException e) {
            throw e;
        } catch (Exception e) {
            throw new MediatorException("Error executing command: " + command.getClass().getSimpleName(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends IResponse> R send(IQuery query) {
        Objects.requireNonNull(query, "Query no puede ser null");

        Supplier<Object> terminalAction = () -> {
            IQueryHandler<IQuery, IResponse> handler = registry.getQueryHandler(query.getClass());
            return handler.handle(query);
        };

        try {
            return (R) executePipeline(query, terminalAction);
        } catch (MediatorException e) {
            throw e;
        } catch (Exception e) {
            throw new MediatorException("Error executing query: " + query.getClass().getSimpleName(), e);
        }
    }

    @Override
    public <M extends ICommandMessage> CompletableFuture<M> sendAsync(ICommand command) {
        return CompletableFuture.supplyAsync(() -> send(command), asyncExecutor);
    }

    @Override
    public <R extends IResponse> CompletableFuture<R> sendAsync(IQuery query) {
        return CompletableFuture.supplyAsync(() -> send(query), asyncExecutor);
    }

    private Object executePipeline(Object request, Supplier<Object> terminalAction) {
        if (pipelineBehaviors.isEmpty()) {
            return terminalAction.get();
        }

        Supplier<Object> chain = terminalAction;
        for (int i = pipelineBehaviors.size() - 1; i >= 0; i--) {
            IPipelineBehavior behavior = pipelineBehaviors.get(i);
            Supplier<Object> next = chain;
            chain = () -> behavior.handle(request, next);
        }
        return chain.get();
    }
}

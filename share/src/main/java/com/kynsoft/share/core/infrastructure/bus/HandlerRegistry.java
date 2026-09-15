package com.kynsoft.share.core.infrastructure.bus;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.share.core.domain.bus.query.IQuery;
import com.kynsoft.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.share.core.domain.bus.query.IResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Service
@SuppressWarnings("unchecked")
public final class HandlerRegistry {

    private static final Logger logger = LoggerFactory.getLogger(HandlerRegistry.class);

    private final Map<Class<? extends ICommand>, ICommandHandler<?>> commandHandlers;
    private final Map<Class<? extends IQuery>, IQueryHandler<?, ?>> queryHandlers;

    public HandlerRegistry(ListableBeanFactory beanFactory) {
        this.commandHandlers = indexCommandHandlers(beanFactory);
        this.queryHandlers = indexQueryHandlers(beanFactory);

        logger.info("HandlerRegistry initialized: {} command handlers, {} query handlers",
                commandHandlers.size(), queryHandlers.size());
    }

    public <T extends ICommand> ICommandHandler<T> getCommandHandler(Class<? extends ICommand> commandClass) {
        ICommandHandler<?> handler = commandHandlers.get(commandClass);
        if (handler == null) {
            throw new MediatorException(
                    "No handler registered for command: " + commandClass.getSimpleName());
        }
        return (ICommandHandler<T>) handler;
    }

    public <Q extends IQuery, R extends IResponse> IQueryHandler<Q, R> getQueryHandler(Class<? extends IQuery> queryClass) {
        IQueryHandler<?, ?> handler = queryHandlers.get(queryClass);
        if (handler == null) {
            throw new MediatorException(
                    "No handler registered for query: " + queryClass.getSimpleName());
        }
        return (IQueryHandler<Q, R>) handler;
    }

    private Map<Class<? extends ICommand>, ICommandHandler<?>> indexCommandHandlers(ListableBeanFactory beanFactory) {
        Map<Class<? extends ICommand>, ICommandHandler<?>> handlers = new HashMap<>();
        for (ICommandHandler<?> handler : beanFactory.getBeansOfType(ICommandHandler.class).values()) {
            Class<? extends ICommand> commandClass = resolveGenericType(handler, ICommandHandler.class);
            ICommandHandler<?> existing = handlers.put(commandClass, handler);
            if (existing != null) {
                throw new IllegalStateException(String.format(
                        "Duplicate command handler for %s: [%s] and [%s]",
                        commandClass.getSimpleName(),
                        getRealClass(existing).getSimpleName(),
                        getRealClass(handler).getSimpleName()));
            }
        }
        return Map.copyOf(handlers);
    }

    private Map<Class<? extends IQuery>, IQueryHandler<?, ?>> indexQueryHandlers(ListableBeanFactory beanFactory) {
        Map<Class<? extends IQuery>, IQueryHandler<?, ?>> handlers = new HashMap<>();
        for (IQueryHandler<?, ?> handler : beanFactory.getBeansOfType(IQueryHandler.class).values()) {
            Class<? extends IQuery> queryClass = resolveGenericType(handler, IQueryHandler.class);
            IQueryHandler<?, ?> existing = handlers.put(queryClass, handler);
            if (existing != null) {
                throw new IllegalStateException(String.format(
                        "Duplicate query handler for %s: [%s] and [%s]",
                        queryClass.getSimpleName(),
                        getRealClass(existing).getSimpleName(),
                        getRealClass(handler).getSimpleName()));
            }
        }
        return Map.copyOf(handlers);
    }

    @SuppressWarnings("unchecked")
    private <R> Class<R> resolveGenericType(Object handler, Class<?> targetInterface) {
        Class<?> realClass = getRealClass(handler);
        for (Type genericInterface : realClass.getGenericInterfaces()) {
            if (genericInterface instanceof ParameterizedType paramType
                    && targetInterface.isAssignableFrom((Class<?>) paramType.getRawType())) {
                return (Class<R>) paramType.getActualTypeArguments()[0];
            }
        }
        throw new IllegalStateException(
                "Could not resolve generic type of " + targetInterface.getSimpleName()
                        + " for: " + realClass.getName());
    }

    private Class<?> getRealClass(Object handler) {
        if (handler instanceof Advised advised) {
            Class<?> targetClass = advised.getTargetClass();
            if (targetClass != null) {
                return targetClass;
            }
        }
        return handler.getClass();
    }
}

package com.kynsoft.share.core.infrastructure.bus;

import com.kynsoft.share.core.domain.bus.command.ICommand;
import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.share.core.domain.bus.command.ITypedCommand;
import com.kynsoft.share.core.domain.bus.query.IQuery;
import com.kynsoft.share.core.domain.bus.query.IResponse;

import java.util.concurrent.CompletableFuture;

public interface IMediator {

    <M extends ICommandMessage> M send(ICommand command);

    default <M extends ICommandMessage> M send(ITypedCommand<M> command) {
        return send((ICommand) command);
    }

    <R extends IResponse> R send(IQuery query);

    <M extends ICommandMessage> CompletableFuture<M> sendAsync(ICommand command);

    <R extends IResponse> CompletableFuture<R> sendAsync(IQuery query);
}

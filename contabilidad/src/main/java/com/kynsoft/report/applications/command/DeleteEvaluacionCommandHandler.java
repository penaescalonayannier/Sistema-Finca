package com.kynsoft.report.applications.command;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.applications.command.message.DeleteEvaluacionMessage;
import com.kynsoft.report.domain.services.IEvaluacionService;
import org.springframework.stereotype.Component;

@Component
public class DeleteEvaluacionCommandHandler implements ICommandHandler<DeleteEvaluacionCommand> {

    private final IEvaluacionService serviceImpl;

    public DeleteEvaluacionCommandHandler(IEvaluacionService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(DeleteEvaluacionCommand command) {
        serviceImpl.delete(command.getId());
    }
}

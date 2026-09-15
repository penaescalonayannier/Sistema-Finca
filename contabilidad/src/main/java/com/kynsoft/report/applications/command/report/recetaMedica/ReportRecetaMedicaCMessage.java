package com.kynsoft.report.applications.command.report.recetaMedica;

import com.kynsoft.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.io.ByteArrayOutputStream;

@Getter
public class ReportRecetaMedicaCMessage implements ICommandMessage {
    private final ByteArrayOutputStream baos;

    public ReportRecetaMedicaCMessage(ByteArrayOutputStream baos) {
        this.baos = baos;
    }
}

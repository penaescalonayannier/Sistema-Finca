/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kynsoft.report.applications.command.report.cuenta110.update.importe;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.report.applications.command.report.cuenta110.update.UpdateCuenta110Message;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateCuenta110ImporteCommand implements ICommand {

    // El ID se obtiene en el Handler, pero lo necesitamos para el ICommandMessage
    private UUID id; 
    private final Double importe;

    public UpdateCuenta110ImporteCommand(Double importe, UUID id) {
        this.importe = importe;
        this.id = id; 
    }

    public static UpdateCuenta110ImporteCommand fromRequest(UpdateCuenta110ImporteRequest request) {
        // En este punto, no conocemos el ID, se inicializa a null
        return new UpdateCuenta110ImporteCommand(request.getImporte(), null);
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public ICommandMessage getMessage() {
        // Asumo que tienes una clase UpdateCuenta110Message que puede tomar el ID
        return new UpdateCuenta110Message(this.id);
    }
}

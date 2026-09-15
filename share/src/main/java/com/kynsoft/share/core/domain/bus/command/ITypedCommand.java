package com.kynsoft.share.core.domain.bus.command;

/**
 * Type-safe version of ICommand that binds the command to its response type at compile time.
 * New commands should prefer this interface over ICommand for compile-time safety.
 *
 * Example:
 * <pre>
 * public class CreateFooCommand implements ITypedCommand&lt;CreateFooMessage&gt; {
 *     private CreateFooMessage message;
 *
 *     {@literal @}Override
 *     public CreateFooMessage getMessage() { return message; }
 *
 *     public void setMessage(CreateFooMessage message) { this.message = message; }
 * }
 * </pre>
 *
 * @param <M> the specific message type this command produces
 */
public interface ITypedCommand<M extends ICommandMessage> extends ICommand {
    @Override
    M getMessage();
}

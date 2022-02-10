package common.simple;

import common.simple.error.SimpleErrorListenerContext;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Timer;
import java.util.function.Consumer;

@Data
@RequiredArgsConstructor
public class AwaitingForResponseRequest {
    private Consumer<CallbackContext> successCallback;
    private Consumer<SimpleErrorListenerContext> errorCallback;
    private Timer timer;
}

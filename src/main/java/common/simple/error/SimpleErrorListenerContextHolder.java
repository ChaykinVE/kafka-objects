package common.simple.error;

public class SimpleErrorListenerContextHolder extends Throwable{
    private SimpleErrorListenerContext simpleErrorListenerContext;

    public SimpleErrorListenerContextHolder(SimpleErrorListenerContext simpleErrorListenerContext) {
        this.simpleErrorListenerContext = simpleErrorListenerContext;
    }

    public SimpleErrorListenerContext getChainErrorListenerContext() {
        return simpleErrorListenerContext;
    }
}

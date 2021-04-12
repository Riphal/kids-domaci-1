package results;

import java.util.Map;

public class WebScannerResult implements Result {
    private final boolean loggerON;
    private final boolean poisonous;

    public WebScannerResult (boolean loggerON) {
        this.loggerON = loggerON;
        this.poisonous = false;
    }

    public WebScannerResult () {
        this.loggerON = false;
        this.poisonous = true;
    }

    @Override
    public void accept (Visitor resultVisitor) {
        resultVisitor.process(this);
    }

    @Override
    public Map<String, Integer> getResult() {
        return null;
    }

    @Override
    public Map<String, Integer> queryResult() {
        return null;
    }

    @Override
    public boolean isLoggerON () {
        return this.loggerON;
    }

    @Override
    public boolean isPoisonous () {
        return this.poisonous;
    }

    public String getDomainName () {
        return null;
    }
}

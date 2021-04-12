package jobs;

public class WebCrawlerJob implements Job {
    private final boolean loggerON;
    private final boolean poisonous;

    public WebCrawlerJob (boolean loggerON) {
        this.loggerON = loggerON;
        this.poisonous = false;
    }

    public WebCrawlerJob () {
        this.loggerON = false;
        this.poisonous = true;
    }

    @Override
    public void accept(Visitor jobVisitor) {
        jobVisitor.dispatch(this);
    }

    @Override
    public boolean isLoggerON() {
        return this.loggerON;
    }

    @Override
    public boolean isPoisonous() {
        return this.poisonous;
    }
}

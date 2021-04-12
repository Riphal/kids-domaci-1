package jobs;

public class DirectoryCrawlerJob implements Job {
    private final String corpusName;
    private final String absolutePath;

    private final boolean loggerON;
    private final boolean poisonous;

    public DirectoryCrawlerJob (String corpusName, String absolutePath, boolean loggerON) {
        this.corpusName = corpusName;
        this.absolutePath = absolutePath;

        this.loggerON = loggerON;
        this.poisonous = false;
    }

    public DirectoryCrawlerJob () {
        this.corpusName = null;
        this.absolutePath = null;

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

    public String getCorpusName () {
        return this.corpusName;
    }

    public String getAbsolutePath () {
        return this.absolutePath;
    }
}

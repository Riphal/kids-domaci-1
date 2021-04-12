package results;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FileScannerResult implements Result {
    private final String corpusName;
    private final List<Future<Map<String, Integer>>> futureResults;

    private Map<String, Integer> cachedResults = new HashMap<>();

    private final boolean loggerON;
    private final boolean poisonous;

    public FileScannerResult (String corpusName, List<Future<Map<String, Integer>>> futureResults, boolean loggerON) {
        this.corpusName = corpusName;
        this.futureResults = futureResults;

        this.loggerON = loggerON;
        this.poisonous = false;
    }

    public FileScannerResult () {
        this.corpusName = null;
        this.futureResults = null;

        this.loggerON = false;
        this.poisonous = true;
    }

    @Override
    public void accept (Visitor resultVisitor) {
        resultVisitor.process(this);
    }

    @Override
    public Map<String, Integer> getResult() {
        if (!cachedResults.isEmpty()) {
            return cachedResults;
        }

        if (futureResults == null) {
            return null;
        }

        Map<String, Integer> result = new HashMap<>();
        for (Future<Map<String, Integer>> futureResult: futureResults) {
            try {
                Map<String, Integer> r = futureResult.get();

                for (Map.Entry<String, Integer> entry : r.entrySet()) {
                    int count = result.getOrDefault(entry.getKey(), 0);
                    result.put(entry.getKey(), count + entry.getValue());
                }
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Failed to return result");
            }
        }

        if (!result.isEmpty()) {
            cachedResults = result;
        }

        return result;
    }

    @Override
    public Map<String, Integer> queryResult() {
        if (!cachedResults.isEmpty()) {
            return cachedResults;
        }

        if (futureResults == null) {
            return null;
        }

        boolean ready = true;
        for (Future<Map<String, Integer>> futureResult: futureResults) {
            if (!futureResult.isDone()) {
                ready = false;
                break;
            }
        }

        if (ready) {
            return getResult();
        }

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

    public String getCorpusName () {
        return this.corpusName;
    }
}

package results;

import java.util.Map;

public class ResultVisitor implements Visitor {
    private final Map<String, FileScannerResult> fileScannerResults;
    private final Map<String, WebScannerResult> webScannerResults;

    public ResultVisitor (
            Map<String, FileScannerResult> fileScannerResults,
            Map<String, WebScannerResult> webScannerResults
    ) {
        this.fileScannerResults = fileScannerResults;
        this.webScannerResults = webScannerResults;
    }

    @Override
    public void process(FileScannerResult fileScannerResult) {
        this.fileScannerResults.put(fileScannerResult.getCorpusName(), fileScannerResult);
    }

    @Override
    public void process(WebScannerResult webScannerResult) {
        this.webScannerResults.put(webScannerResult.getDomainName(), webScannerResult);
    }
}

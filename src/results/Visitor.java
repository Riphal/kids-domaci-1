package results;

public interface Visitor {
    public void process(FileScannerResult fileScannerResult);
    public void process(WebScannerResult webScannerResult);
}

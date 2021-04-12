package jobs;

public interface Visitor {
    public void dispatch(DirectoryCrawlerJob directoryCrawlerJob);
    public void dispatch(WebCrawlerJob webCrawlerJob);
}

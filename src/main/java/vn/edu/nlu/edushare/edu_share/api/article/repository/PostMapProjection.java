package vn.edu.nlu.edushare.edu_share.api.article.repository;

public interface PostMapProjection {
    Integer getId();
    String getTitle();
    String getDescription();
    String getImageUrl();
    Double getPrice();
    String getStatus();
    String getAreaName();
    Double getLatitude();
    Double getLongitude();
}
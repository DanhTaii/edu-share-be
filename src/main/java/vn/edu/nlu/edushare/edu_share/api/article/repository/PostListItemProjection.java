package vn.edu.nlu.edushare.edu_share.api.article.repository;

public interface PostListItemProjection {
    Integer getId();

    String getTitle();

    String getDescription();

    Double getPrice();

    String getImageUrl();

    String getStatus();

    Integer getCategoryId();

    String getCategoryName();

    Integer getLocationId();

    String getLocationName();

    String getAuthorId();

    String getAuthorName();
}

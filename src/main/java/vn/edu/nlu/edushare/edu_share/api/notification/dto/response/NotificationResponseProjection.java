package vn.edu.nlu.edushare.edu_share.api.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;

public interface NotificationResponseProjection {
    Integer getId();

    String getTitle();

    String getContent();

    String getType();

    Integer getReferenceId();

    Boolean getIsRead();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    String getCreatedAt();

    String getAvatarUrl();
}
package vn.edu.nlu.edushare.edu_share.api.article.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "posts")
@Data
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String authorId;
    private int categoryId;
    private int locationId;

    private String title;
    private String description;
    private Double price;
    private String imageUrl;

    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

}

package vn.edu.nlu.edushare.edu_share.api.category.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

    @Entity
    @Table(name = "categories")
    @Data
    public class Category {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @Column(name = "name", nullable = false)
        private String name;

        @Column(name = "icon_url")
        private String iconUrl;

        @Column(name = "created_at", updatable = false)
        private Timestamp createdAt;

        @Column(name = "updated_at")
        private Timestamp updatedAt;
    }



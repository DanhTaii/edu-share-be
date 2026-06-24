package vn.edu.nlu.edushare.edu_share.api.article.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.nlu.edushare.edu_share.api.article.model.LocationDemo;

public interface LocationRepository extends JpaRepository<LocationDemo, Integer> {
}
package pl.medm.javadev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.medm.javadev.model.entity.Lecture;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {

    boolean existsByTitle(String title);
}

package pl.medm.javadev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.medm.javadev.model.Lecture;
import pl.medm.javadev.model.User;

import java.util.List;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {

    boolean existsByTitle(String title);
}

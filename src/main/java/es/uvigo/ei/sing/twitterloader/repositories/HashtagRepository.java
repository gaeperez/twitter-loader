package es.uvigo.ei.sing.twitterloader.repositories;

import es.uvigo.ei.sing.twitterloader.entities.HashtagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HashtagRepository extends JpaRepository<HashtagEntity, Integer> {
    Optional<HashtagEntity> findByTag(String tag);
}

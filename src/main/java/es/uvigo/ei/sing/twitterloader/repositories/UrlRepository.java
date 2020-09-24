package es.uvigo.ei.sing.twitterloader.repositories;


import es.uvigo.ei.sing.twitterloader.entities.UrlEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UrlRepository extends JpaRepository<UrlEntity, Integer> {
    Optional<UrlEntity> findByHash(String hash);

    Set<UrlEntity> findAllByContentIsNull();

    Set<UrlEntity> findAllByResolvedIsNull();

    Page<UrlEntity> findAllByParsedIsFalse(Pageable pageable);
}

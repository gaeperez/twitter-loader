package es.uvigo.ei.sing.twitterloader.repositories;


import es.uvigo.ei.sing.twitterloader.entities.TweetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TweetRepository extends JpaRepository<TweetEntity, Long>, JpaSpecificationExecutor<TweetEntity> {
    Optional<TweetEntity> findById(long id);

    @Query("SELECT DISTINCT(t.lang) FROM TweetEntity t")
    List<String> findDistinctLanguages();
}

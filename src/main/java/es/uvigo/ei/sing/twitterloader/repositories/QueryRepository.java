package es.uvigo.ei.sing.twitterloader.repositories;

import es.uvigo.ei.sing.twitterloader.entities.QueryEntity;
import es.uvigo.ei.sing.twitterloader.entities.TweetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueryRepository extends JpaRepository<QueryEntity, Long>, JpaSpecificationExecutor<QueryEntity> {
    QueryEntity findById(long id);

    @Query("SELECT COUNT(t) FROM QueryEntity q INNER JOIN q.tweetEntities t WHERE q.id= :id")
    long countTweets(@Param("id") long id);

    @Query("SELECT t FROM QueryEntity q INNER JOIN q.tweetEntities t " +
            "LEFT JOIN t.tweetKnowledgeEntity tk WHERE (tk.sourceTweetId = 0 OR tk.sourceTweetId is null) AND q.id= :id")
    Page<TweetEntity> findNotCheckedTweets(@Param("id") long id, Pageable pageable);

    @Query("SELECT t FROM QueryEntity q INNER JOIN q.tweetEntities t WHERE q.id= :id")
    List<TweetEntity> findAllTweetsByQuery(@Param("id") long id);
}

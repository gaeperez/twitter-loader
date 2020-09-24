package es.uvigo.ei.sing.twitterloader.repositories;


import es.uvigo.ei.sing.twitterloader.entities.TweetQuoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TweetQuoteRepository extends JpaRepository<TweetQuoteEntity, Long> {
    Optional<TweetQuoteEntity> findByQuotedTweetEntity_IdAndSourceUserEntity_IdAndTargetUserEntity_Id(long tweetId, long sourceId, long targetId);
}

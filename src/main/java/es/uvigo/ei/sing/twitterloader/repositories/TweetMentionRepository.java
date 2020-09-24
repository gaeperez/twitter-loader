package es.uvigo.ei.sing.twitterloader.repositories;


import es.uvigo.ei.sing.twitterloader.entities.TweetMentionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TweetMentionRepository extends JpaRepository<TweetMentionEntity, Integer> {
    Optional<TweetMentionEntity> findBySourceTweetEntity_IdAndSourceUserEntity_IdAndTargetUserEntity_Id(long tweetId, long sourceId, long targetId);
}

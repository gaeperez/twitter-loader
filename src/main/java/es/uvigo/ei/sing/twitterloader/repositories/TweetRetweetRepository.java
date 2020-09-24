package es.uvigo.ei.sing.twitterloader.repositories;


import es.uvigo.ei.sing.twitterloader.entities.TweetRetweetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TweetRetweetRepository extends JpaRepository<TweetRetweetEntity, Long> {
    Optional<TweetRetweetEntity> findByOriginalTweetEntity_IdAndSourceUserEntity_IdAndTargetUserEntity_Id(long tweetId, long sourceId, long targetId);
}

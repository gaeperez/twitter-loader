package es.uvigo.ei.sing.twitterloader.repositories;


import es.uvigo.ei.sing.twitterloader.entities.TweetKnowledgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TweetKnowledgeRepository extends JpaRepository<TweetKnowledgeEntity, Integer> {
    Optional<TweetKnowledgeEntity> findFirstByTweetEntity_Id(Long id);
}

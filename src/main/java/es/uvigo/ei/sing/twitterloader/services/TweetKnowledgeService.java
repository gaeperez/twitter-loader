package es.uvigo.ei.sing.twitterloader.services;


import es.uvigo.ei.sing.twitterloader.entities.TweetKnowledgeEntity;
import es.uvigo.ei.sing.twitterloader.repositories.TweetKnowledgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TweetKnowledgeService {

    private final TweetKnowledgeRepository tweetKnowledgeRepository;

    @Autowired
    public TweetKnowledgeService(TweetKnowledgeRepository tweetKnowledgeRepository) {
        this.tweetKnowledgeRepository = tweetKnowledgeRepository;
    }

    public TweetKnowledgeEntity save(TweetKnowledgeEntity tweetKnowledgeEntity) {
        Optional<TweetKnowledgeEntity> inDatabase = tweetKnowledgeRepository.findFirstByTweetEntity_Id(tweetKnowledgeEntity.getTweetEntity().getId());
        try {
            inDatabase.ifPresent(knowledgeEntity -> tweetKnowledgeEntity.setId(knowledgeEntity.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tweetKnowledgeRepository.save(tweetKnowledgeEntity);
    }

    public List<TweetKnowledgeEntity> saveAll(List<TweetKnowledgeEntity> tweetsKnowledge) {
        return tweetKnowledgeRepository.saveAll(tweetsKnowledge);
    }

    public void saveAllAndFlush(List<TweetKnowledgeEntity> tweetsKnowledge) {
        tweetsKnowledge.forEach(tweetKnowledgeRepository::saveAndFlush);
    }
}

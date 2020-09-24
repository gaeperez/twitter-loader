package es.uvigo.ei.sing.twitterloader.services;


import es.uvigo.ei.sing.twitterloader.entities.QueryEntity;
import es.uvigo.ei.sing.twitterloader.entities.TweetEntity;
import es.uvigo.ei.sing.twitterloader.entities.TweetMentionEntity;
import es.uvigo.ei.sing.twitterloader.entities.UserEntity;
import es.uvigo.ei.sing.twitterloader.repositories.TweetMentionRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import twitter4j.TwitterException;
import twitter4j.UserMentionEntity;

import java.util.*;

@Log4j2
@Service
public class TweetMentionService {

    private final TweetMentionRepository tweetMentionRepository;

    private final UserService userService;

    @Autowired
    public TweetMentionService(TweetMentionRepository tweetMentionRepository, UserService userService) {
        this.tweetMentionRepository = tweetMentionRepository;
        this.userService = userService;
    }

    public TweetMentionEntity save(TweetMentionEntity tweetMentionEntity) {
        return tweetMentionRepository.save(tweetMentionEntity);
    }

    public List<TweetMentionEntity> saveAll(Collection<TweetMentionEntity> tweetMentionEntities) {
        return tweetMentionRepository.saveAll(tweetMentionEntities);
    }

    public Optional<TweetMentionEntity> findByUniqueIndex(long tweetId, long sourceId, long targetId) {
        return tweetMentionRepository.findBySourceTweetEntity_IdAndSourceUserEntity_IdAndTargetUserEntity_Id(tweetId,
                sourceId, targetId);
    }

    public Set<TweetMentionEntity> generateMentions(UserMentionEntity[] mentions, TweetEntity tweetEntity,
                                                    UserEntity sourceUserEntity, QueryEntity queryEntity) {
        Set<TweetMentionEntity> toRet = new HashSet<>();

        if (mentions != null) {
            long externalId;
            for (UserMentionEntity mention : mentions) {
                externalId = mention.getId();
                UserEntity targetUserEntity;

                try {
                    // Check if the target user was already inserted in database
                    Optional<UserEntity> possibleTargetUserEntity = userService.findById(externalId);
                    if (possibleTargetUserEntity.isPresent()) {
                        // Get saved user
                        targetUserEntity = possibleTargetUserEntity.get();
                    } else {
                        // Create a new target user (saved by cascade)
                        targetUserEntity = userService.searchForUser(externalId);
                    }
                    targetUserEntity.getQueries().add(queryEntity);

                    // Check if the mention among tweet, source and target users was already parsed
                    // Check if the target user was already inserted in database
                    TweetMentionEntity tweetMentionEntity;
                    Optional<TweetMentionEntity> possibleTweetMentionEntity = this.findByUniqueIndex(tweetEntity.getId(),
                            sourceUserEntity.getId(), targetUserEntity.getId());
                    if (!possibleTweetMentionEntity.isPresent()) {
                        // Create a new mention, add relationships
                        tweetMentionEntity = new TweetMentionEntity();
                        tweetMentionEntity.setSourceTweetEntity(tweetEntity);
                        tweetMentionEntity.setSourceUserEntity(sourceUserEntity);
                        tweetMentionEntity.setTargetUserEntity(targetUserEntity);
                    } else
                        tweetMentionEntity = possibleTweetMentionEntity.get();

                    toRet.add(tweetMentionEntity);
                } catch (TwitterException e) {
                    log.warn("Cannot retrieve user with ID {}. See error: code {} - {}",
                            externalId, e.getErrorCode(), e.getMessage());
                }
            }
        }

        return toRet;
    }

}

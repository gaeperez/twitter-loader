package es.uvigo.ei.sing.twitterloader.services;


import es.uvigo.ei.sing.twitterloader.entities.QueryEntity;
import es.uvigo.ei.sing.twitterloader.entities.TweetEntity;
import es.uvigo.ei.sing.twitterloader.entities.TweetRetweetEntity;
import es.uvigo.ei.sing.twitterloader.entities.UserEntity;
import es.uvigo.ei.sing.twitterloader.repositories.TweetRetweetRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.util.*;

@Log4j2
@Service
public class TweetRetweetService {

    private final TweetRetweetRepository tweetRetweetRepository;

    private final UserService userService;
    private final TweetService tweetService;

    @Autowired
    public TweetRetweetService(TweetRetweetRepository tweetRetweetRepository, UserService userService,
                               @Lazy TweetService tweetService) {
        this.tweetRetweetRepository = tweetRetweetRepository;
        this.userService = userService;
        this.tweetService = tweetService;
    }

    public TweetRetweetEntity save(TweetRetweetEntity tweetRetweetEntity) {
        return tweetRetweetRepository.save(tweetRetweetEntity);
    }

    public List<TweetRetweetEntity> saveAll(Collection<TweetRetweetEntity> tweetRetweetEntities) {
        return tweetRetweetRepository.saveAll(tweetRetweetEntities);
    }

    public Optional<TweetRetweetEntity> findByUniqueIndex(long tweetId, long sourceId, long targetId) {
        return tweetRetweetRepository.findByOriginalTweetEntity_IdAndSourceUserEntity_IdAndTargetUserEntity_Id(tweetId,
                sourceId, targetId);
    }

    public Set<TweetRetweetEntity> generateRetweets(Status retweetStatus, TweetEntity sourceTweetEntity,
                                                    UserEntity sourceUserEntity, QueryEntity queryEntity) {
        Set<TweetRetweetEntity> toRet = new HashSet<>();

        // Search up to the original tweet using recursion
        if (retweetStatus != null) {
            long externalId = retweetStatus.getUser().getId();
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

                // Check if the original tweet was already inserted in database (if not, parse it)
                // This method will collect all the middle tweets until it reaches the original (retweetStatus == null)
                TweetEntity originalTweet = tweetService.parseOrRetrieveTweet(retweetStatus, targetUserEntity, queryEntity);

                // Check if the retweet was already inserted in the database
                if (originalTweet != null) {
                    TweetRetweetEntity tweetRetweetEntity;
                    Optional<TweetRetweetEntity> possibleTweetRetweetEntity = this.findByUniqueIndex(originalTweet.getId(),
                            sourceUserEntity.getId(), targetUserEntity.getId());
                    if (possibleTweetRetweetEntity.isPresent()) {
                        tweetRetweetEntity = possibleTweetRetweetEntity.get();
                    } else {
                        // Create a new retweet, add relationships
                        tweetRetweetEntity = new TweetRetweetEntity();
                        tweetRetweetEntity.setSourceUserEntity(sourceUserEntity);
                        tweetRetweetEntity.setTargetUserEntity(targetUserEntity);
                        tweetRetweetEntity.setSourceTweetEntity(sourceTweetEntity);
                        tweetRetweetEntity.setOriginalTweetEntity(originalTweet);
                    }

                    // Add the created retweet in the set
                    toRet.add(tweetRetweetEntity);
                }
            } catch (TwitterException e) {
                log.warn("Cannot retrieve user with ID {}. See error: code {} - {}",
                        externalId, e.getErrorCode(), e.getMessage());
            }
        }

        return toRet;
    }
}

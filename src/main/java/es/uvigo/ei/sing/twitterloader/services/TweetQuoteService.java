package es.uvigo.ei.sing.twitterloader.services;


import es.uvigo.ei.sing.twitterloader.entities.QueryEntity;
import es.uvigo.ei.sing.twitterloader.entities.TweetEntity;
import es.uvigo.ei.sing.twitterloader.entities.TweetQuoteEntity;
import es.uvigo.ei.sing.twitterloader.entities.UserEntity;
import es.uvigo.ei.sing.twitterloader.repositories.TweetQuoteRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Log4j2
@Service
public class TweetQuoteService {

    private final TweetQuoteRepository tweetQuoteRepository;

    private final UserService userService;
    private final TweetService tweetService;

    @Autowired
    public TweetQuoteService(TweetQuoteRepository tweetQuoteRepository, UserService userService,
                             @Lazy TweetService tweetService) {
        this.tweetQuoteRepository = tweetQuoteRepository;
        this.userService = userService;
        this.tweetService = tweetService;
    }

    public TweetQuoteEntity save(TweetQuoteEntity tweetQuoteEntity) {
        return tweetQuoteRepository.save(tweetQuoteEntity);
    }

    public Optional<TweetQuoteEntity> findByUniqueIndex(long sourceTweetId, long sourceUserId, long targetUserId) {
        return tweetQuoteRepository.findByQuotedTweetEntity_IdAndSourceUserEntity_IdAndTargetUserEntity_Id(sourceTweetId,
                sourceUserId, targetUserId);
    }

    public Set<TweetQuoteEntity> generateQuotes(Status quoteStatus, TweetEntity sourceTweetEntity,
                                                UserEntity sourceUserEntity, QueryEntity queryEntity) {
        Set<TweetQuoteEntity> toRet = new HashSet<>();

        if (quoteStatus != null) {
            // Check if the target user was already parsed in the map
            long externalId = quoteStatus.getUser().getId();
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
                // This method will collect all the middle tweets until it reaches the original (quoteStatus == null)
                TweetEntity quoteEntity = tweetService.parseOrRetrieveTweet(quoteStatus, targetUserEntity, queryEntity);

                // Check if the retweet was already inserted in the database
                if (queryEntity != null) {
                    TweetQuoteEntity tweetQuoteEntity;
                    Optional<TweetQuoteEntity> possibleTweetQuoteEntity = this.findByUniqueIndex(quoteEntity.getId(),
                            sourceUserEntity.getId(), targetUserEntity.getId());
                    if (possibleTweetQuoteEntity.isPresent()) {
                        tweetQuoteEntity = possibleTweetQuoteEntity.get();
                    } else {
                        // Create a new quote, add relationships
                        tweetQuoteEntity = new TweetQuoteEntity();
                        tweetQuoteEntity.setSourceUserEntity(sourceUserEntity);
                        tweetQuoteEntity.setTargetUserEntity(targetUserEntity);
                        tweetQuoteEntity.setSourceTweetEntity(sourceTweetEntity);
                        tweetQuoteEntity.setQuotedTweetEntity(quoteEntity);
                    }

                    // Add the created quote in the set
                    toRet.add(tweetQuoteEntity);
                }
            } catch (TwitterException e) {
                log.warn("Cannot retrieve user with ID {}. See error: code {} - {}",
                        externalId, e.getErrorCode(), e.getMessage());
            }
        }

        return toRet;
    }
}

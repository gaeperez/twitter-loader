package es.uvigo.ei.sing.twitterloader.services;

import es.uvigo.ei.sing.twitterloader.entities.*;
import es.uvigo.ei.sing.twitterloader.repositories.TweetRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Log4j2
@Service
public class TweetService {

    private final TweetRepository tweetRepository;

    private final HashtagService hashtagService;
    private final UrlService urlService;
    private final MediaService mediaService;
    private final TweetMentionService tweetMentionService;
    private final TweetRetweetService tweetRetweetService;
    private final TweetQuoteService tweetQuoteService;

    @Autowired
    public TweetService(TweetRepository tweetRepository, HashtagService hashtagService, UrlService urlService,
                        MediaService mediaService, TweetMentionService tweetMentionService,
                        TweetRetweetService tweetRetweetService, TweetQuoteService tweetQuoteService) {
        this.tweetRepository = tweetRepository;
        this.hashtagService = hashtagService;
        this.urlService = urlService;
        this.mediaService = mediaService;
        this.tweetMentionService = tweetMentionService;
        this.tweetRetweetService = tweetRetweetService;
        this.tweetQuoteService = tweetQuoteService;
    }

    public List<TweetEntity> findAll(Set<Long> ids) {
        return tweetRepository.findAllById(ids);
    }

    public Page<TweetEntity> findAll(Pageable pageable) {
        return tweetRepository.findAll(pageable);
    }

    public Optional<TweetEntity> findById(long id) {
        return tweetRepository.findById(id);
    }

    public TweetEntity save(TweetEntity tweetEntity) {
        return tweetRepository.save(tweetEntity);
    }

    @Transactional
    public TweetEntity saveTransactional(TweetEntity tweetEntity) {
        return tweetRepository.save(tweetEntity);
    }

    public void saveAll(Iterable<TweetEntity> tweets) {
        tweetRepository.saveAll(tweets);
    }

    public void createKnowledgeIfNotExists(TweetEntity tweetEntity) {
        if (tweetEntity.getTweetKnowledgeEntity() == null) {
            tweetEntity.setTweetKnowledgeEntity(new TweetKnowledgeEntity());
            tweetEntity.getTweetKnowledgeEntity().setCurated(false);
            tweetEntity.getTweetKnowledgeEntity().setParsed(false);
            tweetEntity.getTweetKnowledgeEntity().setTotalTokens(0);
            tweetEntity.getTweetKnowledgeEntity().setSelfRepeatedCount(0);
            tweetEntity.getTweetKnowledgeEntity().setOtherRepeatedCount(0);
            tweetEntity.getTweetKnowledgeEntity().setSourceChain(false);
            tweetEntity.getTweetKnowledgeEntity().setSourceTweetId(0);
            tweetEntity.getTweetKnowledgeEntity().setTweetEntity(tweetEntity);
        }
    }

    public TweetEntity parseOrRetrieveTweet(Status status, UserEntity sourceUserEntity, QueryEntity queryEntity) {
        // Check if the tweet was already inserted in database
        TweetEntity tweetEntity;
        Optional<TweetEntity> possibleTweetEntity = this.findById(status.getId());
        if (!possibleTweetEntity.isPresent()) {
            // Create the tweet entity
            tweetEntity = new TweetEntity(status);

            // Check tweet hashtags, save them and assign to the tweet
            twitter4j.HashtagEntity[] hashtags = status.getHashtagEntities();
            Set<es.uvigo.ei.sing.twitterloader.entities.HashtagEntity> hashtagEntities = hashtagService.generateHashtags(hashtags);
            hashtagEntities = new HashSet<>(hashtagService.saveAll(hashtagEntities));
            tweetEntity.setHashtagEntities(hashtagEntities);

            // Check tweet urls, save them and assign to the tweet
            URLEntity[] urls = status.getURLEntities();
            Set<UrlEntity> urlEntities = urlService.generateUrls(urls);
            urlEntities = new HashSet<>(urlService.saveAll(urlEntities));
            tweetEntity.setUrlEntities(urlEntities);

            // Check tweet medias, save them and assign to the tweet
            twitter4j.MediaEntity[] medias = status.getMediaEntities();
            Set<es.uvigo.ei.sing.twitterloader.entities.MediaEntity> mediaEntities = mediaService.generateMedias(medias);
            mediaEntities = new HashSet<>(mediaService.saveAll(mediaEntities));
            tweetEntity.setMedias(mediaEntities);

            // Check tweet mentions
            UserMentionEntity[] mentions = status.getUserMentionEntities();
            Set<TweetMentionEntity> tweetMentionEntities = tweetMentionService.generateMentions(mentions, tweetEntity, sourceUserEntity, queryEntity);
            // Saving by cascade
            tweetEntity.setTweetMentionEntities(tweetMentionEntities);

            // Check tweet retweet
            Status retweet = status.getRetweetedStatus();
            Set<TweetRetweetEntity> tweetRetweetEntities = tweetRetweetService.generateRetweets(retweet, tweetEntity, sourceUserEntity, queryEntity);
            // Saving by cascade
            tweetEntity.setTweetRetweetEntities(tweetRetweetEntities);

            // Check tweet quoted
            Status quote = status.getQuotedStatus();
            Set<TweetQuoteEntity> tweetQuoteEntities = tweetQuoteService.generateQuotes(quote, tweetEntity, sourceUserEntity, queryEntity);
            // Saving by cascade
            tweetEntity.setTweetQuoteEntities(tweetQuoteEntities);

            // Add relationships
            tweetEntity.setUserEntity(sourceUserEntity);
            tweetEntity.getQueries().add(queryEntity);
        } else
            tweetEntity = possibleTweetEntity.get();

        return tweetEntity;
    }
}

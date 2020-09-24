package es.uvigo.ei.sing.twitterloader.services;


import es.uvigo.ei.sing.twitterloader.entities.TweetCacheEntity;
import es.uvigo.ei.sing.twitterloader.repositories.TweetCacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TweetCacheService {

    private final TweetCacheRepository tweetCacheRepository;

    @Autowired
    public TweetCacheService(TweetCacheRepository tweetCacheRepository) {
        this.tweetCacheRepository = tweetCacheRepository;
    }

    public List<TweetCacheEntity> findAll() {
        return tweetCacheRepository.findAll();
    }

    public Optional<TweetCacheEntity> findById(int id) {
        return tweetCacheRepository.findById(id);
    }

    public void saveTweetCache(TweetCacheEntity tweetCacheEntity) {
        tweetCacheRepository.save(tweetCacheEntity);
    }

    public void updateTweetCache(TweetCacheEntity tweetCacheEntity) {
        tweetCacheRepository.save(tweetCacheEntity);
    }

    public void deleteTweetCache(TweetCacheEntity tweetCacheEntity) {
        tweetCacheRepository.delete(tweetCacheEntity);
    }
}

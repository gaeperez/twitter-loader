package es.uvigo.ei.sing.twitterloader.services;


import es.uvigo.ei.sing.twitterloader.entities.QueryEntity;
import es.uvigo.ei.sing.twitterloader.entities.TweetEntity;
import es.uvigo.ei.sing.twitterloader.repositories.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QueryService {

    private final QueryRepository queryRepository;

    @Autowired
    public QueryService(QueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    public QueryEntity save(QueryEntity queryEntity) {
        queryEntity.setModified(LocalDateTime.now());
        queryEntity.incrementLaunchedTimes();
        return queryRepository.save(queryEntity);
    }

    public List<QueryEntity> findAll() {
        return queryRepository.findAll();
    }

    public QueryEntity findById(long id) {
        return queryRepository.findById(id);
    }

    public long countTweets(long id) {
        return queryRepository.countTweets(id);
    }

    public Page<TweetEntity> findNotCheckedTweets(int id, Pageable pageable) {
        return queryRepository.findNotCheckedTweets(id, pageable);
    }

    public List<TweetEntity> findAllTweetsByQuery(int id) {
        return queryRepository.findAllTweetsByQuery(id);
    }
}

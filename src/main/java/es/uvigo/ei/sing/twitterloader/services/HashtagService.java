package es.uvigo.ei.sing.twitterloader.services;

import es.uvigo.ei.sing.twitterloader.entities.HashtagEntity;
import es.uvigo.ei.sing.twitterloader.repositories.HashtagRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Log4j2
@Service
public class HashtagService {

    private final HashtagRepository hashtagRepository;

    @Autowired
    public HashtagService(HashtagRepository hashtagRepository) {
        this.hashtagRepository = hashtagRepository;
    }

    public HashtagEntity save(HashtagEntity hashtagEntity) {
        return hashtagRepository.save(hashtagEntity);
    }

    public List<HashtagEntity> saveAll(Collection<HashtagEntity> hashtagEntities) {
        return hashtagRepository.saveAll(hashtagEntities);
    }

    public List<HashtagEntity> findAll() {
        return hashtagRepository.findAll();
    }

    public Optional<HashtagEntity> findById(int id) {
        return hashtagRepository.findById(id);
    }

    public void saveHashtag(HashtagEntity hashtagEntity) {
        hashtagRepository.save(hashtagEntity);
    }

    public void updateHashtag(HashtagEntity hashtagEntity) {
        hashtagRepository.save(hashtagEntity);
    }

    public void deleteHashtag(HashtagEntity hashtagEntity) {
        hashtagRepository.delete(hashtagEntity);
    }

    public Optional<HashtagEntity> findByTag(String tag) {
        return hashtagRepository.findByTag(tag);
    }

    public Set<HashtagEntity> generateHashtags(twitter4j.HashtagEntity[] hashtags) {
        Set<HashtagEntity> toRet = new HashSet<>();

        if (hashtags != null) {
            for (twitter4j.HashtagEntity hashtag : hashtags) {
                // Check if the hashtag was already parsed in the map
                String tag = hashtag.getText();
                es.uvigo.ei.sing.twitterloader.entities.HashtagEntity hashtagEntity;

                // Check if hashtag was already inserted in database
                Optional<es.uvigo.ei.sing.twitterloader.entities.HashtagEntity> possibleHashtagEntity = this.findByTag(tag);
                if (possibleHashtagEntity.isPresent()) {
                    // Get saved hashtag
                    hashtagEntity = possibleHashtagEntity.get();
                } else {
                    // Create a new hashtag
                    hashtagEntity = new es.uvigo.ei.sing.twitterloader.entities.HashtagEntity();
                    hashtagEntity.setTag(tag);
                }

                toRet.add(hashtagEntity);
            }
        }

        return toRet;
    }
}

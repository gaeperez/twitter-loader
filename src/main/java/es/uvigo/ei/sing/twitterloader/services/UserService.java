package es.uvigo.ei.sing.twitterloader.services;


import es.uvigo.ei.sing.twitterloader.entities.UserEntity;
import es.uvigo.ei.sing.twitterloader.entities.UserKnowledgeEntity;
import es.uvigo.ei.sing.twitterloader.repositories.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class UserService {

    private final UserRepository userRepository;
    private final Twitter twitter;

    @Autowired
    public UserService(UserRepository userRepository, Twitter twitter) {
        this.userRepository = userRepository;
        this.twitter = twitter;
    }

    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    public Page<UserEntity> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public List<UserEntity> findByName(String name) {
        return userRepository.findByName(name);
    }

    public UserEntity findByScreenName(String name) {
        return userRepository.findByScreenName(name);
    }

    public UserEntity save(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    public void saveAll(List<UserEntity> userEntities) {
        userRepository.saveAll(userEntities);
    }

    public Optional<UserEntity> findById(long id) {
        return userRepository.findById(id);
    }

    public void createKnowledgeIfNotExists(UserEntity userEntity) {
        if (userEntity.getUserKnowledgeEntity() == null) {
            userEntity.setUserKnowledgeEntity(new UserKnowledgeEntity());
            userEntity.getUserKnowledgeEntity().setCurated(false);
            userEntity.getUserKnowledgeEntity().setParsed(false);
            userEntity.getUserKnowledgeEntity().setUserEntity(userEntity);
        }
    }

    public UserEntity searchForUser(long userId) throws TwitterException {
        UserEntity userEntity;

        // Check if user was already inserted in the database
        Optional<UserEntity> possibleUserEntity = this.findById(userId);
        if (possibleUserEntity.isPresent()) {
            userEntity = possibleUserEntity.get();
        } else {
            // Make the request
            User user = twitter.showUser(userId);

            // Parse user
            userEntity = new UserEntity(user);
        }

        return userEntity;
    }

}
package es.uvigo.ei.sing.twitterloader.services;


import es.uvigo.ei.sing.twitterloader.entities.UserKnowledgeEntity;
import es.uvigo.ei.sing.twitterloader.repositories.UserKnowledgeRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class UserKnowledgeService {
    private final UserKnowledgeRepository userKnowledgeRepository;

    @Autowired
    public UserKnowledgeService(UserKnowledgeRepository userKnowledgeRepository) {
        this.userKnowledgeRepository = userKnowledgeRepository;
    }

    public UserKnowledgeEntity save(UserKnowledgeEntity userKnowledgeEntity) {
        return userKnowledgeRepository.save(userKnowledgeEntity);
    }

}

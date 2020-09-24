package es.uvigo.ei.sing.twitterloader.repositories;


import es.uvigo.ei.sing.twitterloader.entities.UserKnowledgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserKnowledgeRepository extends JpaRepository<UserKnowledgeEntity, Integer> {
}

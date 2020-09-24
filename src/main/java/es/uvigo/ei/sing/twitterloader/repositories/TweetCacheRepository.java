package es.uvigo.ei.sing.twitterloader.repositories;


import es.uvigo.ei.sing.twitterloader.entities.TweetCacheEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetCacheRepository extends JpaRepository<TweetCacheEntity, Integer> {
}

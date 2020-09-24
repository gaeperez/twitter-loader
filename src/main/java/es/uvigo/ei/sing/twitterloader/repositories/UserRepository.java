package es.uvigo.ei.sing.twitterloader.repositories;


import es.uvigo.ei.sing.twitterloader.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    @Query("SELECT u FROM UserEntity u LEFT JOIN u.userKnowledgeEntity uk WHERE uk.classification like 'Unknown' ")
    List<UserEntity> findAllWithUnknownClasification();

    List<UserEntity> findTop10ByOrderByFollowersCountDesc();

    List<UserEntity> findTop100ByOrderByFollowersCountDesc();

    UserEntity findByScreenName(String name);

    List<UserEntity> findByName(String name);

    @Query("SELECT u FROM UserEntity u where u.description <> '' order by function('RAND')")
    List<UserEntity> findRandomNumberOfUsers(Pageable pageable);

    @Query("SELECT u FROM UserEntity u LEFT JOIN u.userKnowledgeEntity uk WHERE uk.parsed= FALSE OR uk.parsed is null")
    Page<UserEntity> findUsersNotParsed(Pageable pageable);

    @Query("SELECT COUNT(u) FROM UserEntity u LEFT JOIN u.userKnowledgeEntity uk WHERE uk.parsed= FALSE OR uk.parsed is null")
    long countUsersNotParsed();

    @Query("SELECT uk.sex, COUNT(uk.id) as total FROM UserEntity u LEFT JOIN u.userKnowledgeEntity uk WHERE uk.parsed= TRUE GROUP BY uk.sex ORDER BY total DESC")
    List<Object[]> findUsersParsedBySex();

    @Query("SELECT uk.city, uk.country, uk.countryCode, uk.geoLat, uk.geoLong, COUNT(uk.id) as total FROM UserEntity u LEFT JOIN u.userKnowledgeEntity uk WHERE uk.countryCode IS NOT NULL AND uk.parsed= TRUE GROUP BY uk.countryCode ORDER BY total DESC")
    Page<Object[]> findUsersParsedByCountry(Pageable pageable);

    @Query("SELECT u FROM UserEntity u WHERE u.userKnowledgeEntity = 'F' OR u.userKnowledgeEntity.sex= 'M' ")
    Page<UserEntity> findByIdentifiedSex(Pageable pageable);

    @Query("SELECT COUNT(u) FROM UserEntity u INNER JOIN u.userKnowledgeEntity uk WHERE uk.sex= 'F' OR uk.sex= 'M'")
    long countIdentifiedSex();
}

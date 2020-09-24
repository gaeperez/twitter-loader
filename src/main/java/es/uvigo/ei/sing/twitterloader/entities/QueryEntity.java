package es.uvigo.ei.sing.twitterloader.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "query")
public class QueryEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    @Column(name = "query", nullable = false, length = 2000)
    private String query;
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private QueryTypeEnum type;
    @Column(name = "created")
    private LocalDateTime created;
    @Column(name = "modified")
    private LocalDateTime modified;
    @Column(name = "launched_times")
    private Long launchedTimes = 0l;
    @Column(name = "exception", length = 2000)
    private String exception;
    @Column(name = "max_id")
    private Long maxId;
    @Column(name = "since_id")
    private Long sinceId;
    @Column(name = "enabled")
    private boolean enabled;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "query_tweet", joinColumns = {
            @JoinColumn(name = "query_id", nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "tweet_id", nullable = false, updatable = false)})
    private Set<TweetEntity> tweetEntities = new HashSet<>(0);

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "query_user", joinColumns = {
            @JoinColumn(name = "query_id", nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "user_id", nullable = false, updatable = false)})
    private Set<UserEntity> userEntities = new HashSet<>(0);

    public void incrementLaunchedTimes() {
        this.launchedTimes++;
    }
}

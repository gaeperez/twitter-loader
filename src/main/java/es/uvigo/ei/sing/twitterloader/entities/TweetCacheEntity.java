package es.uvigo.ei.sing.twitterloader.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tweet_cache", uniqueConstraints = @UniqueConstraint(columnNames = {"tweet_id", "cache_date"}))
public class TweetCacheEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    @Column(name = "cache_date", nullable = false)
    private LocalDateTime cacheDate;
    @Column(name = "serialized_tweet", nullable = false)
    private byte[] serializedTweet;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tweet_id", nullable = false)
    private TweetEntity tweetEntity;
}

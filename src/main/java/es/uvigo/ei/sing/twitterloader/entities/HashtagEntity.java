package es.uvigo.ei.sing.twitterloader.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "hashtag", uniqueConstraints = @UniqueConstraint(columnNames = "tag"))
public class HashtagEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    @Column(name = "tag", unique = true, nullable = false, length = 300)
    private String tag;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tweet_hashtag", joinColumns = {
            @JoinColumn(name = "hashtag_id", nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "tweet_id", nullable = false, updatable = false)})
    private Set<TweetEntity> tweetEntities = new HashSet<>(0);
}

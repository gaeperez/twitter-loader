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
@Table(name = "url", uniqueConstraints = @UniqueConstraint(columnNames = "hash"))
public class UrlEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;
    @Column(name = "url", nullable = false, length = 2000)
    private String url;
    @Column(name = "expanded_url", nullable = false, length = 2000)
    private String expandedUrl;
    @Column(name = "hash", unique = true, nullable = false)
    private String hash;
    @Column(name = "error")
    private boolean error;
    @Column(name = "parsed")
    private boolean parsed;
    @Column(name = "resolved", columnDefinition = "TEXT")
    private String resolved;
    @Column(name = "host")
    private String host;
    @Column(name = "title")
    private String title;
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    @Column(name = "keywords")
    private String keywords;
    @Column(name = "html", columnDefinition = "TEXT")
    @Basic(fetch = FetchType.LAZY)
    private String html;
    @Column(name = "language")
    private String language;

    private transient Long totalCount;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tweet_url", joinColumns = {
            @JoinColumn(name = "url_id", nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "tweet_id", nullable = false, updatable = false)})
    private Set<TweetEntity> tweetEntities = new HashSet<>(0);
}

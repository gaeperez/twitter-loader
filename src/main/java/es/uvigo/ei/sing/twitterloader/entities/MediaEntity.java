package es.uvigo.ei.sing.twitterloader.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "media")
public class MediaEntity {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    @Column(name = "ext_alt_text", columnDefinition = "TEXT")
    private String extAltText;
    @Column(name = "media_url", nullable = false, columnDefinition = "TEXT")
    private String url;
    @Column(name = "media_url_https", nullable = false, columnDefinition = "TEXt")
    private String urlHttps;
    @Column(name = "type", nullable = false, columnDefinition = "TEXT")
    private String type;
    @Column(name = "hash", nullable = false, unique = true)
    private String hash;
    @Column(name = "video_duration_millis", nullable = false)
    private Long videoDurationMs;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tweet_media", joinColumns = {
            @JoinColumn(name = "media_id", nullable = false, updatable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "tweet_id", nullable = false, updatable = false)})
    private Set<TweetEntity> tweets = new HashSet<>(0);

    // Two tweets are equals if they have the same hash
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof MediaEntity))
            return false;
        if (obj == this)
            return true;
        return this.getHash().equals(((MediaEntity) obj).getHash());
    }

    @Override
    public int hashCode() {
        return hash.hashCode();
    }
}

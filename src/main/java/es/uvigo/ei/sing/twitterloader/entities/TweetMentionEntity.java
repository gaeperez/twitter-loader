package es.uvigo.ei.sing.twitterloader.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tweet_mention", uniqueConstraints =
@UniqueConstraint(name = "tweet_mention_unique", columnNames = {"tweet_id", "source_user_id", "target_user_id"}))
public class TweetMentionEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tweet_id", nullable = false)
    private TweetEntity sourceTweetEntity;

    // Person who starts the mention
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_user_id", nullable = false)
    private UserEntity sourceUserEntity;

    // Person who is mentioned
    // Save the target user when saving the mention. The source user and the source tweet should be save beforehand
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "target_user_id", nullable = false)
    private UserEntity targetUserEntity;

    // Two tweets are equals if they have the same ID
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof TweetMentionEntity))
            return false;
        if (obj == this)
            return true;
        return this.getSourceTweetEntity().getId() == ((TweetMentionEntity) obj).getSourceTweetEntity().getId()
                && this.getSourceUserEntity().getId() == ((TweetMentionEntity) obj).getSourceUserEntity().getId()
                && this.getTargetUserEntity().getId() == ((TweetMentionEntity) obj).getTargetUserEntity().getId();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(getSourceTweetEntity().getId()) + Long.hashCode(getSourceUserEntity().getId())
                + Long.hashCode(getTargetUserEntity().getId());
    }
}

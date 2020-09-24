package es.uvigo.ei.sing.twitterloader.services;

import es.uvigo.ei.sing.twitterloader.entities.MediaEntity;
import es.uvigo.ei.sing.twitterloader.repositories.MediaRepository;
import es.uvigo.ei.sing.twitterloader.utils.Functions;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Log4j2
@Service
public class MediaService {

    private final MediaRepository mediaRepository;

    @Autowired
    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public MediaEntity save(MediaEntity mediaEntity) {
        return mediaRepository.save(mediaEntity);
    }

    public List<MediaEntity> saveAll(Collection<MediaEntity> mediaEntities) {
        return mediaRepository.saveAll(mediaEntities);
    }

    public Optional<MediaEntity> findByHash(String hash) {
        return mediaRepository.findByHash(hash);
    }

    public Set<MediaEntity> generateMedias(twitter4j.MediaEntity[] medias) {
        Set<MediaEntity> toRet = new HashSet<>();

        if (medias != null) {
            String mediaUrl, mediaUrlHttps, hash;
            for (twitter4j.MediaEntity media : medias) {
                mediaUrl = media.getMediaURL();
                mediaUrlHttps = media.getMediaURLHttps();
                hash = Functions.doHash(mediaUrl + mediaUrlHttps);

                // Check if media was already inserted in database
                es.uvigo.ei.sing.twitterloader.entities.MediaEntity mediaEntity;
                Optional<es.uvigo.ei.sing.twitterloader.entities.MediaEntity> possibleMediaEntity = this.findByHash(hash);
                if (possibleMediaEntity.isPresent()) {
                    // Get saved media
                    mediaEntity = possibleMediaEntity.get();
                } else {
                    // Create a new media
                    mediaEntity = new es.uvigo.ei.sing.twitterloader.entities.MediaEntity();
                    mediaEntity.setId(media.getId());
                    mediaEntity.setExtAltText(media.getExtAltText());
                    mediaEntity.setUrl(mediaUrl);
                    mediaEntity.setUrlHttps(mediaUrlHttps);
                    mediaEntity.setType(media.getType());
                    mediaEntity.setVideoDurationMs(media.getVideoDurationMillis());
                    mediaEntity.setHash(hash);
                }

                toRet.add(mediaEntity);
            }
        }

        return toRet;
    }
}

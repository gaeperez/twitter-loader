package es.uvigo.ei.sing.twitterloader.services;


import es.uvigo.ei.sing.twitterloader.entities.UrlEntity;
import es.uvigo.ei.sing.twitterloader.repositories.UrlRepository;
import es.uvigo.ei.sing.twitterloader.utils.Functions;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import twitter4j.URLEntity;

import java.util.*;

@Log4j2
@Service
public class UrlService {

    private final UrlRepository urlRepository;

    @Autowired
    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public Optional<UrlEntity> findByHash(String hash) {
        return urlRepository.findByHash(hash);
    }

    public Set<UrlEntity> findAllByContentIsNull() {
        return urlRepository.findAllByContentIsNull();
    }

    public Set<UrlEntity> findAllByResolvedIsNull() {
        return urlRepository.findAllByResolvedIsNull();
    }

    public Page<UrlEntity> findAllByParsedIsFalse(Pageable pageable) {
        return urlRepository.findAllByParsedIsFalse(pageable);
    }

    public UrlEntity save(UrlEntity urlEntity) {
        return urlRepository.save(urlEntity);
    }

    public List<UrlEntity> saveAll(Collection<UrlEntity> urlEntities) {
        return urlRepository.saveAll(urlEntities);
    }

    public Set<UrlEntity> generateUrls(URLEntity[] urls) {
        Set<UrlEntity> toRet = new HashSet<>();

        if (urls != null) {
            for (URLEntity url : urls) {
                // Check if the url was already parsed in the map
                String hash = Functions.doHash(url.getURL());
                UrlEntity urlEntity;

                // Check if url was already inserted in database
                Optional<UrlEntity> possibleUrlEntity = this.findByHash(hash);
                if (possibleUrlEntity.isPresent()) {
                    // Get saved url
                    urlEntity = possibleUrlEntity.get();
                } else {
                    // TODO: 08/07/2020 Parse the URL with JSOUP
                    // Create a new url
                    urlEntity = new UrlEntity();
                    urlEntity.setUrl(url.getURL());
                    urlEntity.setExpandedUrl(url.getExpandedURL());
                    urlEntity.setHash(hash);
                    urlEntity.setError(false);
                    urlEntity.setParsed(false);
                }

                toRet.add(urlEntity);
            }
        }

        return toRet;
    }
}

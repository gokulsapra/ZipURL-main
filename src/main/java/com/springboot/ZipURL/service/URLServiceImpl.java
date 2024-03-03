package com.springboot.ZipURL.service;

import com.google.common.hash.Hashing;
import com.springboot.ZipURL.model.Url;
import com.springboot.ZipURL.model.UrlDTO;
import com.springboot.ZipURL.repository.UrlRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Service
public class URLServiceImpl implements URLService{


    private UrlRepository urlRepository;

    @Autowired
    public URLServiceImpl(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Override
    public Url generateShortLink(UrlDTO urlDTO) {

        if(StringUtils.isNotEmpty(urlDTO.getUrl())){

            String encodedUrl = encodeUrl(urlDTO.getUrl());
            Url urlToPersist = new Url();

            urlToPersist.setCreationDate(LocalDateTime.now());
            urlToPersist.setOriginalUrl(urlDTO.getUrl());
            urlToPersist.setShortLink(encodedUrl);
            urlToPersist.setExpirationDate(getExpirationDate(urlDTO.getExpirationDate(), urlToPersist.getCreationDate()));

            Url UrlToReturn = persistShortLink(urlToPersist);

            if(UrlToReturn != null){
                return UrlToReturn;
            }
        }

        return null;
    }

    private LocalDateTime getExpirationDate(String expirationDate, LocalDateTime creationDate) {

        if(StringUtils.isBlank(expirationDate)){
            //means user hasn't provided it
            //we create expiration time of 5 mins
            return creationDate.plusSeconds(300);
        }

        LocalDateTime expirationDateToReturn = LocalDateTime.parse(expirationDate);

        return expirationDateToReturn;
    }


    //method containing the algorithm that converts a long url into a short link
    private String encodeUrl(String url) {

        String encodedUrl = "";
        LocalDateTime time = LocalDateTime.now();
        encodedUrl = Hashing.adler32()
                        .hashString(url.concat(time.toString()), StandardCharsets.UTF_8)
                        .toString();

        return encodedUrl;
    }

    @Override
    public Url persistShortLink(Url url) {

        //saving the url object into the h2 database
        Url urlToReturn = urlRepository.save(url);
        return urlToReturn;

    }

    @Override
    public Url getEncodedUrl(String url) {

        //returning url object using just the short link
        Url urlToReturn = urlRepository.findByShortLink(url);
        return urlToReturn;
    }

    @Override
    public void deleteShortLink(Url url) {

        urlRepository.delete(url);

    }
}

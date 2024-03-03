package com.springboot.ZipURL.service;

import com.springboot.ZipURL.model.Url;
import com.springboot.ZipURL.model.UrlDTO;
import org.springframework.stereotype.Service;


public interface URLService {

    public Url generateShortLink(UrlDTO urlDTO);
    public Url persistShortLink(Url url);
    public Url getEncodedUrl(String url);
    public void deleteShortLink(Url url);


}

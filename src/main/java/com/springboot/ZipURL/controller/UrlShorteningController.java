package com.springboot.ZipURL.controller;

import com.springboot.ZipURL.model.Url;
import com.springboot.ZipURL.model.UrlDTO;
import com.springboot.ZipURL.model.UrlErrorResponseDTO;
import com.springboot.ZipURL.model.UrlResponseDTO;
import com.springboot.ZipURL.service.URLService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
public class UrlShorteningController {

    private URLService urlService;

    @Autowired
    public UrlShorteningController(URLService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateShortLink(@RequestBody UrlDTO urlDTO){

        Url urlToReturn = urlService.generateShortLink(urlDTO);

        if(urlToReturn != null){
            UrlResponseDTO urlResponseDTO = new UrlResponseDTO();
            urlResponseDTO.setOriginalUrl(urlToReturn.getOriginalUrl());
            urlResponseDTO.setShortLink(urlToReturn.getShortLink());
            urlResponseDTO.setExpirationDate(urlToReturn.getExpirationDate());

            return new ResponseEntity<UrlResponseDTO>(urlResponseDTO, HttpStatus.OK);
        }

        UrlErrorResponseDTO urlErrorResponseDTO = new UrlErrorResponseDTO();
        urlErrorResponseDTO.setError("404");
        urlErrorResponseDTO.setStatus("There is an error processing your request. Please try again after some time!");

        return new ResponseEntity<UrlErrorResponseDTO>(urlErrorResponseDTO, HttpStatus.NOT_FOUND);

    }

    @GetMapping("/{shortLink}")
    public ResponseEntity<?> redirectToOriginalUrl(@PathVariable String shortLink){

        if(StringUtils.isEmpty(shortLink)){
            UrlErrorResponseDTO urlErrorResponseDTO = new UrlErrorResponseDTO();
            urlErrorResponseDTO.setError("Invalid Response");
            urlErrorResponseDTO.setStatus("400");

            return new ResponseEntity<UrlErrorResponseDTO>(urlErrorResponseDTO, HttpStatus.NOT_FOUND);
        }

        Url urlToReturn = urlService.getEncodedUrl(shortLink);

        if(urlToReturn == null){
            UrlErrorResponseDTO urlErrorResponseDTO = new UrlErrorResponseDTO();
            urlErrorResponseDTO.setError("URL does not exist.");
            urlErrorResponseDTO.setStatus("400");

            return new ResponseEntity<UrlErrorResponseDTO>(urlErrorResponseDTO, HttpStatus.NOT_FOUND);
        }

        if(urlToReturn.getExpirationDate().isBefore(LocalDateTime.now())){
            UrlErrorResponseDTO urlErrorResponseDTO = new UrlErrorResponseDTO();
            urlErrorResponseDTO.setError("Short link for this URL has expired. Please generate a new one!");
            urlErrorResponseDTO.setStatus("200");

            return new ResponseEntity<UrlErrorResponseDTO>(urlErrorResponseDTO, HttpStatus.OK);
        }

        //if short link is valid and not expired
        response.sendRedirect(urlToReturn.getShortLink());
        return null;

    }
}

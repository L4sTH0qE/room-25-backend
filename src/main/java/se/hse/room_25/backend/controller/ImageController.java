package se.hse.room_25.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/image")
public class ImageController {

    private RestTemplate restTemplate;

    @Autowired
    public void prepare(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping()
    public ResponseEntity<byte[]> getImage(@RequestParam String imagePath) {

        String SOURCE_URL = "https://github.com/L4sTH0qE/room-25-assets/raw/main/";
        byte[] imageBytes = restTemplate.getForObject(SOURCE_URL + imagePath, byte[].class);

        if (imageBytes != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

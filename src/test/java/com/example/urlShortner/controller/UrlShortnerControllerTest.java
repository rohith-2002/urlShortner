package com.example.urlShortner.controller;

import com.example.urlShortner.DTO.ShortenResponce;
import com.example.urlShortner.exception.DuplicateAliasException;
import com.example.urlShortner.exception.UrlNotFoundException;
import com.example.urlShortner.exception.GlobalExceptionHandler;
import com.example.urlShortner.service.ShortUrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UrlShortnerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ShortUrlService service;

    @BeforeEach
    void setup() {
        UrlShortnerController controller = new UrlShortnerController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testShortenSuccess() throws Exception {
        ShortenResponce resp = new ShortenResponce("q0", "http://localhost:8080/q0", "https://example.com/very/long/path", false, null);
        when(service.shorten(any())).thenReturn(resp);

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"url\":\"https://example.com/very/long/path\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode").value("q0"))
                .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/q0"));
    }

    @Test
    void testShortenDuplicateAlias() throws Exception {
        when(service.shorten(any())).thenThrow(new DuplicateAliasException("Alias 'my-link' is already taken"));

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"url\":\"https://example.com/other\",\"customAlias\":\"my-link\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Alias 'my-link' is already taken"));
    }

    @Test
    void testShortenInvalidUrl() throws Exception {
        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"url\":\"not-a-url\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[0]").exists());
    }

    @Test
    void testRedirectSuccess() throws Exception {
        when(service.resolve(eq("q0"))).thenReturn("https://example.com/very/long/path");

        mockMvc.perform(get("/q0"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com/very/long/path"));
    }

    @Test
    void testRedirectNotFound() throws Exception {
        when(service.resolve(eq("doesnotexist"))).thenThrow(new UrlNotFoundException("No URL found for code: doesnotexist"));

        mockMvc.perform(get("/doesnotexist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No URL found for code: doesnotexist"));
    }
}

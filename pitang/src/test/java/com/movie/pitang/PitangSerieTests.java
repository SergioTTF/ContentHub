package com.movie.pitang;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.movie.pitang.models.Serie;
import com.movie.pitang.repositorio.SerieRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PitangSerieTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SerieRepository serieMockRepository;

    @Test
    public void serieControllerPopulate() throws Exception {
        mockMvc.perform(post("/series/populate").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(20)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").isNotEmpty());
        Mockito.verify(serieMockRepository, Mockito.times(20)).save(ArgumentMatchers.any(Serie.class));
    }

    @Test
    public void serieControllerGetAll() throws Exception {
        Serie serie = new Serie();
        serie.setTitle("GOT");
        Page<Serie> seriePage = new PageImpl<Serie>(Arrays.asList(serie));
        when(serieMockRepository.findAll(ArgumentMatchers.isA(Pageable.class))).thenReturn(seriePage);
        mockMvc.perform(get("/series").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].title").value(serie.getTitle()));
    }

    @Test
    public void serieControllerGetById() throws Exception {
        Serie serie = new Serie();
        serie.setId(1L);
        serie.setTitle("GOT");
        Optional<Serie> serieOptional = Optional.of(serie);
        when(serieMockRepository.findById(1L)).thenReturn(serieOptional);
        mockMvc.perform(get("/series/{id}", 1).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(serie.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }

    @Test
    public void serieControllerPost() throws Exception {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Serie serie = new Serie();
        serie.setId(1L);
        serie.setTitle("GOT");
        mockMvc.perform(post("/series")
                .content(ow.writeValueAsString(serie))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(serie.getTitle()));
        Mockito.verify(serieMockRepository, Mockito.times(1)).save(ArgumentMatchers.any(Serie.class));
    }

    @Test
    public void serieControllerPut() throws Exception {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Serie serie = new Serie();
        serie.setId(1L);
        serie.setTitle("GOT");
        Optional<Serie> serieOptional = Optional.of(serie);
        when(serieMockRepository.findById(1L)).thenReturn(serieOptional);
        Serie updatedSerie = new Serie();
        updatedSerie.setId(1L);
        updatedSerie.setTitle("Flash");
        mockMvc.perform(put("/series")
                .param("id", "1")
                .content(ow.writeValueAsString(updatedSerie))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(updatedSerie.getTitle()));
    }

    @Test
    public void serieControllerDelete() throws Exception {
        Serie serie = new Serie();
        serie.setId(1L);
        serie.setTitle("GOT");
        Optional<Serie> serieOptional = Optional.of(serie);
        when(serieMockRepository.findById(1L)).thenReturn(serieOptional);
        mockMvc.perform(delete("/series/{id}", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(serie.getTitle()));
        Mockito.verify(serieMockRepository, Mockito.times(1)).deleteById(ArgumentMatchers.any(Long.class));
    }

    @Test
    public void serieControllerFilter() throws Exception {
        Serie serie = new Serie();
        serie.setId(1L);
        serie.setTitle("GOT");
        serie.setYearOfRelease(2009);
        serie.setLanguage("en");
        Page<Serie> seriePage = new PageImpl<Serie>(Arrays.asList(serie));
        when(serieMockRepository.findAll(ArgumentMatchers.isA(Specification.class),ArgumentMatchers.isA(Pageable.class))).thenReturn(seriePage);
        mockMvc.perform(get("/series/filter/")
                .param("title", serie.getTitle())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].title").value(serie.getTitle()));
    }
}

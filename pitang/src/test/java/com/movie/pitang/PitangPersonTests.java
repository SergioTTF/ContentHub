package com.movie.pitang;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.movie.pitang.models.Movie;
import com.movie.pitang.models.Person;
import com.movie.pitang.repositorio.PersonRepository;
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
public class PitangPersonTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonRepository personMockRepository;

    @Test
    public void personControllerPopulate() throws Exception {
        mockMvc.perform(post("/people/populate")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(20)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").isNotEmpty());
        Mockito.verify(personMockRepository, Mockito.times(20)).save(ArgumentMatchers.any(Person.class));
    }

    @Test
    public void personControllerGetAll() throws Exception {
        Person person = new Person();
        person.setName("Nathan");
        Page<Person> personPage = new PageImpl<Person>(Arrays.asList(person));
        when(personMockRepository.findAll(ArgumentMatchers.isA(Pageable.class))).thenReturn(personPage);
        mockMvc.perform(get("/people").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value(person.getName()));
    }

    @Test
    public void personControllerGetById() throws Exception {
        Person person = new Person();
        person.setId(1L);
        person.setName("Nathan");
        Optional<Person> optionalPerson = Optional.of(person);
        when(personMockRepository.findById(1L)).thenReturn(optionalPerson);
        mockMvc.perform(get("/people/{id}", 1).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(person.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }

    @Test
    public void personControllerPost() throws Exception {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Person person = new Person();
        person.setId(1L);
        person.setName("Nathan");
        mockMvc.perform(post("/people")
                .content(ow.writeValueAsString(person))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(person.getName()));
        Mockito.verify(personMockRepository, Mockito.times(1)).save(ArgumentMatchers.any(Person.class));
    }

    @Test
    public void personControllerPut() throws Exception {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Person person = new Person();
        person.setId(1L);
        person.setName("Nathan");
        Optional<Person> optionalPerson = Optional.of(person);
        when(personMockRepository.findById(1L)).thenReturn(optionalPerson);
        Person updatedPerson = new Person();
        updatedPerson.setId(1L);
        updatedPerson.setName("Tony");
        mockMvc.perform(put("/people")
                .param("id", "1")
                .content(ow.writeValueAsString(updatedPerson))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(updatedPerson.getName()));
    }

    @Test
    public void personControllerDelete() throws Exception {
        Person person = new Person();
        person.setId(1L);
        person.setName("Nathan");
        Optional<Person> optionalPerson = Optional.of(person);
        when(personMockRepository.findById(1L)).thenReturn(optionalPerson);
        mockMvc.perform(delete("/people/{id}", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(person.getName()));
        Mockito.verify(personMockRepository, Mockito.times(1)).deleteById(ArgumentMatchers.any(Long.class));
    }

    @Test
    public void personControllerFilter() throws Exception {
        Person person = new Person();
        person.setId(1L);
        person.setName("Nathan");
        Page<Person> personPage = new PageImpl<Person>(Arrays.asList(person));
        when(personMockRepository.findByNameContaining(ArgumentMatchers.isA(String.class),ArgumentMatchers.isA(Pageable.class))).thenReturn(personPage);
        mockMvc.perform(get("/people/filter/")
                .param("name", person.getName())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value(person.getName()));
    }
}

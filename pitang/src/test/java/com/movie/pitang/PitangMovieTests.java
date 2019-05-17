package com.movie.pitang;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.movie.pitang.models.Movie;
import com.movie.pitang.repositorio.MovieRepository;
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
public class PitangMovieTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MovieRepository movieMockRepository;

	@Test
	public void movieControllerPopulate() throws Exception {
		mockMvc.perform(post("/movies/populate").accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(20)))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].id").isNotEmpty());
		Mockito.verify(movieMockRepository, Mockito.times(20)).save(ArgumentMatchers.any(Movie.class));
	}

	@Test
	public void movieControllerGetAll() throws Exception {
		Movie movie = new Movie();
		movie.setTitle("Batman");
		Page<Movie> moviePage = new PageImpl<Movie>(Arrays.asList(movie));
		when(movieMockRepository.findAll(ArgumentMatchers.isA(Pageable.class))).thenReturn(moviePage);
		mockMvc.perform(get("/movies").accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.content").exists())
				.andExpect(MockMvcResultMatchers.jsonPath("$.content", hasSize(1)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.content[0].title").value(movie.getTitle()));
	}

	@Test
	public void movieControllerGetById() throws Exception {
		Movie movie = new Movie();
		movie.setId(1L);
		movie.setTitle("Batman");
		Optional<Movie> movieOptional = Optional.of(movie);
		when(movieMockRepository.findById(1L)).thenReturn(movieOptional);
		mockMvc.perform(get("/movies/{id}", 1).accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.title").value(movie.getTitle()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
	}

	@Test
	public void movieControllerPost() throws Exception {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		Movie movie = new Movie();
		movie.setId(1L);
		movie.setTitle("Batman");
		mockMvc.perform(post("/movies")
				.content(ow.writeValueAsString(movie))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.title").value(movie.getTitle()));
		Mockito.verify(movieMockRepository, Mockito.times(1)).save(ArgumentMatchers.any(Movie.class));
	}

	@Test
	public void movieControllerPut() throws Exception {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		Movie movie = new Movie();
		movie.setId(1L);
		movie.setTitle("Batman");
		Optional<Movie> movieOptional = Optional.of(movie);
		when(movieMockRepository.findById(1L)).thenReturn(movieOptional);
		Movie updatedMovie = new Movie();
		updatedMovie.setId(1L);
		updatedMovie.setTitle("Superman");
		mockMvc.perform(put("/movies")
				.param("id", "1")
				.content(ow.writeValueAsString(updatedMovie))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.title").value(updatedMovie.getTitle()));
	}

	@Test
	public void movieControllerDelete() throws Exception {
		Movie movie = new Movie();
		movie.setId(1L);
		movie.setTitle("Batman");
		Optional<Movie> movieOptional = Optional.of(movie);
		when(movieMockRepository.findById(1L)).thenReturn(movieOptional);
		mockMvc.perform(delete("/movies/{id}", 1)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.title").value(movie.getTitle()));
		Mockito.verify(movieMockRepository, Mockito.times(1)).deleteById(ArgumentMatchers.any(Long.class));
	}

	@Test
	public void movieControllerFilter() throws Exception {
		Movie movie = new Movie();
		movie.setId(1L);
		movie.setTitle("Batman");
		movie.setYearOfRelease(2000);
		movie.setLanguage("en");
		Page<Movie> moviePage = new PageImpl<Movie>(Arrays.asList(movie));
		when(movieMockRepository.findAll(ArgumentMatchers.isA(Specification.class),ArgumentMatchers.isA(Pageable.class))).thenReturn(moviePage);
		mockMvc.perform(get("/movies/filter/")
				.param("title", movie.getTitle())
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.content[0].title").value(movie.getTitle()));
	}

}

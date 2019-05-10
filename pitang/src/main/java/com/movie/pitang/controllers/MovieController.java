package com.movie.pitang.controllers;

import com.movie.pitang.error.ResourceNotFoundException;
import com.movie.pitang.models.Movie;
import com.movie.pitang.models.Person;
import com.movie.pitang.repositorio.PersonRepository;
import com.movie.pitang.utils.MovieSpecification;
import com.movie.pitang.repositorio.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

import static com.movie.pitang.utils.ApiDmdb.*;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/movies")
public class MovieController {


    @Autowired
    public MovieRepository movieRepository;

    @Autowired
    PersonRepository personRepository;

    @GetMapping
    public ResponseEntity<?> getAll(Pageable pageable){
        Page<Movie> moviePage = movieRepository.findAll(pageable);
        return new ResponseEntity<>(moviePage, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable Long id){
        Optional<Movie> movie = movieRepository.findById(id);
        if (movie.isPresent()){
            return new ResponseEntity<>(movie.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException("There is no movie with this id");
        }
    }

    @GetMapping(path = "/filter/")
    public ResponseEntity<?> getMovieByFields(Pageable pageable, @RequestParam(required = false) String title,
                                              @RequestParam(required = false) Integer year,
                                              @RequestParam(required = false) String language){
        Page<Movie> moviePage = movieRepository.findAll(MovieSpecification.searchMovie(title, year, language), pageable);
        return new ResponseEntity<>(moviePage,HttpStatus.OK);
    }

    @Transactional(rollbackFor = Exception.class)
    @PostMapping
    public ResponseEntity<?> createMovie(@Valid @RequestBody Movie movie){
        this.movieRepository.save(movie);
        return new ResponseEntity<>(movie, HttpStatus.CREATED);
    }

    @Transactional(rollbackFor = Exception.class)
    @PutMapping
    public ResponseEntity<?> updateMovie(@Valid @RequestBody Movie movie, @RequestParam Long id){
        Optional<Movie> optionalMovie = movieRepository.findById(id);
        if (optionalMovie.isPresent()){
            movie.setId(id);
            movieRepository.save(movie);
            return new ResponseEntity<>(optionalMovie.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException("There is no movie with this id");
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable Long id){
        Optional<Movie> optionalMovie = movieRepository.findById(id);
        if (optionalMovie.isPresent()){
            movieRepository.deleteById(id);
            return new ResponseEntity<>(optionalMovie.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException("There is no movie with this id");
        }
    }

    @PostMapping(path = "/populate")
    public ResponseEntity<?> fillDatabase(){
        List<Object> list = getTrendingObjects("movie");
        for (Object o:list){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LinkedHashMap<String, Object> obj = (LinkedHashMap<String, Object>)o;

            Map<String, Object> mapMovie = getJson("movie",(Integer) obj.get("id"));

            if (mapMovie!=null) {
                Movie movie = new Movie();

                if(mapMovie.get("title") != null){
                    movie.setTitle(mapMovie.get("title").toString());
                }

                if (mapMovie.get("overview")!= null){
                    String desc = (String)mapMovie.get("overview");
                    if (desc.length()<255){
                        movie.setDescription(desc);
                    } else {
                        movie.setDescription(desc.substring(0,254));
                    }
                }

                if (mapMovie.get("release_date")!=null){
                    movie.setYearOfRelease(Integer.parseInt(mapMovie.get("release_date").toString().substring(0,4)));
                }

                if (mapMovie.get("production_countries")!=null && ((List<LinkedHashMap>)mapMovie.get("production_countries")).size()>0){
                    movie.setHomeCountry((((List<LinkedHashMap>)mapMovie.get("production_countries"))
                            .get(0)).get("name").toString());
                }

                if (mapMovie.get("original_language")!=null){
                    movie.setLanguage(mapMovie.get("original_language").toString());
                }

                if (mapMovie.get("runtime")!=null){
                    movie.setDuration((Integer) mapMovie.get("runtime"));
                }

                if(mapMovie.get("genres")!=null){
                    List<String> genres = new ArrayList<>();
                    for(LinkedHashMap genre:(List<LinkedHashMap>)mapMovie.get("genres")){
                        genres.add((String) genre.get("name"));
                    }

                    movie.setGenres(genres);
                }

                List<LinkedHashMap<String,Object>> listCast = getCast("movie", (Integer)obj.get("id"));
                List<Person> cast = new ArrayList<>();

                if (listCast!=null){
                    for(LinkedHashMap<String,Object> personLinkedMap:listCast){
                        Map<String, Object> mapCast = getJson("person",(Integer) personLinkedMap.get("id"));
                        if (mapCast!=null && personRepository.findByName((String)mapCast.get("name"))==null){
                            cast.add(persistPerson(mapCast));
                        }
                    }
                    movie.setCast(cast);
                }

                if (mapMovie.get("backdrop_path")!=null){
                    movie.setImgPath(mapMovie.get("backdrop_path").toString());
                }
                movieRepository.save(movie);
            }
        }
        return new ResponseEntity<>(list, HttpStatus.CREATED);
    }

    public Person persistPerson(Map<String, Object> mapPerson){
        Person person = new Person();
        if (mapPerson.get("name") != null){
            person.setName((String) mapPerson.get("name"));
        }
        if (mapPerson.get("gender")!=null){
            person.setGender((Integer) mapPerson.get("gender"));
        }
        if (mapPerson.get("place_of_birth")!=null){
            person.setHomeTown((String)mapPerson.get("place_of_birth"));
        }
        List<LinkedHashMap<String,Object>> listImg = getPersonImg((Integer) mapPerson.get("id"));
        if (listImg != null && listImg.size()>0){
            person.setImagePath((String) listImg.get(0).get("file_path"));
        }
        person.setHeight(1.70f);
        person.setLivingCountry("USA");
        return personRepository.save(person);
    }
}

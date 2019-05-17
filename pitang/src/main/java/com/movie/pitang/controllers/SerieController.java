package com.movie.pitang.controllers;

import com.movie.pitang.error.ResourceNotFoundException;
import com.movie.pitang.models.Person;
import com.movie.pitang.models.Serie;
import com.movie.pitang.repositorio.PersonRepository;
import com.movie.pitang.repositorio.SerieRepository;
import com.movie.pitang.utils.SerieSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

import static com.movie.pitang.utils.ApiDmdb.*;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/series")
public class SerieController  {

    @Autowired
    SerieRepository serieRepository;

    @Autowired
    PersonRepository personRepository;

    @GetMapping
    public ResponseEntity<?> getAll(Pageable pageable){
        Page<Serie> moviePage = serieRepository.findAll(pageable);
        return new ResponseEntity<>(moviePage, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getSerieById(@PathVariable Long id){
        Optional<Serie> serie = serieRepository.findById(id);
        if (serie.isPresent()){
            return new ResponseEntity<>(serie.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException("There is no tv serie with this id");
        }
    }

    @GetMapping(path = "/filter/")
    public ResponseEntity<?> getSerieByFields(Pageable pageable, @RequestParam(required = false) String title,
                                              @RequestParam(required = false) Integer year,
                                              @RequestParam(required = false) String language){
        Page<Serie> seriePage = serieRepository.findAll(SerieSpecification.searchSerie(title, year, language), pageable);
        return new ResponseEntity<>(seriePage,HttpStatus.OK);
    }

    @Transactional(rollbackFor = Exception.class)
    @PostMapping
    public ResponseEntity<?> createSerie(@Valid @RequestBody Serie serie){
        this.serieRepository.save(serie);
        return new ResponseEntity<>(serie, HttpStatus.CREATED);
    }

    @Transactional(rollbackFor = Exception.class)
    @PutMapping
    public ResponseEntity<?> updateSerie(@Valid @RequestBody Serie serie, @RequestParam Long id){
        Optional<Serie> optionalSerie = serieRepository.findById(id);
        if (optionalSerie.isPresent()){
            serie.setId(id);
            serieRepository.save(serie);
            return new ResponseEntity<>(serie, HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException("There is no tv serie with this id");
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteSerie(@PathVariable Long id){
        Optional<Serie> optionalSerie = serieRepository.findById(id);
        if (optionalSerie.isPresent()){
            serieRepository.deleteById(id);
            return new ResponseEntity<>(optionalSerie.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException("There is no tv serie with this id");
        }
    }

    @PostMapping("/populate")
    public ResponseEntity<?> fillDateBase(){

        List<Object> list = getTrendingObjects("tv");
        for (Object o:list){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LinkedHashMap<String, Object> obj = (LinkedHashMap<String, Object>)o;

            Map<String, Object> mapSerie = getJson("tv",(Integer) obj.get("id"));
            if (mapSerie!=null){
                Serie serie = new Serie();
                if(mapSerie.get("name") != null){
                    serie.setTitle(mapSerie.get("name").toString());
                }
                if (mapSerie.get("overview")!= null){
                    String desc = (String)mapSerie.get("overview");
                    if (desc.length()<255){
                        serie.setDescription(desc);
                    } else {
                        serie.setDescription(desc.substring(0,254));
                    }

                }
                if (mapSerie.get("episode_run_time")!=null && ((List)mapSerie.get("episode_run_time")).size() > 0){
                    serie.setDuration((Integer) ((List)mapSerie.get("episode_run_time")).get(0));
                }
                if (mapSerie.get("original_language")!=null){
                    serie.setLanguage(mapSerie.get("original_language").toString());
                }
                if (mapSerie.get("origin_country")!=null && ((List)mapSerie.get("origin_country")).size() > 0){
                    serie.setHomeCountry((((List)mapSerie.get("origin_country")).get(0)).toString());
                }
                if(mapSerie.get("number_of_seasons")!=null){
                    serie.setNumberOfSeasons((Integer) mapSerie.get("number_of_seasons"));
                }
                if (mapSerie.get("first_air_date")!=null){
                    serie.setYearOfRelease(Integer.parseInt(mapSerie.get("first_air_date").toString().substring(0,4)));
                }
                if(mapSerie.get("genres")!=null){
                    List<String> genres = new ArrayList<>();
                    for(LinkedHashMap genre:(List<LinkedHashMap>)mapSerie.get("genres")){
                        genres.add((String) genre.get("name"));
                    }

                    serie.setGenres(genres);
                }

                List<LinkedHashMap<String,Object>> listCast = getCast("tv", (Integer)obj.get("id"));
                List<Person> cast = new ArrayList<>();
                if (listCast!=null){
                    for(LinkedHashMap<String,Object> personLinkedMap:listCast){
                        Map<String, Object> mapCast = getJson("person",(Integer) personLinkedMap.get("id"));
                        if (mapCast!=null && personRepository.findByName((String)mapCast.get("name"))==null){
                            cast.add(persistPerson(mapCast));
                        }
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    serie.setCast(cast);
                }
                if (mapSerie.get("backdrop_path")!=null){
                    serie.setImgPath(mapSerie.get("backdrop_path").toString());
                }

                serieRepository.save(serie);
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

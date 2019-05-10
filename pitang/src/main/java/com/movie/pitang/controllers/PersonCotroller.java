package com.movie.pitang.controllers;

import com.movie.pitang.error.ResourceNotFoundException;
import com.movie.pitang.models.Person;
import com.movie.pitang.repositorio.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.text.WordUtils;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.movie.pitang.utils.ApiDmdb.*;

@RestController
@RequestMapping("/people")
@CrossOrigin(origins = "http://localhost:4200")
public class PersonCotroller {

    @Autowired
    private PersonRepository personRepository;

    @GetMapping
    public ResponseEntity<?> getAll(Pageable pageable) {
        Page<Person> peoplePage = personRepository.findAll(pageable);
        return new ResponseEntity<>(peoplePage, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getPersonById(@PathVariable Long id) {
        Optional<Person> person = personRepository.findById(id);
        if (person.isPresent()) {
            return new ResponseEntity<>(person.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException("There is no person with this id");
        }
    }

    @GetMapping(path = "/filter/")
    public ResponseEntity<?> getPersonByName(Pageable pageable, @RequestParam(value = "name") String name) {
        Page<Person> personPage = personRepository.findByNameContaining(WordUtils.capitalize(name), pageable);
        return new ResponseEntity<>(personPage, HttpStatus.OK);

    }
    @Transactional(rollbackFor = Exception.class)
    @PostMapping
    public ResponseEntity<?> createPerson(@Valid @RequestBody Person person){
        this.personRepository.save(person);
        return new ResponseEntity<>(person, HttpStatus.CREATED);
    }

    @Transactional(rollbackFor = Exception.class)
    @PutMapping
    public ResponseEntity<?> updatePerson(@RequestBody Person person, @RequestParam(value = "id") Long id){
        Optional<Person> response = personRepository.findById(id);
        if (response.isPresent()) {
            person.setId(id);
            personRepository.save(person);
            return new ResponseEntity<>(response.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException("There is no person with this id");
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deletePerson(@PathVariable Long id){
        Optional<Person> person = personRepository.findById(id);
        if (person.isPresent()) {
            personRepository.deleteById(id);
            return new ResponseEntity<>(person.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException("There is no person with this id");
        }
    }

    @PostMapping(path = "/populate")
    public ResponseEntity<?> fillDatabase(){

        List<Object> list = getTrendingObjects("person");
        for(Object o:list){
            LinkedHashMap<String, Object> obj = (LinkedHashMap<String, Object>)o;
            persistPerson(getJson("person",(Integer) obj.get("id")));
        }

        return new ResponseEntity<>(list, HttpStatus.CREATED);
    }

    public void persistPerson(Map<String, Object> mapPerson){
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
        if (mapPerson.get("place_of_birth")!=null){
            person.setHomeTown((String)mapPerson.get("place_of_birth"));
        }
        List<LinkedHashMap<String,Object>> listImg = getPersonImg((Integer) mapPerson.get("id"));
        if (listImg != null && listImg.size()>0){
            person.setImagePath((String) listImg.get(0).get("file_path"));
        }
        person.setHeight(1.70f);
        person.setLivingCountry("USA");
        personRepository.save(person);
    }

    public PersonRepository getPersonRepository() {
        return personRepository;
    }

    public void setPersonRepository(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }
}

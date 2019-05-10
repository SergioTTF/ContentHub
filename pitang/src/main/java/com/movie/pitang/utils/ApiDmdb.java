package com.movie.pitang.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ApiDmdb {
    public static final String urlBase = "https://api.themoviedb.org/3/";
    public static final String APIkey = "391a7c4171333b8b4a109fbcc255bba6";

    public static List<Object> getTrendingObjects(String type){
        try {
            URL url = new URL(urlBase + "trending/" + type + "/day?api_key=" + APIkey);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> map = objectMapper.readValue(url, Map.class);
            return (List<Object>) map.get("results");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, Object> getJson(String type, Integer id){
        try {
            URL url = new URL(urlBase + type + "/" + id + "?api_key=" + APIkey);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(url, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<LinkedHashMap<String,Object>> getCast(String type, Integer id){
        try {
            URL url = new URL(urlBase + type + "/" + id + "/credits?api_key=" + APIkey);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> map =  objectMapper.readValue(url, Map.class);
            return (List<LinkedHashMap<String,Object>>)map.get("cast");
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static List<LinkedHashMap<String,Object>> getPersonImg(Integer id){
        try {
            URL url = new URL(urlBase  + "person/" + id + "/images?api_key=" + APIkey);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> map =  objectMapper.readValue(url, Map.class);
            return (List<LinkedHashMap<String,Object>>)map.get("profiles");
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

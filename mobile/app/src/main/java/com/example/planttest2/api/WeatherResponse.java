package com.example.planttest2.api;

import java.util.List;
public class WeatherResponse {
    public class Main {
        public float temp;
        public float temp_min;
        public float temp_max;
        public int humidity;
    }

    public class Weather {
        public String main;
        public String description;
        public String icon;
    }

    public class Sys {
        public String country;
    }

    public Main main;
    public List<Weather> weather;
    public String name;
    public Sys sys;
}

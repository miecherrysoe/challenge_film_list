package com.example.omdb_challenge_app.model;

public class MovieDetail {
        private String  released;
        private String runtime;
        private String genre;
        private String director;
        private String  production;
        private String imdbRating;
        private String  writer;
        private String   actors;
        private String  title;
        private String  year;
        private String   poster;


        public MovieDetail() {
        }

        public String getReleased() {
            return released;
        }

        public void   setReleased(String released) {
            this.released = released;
        }

        public String getRuntime() {
            return runtime;
        }

        public void   setRuntime(String runtime) {
            this.runtime = runtime;
        }

        public String getGenre() {
            return genre;
        }

        public void   setGenre(String genre) {
            this.genre = genre;
        }

        public String getDirector() {
            return director;
        }

        public void   setDirector(String director) {
            this.director = director;
        }

        public String getProduction() {
            return production;
        }

        public void   setProduction(String production) {
            this.production = production;
        }

        public String getImdbRating() {
            return imdbRating;
        }

        public void   setImdbRating(String imdbRating) {
            this.imdbRating = imdbRating;
        }

        public String getWriter() {
            return writer;
        }

        public void   setWriter(String writer) {
            this.writer = writer;
        }

        public String getActors() {
            return actors;
        }

        public void   setActors(String actors) {
            this.actors = actors;
        }

        public String getTitle() {
            return title;
        }

        public void   setTitle(String title) {
            this.title = title;
        }

        public String getYear() {
            return year;
        }

        public void   setYear(String year) {
            this.year = year;
        }

        public String getPoster() {
            return poster;
        }

        public void   setPoster(String poster) {
            this.poster = poster;
        }
    }

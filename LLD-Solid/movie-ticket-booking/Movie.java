public class Movie {
    private String id;
    private String title;
    private String language;
    private String genre;
    private int durationMinutes;

    public Movie(String id, String title, String language, String genre, int durationMinutes) {
        this.id = id;
        this.title = title;
        this.language = language;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getLanguage() { return language; }
    public String getGenre() { return genre; }
    public int getDurationMinutes() { return durationMinutes; }

    @Override
    public String toString() {
        return title + " (" + language + ", " + genre + ", " + durationMinutes + " min)";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return id.equals(movie.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}

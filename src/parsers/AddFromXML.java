package parsers;

public class AddFromXML {
    public static void main(String[] args) {
        SAXActorParser spe = new SAXActorParser();
        spe.runExample();
        SAXCastParser sce = new SAXCastParser();
        sce.runExample();
        SAXMainParser sme = new SAXMainParser();
        sme.runExample();

        LoadFromFile lf = new LoadFromFile();
        lf.loadMoviedata();
        lf.loadStarsdata("src/parsers/stars.txt");

        lf.loadCast("src/parsers/cast.txt");
        lf.loadGenresInMovies("src/parsers/genres-in-movies.txt");
    }
}

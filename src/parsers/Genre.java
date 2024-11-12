package parsers;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashMap;

public class Genre {
    private String name;
    private Integer id;
    private boolean inconsistent;

    private HashMap<String, String> genreMap = new HashMap<String, String>();

    private void loadHashMap() {
        genreMap.put("actn", "Action");
        genreMap.put("adlt", "Adult");
        genreMap.put("advt", "Adventure");
        genreMap.put("anim", "Animation");
        genreMap.put("bio", "Biography");
        genreMap.put("comd", "Comedy");
        genreMap.put("crim", "Crime");
        genreMap.put("docu", "Documentary");
        genreMap.put("dram", "Drama");
        genreMap.put("faml", "Family");
        genreMap.put("fant", "Fantasy");
        genreMap.put("hist", "History");
        genreMap.put("horr", "Horror");
        genreMap.put("music", "Music");
        genreMap.put("musical", "Musical");
        genreMap.put("myst", "Mystery");
        genreMap.put("retv", "Reality-TV");
        genreMap.put("romt", "Romance");
        genreMap.put("scfi", "Sci-Fi");
        genreMap.put("sprt", "Sport");
        genreMap.put("thrl", "Thriller");
        genreMap.put("war", "War");
        genreMap.put("west", "Western");
    }

    public Genre(){
        loadHashMap();
    }

    public Genre(Integer id, String name) {
        this.name = name;
        this.id  = id;
        loadHashMap();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(genreMap.get(name.toLowerCase()) == null){
            this.inconsistent = true;
            this.name = name;
        }
        else{
            this.inconsistent = false;
            this.name = genreMap.get(name.toLowerCase());
        }
    }

    public void setInconsistent(boolean b){
        this.inconsistent = b;
    }

    public boolean getInconsistent(){
        return this.inconsistent;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ID: " + getId());
        sb.append("Name: " + getName());

        return sb.toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(name).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Genre))
            return false;
        if (obj == this)
            return true;

        Genre genreInstance = (Genre) obj;
        return new EqualsBuilder().append(name, genreInstance.name).isEquals();
    }
}
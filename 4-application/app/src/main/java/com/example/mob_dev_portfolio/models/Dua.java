package com.example.mob_dev_portfolio.models;

public class Dua {

    private int id;
    private String title;
    private String arabic;
    private String transliteration;
    private String meaning;

    public Dua(int id, String title, String arabic, String transliteration, String meaning) {
        this.id = id;
        this.title = title;
        this.arabic = arabic;
        this.transliteration = transliteration;
        this.meaning = meaning;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArabic() {
        return arabic;
    }

    public void setArabic(String arabic) {
        this.arabic = arabic;
    }

    public String getTransliteration() {
        return transliteration;
    }

    public void setTransliteration(String transliteration) {
        this.transliteration = transliteration;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    @Override
    public String toString() {
        return "Dua{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", arabic='" + arabic + '\'' +
                ", transliteration='" + transliteration + '\'' +
                ", meaning='" + meaning + '\'' +
                '}';
    }
}

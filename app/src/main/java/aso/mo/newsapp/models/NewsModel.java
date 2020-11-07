package aso.mo.newsapp.models;

import java.util.Objects;

public class NewsModel {
    private String category;
    private String title;
    private String author;
    private String date;
    private String url;

    public NewsModel(String category, String title, String author, String date, String url) {
        this.category = category;
        this.title = title;
        this.author = author;
        this.date = date;
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewsModel)) return false;
        NewsModel newsModel = (NewsModel) o;
        return Objects.equals(category, newsModel.category) &&
                Objects.equals(title, newsModel.title) &&
                Objects.equals(author, newsModel.author) &&
                Objects.equals(date, newsModel.date) &&
                Objects.equals(url, newsModel.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, title, author, date, url);
    }
}

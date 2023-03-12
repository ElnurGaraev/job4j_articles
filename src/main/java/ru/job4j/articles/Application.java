package ru.job4j.articles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.articles.service.SimpleArticleService;
import ru.job4j.articles.service.generator.RandomArticleGenerator;
import ru.job4j.articles.store.ArticleStore;
import ru.job4j.articles.store.WordStore;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.Properties;

public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class.getSimpleName());

    public static final int TARGET_COUNT = 1_000_000;

    public static void main(String[] args) {
        var properties = loadProperties();
        SoftReference<WordStore> wordStoreSoft = new SoftReference<>(new WordStore(properties));
        var wordStore = wordStoreSoft.get();
        SoftReference<ArticleStore> articleStoreSoft = new SoftReference<>(new ArticleStore(properties));
        var articleStore = articleStoreSoft.get();
        SoftReference<RandomArticleGenerator> randomArticle = new SoftReference<>(new RandomArticleGenerator());
        var articleGenerator = randomArticle.get();
        SoftReference<SimpleArticleService> simpleArticleService = new SoftReference<>(new SimpleArticleService(articleGenerator));
        var articleService = simpleArticleService.get();
        articleService.generate(wordStore, TARGET_COUNT, articleStore);
    }

    private static Properties loadProperties() {
        LOGGER.info("Загрузка настроек приложения");
        var properties = new Properties();
        try (InputStream in = Application.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(in);
        } catch (Exception e) {
            LOGGER.error("Не удалось загрузить настройки. { }", e.getCause());
            throw new IllegalStateException();
        }
        return properties;
    }
}

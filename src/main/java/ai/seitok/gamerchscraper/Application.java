package ai.seitok.gamerchscraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Application {

    public static final String BASE_URL = "http://kemono-friends.gamerch.com/";

    public static void main(String[] args) throws Throwable {
        //Document friendsPage = Jsoup.connect(BASE_URL + URLEncoder.encode("キャラ一覧")).get();
        Document itemsPage = Jsoup.connect(BASE_URL + URLEncoder.encode("アイテム一覧")).get();

        ExecutorService exec = Executors.newFixedThreadPool(8);

        //DataMine friendsMine = new DataMine(findTable(friendsPage));
        //friendsMine.scoutJewels("tbody");

        DataMine itemsMine = new DataMine(findTable(itemsPage));
        itemsMine.scoutJewels("tbody");

//        List<Future<Jewel>> miningOperations = friendsMine.mineAllJewelsWithService(exec);
//        miningOperations.addAll(itemsMine.mineAllJewelsWithService(exec));
        List<Future<Jewel>> miningOperations = itemsMine.mineAllJewelsWithService(exec);

        exec.shutdown();
        exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

        miningOperations.forEach(future -> {
            try {
                Jewel minedJewel = future.get();
                System.out.println(minedJewel.getName());
                minedJewel.getValues().forEach((key, value) -> {
                    String tldKey = Translator.tryKeyTranslation(key);
                    String tldValue = Translator.tryValueTranslation(value);

                    System.out.println("\t" + tldKey + ": " + tldValue);

                    String details = minedJewel.getMinedDetails().get(key);
                    if(details != null){
                        System.out.println("\t\t" + details);
                    }
                });
            } catch (Throwable t){
                t.printStackTrace();
            }
        });


//        friendsMine.getErrors().forEach((on, err) -> {
//            System.out.println("Error while mining \"" + on + "\"");
//            err.printStackTrace();
//        });

        itemsMine.getErrors().forEach((on, err) -> {
            System.out.println("Error while mining \"" + on + "\"");
            err.printStackTrace();
        });
    }


    public static Element findTable(Document doc){
        Elements tables = doc.getElementsByTag("table");
        return tables.stream()
                .filter(e -> e.id().startsWith("ui_wikidb_table_"))
                .reduce((a, b) -> { throw new IllegalStateException("multiple tables with \"ui_wikidb_table_\" found");})
                .orElseThrow(NoSuchElementException::new);
    }

}

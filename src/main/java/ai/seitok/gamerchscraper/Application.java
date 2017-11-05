package ai.seitok.gamerchscraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.*;

public class Application {

    public static final String BASE_URL = "http://kemono-friends.gamerch.com/";
    public static final String FRIENDS_PAGE_PATH = "キャラ一覧";
    public static final String ITEMS_PAGE_PATH = "アイテム一覧";

    public static void main(String[] args) throws Throwable {
        Document friendsPage = Jsoup.connect(BASE_URL + URLEncoder.encode(FRIENDS_PAGE_PATH)).get();
        Document itemsPage = Jsoup.connect(BASE_URL + URLEncoder.encode(ITEMS_PAGE_PATH)).get();
        ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        DataMine friendsMine = new DataMine(findTable(friendsPage));
        DataMine itemsMine = new DataMine(findTable(itemsPage));

        friendsMine.scoutJewels("tbody");
        itemsMine.scoutJewels("tbody");

        friendsMine.mineAllJewelsWithService(exec);
        itemsMine.mineAllJewelsWithService(exec);

        exec.shutdown();
        exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

        writeResults("friends.txt", friendsMine);
        writeResults("items.txt", itemsMine);

        friendsMine.getErrors().forEach((on, err) -> {
            System.out.println("Error while mining \"" + on + "\"");
            err.printStackTrace();
        });

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

    public static void writeResults(String path, DataMine mine) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        mine.getMiners().forEach(miner -> {
            try {
                Jewel minedJewel = miner.get();
                bw.write(minedJewel.getName());
                bw.newLine();

                for(Map.Entry<String, String> entry : minedJewel.getValues().entrySet()){
                    String tldKey = Translator.tryKeyTranslation(entry.getKey());
                    String tldValue = Translator.tryValueTranslation(entry.getValue());

                    bw.write("\t" + tldKey + ": " + tldValue);
                    bw.newLine();

                    String details = minedJewel.getMinedDetails().get(entry.getKey());
                    if(details != null){
                        bw.write("\t\t" + details);
                        bw.newLine();
                    }
                }
            } catch (InterruptedException e){
                // drop exception; interruption would have to be outside
            } catch (ExecutionException e){
                e.getCause().printStackTrace(); // ExecutionException is just a wrapper; we have to call getCause()
            } catch (IOException e){
                e.printStackTrace(); // error writing :wao:
            }
        });
        bw.flush();
        bw.close();
    }

}

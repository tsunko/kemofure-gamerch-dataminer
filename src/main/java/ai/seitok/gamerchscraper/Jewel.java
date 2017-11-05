package ai.seitok.gamerchscraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class Jewel {

    private static final String[] SECTIONS_TO_MINE = {
            "ui_wikidb_top_area",
            "ui_wikidb_top_pc",
            "ui_wikidb_middle_area",
            "ui_wiki_db_bottom_wrapper"
    };

    private final String pagePath;
    private final String name;
    private Map<String, String> minedValues;
    private Map<String, String> minedDetails;

    private Jewel(String pagePath){
        this.name = pagePath;
        this.pagePath = URLEncoder.encode(pagePath).replace("+", "%20");
    }

    public void mine() throws IOException {
        if(minedValues != null) throw new IOException("Attempted to re-mine Jewel " + pagePath);

        Document doc = Jsoup.connect(Application.BASE_URL + pagePath).get();
        minedValues = new HashMap<>();
        minedDetails = new HashMap<>();

        Element section;
        for(String divId : SECTIONS_TO_MINE){
            section = doc.getElementsByClass(divId).first();
            for(Element stat : section.getElementsByTag("p")){
                if(stat.children().size() == 0) continue;

                // thank god SO exists: 12067570
                // basically, unwrap any links contained inside so that we can get a full text
                stat.select("a").unwrap();

                String name = stat.child(0).ownText().trim();
                if(name.isEmpty()) continue; // bad stat?

                String value = stat.ownText().replace("\u00A0", "").trim();
                if(value.isEmpty() && stat.children().size() > 1)
                    value = stat.child(1).text();

                Elements details = stat.getElementsByClass("ui_bottom_detail");
                if(details.size() > 0){
                    StringBuilder sb = new StringBuilder();
                    for(Element detailLine : details){
                        if(detailLine.tagName().equalsIgnoreCase("br"))
                            continue;
                        if(sb.length() != 0)
                            sb.append("<newline>");
                        sb.append(detailLine.text());
                    }

                    String detail = sb.toString().trim();
                    if(!detail.isEmpty()) {
                        if (detail.endsWith("<newline>")) {
                            detail = detail.substring(0, detail.lastIndexOf("<newline>"));
                        }
                        minedDetails.put(name, detail);
                    }
                }

                minedValues.put(name, value);
            }
        }
    }

    public String getName(){
        return name;
    }

    public Map<String, String> getMinedDetails(){
        if(minedDetails == null) throw new IllegalStateException("Jewel \"" + pagePath + "\" has not been mined yet.");
        return minedDetails;
    }

    public Map<String, String> getValues(){
        if(minedValues == null) throw new IllegalStateException("Jewel \"" + pagePath + "\" has not been mined yet.");
        return minedValues;
    }

    public static Jewel fromElement(Element element){
        return new Jewel(
                element.getElementsByAttributeValue("data-col", "1")
                        .first()
                        .select("a")
                        .first().attr("href").substring(1)
        );
    }

}

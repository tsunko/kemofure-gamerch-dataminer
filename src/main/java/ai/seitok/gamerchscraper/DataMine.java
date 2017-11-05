package ai.seitok.gamerchscraper;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DataMine {

    private final Element parent;
    private List<Jewel> discoveredJewels;
    private Map<String, Exception> errors;
    private List<Future<Jewel>> miners;

    public DataMine(Element parent){
        this.parent = parent;
    }

    public void scoutJewels(String targetNode){
        if(discoveredJewels != null) System.out.println("Data mine for \"" + parent + "\" wasn't fully mined.");
        discoveredJewels = new ArrayList<>();

        Elements jewels = parent.getElementsByTag(targetNode).first().children();
        System.out.println("Scouting " + jewels.size() + " jewels...");

//        int ___debug_count = 3;
        for(Element ele : jewels){
//            if(--___debug_count < 0) break;
            if(!ele.tagName().equalsIgnoreCase("tr")) continue; // skip over none table rows
            Jewel jewel = Jewel.fromElement(ele);
            discoveredJewels.add(jewel);
        }
    }

    public void mineAllJewelsWithService(ExecutorService exec){
        if(discoveredJewels == null) throw new IllegalStateException("Data mine for \"" + parent + "\" hasn't been scouted yet.");

        errors = new ConcurrentHashMap<>();
        miners = new ArrayList<>();

        for(Jewel jewel : discoveredJewels){
            System.out.println("Scheduled mining operation on " + jewel.getName());
            miners.add(exec.submit(() -> mineJewel(jewel), jewel));
        }
    }

    public List<Future<Jewel>> getMiners() {
        return miners;
    }

    public Map<String, Exception> getErrors() {
        return errors;
    }

    private void mineJewel(Jewel jewel){
        try {
            System.out.println("Started mining operation on " + jewel.getName());
            jewel.mine();
            System.out.println("Finished mining operation on " + jewel.getName());
        } catch (Exception e){
            errors.put(jewel.getName(), e);
        }
    }

}

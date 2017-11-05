package ai.seitok.gamerchscraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// TY kjw for the bulk of the TL'ing work (spreadsheet; esp. for skills)
public class _original_Application {

    private static final String BASE_URL = "https://kemono-friends.gamerch.com/";
    private static final Map<String, String> STAT_TRANSLATION = build("","",
            "初期HP", "Initial HP",
            "初期ATK", "Initial ATK",
            "最大HP", "Max HP",
            "最大ATK", "Max ATK",
            "移動速度", "Movement Speed",
            "攻撃速度", "Attack Speed",
            "ノックバック", "Knockback",
            "アンチノックバック", "Anti-Knockback",
            "リーチ", "Range/Reach",
            "DPS(1体)", "DPS (Single target)",
            "DPS(全体)", "DPS (Overall)",
            "最大DPS(1体)", "Max DPS (Single target)",
            "最大DPS(全体)", "Max DPS (Overall)",
            "スキルチャージ", "Skill charge rate",
            "同時攻撃数", "Targets",
            "サイズ", "Size",
            "属性", "Attribute",
            "レアリティ", "Rarity",
            "攻撃タイプ", "Attack Type",
            "スキル種類", "Skill Type",
            "コスト", "Cost",
            "最大Lv/最大解放LV", "Max LV/Max Limit-Broken LV",
            "野生解放", "Wild Release Condition",

            // trivia/other stuff
            "CV・プロフィール", "Seiyuu",
            "夜行性", "Nocturnal",
            "得意地形", "Advantageous Terrain",
            "苦手地形", "Disadvantageous Terrain",
            "入手方法など", "How to Obtain",
            "グループ", "Group/Clan/Guild",
            "スキル", "Special Skill"
    );
    private static final Map<String, String> STAT_VALUE_TRANSLATION = build("","",
            // attribute
            "ピュア", "Pure",
            "クール", "Cool",
            "パッション", "Passion",

            // "wild release"/limit breaking
            "同一フレンズのみ", "Same Friend Only",
            "特殊野生解放", "Special Item",

            // attack range
            "回復", "Healing",
            "近距離", "Short",
            "中距離", "Medium",
            "遠距離", "Long",

            // terrain
            "平原", "Plains",
            "森林", "Forest",
            "砂地", "Sandy Soil",
            "水辺", "Waterfront",
            "雪原", "Snowfield",
            "雲海", "Sky",
            "海洋", "Ocean",
            "都市", "City",
            "なし", "None",

            // skill names
            "全攻速上昇", "All-Friend ATK Speed Up",
            "全ATK上昇", "All-Friend ATK Up",
            "全防御上昇", "All-Friend DEF Up",
            "全体回復", "All-Friend Heal",
            "全移動上昇", "All-Friend Movement Up",
            "全ATK低下", "All-Target Attack Down",
            "全攻速低下", "All-Target ATK Speed Down",
            "全体攻撃", "All-Target Attack",
            "全防御低下", "All-Target DEF Down",
            "全移動低下", "All-Target Movement Down",
            "ク移動上昇", "Cool Friend Movement Up",
            "クATK低下", "Cool Target ATK Down",
            "ク全体攻撃", "Cool Target Attack",
            "パ攻速上昇", "Passion Friend ATK Speed Up",
            "パ防御上昇", "Passion Friend DEF Up",
            "ピ攻速上昇", "Pure Friend ATK Speed Up",
            "ピ防御上昇", "Pure Friend DEF Up",
            "ピ全体攻撃", "Pure Target Attack",
            "単攻速上昇", "Self ATK Speed Up",
            "単ATK上昇", "Self ATK Up",
            "単防御上昇", "Self DEF Up",
            "単移動上昇", "Self Movement Up",
            "単体回復", "Single-Friend Healer",
            "単ATK低下", "Single-Target ATK Down",
            "単攻速低下", "Single-Target ATK Speed Down",
            "単体攻撃", "Single-Target Attack",
            "単防御低下", "Single-Target DEF Down",

            // special groups/guilds/clans for friends
            "エプロン愛好会", "Apron Lovers' Club",
            "トリ系", "Birds", // TODO: ask kaji why this is "Birds"
            "百獣の王の一族", "Clan of the Kings of a Hundred Beasts",
            "キツネ・タヌキ系", "Foxes/Tanuki",
            "けも勇槍騎士団", "Kemo Courageous Spears Chivalric Order",
            "にゃんにゃんファミリー", "Nyan Nyan Family",
            "パワフルっ娘連合", "Powerful Girls Alliance",
            "まったり浮遊部", "Relaxing Floaters' Club (?)",
            "海洋系", "Sea Creatures",
            "チーム・噛んじゃうぞ", "Team \"I'll Bite You\"",
            "ウォーターガールズ", "Water Girls",
            "オオカミ連盟", "Wolf Federation"
    );

    private static final ExecutorService THREADING = Executors.newFixedThreadPool(8);

    public static void main(String[] args) throws Exception {
        Document doc = Jsoup.connect(BASE_URL + "%E3%82%AD%E3%83%A3%E3%83%A9%E4%B8%80%E8%A6%A7").get();
        int tableTagIndex = doc.toString().indexOf("ui_wikidb_table_") + "ui_wikidb_table_".length();
        String tableId = doc.toString().substring(tableTagIndex, tableTagIndex + 6);
        System.out.println("Reading from ui_wikidb_table_" + tableId);
        Element mainTable = doc.getElementById("ui_wikidb_table_" + tableId).getElementsByTag("tbody").first();
        Map<String, Map<String, String>> friends = new ConcurrentHashMap<>();
        for (Element ele : mainTable.children()) {
            String name = ele.children().get(1).getAllElements()
                    .first().children()
                    .first().children()
                    .first().text(); // this is horrible, but hopefully gamerch doesn't change their format

            final String encodedName = URLEncoder.encode(name).replace("+", "%20");
            final String goddamn_java_this_doesnt_get_changed = name;

            System.out.println("Submitting data mining task for \"" + goddamn_java_this_doesnt_get_changed + "\"");
            THREADING.submit(()->{
                Document friendPage;
                try {
                    friendPage = Jsoup.connect(BASE_URL + encodedName).get();
                } catch (IOException e){
                    e.printStackTrace();
                    return;
                }

                Map<String, String> map = new HashMap<>();
                map.putAll(readValues(friendPage,"ui_wikidb_top_area"));
                map.putAll(readValues(friendPage,"ui_wikidb_top_pc"));
                map.putAll(readValues(friendPage,"ui_wikidb_middle_area"));
                map.putAll(readValues(friendPage,"ui_wiki_db_bottom_wrapper"));
                friends.put(goddamn_java_this_doesnt_get_changed, map);
                System.out.println("Finished data mining for \"" + goddamn_java_this_doesnt_get_changed + "\"");
            });
        }

        THREADING.shutdown();
        THREADING.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);

        friends.forEach((name, data)->{
            System.out.println(name);
            data.forEach(_original_Application::printPair);
        });
    }

    public static void printPair(String a, String b){
        System.out.println("\t" + a + " = " + b);
    }

    public static Map<String, String> readValues(Document doc, String statSectionClass){
        Map<String, String> map = new HashMap<>();
        Element stats = doc.getElementsByClass(statSectionClass).first();
        for (Element stat : stats.getElementsByTag("p")) {
            Elements matched = stat.getElementsByClass("ui_wikidb_title");
            if(matched.size() <= 0) continue;
            String statName = matched.first().text().trim();
            // grr, they have a special space for some reason
            String statValue = stat.ownText().replace("\u00A0", "").trim();
            if (statValue.isEmpty() && stat.children().size() > 0) {
                statValue = stat.children().get(1).ownText();
            }

            map.put(STAT_TRANSLATION.getOrDefault(statName, statName), STAT_VALUE_TRANSLATION.getOrDefault(statValue, statValue));
        }
        return map;
    }

    public static Map<String, String> build(String... data){
        Map<String, String> result = new HashMap<String, String>();

        if(data.length % 2 != 0)
            throw new IllegalArgumentException("Odd number of arguments");

        String key = null;
        Integer step = -1;

        for(String value : data){
            step++;
            switch(step % 2){
                case 0:
                    if(value == null)
                        throw new IllegalArgumentException("Null key value");
                    key = value;
                    continue;
                case 1:
                    result.put(key, value);
                    break;
            }
        }

        return result;
    }

}

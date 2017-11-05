package ai.seitok.gamerchscraper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Translator {

    private static final Map<String, String> FIELD_NAME_DICTIONARY = buildFromPairs(
/* *****************************CHARACTERS (キャラ)***************************** */
            // basic stats
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
            "スキル", "Special Skill",

/* *****************************ITEMS (アイテム)***************************** */
            "種類", "Type",
            // rarity already defined
            // size already defined
            "購入価格", "Purchase Price",
            "売却価格", "Sellback Price",
            "効果・説明", "Effect/Description",
            "入手方法", "How to Get" // diff. from characters "How to Obtain"
    );

    private static final Map<String, String> FIELD_VALUE_TRANSLATION = buildFromPairs(
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

            // how to obtain
            "キラキラガチャ", "Glittering Gacha",
            "ガチャ", "Standard Gacha",
            "イベント", "Event",
            "クエストクリア報酬", "Quest Reward",
            "ミッションパネル報酬", "Mission Panel Reward",
            "ケロロ小隊スカウト", "Keroro Plantoon Collab(?) Scout",
            "交換所", "Trading",
            "イベントスカウト", "Event Scout",
            "攻殻機動隊タイアップスカウト", "Ghost in the Shell Collab(?) Scout",

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

    public static String tryKeyTranslation(String jp){
        return FIELD_NAME_DICTIONARY.getOrDefault(jp, jp);
    }

    public static String tryValueTranslation(String jp){
        return FIELD_VALUE_TRANSLATION.getOrDefault(jp, jp);
    }

    private static Map<String, String> buildFromPairs(String... pairs){
        if(pairs.length % 2 != 0){
            throw new IllegalArgumentException("\"pairs\" must be in multiples of 2");
        }

        Map<String, String> mapping = new HashMap<>();
        for(int i=0; i < pairs.length; i += 2) {
            String key = pairs[i];
            String value = pairs[i + 1];
            mapping.put(key, value);
        }
        return Collections.unmodifiableMap(mapping);
    }

}

package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import static java.util.Arrays.asList;

import uk.gov.moj.cpp.unifiedsearch.test.util.ingest.document.AddressDocument;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomAddresses {

    private static final int ADDRESS_COUNT = 120;


    private static final List<String> POSTCODE_LIST = asList(
            "SW72 57NW", "OX98 42S", "SW88 41SW", "PT77 57HH", "BN57 36BN", "S14 19OX", "S53 99S", "S41 24S",
            "GL91 38BN", "BN78 26HH", "SW3 94GL", "HH52 41OX", "SW9 92OX", "NW26 39SW", "GL44 11NW", "PT8 33OX",
            "NW36 34PT", "OX47 38PT", "SW46 11NW", "GL81 11S", "HH21 91OX", "NW99 92OX", "GL5 39NW", "GL15 53BN",
            "PT15 18HH", "GL27 60PT", "S74 96NW", "NW4 43GL", "PT37 60GL", "OX57 99NW", "BN82 37GL", "GL83 80GL",
            "PT48 47OX", "S74 7GL", "NW26 35BN", "NW6 57SW", "GL96 19NW", "OX23 61SW", "SW35 95SW", "OX98 70S",
            "HH37 6OX", "BN52 16GL", "NW52 44S", "OX33 36SW", "GL75 55S", "SW94 80S", "BN49 51OX", "BN82 73GL",
            "OX62 29NW", "NW22 25SW", "OX66 24OX", "PT76 10HH", "HH1 63S", "OX35 92NW", "HH22 30SW", "HH76 54PT",
            "S81 17BN", "BN32 15SW", "NW19 77NW", "NW86 72GL", "SW22 60NW", "GL31 17SW", "GL41 98HH", "BN48 75NW",
            "PT89 93SW", "HH63 3PT", "NW44 29PT", "PT48 12NW", "GL6 28GL", "PT76 90S", "PT68 82GL", "HH79 71OX",
            "HH33 90HH", "NW90 21OX", "OX19 97SW", "PT91 6NW", "GL8 81S", "OX84 62BN", "S77 33BN", "NW56 31PT",
            "PT60 53PT", "S67 93GL", "PT22 23NW", "PT13 4S", "SW36 28OX", "GL83 46HH", "S17 32S", "PT37 77GL",
            "BN10 36PT", "GL84 40PT", "OX7 99PT", "GL89 32GL", "PT54 71PT", "GL38 37NW", "OX66 6S", "GL38 71PT",
            "SW23 53OX", "SW11 66NW", "BN79 38SW", "OX90 72PT", "SW96 99HH", "NW9 36OX", "GL35 12SW", "OX32 79HH",
            "NW58 49PT", "HH92 24S", "OX16 48SW", "SW62 86S", "S3 3BN", "GL64 70PT", "OX22 68BN", "NW84 58OX",
            "OX21 21GL", "PT23 28BN", "S70 42OX", "NW21 65GL", "NW44 41GL", "NW3 42HH", "SW48 66S", "SW33 43SW"
    );

    private static final List<String> ADDRESS_LIST = asList(
            "179 Chester Avenue Portsmouth Arkansas",
            "30 Harwood Place Brighton Indiana",
            "170 Knickerbocker Avenue Oxford Georgia",
            "177 Charles Place Guildford Iowa",
            "7 Jodie Court Hemel Hemstead Puerto Rico",
            "145 Pine Street London Maine",
            "84 Bergen Avenue Southampton Pennsylvania",
            "55 Albemarle Road London South Dakota",
            "58 Vandervoort Place Southampton Montana",
            "154 Grace Court Hemel Hemstead Virgin Islands",
            "192 Pineapple Street Guildford Illinois",
            "147 Lamont Court Oxford Colorado",
            "15 Norwood Avenue Portsmouth Massachusetts",
            "150 Channel Avenue Hertford New Hampshire",
            "185 Dodworth Street Hertford Florida",
            "137 Legion Street London Nebraska",
            "5 Prospect Place Brighton Nevada",
            "32 Frank Court Portsmouth Missouri",
            "24 Bridge Street Woking Wisconsin",
            "44 Noel Avenue Southampton Utah",
            "174 Willmohr Street Guildford Connecticut",
            "67 Holmes Lane Hertford Marshall Islands",
            "57 Crooke Avenue Southampton New Jersey",
            "97 Duryea Place Brighton District Of Columbia",
            "173 Fillmore Avenue Hemel Hemstead Delaware",
            "140 Tapscott Avenue Brighton Oklahoma",
            "161 Dewitt Avenue Oxford Mississippi",
            "82 Lenox Road Oxford Arizona",
            "174 Sands Street Southampton Vermont",
            "166 Clinton Avenue Guildford Rhode Island",
            "129 Elizabeth Place Woking Guam",
            "172 Monument Walk Portsmouth New York",
            "85 Louise Terrace Portsmouth Virginia",
            "50 Hicks Street Guildford New Mexico",
            "50 Kermit Place Oxford South Carolina",
            "46 Montauk Court Hemel Hemstead Tennessee",
            "118 Alton Place Southampton Texas",
            "180 Seigel Street Hertford Michigan",
            "198 Hinsdale Street Brighton West Virginia",
            "32 Glendale Court Woking Kentucky",
            "105 Prescott Place Hemel Hemstead Federated States Of Micronesia",
            "129 Pitkin Avenue Woking Kansas",
            "103 Brevoort Place Portsmouth Maryland",
            "118 Moultrie Street Oxford Louisiana",
            "102 Navy Walk Hertford Northern Mariana Islands",
            "142 Fanchon Place London Hawaii",
            "163 Ryerson Street Woking Ohio",
            "198 Judge Street Woking Palau",
            "42 Bergen Place Hemel Hemstead California",
            "129 Remsen Street Oxford North Carolina",
            "8 Vandervoort Avenue Southampton American Samoa",
            "103 Richmond Street Brighton Oregon",
            "49 Sunnyside Avenue Hemel Hemstead Alaska",
            "187 Fay Court Woking Idaho",
            "107 Lefferts Avenue Portsmouth Minnesota",
            "66 Banker Street Hemel Hemstead Wyoming",
            "124 Richards Street Portsmouth Washington",
            "30 Rutland Road Hemel Hemstead North Dakota",
            "138 Lorimer Street Southampton Arkansas",
            "138 Franklin Street Portsmouth Indiana",
            "173 Main Street Oxford Georgia",
            "193 Manhattan Avenue London Iowa",
            "32 Locust Street Guildford Puerto Rico",
            "58 Love Lane Hertford Maine",
            "52 Sheffield Avenue Oxford Pennsylvania",
            "83 Beaumont Street Woking South Dakota",
            "113 Wakeman Place Southampton Montana",
            "78 Rutherford Place Southampton Virgin Islands",
            "115 Dorset Street Hertford Illinois",
            "72 Norman Avenue Southampton Colorado",
            "157 Greenwood Avenue Brighton Massachusetts",
            "124 Seagate Avenue London New Hampshire",
            "58 Kenmore Terrace Oxford Florida",
            "128 Royce Street Hemel Hemstead Nebraska",
            "26 Bokee Court Hemel Hemstead Nevada",
            "118 Flatlands Avenue Hemel Hemstead Missouri",
            "97 Gerritsen Avenue Oxford Wisconsin",
            "198 Story Street Hemel Hemstead Utah",
            "180 Douglass Street London Connecticut",
            "18 Jamaica Avenue London Marshall Islands",
            "131 Clinton Street Hemel Hemstead New Jersey",
            "199 Bridgewater Street Woking District Of Columbia",
            "49 Central Avenue Oxford Delaware",
            "90 Everit Street Hertford Oklahoma",
            "128 Devon Avenue Guildford Mississippi",
            "82 Tilden Avenue Portsmouth Arizona",
            "50 Veronica Place Southampton Vermont",
            "43 Anthony Street Portsmouth Rhode Island",
            "108 Crown Street Hertford Guam",
            "84 Ryder Street Guildford New York",
            "14 Gerald Court Brighton Virginia",
            "130 Wolcott Street Oxford New Mexico",
            "112 Hendrickson Place London South Carolina",
            "159 Grand Street London Tennessee",
            "150 Sullivan Place Hemel Hemstead Texas",
            "76 Newel Street Guildford Michigan",
            "62 Canda Avenue Hertford West Virginia",
            "105 Banner Avenue Brighton Kentucky",
            "176 Conselyea Street Portsmouth Federated States Of Micronesia",
            "199 Humboldt Street Oxford Kansas",
            "145 Richardson Street Woking Maryland",
            "109 Montieth Street Hemel Hemstead Louisiana",
            "98 Box Street Hertford Northern Mariana Islands",
            "200 Hoyt Street London Hawaii",
            "139 Gelston Avenue Oxford Ohio",
            "94 Locust Avenue Woking Palau",
            "56 Miami Court Brighton California",
            "25 Furman Avenue Oxford North Carolina",
            "180 Hewes Street Southampton American Samoa",
            "133 Clifton Place Brighton Oregon",
            "26 Hamilton Avenue Woking Alaska",
            "66 Manhattan Court Woking Idaho",
            "175 Keap Street Woking Minnesota",
            "200 Virginia Place Hemel Hemstead Wyoming",
            "51 Court Square Hemel Hemstead Washington",
            "145 Degraw Street Woking North Dakota",
            "195 Merit Court Hemel Hemstead Arkansas",
            "108 Thames Street Southampton Indiana",
            "123 Suydam Place Guildford Georgia",
            "35 Myrtle Avenue Portsmouth Iowa"
    );


    public static String randomAddress() {
        return ADDRESS_LIST.get(ThreadLocalRandom.current().nextInt(0, ADDRESS_COUNT - 1));
    }

    public static String randomPostcode() {
        return POSTCODE_LIST.get(ThreadLocalRandom.current().nextInt(0, ADDRESS_COUNT - 1));
    }

    public static AddressDocument randomAddressObject() {
        final String addressString = ADDRESS_LIST.get(ThreadLocalRandom.current().nextInt(0, ADDRESS_COUNT - 1));

        final String [] tokens = addressString.split("\\s");
        final String address1 = String.format("%s %s %s", tokens[0], tokens[1], tokens[2]);
        final String address2 = tokens[3];
        final String address3 = tokens.length > 4 ? tokens[4] : null;
        final String address4 = tokens.length > 5 ? tokens[5] : null;

        return new AddressDocument(address1, address2, address3, address4, null, randomPostcode());
    }
}

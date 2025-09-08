package uk.gov.moj.cpp.unifiedsearch.test.util.ingest.mothers;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomNames {

    private static final int NAME_COUNT = 120;

    private static final List<String> FIRST_NAMES = asList(
            "Lidia", "Brennan", "Mcfarland", "Kenya", "Jerri", "Talley", "Luann", "Morton", "Meredith", "Concetta", "Maldonado", "Raymond",
            "Shaffer", "Sue", "Lydia", "Meyers", "Lakeisha", "Barber", "Berta", "Jan", "Burch", "Erna", "Witt", "Jolene", "Danielle", "Rojas",
            "Todd", "Kristie", "Earline", "Carey", "Wolfe", "Tanisha", "Angelina", "Nelson", "Audrey", "Dale", "Valerie", "Latasha", "Catherine",
            "Jennifer", "Dianna", "Eliza", "David", "Sonya", "Murray", "Barnett", "Atkinson", "Mcneil", "Parrish", "Daugherty", "Skinner",
            "Savannah", "Ryan", "Lesley", "Hobbs", "Vazquez", "Francisca", "Mccall", "Wilkerson", "Robbins", "Reese", "Lloyd", "Simon",
            "Stevenson", "Knight", "Dean", "Lina", "Arlene", "Malone", "Buck", "Ann", "Delacruz", "Chrystal", "Martinez", "Claudine",
            "Frost", "Arline", "Dominique", "Meadows", "Phoebe", "Hewitt", "Virginia", "Oneal", "Rachel", "Steele", "Nancy", "Cohen", "Hebert",
            "Cooke", "Ophelia", "Farley", "Roslyn", "Harmon", "Craft", "Tamera", "Preston", "Pollard", "Patterson", "Amalia", "Le", "Mandy",
            "Alana", "Gilmore", "Consuelo", "Roxie", "Carolyn", "Angelita", "Mejia", "Chapman", "Lucinda", "Christine", "Adrienne", "Sosa",
            "Becker", "Stone", "Malinda", "Brooke", "Tamara", "Renee", "Craig"
    );


    private static final List<String> MIDDLE_NAMES = asList(
            "Cleveland", "Booth", "Miranda", "Puckett", "Samantha", "Medina", "Delgado", "Blanchard", "Oconnor",
            "Sheryl", "Noelle", "Loraine", "Kline", "Lindsey", "Gibbs", "Gordon", "Cathy", "Annabelle", "Shawna",
            "Henderson", "Guerrero", "Jasmine", "Mercer", "Mae", "Elinor", "Brandy", "Lynch", "Mcdaniel", "Kara",
            "Willis", "Hoover", "Mccarthy", "Faye", "Stevens", "Glass", "Ortega", "Loretta", "Agnes", "Tonia",
            "Acevedo", "Stanley", "Coleman", "Hardin", "Violet", "Amy", "Wright", "Aileen", "Cristina", "Hodges",
            "Felecia", "Ginger", "Megan", "Holloway", "Callie", "Anne", "Acosta", "Fernandez", "Paula", "Madge",
            "Beasley", "Myrtle", "Maribel", "Zimmerman", "Freeman", "Dalton", "Soto", "Wilkinson", "Campos", "Alma",
            "Morris", "Ofelia", "Lizzie", "Snider", "Vincent", "Muriel", "Holden", "Stark", "Gay", "Bethany", "Landry",
            "Floyd", "Elizabeth", "Wilma", "Earlene", "Cecile", "Pruitt", "Fields", "Rice", "Kaitlin", "Magdalena", "Hattie",
            "Fern", "Rosetta", "Margie", "Kristy", "Richard", "Garner", "Corrine", "Hinton", "Monica", "Huff", "Randi", "Suarez",
            "Chasity", "Fuentes", "Alta", "Lee", "Dyer", "Beverley", "Baird", "Lara", "Alston", "Kristen", "Shelton", "Hayden",
            "Bonner", "Constance", "Macdonald", "Dodson", "Deborah"
    );


    private static final List<String> LAST_NAMES = asList(
            "Hanson", "Nelson", "Greer", "Hampton", "Bentley", "Booker", "Mercer", "Gillespie", "Ruiz",
            "Blanchard", "Hammond", "Flowers", "Casey", "Olsen", "Pennington", "Farley", "Joyner",
            "Baxter", "Henson", "Barnes", "Fisher", "Green", "Conley", "Weaver", "Thornton", "Washington",
            "Maxwell", "Mccall", "Mcleod", "Stuart", "Francis", "Combs", "Shaffer", "Kramer", "Ewing",
            "Berger", "Cooper", "Henry", "Snider", "Robertson", "Sosa", "Grimes", "Gould", "Duffy", "Holmes",
            "Aguilar", "Ramos", "Villarreal", "Dyer", "Reid", "Wooten", "Waller", "Holden", "Turner", "Davenport",
            "Trujillo", "Freeman", "House", "Duran", "Morse", "Mccray", "Ayala", "Abbott", "Mejia", "Ford",
            "Serrano", "Hart", "Shaw", "Huber", "Acevedo", "Camacho", "Reese", "Herman", "Holland", "Burt",
            "Delacruz", "Jefferson", "Martin", "Durham", "Potter", "Morrison", "Sanders", "Dillard", "Finch",
            "Vazquez", "Salinas", "Gordon", "Skinner", "Calderon", "Pate", "Moreno", "Dotson", "Fernandez",
            "Ramsey", "Rosales", "Vargas", "Stone", "Leach", "Richardson", "Spencer", "Rollins", "Warner", "Fuentes",
            "Joseph", "Rivas", "Campbell", "Bradshaw", "Tanner", "Rosario", "Torres", "Chapman", "Witt", "Bullock",
            "Hansen", "Brown", "Barrera", "Alexander", "Sanford", "Ashley", "Owen"
    );


    private static final List<String> ORGANISATION_NAMES = asList(
            "Pyrami", "Glukgluk", "Namebox", "Brainquil", "Peticular", "Squish", "Perkle", "Inquala",
            "Valreda", "Glasstep", "Bunga", "Konnect", "Petigems", "Sulfax", "Gazak", "Bluegrain", "Isopop",
            "Acium", "Ontagene", "Aquasseur", "Qiao", "Fleetmix", "Multron", "Ramjob", "Xyqag", "Zerbina",
            "Exotechno", "Oulu", "Acruex", "Intrawear", "Accidency", "Biohab", "Geeky", "Comtours", "Eyeris",
            "Accuprint", "Earthmark", "Flyboyz", "Omatom", "Sloganaut", "Dogtown", "Comtrail", "Bullzone",
            "Endipine", "Maxemia", "Corepan", "Amril", "Bulljuice", "Grupoli", "Xerex", "Yogasm", "Incubus",
            "Isbol", "Zilencio", "Inear", "Callflex", "Ohmnet", "Portalis", "Poshome", "Softmicro", "Corporana",
            "Zentime", "Niquent", "Zaj", "Accel", "Viocular", "Futuris", "Zipak", "Repetwire", "Megall", "Equicom",
            "Syntac", "Lovepad", "Applica", "Quarex", "Lyrichord", "Savvy", "Typhonica", "Mantro", "Comtent", "Enthaze",
            "Qimonk", "Futurity", "Frosnex", "Polaria", "Qnekt", "Duoflex", "Coriander", "Eplode", "Powernet", "Tellifly",
            "Insurety", "Kiggle", "Waterbaby", "Kenegy", "Vortexaco", "Joviold", "Ginkle", "Endipin", "Enomen", "Undertap",
            "Immunics", "Isonus", "Hatology", "Neptide", "Zytrek", "Concility", "Interfind", "Neocent", "Verton", "Reversus",
            "Candecor", "Quailcom", "Zaggles", "Xelegyl", "Quadeebo", "Zilch", "Unisure", "Comvey", "Martgo"
    );


    public static String randomFirstName() {
        return FIRST_NAMES.get(ThreadLocalRandom.current().nextInt(0, NAME_COUNT - 1));
    }

    public static String randomMiddleName() {
        return MIDDLE_NAMES.get(ThreadLocalRandom.current().nextInt(0, NAME_COUNT - 1));
    }

    public static String randomLastName() {
        return LAST_NAMES.get(ThreadLocalRandom.current().nextInt(0, NAME_COUNT - 1));
    }

    public static String randomOrganisationName() {
        return ORGANISATION_NAMES.get(ThreadLocalRandom.current().nextInt(0, NAME_COUNT - 1));
    }
}

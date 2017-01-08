package home.criminalintent.database;

/**
 * Created by Дима on 07.01.2017.
 */

public class CrimeDbShema
{
    public static final class CrimeTable
    {
        public static final String NAME = "CRIMES";

        public static final class Cols
        {
            public static final String UUID = "UUID";
            public static final String TITLE = "TITLE";
            public static final String DATE = "DATE";
            public static final String SOLVED = "SOLVED";
        }
    }
}

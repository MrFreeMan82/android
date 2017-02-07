package home.animals.database;

/**
 * Created by Дима on 07.02.2017.
 */

public class DBShema {
    public static final class NodeTable{
        public static final String NAME = "NODES";

        public static final class Cols{
            public static final String NODE_ID = "NODE_ID";
            public static final String QUESTION = "QUESTION";
            public static final String ANSWEAR = "ANSWEAR";
            public static final String YES_NODE_ID = "YES_NODE_ID";
            public static final String NO_NODE_ID = "NO_NODE_ID";
        }
    }
}

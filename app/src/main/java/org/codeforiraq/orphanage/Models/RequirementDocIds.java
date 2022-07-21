package org.codeforiraq.orphanage.Models;

import java.util.ArrayList;
import java.util.List;

public class RequirementDocIds {

    public static List<String> reqDocIds;

    public static void initialize() {
        reqDocIds = new ArrayList<>();
    }

    public static void setReqDocIds(String docId) {
        reqDocIds.add(docId);
    }
}

package net.rainbowcreation.spongelobby.queue;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class Qlist {
    ArrayList<QManager>[] qlist = new ArrayList[20];
    public Qlist() {
        for (int i = 0; i < 20; i++) {
            qlist[i] = new ArrayList<>();
        }
    }
    public ArrayList<QManager>[] get() {
        return qlist;
    }

    public ArrayList<QManager> get(int priority) {
        return qlist[priority];
    }

    public ArrayList<QManager> getHightestPriority() {
        for (ArrayList<QManager> qManagerArrayList: qlist) {
            if (!qManagerArrayList.isEmpty()) {
                return qManagerArrayList;
            }
        }
        return null;
    }

    public Boolean contains(int priority) {
        return qlist[priority].isEmpty();
    }

    public Qlist add(QManager qManager) {
        int prio = qManager.getPriority();
        if (prio >= 0 && prio <= 19)
            qlist[prio].add(qManager);
        return this;
    }
}

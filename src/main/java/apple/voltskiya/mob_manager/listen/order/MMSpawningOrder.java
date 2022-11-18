package apple.voltskiya.mob_manager.listen.order;

public enum MMSpawningOrder {
    SOONEST,
    SOONER,
    SOON,
    NORMAL,
    LATE,
    LATER,
    LATEST;

    public int order() {
        return ordinal();
    }
}

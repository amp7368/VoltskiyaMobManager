package apple.voltskiya.mob_manager.storage;

import java.util.List;

public class MMSpawnedSaved {

    private List<String> listeners;

    public MMSpawnedSaved() {
    }

    public MMSpawnedSaved(List<String> listeners) {
        this.listeners = listeners;
    }

    public List<String> getListeners() {
        return this.listeners;
    }
}

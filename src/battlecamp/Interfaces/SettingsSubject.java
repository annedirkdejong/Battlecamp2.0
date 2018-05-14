package battlecamp.Interfaces;

public interface SettingsSubject {

    void register(SettingsObserver o);
    void unregister(SettingsObserver o);

}

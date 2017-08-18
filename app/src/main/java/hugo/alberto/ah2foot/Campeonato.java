package hugo.alberto.ah2foot;

/**
 * Created by Alberto on 18/08/2017.
 */

public class Campeonato {
    String id;
    String campeonato;

    public Campeonato(String id, String campeonato) {
        this.id = id;
        this.campeonato = campeonato;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCampeonato() {
        return campeonato;
    }

    public void setCampeonato(String campeonato) {
        this.campeonato = campeonato;
    }
}

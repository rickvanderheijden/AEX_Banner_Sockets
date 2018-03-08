package shared.request;

import java.util.List;

public class RequestFondsen extends Request {
    public RequestFondsen(List<String> fondsen) {
        super();
        this.fondsen = fondsen;
    }
}

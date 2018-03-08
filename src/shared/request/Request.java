package shared.request;

import shared.interfaces.IRequest;

import java.util.ArrayList;
import java.util.List;

public class Request implements IRequest {
    List<String> fondsen = new ArrayList<>();

    public List<String> getRequestFondsen() {
        return fondsen;
    }
}

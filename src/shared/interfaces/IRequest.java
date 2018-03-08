package shared.interfaces;

import java.io.Serializable;
import java.util.List;

public interface IRequest extends Serializable{
    List<String> getRequestFondsen();
}
